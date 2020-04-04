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

package greengrass;

import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.CrtRuntimeException;
import software.amazon.awssdk.crt.http.HttpProxyOptions;
import software.amazon.awssdk.crt.io.*;
import software.amazon.awssdk.crt.mqtt.*;
import software.amazon.awssdk.iot.AwsIotMqttConnectionBuilder;
import software.amazon.awssdk.iot.discovery.DiscoveryClient;
import software.amazon.awssdk.iot.discovery.DiscoveryClientConfig;
import software.amazon.awssdk.iot.discovery.model.DiscoverResponse;
import software.amazon.awssdk.iot.discovery.model.GGCore;
import software.amazon.awssdk.iot.discovery.model.GGGroup;
import software.amazon.awssdk.iot.iotjobs.model.RejectedError;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static software.amazon.awssdk.iot.discovery.DiscoveryClient.TLS_EXT_ALPN;

class Discovery {
    static String thingName;
    static String rootCaPath;
    static String certPath;
    static String keyPath;
    static String region = "us-east-1";
    static String topic = "/samples/test";
    static String mode = "both";
    static boolean showHelp = false;

    static String proxyHost;
    static int proxyPort;

    static void printUsage() {
        System.out.println(
                "Usage:\n"+
                "  --help        This message\n" +
                "  --thing_name  Thing name to use\n" +
                "  -r|--region   AWS IoT service region\n" +
                "  -a|--rootca   Path to the root certificate\n" +
                "  -c|--cert     Path to the IoT thing certificate\n" +
                "  -k|--key      Path to the IoT thing private key\n" +
                "  -t|--topic    Topic to subscribe/publish to (optional)\n" +
                "  -m|--mode  Message to publish (optional)\n" +
                "  --proxyhost   Websocket proxy host to use\n" +
                "  --proxyport   Websocket proxy port to use\n");
    }

    static void parseCommandLine(String[] args) {
        for (int idx = 0; idx < args.length; ++idx) {
            switch (args[idx]) {
                case "--help":
                    showHelp = true;
                    break;
                case "--thingName":
                    if (idx + 1 < args.length) {
                        thingName = args[++idx];
                    }
                    break;
                case "-r":
                case "--region":
                    if (idx + 1 < args.length) {
                        region = args[++idx];
                    }
                    break;
                case "-a":
                case "--rootca":
                    if (idx + 1 < args.length) {
                        rootCaPath = args[++idx];
                        final File rootCaFile = new File(rootCaPath);
                        if (!rootCaFile.isFile()) {
                            throw new RuntimeException("Cannot load root CA from path: " + rootCaFile.getAbsolutePath());
                        }
                        rootCaPath = rootCaFile.getAbsolutePath();
                    }
                    break;
                case "-c":
                case "--cert":
                    if (idx + 1 < args.length) {
                        certPath = args[++idx];
                        final File certFile = new File(certPath);
                        if (!certFile.isFile()) {
                            throw new RuntimeException("Cannot load certificate from path: " + certFile.getAbsolutePath());
                        }
                        certPath = certFile.getAbsolutePath();
                    }
                    break;
                case "-k":
                case "--key":
                    if (idx + 1 < args.length) {
                        keyPath = args[++idx];
                        final File keyFile = new File(keyPath);
                        if (!keyFile.isFile()) {
                            throw new RuntimeException("Cannot load private key from path: " + keyFile.getAbsolutePath());
                        }
                        keyPath = keyFile.getAbsolutePath();
                    }
                    break;
                case "-t":
                case "--topic":
                    if (idx + 1 < args.length) {
                        topic = args[++idx];
                    }
                    break;
                case "-m":
                case "--mode":
                    if (idx + 1 < args.length) {
                        mode = args[++idx];
                    }
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
                default:
                    System.out.println("Unrecognized argument: " + args[idx]);
            }
        }
    }

    public static void main(String[] args) {
        parseCommandLine(args);
        if (showHelp || thingName == null) {
            printUsage();
            return;
        }

        if (certPath == null || keyPath == null) {
            printUsage();
            return;
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

        MqttClientConnection connection = null;
        try(EventLoopGroup eventLoopGroup = new EventLoopGroup(1)) {
            final HostResolver resolver = new HostResolver(eventLoopGroup);
            final ClientBootstrap clientBootstrap = new ClientBootstrap(eventLoopGroup, resolver);

            final TlsContextOptions tlsCtxOptions = TlsContextOptions.createWithMtlsFromPath(certPath, keyPath);
            if(TlsContextOptions.isAlpnSupported()) {
                tlsCtxOptions.withAlpnList(TLS_EXT_ALPN);
            }
            if(rootCaPath != null) {
                tlsCtxOptions.overrideDefaultTrustStoreFromPath(null, rootCaPath);
            }
            HttpProxyOptions proxyOptions = null;
            if (proxyHost != null && proxyPort > 0) {
                proxyOptions = new HttpProxyOptions();
                proxyOptions.setHost(proxyHost);
                proxyOptions.setPort(proxyPort);
            }
            final TlsContext tlsCtx = new TlsContext(tlsCtxOptions);
            final DiscoveryClientConfig discoveryClientConfig =
                    new DiscoveryClientConfig(clientBootstrap, tlsCtx,
                            new SocketOptions(), region, 1, proxyOptions);
            final DiscoveryClient discoveryClient = new DiscoveryClient(discoveryClientConfig);
            connection = getClientFromDiscovery(discoveryClient, clientBootstrap);

            if(!connection.connect().get()) {
                throw new RuntimeException("Failed to connect to GG Core using MQTT");
            }

            if ("subscribe".equals(mode) || "both".equals(mode)) {
                final CompletableFuture<Integer> subFuture = connection.subscribe(topic, QualityOfService.AT_LEAST_ONCE, message -> {
                    System.out.println(String.format("Message received on topic %s: %s",
                            message.getTopic(), new String(message.getPayload(), StandardCharsets.UTF_8)));
                });
                System.out.println("Subscribe future returned: " + subFuture.get());
            }

            final Scanner scanner = new Scanner(System.in);
            while(true) {
                String input = null;
                if("publish".equals(mode) || "both".equals(mode)) {
                    System.out.println("Enter the message you want to publish to topic %s and press Enter. " +
                            "Type 'exit' or 'quit' to exit this program.");
                    input = scanner.nextLine();
                }

                if("exit".equals(input) || "quit".equals(input)) {
                    System.out.println("Terminating...");
                    break;
                }

                if("publish".equals(mode) || "both".equals(mode)) {
                    System.out.println("Publishing message!");
                    final CompletableFuture<Integer> publishResult = connection.publish(new MqttMessage(topic,
                            input.getBytes(StandardCharsets.UTF_8)), QualityOfService.AT_LEAST_ONCE, false);
                    Integer result = publishResult.get();
                    System.out.println("Publish result code: " + result);
                }
            }
        } catch (CrtRuntimeException | InterruptedException | ExecutionException ex) {
            System.out.println("Exception encountered: " + ex.toString());
            ex.printStackTrace();
        }
        finally {
            if(connection != null && !connection.isNull()) {
                CompletableFuture<Void> disconnected = connection.disconnect();
                try {
                    disconnected.get();
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException("Problem disconnecting the MQTT connection cleanly: " + e.getMessage());
                }
            }
        }
        System.out.println("Complete!");
    }

    private static MqttClientConnection getClientFromDiscovery(final DiscoveryClient discoveryClient,
                                                               final ClientBootstrap bootstrap) throws ExecutionException, InterruptedException {
        final CompletableFuture<DiscoverResponse> futureResponse = discoveryClient.discover(thingName);
        final DiscoverResponse response = futureResponse.get();
        if(response.getGGGroups() != null) {
            final Optional<GGGroup> groupOpt = response.getGGGroups().stream().findFirst();
            if(groupOpt.isPresent()) {
                final GGGroup group = groupOpt.get();
                final GGCore core = group.getCores().stream().findFirst().get();
                final String dnsOrIp = core.getConnectivity().get(0).getHostAddress();
                final Integer port = core.getConnectivity().get(0).getPortNumber();
                System.out.println(String.format("Connecting to group ID %s, with thing arn %s, using endpoint %s:%d",
                        group.getGGGroupId(), core.getThingArn(), dnsOrIp, port));

                final AwsIotMqttConnectionBuilder connectionBuilder = AwsIotMqttConnectionBuilder.newMtlsBuilderFromPath(certPath, keyPath)
                        .withClientId("sample-client-id")
                        .withBootstrap(bootstrap)
                        .withPort(port.shortValue())
                        .withEndpoint(dnsOrIp)
                        .withConnectionEventCallbacks(new MqttClientConnectionEvents() {
                            @Override
                            public void onConnectionInterrupted(int i) {
                                System.out.println("Connection interrupted!");
                            }
                            @Override
                            public void onConnectionResumed(boolean b) {
                                System.out.println("Connection resumed!");
                            }
                        });
                if(group.getCAs() != null) {
                    connectionBuilder.withCertificateAuthority(group.getCAs().get(0));
                }
                return connectionBuilder.build();
            }
        }
        throw new RuntimeException("ThingName " + thingName + " does not have a Greengrass group/core configuration");
    }
}
