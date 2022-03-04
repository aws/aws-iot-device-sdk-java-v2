/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package pubsubstress;

import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.CrtResource;
import software.amazon.awssdk.crt.http.HttpProxyOptions;
import software.amazon.awssdk.crt.Log;
import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt.MqttClientConnectionEvents;
import software.amazon.awssdk.crt.mqtt.MqttMessage;
import software.amazon.awssdk.crt.mqtt.QualityOfService;
import software.amazon.awssdk.iot.AwsIotMqttConnectionBuilder;
import software.amazon.awssdk.iot.iotjobs.model.RejectedError;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import utils.commandlineutils.CommandLineUtils;

public class PubSubStress {
    private static final int PROGRESS_OP_COUNT = 100;

    static String clientId = "test-" + UUID.randomUUID().toString();
    static String rootCaPath;
    static String certPath;
    static String keyPath;
    static String endpoint;
    static String topic = "test/topic";
    static String message = "Hello World!";
    static int    messagesToPublish = 5000;
    static boolean showHelp = false;
    static int connectionCount = 1000;
    static int eventLoopThreadCount = 1;
    static int testIterations = 1;

    static String region = "us-east-1";
    static String proxyHost;
    static int proxyPort;
    static boolean useWebsockets;

    private static Map<String, MqttClientConnection> connections = new HashMap<>();
    private static List<String> validClientIds = new ArrayList<>();
    private static List<String> validTopics = new ArrayList<>();

    static CommandLineUtils cmdUtils;

    static void onRejectedError(RejectedError error) {
        System.out.println("Request rejected: " + error.code.toString() + ": " + error.message);
    }

    static class ConnectionState {
        public ConnectionState() {}

        public String clientId;
        public String topic;
        public MqttClientConnection connection;
        public CompletableFuture<Boolean> connectFuture;
        public CompletableFuture<Integer> subscribeFuture;
    }

    static void initConnections(AwsIotMqttConnectionBuilder builder) {
        List<ConnectionState> connectionsInProgress = new ArrayList<>();

        for (int i = 0; i < connectionCount; ++i) {
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

            String newClientId = String.format("%s%d", clientId, i);

            builder.withClientId(newClientId)
                    .withConnectionEventCallbacks(callbacks);

            MqttClientConnection connection = builder.build();

            try {
                ConnectionState connectionState = new ConnectionState();
                connectionState.clientId = newClientId;
                connectionState.connectFuture = connection.connect();
                connectionState.connection = connection;

                connectionsInProgress.add(connectionState);

                if ((i + 1) % PROGRESS_OP_COUNT == 0) {
                    System.out.println(String.format("(Main Thread) Connect start count: %d", i + 1));
                }

                // Simple throttle to avoid Iot Connect/Second limit
                Thread.sleep(5);
            } catch (Exception ignored) {
                connection.disconnect();
                connection.close();
            }
        }

        System.out.println(String.format("(Main Thread) Started %d connections", connectionsInProgress.size()));

        for (int i = 0; i < connectionsInProgress.size(); ++i) {
            ConnectionState connectionState = connectionsInProgress.get(i);
            CompletableFuture<Boolean> connectFuture = connectionState.connectFuture;
            if (connectFuture == null) {
                continue;
            }

            try {
                connectFuture.get(5, TimeUnit.SECONDS);

                String clientTopic = String.format("%s%d", topic, i);
                connectionState.topic = clientTopic;
                connectionState.subscribeFuture = connectionState.connection.subscribe(clientTopic, QualityOfService.AT_LEAST_ONCE, (message) -> {
                    try {
                        String payload = new String(message.getPayload(), "UTF-8");
                    } catch (UnsupportedEncodingException ex) {
                        System.out.println(String.format("(Topic %s): Unable to decode payload: %s", clientTopic, ex.getMessage()));
                    }
                });

                if ((i + 1) % PROGRESS_OP_COUNT == 0) {
                    System.out.println(String.format("(Main Thread) Subscribe start count: %d", i + 1));
                }

                // Simple throttle to avoid Iot Subscribe/Second limit
                Thread.sleep(5);
            } catch (Exception e) {
                connectionState.connection.disconnect();
                connectionState.connection.close();
                connectionState.connection = null;
            }
        }

        System.out.println(String.format("(Main Thread) Started subscriptions for %d connections", connectionsInProgress.size()));

        for (int i = 0; i < connectionsInProgress.size(); ++i) {
            ConnectionState connectionState = connectionsInProgress.get(i);
            CompletableFuture<Integer> subscribeFuture = connectionState.subscribeFuture;
            if (subscribeFuture == null) {
                continue;
            }

            try {
                subscribeFuture.get(5, TimeUnit.SECONDS);

                connections.put(connectionState.clientId, connectionState.connection);
                validClientIds.add(connectionState.clientId);
                validTopics.add(connectionState.topic);
            } catch (Exception e) {
                connectionState.connection.disconnect();
                connectionState.connection.close();
                connectionState.connection = null;
            }
        }

        System.out.println(String.format("(Main Thread) Successfully established %d connections", connections.size()));
    }

    private static void cleanupConnections() {
        List<CompletableFuture<Void>> disconnectFutures = new ArrayList<>();

        for (MqttClientConnection connection : connections.values()) {
            try {
                disconnectFutures.add(connection.disconnect());
            } catch (Exception e) {
                System.out.println(String.format("Disconnect Exception: %s", e.getMessage()));
            }
        }

        for (CompletableFuture<Void> future : disconnectFutures) {
            try {
                future.get(60, TimeUnit.SECONDS);
            } catch (Exception e) {
                System.out.println(String.format("Disconnect Future Exception: %s", e.getMessage()));
            }
        }

        for (MqttClientConnection connection : connections.values()) {
            try {
                connection.close();
            } catch (Exception e) {
                System.out.println(String.format("Close Exception: %s", e.getMessage()));
            }
        }
    }

    public static void main(String[] args) {

        cmdUtils = new CommandLineUtils();
        cmdUtils.registerProgramName("PubSubStress");
        cmdUtils.addCommonMQTTCommands();
        cmdUtils.registerCommand("client_id", "<int>", "Client id to use (optional, default='test-*')");
        cmdUtils.registerCommand("topic", "<str>", "Topic to subscribe/publish to (optional, default='test/topic').");
        cmdUtils.registerCommand("message", "<str>", "Message to publish (optional, default='Hello World').");
        cmdUtils.registerCommand("count", "<int>", "Number of messages to publish (optional, default='10').");
        cmdUtils.registerCommand("connections", "<int>", "Number of connections to make (optional, default='1000').");
        cmdUtils.registerCommand("threads", "<int>", "Number of IO threads to use (optional, default='1').");
        cmdUtils.registerCommand("iterations", "<int>", "Number of times to repeat the basic stress test logic (optional, default='1').");
        cmdUtils.registerCommand("use_websockets", "", "Use websockets (optional).");
        cmdUtils.registerCommand("proxy_host", "<str>", "Websocket proxy host to use (optional, required for websockets).");
        cmdUtils.registerCommand("proxy_port", "<int>", "Websocket proxy port to use (optional, required for websockets).");
        cmdUtils.registerCommand("region", "<str>", "Websocket signing region to use (optional, default='us-east-1').");
        cmdUtils.registerCommand("help", "", "Prints this message");
        cmdUtils.sendArguments(args);
        cmdUtils.startLogging();

        if (cmdUtils.hasCommand("help")) {
            cmdUtils.printHelp();
            System.exit(1);
        }

        endpoint = cmdUtils.getCommandRequired("endpoint", "");
        rootCaPath = cmdUtils.getCommandOrDefault("root_ca", rootCaPath);
        certPath = cmdUtils.getCommandOrDefault("cert", certPath);
        keyPath = cmdUtils.getCommandOrDefault("key", keyPath);
        clientId = cmdUtils.getCommandOrDefault("client_id", clientId);
        topic = cmdUtils.getCommandOrDefault("topic", topic);
        message = cmdUtils.getCommandOrDefault("message", message);
        messagesToPublish = Integer.parseInt(cmdUtils.getCommandOrDefault("count", String.valueOf(messagesToPublish)));
        connectionCount = Integer.parseInt(cmdUtils.getCommandOrDefault("connections", String.valueOf(connectionCount)));
        eventLoopThreadCount = Integer.parseInt(cmdUtils.getCommandOrDefault("threads", String.valueOf(eventLoopThreadCount)));
        testIterations = Integer.parseInt(cmdUtils.getCommandOrDefault("iterations", String.valueOf(testIterations)));
        useWebsockets = cmdUtils.hasCommand("use_websockets");
        proxyHost = cmdUtils.getCommandOrDefault("proxy_host", proxyHost);
        proxyPort = Integer.parseInt(cmdUtils.getCommandOrDefault("proxy_port", String.valueOf(proxyPort)));
        region = cmdUtils.getCommandOrDefault("region", region);

        if (!useWebsockets) {
            if (certPath == null || keyPath == null) {
                cmdUtils.printHelp();
                System.out.println("--cert and --key required if not using websockets.");
                System.exit(-1);
            }
        }

        int iteration = 0;
        while(iteration < testIterations) {
            System.out.println(String.format("Starting iteration %d", iteration));

            try (
                AwsIotMqttConnectionBuilder builder = AwsIotMqttConnectionBuilder.newMtlsBuilderFromPath(certPath, keyPath)) {

                builder.withCertificateAuthorityFromPath(null, rootCaPath)
                    .withEndpoint(endpoint)
                    .withCleanSession(true)
                    .withProtocolOperationTimeoutMs(10000);

                if (proxyHost != null && proxyPort > 0) {
                    HttpProxyOptions proxyOptions = new HttpProxyOptions();
                    proxyOptions.setHost(proxyHost);
                    proxyOptions.setPort(proxyPort);

                    builder.withHttpProxyOptions(proxyOptions);
                }

                if (useWebsockets) {
                    builder.withWebsockets(true);
                    builder.withWebsocketSigningRegion(region);
                }

                try {
                    initConnections(builder);

                    Log.log(Log.LogLevel.Info, Log.LogSubject.MqttGeneral, "START OF PUBLISH......");

                    Random rng = new Random(0);

                    List<CompletableFuture<Integer>> publishFutures = new ArrayList<>();

                    for (int count = 0; count < messagesToPublish; ++count) {
                        String messageContent = String.format("%s #%d", message, count + 1);

                        // Pick a random connection to publish from
                        String connectionId = validClientIds.get(Math.abs(rng.nextInt()) % validClientIds.size());
                        MqttClientConnection connection = connections.get(connectionId);

                        // Pick a random subscribed topic to publish to
                        String publishTopic = validTopics.get(Math.abs(rng.nextInt()) % validTopics.size());

                        try {
                            publishFutures.add(connection.publish(new MqttMessage(publishTopic, messageContent.getBytes(), QualityOfService.AT_LEAST_ONCE, false)));
                        } catch (Exception e) {
                            System.out.println(String.format("Publishing Exception: %s", e.getMessage()));
                        }

                        if (count % PROGRESS_OP_COUNT == 0) {
                            System.out.println(String.format("(Main Thread) Message publish count: %d", count));
                        }
                    }

                    for (CompletableFuture<Integer> publishFuture : publishFutures) {
                        publishFuture.get();
                    }

                    System.out.println("zzzzz");

                    Thread.sleep(1000);
                } catch (Exception e) {
                    System.out.println(String.format("Exception: %s", e.getMessage()));
                } finally {
                    cleanupConnections();
                }
            } catch (Exception e) {
                System.out.println("Exception encountered: " + e.toString());
            }

            System.out.println("Complete! Waiting on managed cleanup");
            CrtResource.waitForNoResources();
            System.out.println("Managed cleanup complete");

            /* Not particularly effective, but psychologically reassuring to do before checking memory */
            for (int i = 0; i < 10; ++i) {
                System.gc();
            }
            long nativeMemoryInUse = CRT.nativeMemory();
            System.out.println(String.format("Native memory: %d", nativeMemoryInUse));
            long javaMemoryInUse = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
            System.out.println(String.format("Java memory: %d", javaMemoryInUse));

            iteration++;

            validClientIds.clear();
            validTopics.clear();
        }
    }
}
