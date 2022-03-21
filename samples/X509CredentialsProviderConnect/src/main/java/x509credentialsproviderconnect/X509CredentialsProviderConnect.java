/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package x509credentialsproviderconnect;

import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.CrtResource;
import software.amazon.awssdk.crt.CrtRuntimeException;
import software.amazon.awssdk.crt.auth.credentials.X509CredentialsProvider;
import software.amazon.awssdk.crt.http.HttpProxyOptions;
import software.amazon.awssdk.crt.io.ClientBootstrap;
import software.amazon.awssdk.crt.io.ClientTlsContext;
import software.amazon.awssdk.crt.io.TlsContextOptions;
import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt.MqttClientConnectionEvents;
import software.amazon.awssdk.crt.mqtt.MqttMessage;
import software.amazon.awssdk.crt.mqtt.QualityOfService;
import software.amazon.awssdk.iot.AwsIotMqttConnectionBuilder;
import software.amazon.awssdk.iot.iotjobs.model.RejectedError;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import utils.commandlineutils.CommandLineUtils;

public class X509CredentialsProviderConnect {
    // When run normally, we want to exit nicely even if something goes wrong
    // When run from CI, we want to let an exception escape which in turn causes the
    // exec:java task to return a non-zero exit code
    static String ciPropValue = System.getProperty("aws.crt.ci");
    static boolean isCI = ciPropValue != null && Boolean.valueOf(ciPropValue);

    static CommandLineUtils cmdUtils;

    static void onRejectedError(RejectedError error) {
        System.out.println("Request rejected: " + error.code.toString() + ": " + error.message);
    }

    /*
     * When called during a CI run, throw an exception that will escape and fail the exec:java task
     * When called otherwise, print what went wrong (if anything) and just continue (return from main)
     */
    static void onApplicationFailure(Throwable cause) {
        if (isCI) {
            throw new RuntimeException("BasicConnect execution failure", cause);
        } else if (cause != null) {
            System.out.println("Exception encountered: " + cause.toString());
        }
    }

    public static void main(String[] args) {

        cmdUtils = new CommandLineUtils();
        cmdUtils.registerProgramName("x509CredentialsProviderConnect");
        cmdUtils.addCommonMQTTCommands();
        cmdUtils.addCommonProxyCommands();
        cmdUtils.addCommonX509Commands();
        cmdUtils.registerCommand("signing_region", "<str>", "AWS IoT service region.");
        cmdUtils.registerCommand("client_id", "<int>", "Client id to use (optional, default='test-*').");
        cmdUtils.registerCommand("port", "<int>", "Port to connect to on the endpoint (optional, default='8883').");
        cmdUtils.sendArguments(args);

        String endpoint = cmdUtils.getCommandRequired("endpoint", "");
        String clientId = cmdUtils.getCommandOrDefault("client_id", "test-" + UUID.randomUUID().toString());
        int port = Integer.parseInt(cmdUtils.getCommandOrDefault("port", String.valueOf(8883)));
        String caPath = cmdUtils.getCommandOrDefault("ca_file", "");
        String signingRegion = cmdUtils.getCommandRequired("signing_region", "");
        String proxyHost = cmdUtils.getCommandOrDefault("proxy_host", "");
        int proxyPort = Integer.parseInt(cmdUtils.getCommandOrDefault("proxy_port", "8080"));

        String x509RoleAlias = cmdUtils.getCommandRequired("x509_role_alias", "");
        String x509Endpoint = cmdUtils.getCommandRequired("x509_endpoint", "");
        String x509Thing = cmdUtils.getCommandRequired("x509_thing", "");
        String x509CertPath = cmdUtils.getCommandRequired("x509_cert", "");
        String x509KeyPath = cmdUtils.getCommandRequired("x509_key", "");
        String x509CaPath = cmdUtils.getCommandOrDefault("x509_ca_file", "");

        MqttClientConnectionEvents callbacks = new MqttClientConnectionEvents() {
            @Override
            public void onConnectionInterrupted(int errorCode) {
                if (errorCode != 0) {
                    System.out.println("Connection interrupted: " + errorCode + ": " + CRT.awsErrorString(errorCode));
                }
            }

            @Override
            public void onConnectionResumed(boolean sessionPresent) {
                System.out.println("Connection resumed: " + (sessionPresent ? "existing session" : "clean session"));
            }
        };

        try (
            AwsIotMqttConnectionBuilder builder = AwsIotMqttConnectionBuilder.newMtlsBuilderFromPath(null, null)) {

            if (caPath != null) {
                builder.withCertificateAuthorityFromPath(null, caPath);
            }

            builder.withConnectionEventCallbacks(callbacks)
                    .withClientId(clientId)
                    .withEndpoint(endpoint)
                    .withPort((short)port)
                    .withCleanSession(true)
                    .withProtocolOperationTimeoutMs(60000);

            HttpProxyOptions proxyOptions = null;
            if (proxyHost != null && proxyHost != "" && proxyPort > 0) {
                proxyOptions = new HttpProxyOptions();
                proxyOptions.setHost(proxyHost);
                proxyOptions.setPort(proxyPort);
                builder.withHttpProxyOptions(proxyOptions);
            }

            builder.withWebsockets(true);
            builder.withWebsocketSigningRegion(signingRegion);

            try (TlsContextOptions x509TlsOptions = TlsContextOptions.createWithMtlsFromPath(x509CertPath, x509KeyPath)) {
                if (x509CaPath != null && x509CaPath != "") {
                    x509TlsOptions.withCertificateAuthorityFromPath(null, x509CaPath);
                }

                try (ClientTlsContext x509TlsContext = new ClientTlsContext(x509TlsOptions)) {
                    X509CredentialsProvider.X509CredentialsProviderBuilder x509builder = new X509CredentialsProvider.X509CredentialsProviderBuilder()
                            .withTlsContext(x509TlsContext)
                            .withEndpoint(x509Endpoint)
                            .withRoleAlias(x509RoleAlias)
                            .withThingName(x509Thing)
                            .withProxyOptions(proxyOptions);
                    try (X509CredentialsProvider provider = x509builder.build()) {
                        builder.withWebsocketCredentialsProvider(provider);
                    }
                }
            }

            try(MqttClientConnection connection = builder.build()) {
                CompletableFuture<Boolean> connected = connection.connect();
                try {
                    boolean sessionPresent = connected.get();
                    System.out.println("Connected to " + (!sessionPresent ? "new" : "existing") + " session!");
                } catch (Exception ex) {
                    throw new RuntimeException("Exception occurred during connect", ex);
                }
                System.out.println("Disconnecting...");
                CompletableFuture<Void> disconnected = connection.disconnect();
                disconnected.get();
                System.out.println("Disconnected.");
            }
        } catch (CrtRuntimeException | InterruptedException | ExecutionException ex) {
            onApplicationFailure(ex);
        }

        CrtResource.waitForNoResources();

        System.out.println("Complete!");
    }
}
