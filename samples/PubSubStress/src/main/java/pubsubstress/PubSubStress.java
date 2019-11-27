/* Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package pubsubstress;

import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.CrtResource;
import software.amazon.awssdk.crt.io.ClientBootstrap;
import software.amazon.awssdk.crt.io.TlsContext;
import software.amazon.awssdk.crt.io.TlsContextOptions;
import software.amazon.awssdk.crt.Log;
import software.amazon.awssdk.crt.mqtt.MqttClient;
import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt.MqttClientConnectionEvents;
import software.amazon.awssdk.crt.mqtt.MqttMessage;
import software.amazon.awssdk.crt.mqtt.QualityOfService;
import software.amazon.awssdk.iot.iotjobs.model.RejectedError;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

class PubSubStress {
    private static final int PROGRESS_OP_COUNT = 100;

    static String clientId = "samples-client-id";
    static String rootCaPath;
    static String certPath;
    static String keyPath;
    static String endpoint;
    static String topic = "/samples/test";
    static String message = "Hello World!";
    static int    messagesToPublish = 5000;
    static boolean showHelp = false;
    static int port = 8883;
    static int connectionCount = 1000;
    static int eventLoopThreadCount = 1;
    static int testDurationInSeconds = 1;

    private static Map<String, MqttClientConnection> connections = new HashMap<>();
    private static List<Integer> validIndices = new ArrayList<>();

    static void printUsage() {
        System.out.println(
                "Usage:\n"+
                "  --help        This message\n"+
                "  --clientId    Client ID to use when connecting (optional)\n"+
                "  -e|--endpoint AWS IoT service endpoint hostname\n"+
                "  -p|--port     Port to connect to on the endpoint\n"+
                "  -r|--rootca   Path to the root certificate\n"+
                "  -c|--cert     Path to the IoT thing certificate\n"+
                "  -k|--key      Path to the IoT thing public key\n"+
                "  -t|--topic    Topic to subscribe/publish to (optional)\n"+
                "  -m|--message  Message to publish (optional)\n"+
                "  -n|--count    Number of messages to publish (optional)\n"+
                "  --connections Number of connections to make (optional)\n"+
                "  --threads     Number of IO threads to use (optional)\n" +
                "  -d|--duration Time in seconds to repeat the stress test (optional)\n"
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
                case "-p":
                case "--port":
                    if (idx + 1 < args.length) {
                        port = Integer.parseInt(args[++idx]);
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
                case "-d":
                case "--duration":
                    if (idx + 1 < args.length) {
                        testDurationInSeconds = Integer.parseInt(args[++idx]);
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
        public MqttClientConnection connection;
        public CompletableFuture<Boolean> connectFuture;
        public CompletableFuture<Integer> subscribeFuture;
    }

    static void initConnections(MqttClient client) {
        List<ConnectionState> connectionsInProgress = new ArrayList<>();

        for (int i = 0; i < connectionCount; ++i) {
            MqttClientConnection connection = new MqttClientConnection(client, new MqttClientConnectionEvents() {
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
            });

            try {
                ConnectionState connectionState = new ConnectionState();
                connectionState.clientId = String.format("%s%d", clientId, i);
                connectionState.connection = connection;
                connectionState.connectFuture = connection.connect(
                        connectionState.clientId,
                        endpoint, port,
                        null, true, 0, 0);

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
                if (connectFuture.isCancelled() || connectFuture.isCompletedExceptionally()) {
                    connectionState.connection.disconnect();
                    connectionState.connection.close();
                    continue;
                }

                String clientTopic = String.format("%s%d", topic, i);
                connectionState.subscribeFuture = connectionState.connection.subscribe(clientTopic, QualityOfService.AT_LEAST_ONCE, (message) -> {
                    try {
                        String payload = new String(message.getPayload(), "UTF-8");
                        //System.out.println(String.format("(Topic %s): MESSAGE: %s", clientTopic, payload));
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
                if (subscribeFuture.isCancelled() || subscribeFuture.isCompletedExceptionally()) {
                    connectionState.connection.disconnect();
                    connectionState.connection.close();
                    continue;
                }

                connections.put(connectionState.clientId, connectionState.connection);
                validIndices.add(i);
            } catch (Exception e) {
                connectionState.connection.disconnect();
                connectionState.connection.close();
            }
        }

        System.out.println(String.format("(Main Thread) Successfully established %d connections", connections.size()));
    }

    private static void cleanupConnections() {
        List<CompletableFuture<Void>> disconnectFutures = new ArrayList<>();

        for (MqttClientConnection connection : connections.values()) {
            disconnectFutures.add(connection.disconnect());
        }

        for (CompletableFuture<Void> future : disconnectFutures) {
            try {
                future.get(60, TimeUnit.SECONDS);
            } catch (Exception e) {
                ;
            }
        }

        for (MqttClientConnection connection : connections.values()) {
            connection.close();
        }
    }

    public static void main(String[] args) {
        parseCommandLine(args);
        if (showHelp || endpoint == null || rootCaPath == null || certPath == null || keyPath == null) {
            printUsage();
            return;
        }

        Instant startTime = Instant.now();
        int iteration = 0;
        while(Instant.now().isBefore(startTime.plusSeconds(testDurationInSeconds))) {
            System.out.println(String.format("Starting iteration %d", iteration));

            try (ClientBootstrap clientBootstrap = new ClientBootstrap(eventLoopThreadCount);
                 TlsContextOptions tlsContextOptions = TlsContextOptions.createWithMtlsFromPath(certPath, keyPath)) {
                tlsContextOptions.overrideDefaultTrustStoreFromPath(null, rootCaPath);

                try (TlsContext tlsContext = new TlsContext(tlsContextOptions);
                     MqttClient client = new MqttClient(clientBootstrap, tlsContext)) {

                    try {
                        initConnections(client);

                        Log.log(Log.LogLevel.Info, Log.LogSubject.MqttGeneral, "START OF PUBLISH......");

                        Random rng = new Random(0);

                        List<CompletableFuture<Integer>> publishFutures = new ArrayList<>();

                        for (int count = 0; count < messagesToPublish; ++count) {
                            String messageContent = String.format("%s #%d", message, count + 1);

                            // Pick a random connection to publish from
                            int connectionIndex = validIndices.get(Math.abs(rng.nextInt()) % validIndices.size());
                            String connectionId = String.format("%s%d", clientId, connectionIndex);
                            MqttClientConnection connection = connections.get(connectionId);

                            // Pick a random subscribed topic to publish to
                            int topicIndex = validIndices.get(Math.abs(rng.nextInt()) % validIndices.size());
                            String publishTopic = String.format("%s%d", topic, topicIndex);

                            publishFutures.add(connection.publish(new MqttMessage(publishTopic, messageContent.getBytes()), QualityOfService.AT_LEAST_ONCE, false));

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
                }
            } catch (Exception e) {
                System.out.println("Exception encountered: " + e.toString());
            }

            System.out.println("Complete! Waiting on managed cleanup");
            CrtResource.waitForNoResources();
            System.out.println("Managed cleanup complete");
            iteration++;
        }
    }
}
