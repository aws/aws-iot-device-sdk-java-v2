/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package x509credentialsproviderconnect;

import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.CrtResource;
import software.amazon.awssdk.crt.CrtRuntimeException;
import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt.MqttClientConnectionEvents;
import software.amazon.awssdk.crt.http.HttpProxyOptions;
import software.amazon.awssdk.crt.io.TlsContextOptions;
import software.amazon.awssdk.crt.io.ClientTlsContext;
import software.amazon.awssdk.crt.auth.credentials.X509CredentialsProvider;
import software.amazon.awssdk.iot.AwsIotMqttConnectionBuilder;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.CompletableFuture;
import java.util.UUID;

import utils.commandlineutils.CommandLineUtils;

public class X509CredentialsProviderConnect {
    // When run normally, we want to exit nicely even if something goes wrong
    // When run from CI, we want to let an exception escape which in turn causes the
    // exec:java task to return a non-zero exit code
    static String ciPropValue = System.getProperty("aws.crt.ci");
    static boolean isCI = ciPropValue != null && Boolean.valueOf(ciPropValue);

    static CommandLineUtils cmdUtils;

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

        /**
         * Register the command line inputs
         */
        cmdUtils = new CommandLineUtils();
        cmdUtils.registerProgramName("x509CredentialsProviderConnect");
        cmdUtils.addCommonMQTTCommands();
        cmdUtils.addCommonProxyCommands();
        cmdUtils.addCommonX509Commands();
        cmdUtils.registerCommand("signing_region", "<str>", "AWS IoT service region.");
        cmdUtils.registerCommand("client_id", "<int>", "Client id to use (optional, default='test-*').");
        cmdUtils.registerCommand("port", "<int>", "Port to connect to on the endpoint (optional, default='8883').");
        cmdUtils.sendArguments(args);

        /**
         * Gather the input from the command line
         */
        String input_endpoint = cmdUtils.getCommandRequired("endpoint", "");
        String input_ca = cmdUtils.getCommandOrDefault("ca", "");
        String input_signingRegion = cmdUtils.getCommandRequired("signing_region", "");
        String input_client_id = cmdUtils.getCommandOrDefault("client_id", "test-" + UUID.randomUUID().toString());
        int input_port = Integer.parseInt(cmdUtils.getCommandOrDefault("port", "443"));
        String input_proxyHost = cmdUtils.getCommandOrDefault("proxy_host", "");
        int input_proxyPort = Integer.parseInt(cmdUtils.getCommandOrDefault("proxy_port", "0"));
        String input_x509Endpoint = cmdUtils.getCommandRequired("x509_endpoint", "");
        String input_x509Role = cmdUtils.getCommandRequired("x509_role_alias", "");
        String input_x509ThingName = cmdUtils.getCommandRequired("x509_thing_name", "");
        String input_x509Cert = cmdUtils.getCommandRequired("x509_cert", "");
        String input_x509Key = cmdUtils.getCommandRequired("x509_key", "");
        String input_x509Ca = cmdUtils.getCommandOrDefault("x509_ca_file", null);

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

        try {

            /**
             * Build the MQTT connection using the builder
             */
            // ==============================
            AwsIotMqttConnectionBuilder builder = AwsIotMqttConnectionBuilder.newMtlsBuilderFromPath(null, null);
            if (input_ca != "") {
                builder.withCertificateAuthorityFromPath(null, input_ca);
            }
            builder.withConnectionEventCallbacks(callbacks)
                .withClientId(input_client_id)
                .withEndpoint(input_endpoint)
                .withPort((short)input_port)
                .withCleanSession(true)
                .withProtocolOperationTimeoutMs(60000);
            HttpProxyOptions proxyOptions = null;
            if (input_proxyHost != "" && input_proxyPort > 0) {
                proxyOptions = new HttpProxyOptions();
                proxyOptions.setHost(input_proxyHost);
                proxyOptions.setPort(input_proxyPort);
                builder.withHttpProxyOptions(proxyOptions);
            }
            builder.withWebsockets(true);
            builder.withWebsocketSigningRegion(input_signingRegion);

            TlsContextOptions x509TlsOptions = TlsContextOptions.createWithMtlsFromPath(input_x509Cert, input_x509Key);
            if (input_x509Ca != null) {
                x509TlsOptions.withCertificateAuthorityFromPath(null, input_x509Ca);
            }

            ClientTlsContext x509TlsContext = new ClientTlsContext(x509TlsOptions);
            X509CredentialsProvider.X509CredentialsProviderBuilder x509builder = new X509CredentialsProvider.X509CredentialsProviderBuilder()
                .withTlsContext(x509TlsContext)
                .withEndpoint(input_x509Endpoint)
                .withRoleAlias(input_x509Role)
                .withThingName(input_x509ThingName)
                .withProxyOptions(proxyOptions);
            X509CredentialsProvider provider = x509builder.build();
            builder.withWebsocketCredentialsProvider(provider);

            MqttClientConnection connection = builder.build();
            builder.close();
            provider.close();

            if (connection == null)
            {
                onApplicationFailure(new RuntimeException("MQTT connection creation failed!"));
            }
            // ==============================

            /**
             * Connect and disconnect
             */
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

            /**
             * Close the connection now that it is complete
             */
            connection.close();

        } catch (CrtRuntimeException | InterruptedException | ExecutionException ex) {
            onApplicationFailure(ex);
        }

        CrtResource.waitForNoResources();

        System.out.println("Complete!");
    }
}
