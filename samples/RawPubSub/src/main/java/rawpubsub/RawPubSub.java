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

package rawpubsub;

import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.CrtRuntimeException;
import software.amazon.awssdk.crt.io.ClientBootstrap;
import software.amazon.awssdk.crt.io.TlsContext;
import software.amazon.awssdk.crt.io.TlsContextOptions;
import software.amazon.awssdk.crt.mqtt.MqttClient;
import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt.MqttClientConnectionEvents;
import software.amazon.awssdk.crt.mqtt.MqttMessage;
import software.amazon.awssdk.crt.mqtt.QualityOfService;
import software.amazon.awssdk.iot.iotjobs.model.RejectedError;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

class RawPubSub {
    static String clientId = "samples-client-id";
    static String rootCaPath;
    static String certPath;
    static String keyPath;
    static String endpoint;
    static String topic = "/samples/test";
    static String message = "Hello World!";
    static int    messagesToPublish = 10;
    static boolean showHelp = false;

    static String userName;
    static String password;
    static String protocolName = "mqtt";
    static List<String> authParams;

    static void printUsage() {
        System.out.println(
                "Usage:\n"+
                "  --help        This message\n"+
                "  --clientId    Client ID to use when connecting (optional)\n"+
                "  -e|--endpoint AWS IoT service endpoint hostname\n"+
                "  -r|--rootca   Path to the root certificate\n"+
                "  -c|--cert     Path to the IoT thing certificate\n"+
                "  -k|--key      Path to the IoT thing public key\n"+
                "  -t|--topic    Topic to subscribe/publish to (optional)\n"+
                "  -m|--message  Message to publish (optional)\n"+
                "  -n|--count    Number of messages to publish (optional)"+
                "  -u|--username Username to use as part of the connection/authentication process\n"+
                "  --password    Password to use as part of the connection/authentication process\n"+
                "  --protocol    (optional) Communication protocol to use; defaults to mqtt\n"+
                "  --auth_params (optional) Comma delimited list of auth parameters. For websockets these will be set as headers.  For raw mqtt these will be appended to user_name.\n"
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
                case "-u":
                case "--username":
                    if (idx + 1 < args.length) {
                        userName = args[++idx];
                    }
                    break;
                case "--password":
                    if (idx + 1 < args.length) {
                        password = args[++idx];
                    }
                    break;
                case "--protocol":
                    if (idx + 1 < args.length) {
                        protocolName = args[++idx];
                    }
                    break;
                case "--auth_params":
                    if (idx + 1 < args.length) {
                        authParams = Arrays.asList(args[++idx].split("\\s*,\\s*"));
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


        try(ClientBootstrap clientBootstrap = new ClientBootstrap(1);
            TlsContextOptions tlsContextOptions = TlsContextOptions.createWithMtlsFromPath(certPath, keyPath)) {
            tlsContextOptions.overrideDefaultTrustStoreFromPath(null, rootCaPath);

            int port = 8883;
            if (TlsContextOptions.isAlpnSupported())
            {
                port = 443;
                tlsContextOptions.withAlpnList(protocolName);
            }

            try(TlsContext tlsContext = new TlsContext(tlsContextOptions);
                MqttClient client = new MqttClient(clientBootstrap, tlsContext);
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
                })) {


                if (authParams != null && authParams.size() > 0) {
                    if (userName.length() > 0) {
                        StringBuilder usernameBuilder = new StringBuilder();

                        usernameBuilder.append(userName);
                        usernameBuilder.append("?");
                        for (int i = 0; i < authParams.size(); ++i) {
                            usernameBuilder.append(authParams.get(i));
                            if (i + 1 < authParams.size())
                            {
                                usernameBuilder.append("&");
                            }
                        }

                        userName = usernameBuilder.toString();
                    }
                }

                if (userName != null && userName.length() > 0) {
                    connection.setLogin(userName, password);
                }

                CompletableFuture<Boolean> connected = connection.connect(
                        clientId,
                        endpoint, port,
                        null, true, 0, 0)
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
