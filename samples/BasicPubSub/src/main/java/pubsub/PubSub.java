/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package pubsub;

import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.CrtResource;
import software.amazon.awssdk.crt.CrtRuntimeException;
import software.amazon.awssdk.crt.Log;
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

public class PubSub {

    // When run normally, we want to exit nicely even if something goes wrong
    // When run from CI, we want to let an exception escape which in turn causes the
    // exec:java task to return a non-zero exit code
    static String ciPropValue = System.getProperty("aws.crt.ci");
    static boolean isCI = ciPropValue != null && Boolean.valueOf(ciPropValue);

    static String clientId = "test-" + UUID.randomUUID().toString();
    static String rootCaPath;
    static String certPath;
    static String keyPath;
    static String endpoint;
    static String topic = "test/topic";
    static String message = "Hello World!";
    static int    messagesToPublish = 10;
    static int port = 8883;

    static String proxyHost;
    static int proxyPort;
    static String region = "us-east-1";
    static boolean useWebsockets = false;
    static boolean useX509Credentials = false;
    static String x509RoleAlias;
    static String x509Endpoint;
    static String x509Thing;
    static String x509CertPath;
    static String x509KeyPath;
    static String x509RootCaPath;

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
            throw new RuntimeException("BasicPubSub execution failure", cause);
        } else if (cause != null) {
            System.out.println("Exception encountered: " + cause.toString());
        }
    }

    public static void main(String[] args) {

        cmdUtils = new CommandLineUtils();
        cmdUtils.registerProgramName("PubSub");
        cmdUtils.addCommonMQTTCommands();
        cmdUtils.registerCommand("client_id", "<int>", "Client id to use (optional, default='test-*').");
        cmdUtils.registerCommand("port", "<int>", "Port to connect to on the endpoint (optional, default='8883').");
        cmdUtils.registerCommand("topic", "<str>", "Topic to subscribe/publish to (optional, default='test/topic').");
        cmdUtils.registerCommand("message", "<str>", "Message to publish (optional, default='Hello World').");
        cmdUtils.registerCommand("count", "<int>", "Number of messages to publish (optional, default='10').");
        cmdUtils.registerCommand("use_websocket", "", "Use websockets (optional).");
        cmdUtils.registerCommand("x509", "", "Use the x509 credentials provider while using websockets (optional).");
        cmdUtils.registerCommand("x509_role_alias", "<str>", "Role alias to use with the x509 credentials provider (required for x509).");
        cmdUtils.registerCommand("x509_endpoint", "<str>", "Endpoint to fetch x509 credentials from (required for x509).");
        cmdUtils.registerCommand("x509_thing", "<str>", "Thing name to fetch x509 credentials on behalf of (required for x509).");
        cmdUtils.registerCommand("x509_cert", "<path>", "Path to the IoT thing certificate used in fetching x509 credentials (required for x509).");
        cmdUtils.registerCommand("x509_key", "<path>", "Path to the IoT thing private key used in fetching x509 credentials (required for x509).");
        cmdUtils.registerCommand("x509_ca_file", "<path>", "Path to the root certificate used in fetching x509 credentials (required for x509).");
        cmdUtils.registerCommand("proxy_host", "<str>", "Websocket proxy host to use (optional, required if --proxy_port is set).");
        cmdUtils.registerCommand("proxy_port", "<int>", "Websocket proxy port to use (optional, required if --proxy_host is set).");
        cmdUtils.registerCommand("region", "<str>", "AWS IoT service region (optional, default='us-east-1').");

        cmdUtils.registerCommand("help", "", "Prints this message");
        cmdUtils.sendArguments(args);
        Log.initLoggingFromSystemProperties();

        if (cmdUtils.hasCommand("help")) {
            cmdUtils.printHelp();
            System.exit(1);
        }

        endpoint = cmdUtils.getCommandRequired("endpoint", "");
        clientId = cmdUtils.getCommandOrDefault("client_id", clientId);
        port = Integer.parseInt(cmdUtils.getCommandOrDefault("port", String.valueOf(port)));
        rootCaPath = cmdUtils.getCommandOrDefault("root_ca", rootCaPath);
        certPath = cmdUtils.getCommandOrDefault("cert", certPath);
        keyPath = cmdUtils.getCommandOrDefault("key", keyPath);
        topic = cmdUtils.getCommandOrDefault("topic", topic);
        message = cmdUtils.getCommandOrDefault("message", message);
        messagesToPublish = Integer.parseInt(cmdUtils.getCommandOrDefault("count", String.valueOf(messagesToPublish)));
        useWebsockets = cmdUtils.hasCommand("use_websocket");
        useX509Credentials = cmdUtils.hasCommand("x509");
        if (useX509Credentials) {
            useWebsockets = true;
        }
        x509RoleAlias = cmdUtils.getCommandOrDefault("x509_role_alias", x509RoleAlias);
        x509Endpoint = cmdUtils.getCommandOrDefault("x509_endpoint", x509Endpoint);
        x509Thing = cmdUtils.getCommandOrDefault("x509_thing", x509Thing);
        x509CertPath = cmdUtils.getCommandOrDefault("x509_cert", x509CertPath);
        x509KeyPath = cmdUtils.getCommandOrDefault("x509_key", x509KeyPath);
        x509RootCaPath = cmdUtils.getCommandOrDefault("x509_ca_file", x509RootCaPath);
        proxyHost = cmdUtils.getCommandOrDefault("proxy_host", proxyHost);
        proxyPort = Integer.parseInt(cmdUtils.getCommandOrDefault("proxy_port", String.valueOf(proxyPort)));
        region = cmdUtils.getCommandOrDefault("region", region);

        if (useWebsockets == false) {
            if (certPath == null || keyPath == null) {
                cmdUtils.printHelp();
                System.out.println("--cert and --key required if not using --use_websocket.");
                onApplicationFailure(null);
                return;
            }
        } else if (useX509Credentials) {
            if (x509RoleAlias == null || x509Endpoint == null || x509Thing == null || x509CertPath == null || x509KeyPath == null) {
                cmdUtils.printHelp();
                System.out.println("--x509_role_alias, --x509_endpoint, --x509_thing, --x509_cert, and --x509_key required if using x509.");
                onApplicationFailure(null);
                return;
            }
        }

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
            AwsIotMqttConnectionBuilder builder = AwsIotMqttConnectionBuilder.newMtlsBuilderFromPath(certPath, keyPath)) {

            if (rootCaPath != null) {
                builder.withCertificateAuthorityFromPath(null, rootCaPath);
            }
            
            builder.withConnectionEventCallbacks(callbacks)
                    .withClientId(clientId)
                    .withEndpoint(endpoint)
                    .withPort((short)port)
                    .withCleanSession(true)
                    .withProtocolOperationTimeoutMs(60000);

            HttpProxyOptions proxyOptions = null;
            if (proxyHost != null && proxyPort > 0) {
                proxyOptions = new HttpProxyOptions();
                proxyOptions.setHost(proxyHost);
                proxyOptions.setPort(proxyPort);

                builder.withHttpProxyOptions(proxyOptions);
            }

            if (useWebsockets) {
                builder.withWebsockets(true);
                builder.withWebsocketSigningRegion(region);

                if (useX509Credentials) {
                    try (TlsContextOptions x509TlsOptions = TlsContextOptions.createWithMtlsFromPath(x509CertPath, x509KeyPath)) {
                        if (x509RootCaPath != null) {
                            x509TlsOptions.withCertificateAuthorityFromPath(null, x509RootCaPath);
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

                CountDownLatch countDownLatch = new CountDownLatch(messagesToPublish);

                CompletableFuture<Integer> subscribed = connection.subscribe(topic, QualityOfService.AT_LEAST_ONCE, (message) -> {
                    String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                    System.out.println("MESSAGE: " + payload);
                    countDownLatch.countDown();
                });

                subscribed.get();

                int count = 0;
                while (count++ < messagesToPublish) {
                    CompletableFuture<Integer> published = connection.publish(new MqttMessage(topic, message.getBytes(), QualityOfService.AT_LEAST_ONCE, false));
                    published.get();
                    Thread.sleep(1000);
                }
                
                countDownLatch.await();

                CompletableFuture<Void> disconnected = connection.disconnect();
                disconnected.get();
            }
        } catch (CrtRuntimeException | InterruptedException | ExecutionException ex) {
            onApplicationFailure(ex);
        }

        CrtResource.waitForNoResources();

        System.out.println("Complete!");
    }
}
