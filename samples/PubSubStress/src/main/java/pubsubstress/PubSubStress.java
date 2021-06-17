/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package pubsubstress;

import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.CrtResource;
import software.amazon.awssdk.crt.http.HttpProxyOptions;
import software.amazon.awssdk.crt.io.ClientBootstrap;
import software.amazon.awssdk.crt.io.EventLoopGroup;
import software.amazon.awssdk.crt.io.HostResolver;
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

    static void printUsage() {
        System.out.println(
                "Usage:\n"+
                "  --help        This message\n"+
                "  --clientId    Client ID to use when connecting (optional)\n"+
                "  -e|--endpoint AWS IoT service endpoint hostname\n"+
                "  -r|--rootca   Path to the root certificate\n"+
                "  -c|--cert     Path to the IoT thing certificate\n"+
                "  -k|--key      Path to the IoT thing private key\n"+
                "  -t|--topic    Topic to subscribe/publish to (optional)\n"+
                "  -m|--message  Message to publish (optional)\n"+
                "  -n|--count    Number of messages to publish (optional)\n"+
                "  --connections Number of connections to make (optional)\n"+
                "  --threads     Number of IO threads to use (optional)\n" +
                "  -i|--iterations Number of times to repeat the basic stress test logic (optional)\n" +
                "  -w|--websockets Use websockets\n" +
                "  --proxyhost   Websocket proxy host to use\n" +
                "  --proxyport   Websocket proxy port to use\n" +
                "  --region      Websocket signing region to use\n"
        );
    }

    static void parseCommandLine(String[] args) {
        for (int idx = 0; idx < args.length; ++idx) {
            switch (args[idx]) {
                case "--help":
                    showHelp = true;
                    break;
                case "--clientId":
                    if (idx + 1 < args.length) {
                        clientId = args[++idx];
                    }
                    break;
                case "-e":
                case "--endpoint":
                    if (idx + 1 < args.length) {
                        endpoint = args[++idx];
                    }
                    break;
                case "-r":
                case "--rootca":
                    if (idx + 1 < args.length) {
                        rootCaPath = args[++idx];
                    }
                    break;
                case "-c":
                case "--cert":
                    if (idx + 1 < args.length) {
                        certPath = args[++idx];
                    }
                    break;
                case "-k":
                case "--key":
                    if (idx + 1 < args.length) {
                        keyPath = args[++idx];
                    }
                    break;
                case "-t":
                case "--topic":
                    if (idx + 1 < args.length) {
                        topic = args[++idx];
                    }
                    break;
                case "-m":
                case "--message":
                    if (idx + 1 < args.length) {
                        message = args[++idx];
                    }
                    break;
                case "-n":
                case "--count":
                    if (idx + 1 < args.length) {
                        messagesToPublish = Integer.parseInt(args[++idx]);
                    }
                    break;
                case "--connections":
                    if (idx + 1 < args.length) {
                        connectionCount = Integer.parseInt(args[++idx]);
                    }
                    break;
                case "--threads":
                    if (idx + 1 < args.length) {
                        eventLoopThreadCount = Integer.parseInt(args[++idx]);
                    }
                    break;
                case "-i":
                case "--iterations":
                    if (idx + 1 < args.length) {
                        testIterations = Integer.parseInt(args[++idx]);
                    }
                    break;
                case "-w":
                    useWebsockets = true;
                    break;
                case "--proxyhost":
                    if (idx + 1 < args.length) {
                        proxyHost = args[++idx];
                    }
                    break;
                case "--proxyport":
                    if (idx + 1 < args.length) {
                        proxyPort = Integer.parseInt(args[++idx]);
                    }
                    break;
                case "--region":
                    if (idx + 1 < args.length) {
                        region = args[++idx];
                    }
                    break;
                default:
                    System.out.println("Unrecognized argument: " + args[idx]);
            }
        }
    }

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
        parseCommandLine(args);
        if (showHelp || endpoint == null) {
            printUsage();
            return;
        }

        if (!useWebsockets) {
            if (certPath == null || keyPath == null) {
                printUsage();
                return;
            }
        }

        int iteration = 0;
        while(iteration < testIterations) {
            System.out.println(String.format("Starting iteration %d", iteration));

            try (EventLoopGroup eventLoopGroup = new EventLoopGroup(eventLoopThreadCount);
                HostResolver resolver = new HostResolver(eventLoopGroup);
                ClientBootstrap clientBootstrap = new ClientBootstrap(eventLoopGroup, resolver);
                AwsIotMqttConnectionBuilder builder = AwsIotMqttConnectionBuilder.newMtlsBuilderFromPath(certPath, keyPath)) {

                builder.withCertificateAuthorityFromPath(null, rootCaPath)
                    .withEndpoint(endpoint)
                    .withCleanSession(true)
                    .withBootstrap(clientBootstrap)
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
