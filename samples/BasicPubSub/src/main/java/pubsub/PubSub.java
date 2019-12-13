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

package pubsub;

import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.CrtRuntimeException;
import software.amazon.awssdk.crt.http.HttpProxyOptions;
import software.amazon.awssdk.crt.io.ClientBootstrap;
import software.amazon.awssdk.crt.io.EventLoopGroup;
import software.amazon.awssdk.crt.io.HostResolver;
import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt.MqttClientConnectionEvents;
import software.amazon.awssdk.crt.mqtt.MqttMessage;
import software.amazon.awssdk.crt.mqtt.QualityOfService;
import software.amazon.awssdk.iot.AwsIotMqttConnectionBuilder;
import software.amazon.awssdk.iot.iotjobs.model.RejectedError;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

class PubSub {
    static String clientId = "samples-client-id";
    static String rootCaPath;
    static String certPath;
    static String keyPath;
    static String endpoint;
    static String topic = "/samples/test";
    static String message = "Hello World!";
    static int    messagesToPublish = 10;
    static boolean showHelp = false;
    static int port = 8883;

    static String proxyHost;
    static int proxyPort;
    static String region = "us-east-1";
    static boolean useWebsockets = false;

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
                "  -n|--count    Number of messages to publish (optional)\n" +
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

        try(EventLoopGroup eventLoopGroup = new EventLoopGroup(1);
            HostResolver resolver = new HostResolver(eventLoopGroup);
            ClientBootstrap clientBootstrap = new ClientBootstrap(eventLoopGroup, resolver);
            AwsIotMqttConnectionBuilder builder = AwsIotMqttConnectionBuilder.newMtlsBuilderFromPath(certPath, keyPath)) {

            if (rootCaPath != null) {
                builder.withCertificateAuthorityFromPath(null, rootCaPath);
            }

            builder.withBootstrap(clientBootstrap)
                .withConnectionEventCallbacks(callbacks)
                .withClientId(clientId)
                .withEndpoint(endpoint)
                .withCleanSession(true);

            if (useWebsockets) {
                builder.withWebsockets(true);
                builder.withWebsocketSigningRegion(region);

                if (proxyHost != null && proxyPort > 0) {
                    HttpProxyOptions proxyOptions = new HttpProxyOptions();
                    proxyOptions.setHost(proxyHost);
                    proxyOptions.setPort(proxyPort);

                    builder.withWebsocketProxyOptions(proxyOptions);
                }
            }

            try(MqttClientConnection connection = builder.build()) {

                CompletableFuture<Boolean> connected = connection.connect()
                        .exceptionally((ex) -> {
                            System.out.println("Exception occurred during connect: " + ex.toString());
                            return null;
                        });
                boolean sessionPresent = connected.get();
                System.out.println("Connected to " + (!sessionPresent ? "new" : "existing") + " session!");

                CompletableFuture<Integer> subscribed = connection.subscribe(topic, QualityOfService.AT_LEAST_ONCE, (message) -> {
                    try {
                        String payload = new String(message.getPayload(), "UTF-8");
                        System.out.println("MESSAGE: " + payload);
                    } catch (UnsupportedEncodingException ex) {
                        System.out.println("Unable to decode payload: " + ex.getMessage());
                    }
                });

                subscribed.get();

                int count = 0;
                while (count++ < messagesToPublish) {
                    CompletableFuture<Integer> published = connection.publish(new MqttMessage(topic, message.getBytes()), QualityOfService.AT_LEAST_ONCE, false);
                    published.get();
                    Thread.sleep(1000);
                }

                CompletableFuture<Void> disconnected = connection.disconnect();
                disconnected.get();
            }
        } catch (CrtRuntimeException | InterruptedException | ExecutionException ex) {
            System.out.println("Exception encountered: " + ex.toString());
        }

        System.out.println("Complete!");
    }
}
