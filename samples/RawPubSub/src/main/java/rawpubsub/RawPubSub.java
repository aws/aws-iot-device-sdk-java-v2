/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package rawpubsub;

import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.CrtResource;
import software.amazon.awssdk.crt.CrtRuntimeException;
import software.amazon.awssdk.crt.io.ClientBootstrap;
import software.amazon.awssdk.crt.io.TlsContext;
import software.amazon.awssdk.crt.io.TlsContextOptions;
import software.amazon.awssdk.crt.mqtt.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import utils.commandlineutils.CommandLineUtils;

public class RawPubSub {
    static String clientId = "test-" + UUID.randomUUID().toString();
    static String rootCaPath;
    static String certPath;
    static String keyPath;
    static String endpoint;
    static String topic = "test/topic";
    static String message = "Hello World!";
    static int    messagesToPublish = 10;
    static boolean showHelp = false;

    static String userName;
    static String password;
    static String protocolName = "x-amzn-mqtt-ca";
    static List<String> authParams;

    static CommandLineUtils cmdUtils;

    public static void main(String[] args) {

        cmdUtils = new CommandLineUtils();
        cmdUtils.registerProgramName("RawPubSub");
        cmdUtils.addCommonMQTTCommands();
        cmdUtils.registerCommand("topic", "<str>", "Topic to subscribe/publish to (optional, default='test/topic').");
        cmdUtils.registerCommand("message", "<str>", "Message to publish (optional, default='Hello World').");
        cmdUtils.registerCommand("count", "<int>", "Number of messages to publish (optional, default='10').");
        cmdUtils.registerCommand("username", "<str>", "Username to use as part of the connection/authentication process.");
        cmdUtils.registerCommand("password", "<str>", "Password to use as part of the connection/authentication process.");
        cmdUtils.registerCommand("protocol", "<str>", "ALPN protocol to use (optional, default='x-amzn-mqtt-ca').");
        cmdUtils.registerCommand("auth_params", "<comma delimited list>",
                "Comma delimited list of auth parameters. For websockets these will be set as headers. " +
                "For raw mqtt these will be appended to user_name. (optional)");
        cmdUtils.registerCommand("client_id", "<int>", "Client id to use (optional, default='test-*')");
        cmdUtils.registerCommand("help", "", "Prints this message");
        cmdUtils.sendArguments(args);
        cmdUtils.startLogging();

        if (cmdUtils.hasCommand("help")) {
            cmdUtils.printHelp();
            System.exit(1);
        }

        clientId = cmdUtils.getCommandOrDefault("client_id", clientId);
        endpoint = cmdUtils.getCommandRequired("endpoint", "");
        rootCaPath = cmdUtils.getCommandOrDefault("root_ca", rootCaPath);
        certPath = cmdUtils.getCommandRequired("cert", "");
        keyPath = cmdUtils.getCommandRequired("key", "");
        topic = cmdUtils.getCommandOrDefault("topic", topic);
        message = cmdUtils.getCommandOrDefault("message", message);
        messagesToPublish = Integer.parseInt(cmdUtils.getCommandOrDefault("count", String.valueOf(messagesToPublish)));
        userName = cmdUtils.getCommandRequired("username", "");
        password = cmdUtils.getCommandRequired("password", "");
        protocolName = cmdUtils.getCommandOrDefault("protocol", protocolName);
        if (cmdUtils.hasCommand("auth_params")) {
            authParams = Arrays.asList(cmdUtils.getCommand("auth_params").split("\\s*,\\s*"));
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

        if (authParams != null && authParams.size() > 0) {
            if (userName.length() > 0) {
                StringBuilder usernameBuilder = new StringBuilder();

                usernameBuilder.append(userName);
                usernameBuilder.append("?");
                for (int i = 0; i < authParams.size(); ++i) {
                    usernameBuilder.append(authParams.get(i));
                    if (i + 1 < authParams.size()) {
                        usernameBuilder.append("&");
                    }
                }

                userName = usernameBuilder.toString();
            }
        }

        try(
            TlsContextOptions tlsContextOptions = TlsContextOptions.createWithMtlsFromPath(certPath, keyPath)) {

            if (rootCaPath != null) {
                tlsContextOptions.overrideDefaultTrustStoreFromPath(null, rootCaPath);
            }

            int port = 8883;
            if (TlsContextOptions.isAlpnSupported()) {
                port = 443;
                tlsContextOptions.withAlpnList(protocolName);
            }

            try(TlsContext tlsContext = new TlsContext(tlsContextOptions);
                MqttClient client = new MqttClient(tlsContext);
                MqttConnectionConfig config = new MqttConnectionConfig()) {

                config.setMqttClient(client);
                config.setClientId(clientId);
                config.setConnectionCallbacks(callbacks);
                config.setCleanSession(true);
                config.setEndpoint(endpoint);
                config.setPort(port);

                if (userName != null && userName.length() > 0) {
                    config.setLogin(userName, password);
                }

                try (MqttClientConnection connection = new MqttClientConnection(config)) {

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
            }
        } catch (CrtRuntimeException | InterruptedException | ExecutionException ex) {
            System.out.println("Exception encountered: " + ex.toString());
        }

        CrtResource.waitForNoResources();
        System.out.println("Complete!");
    }
}
