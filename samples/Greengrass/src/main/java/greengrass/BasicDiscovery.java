/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package greengrass;

import software.amazon.awssdk.crt.CrtResource;
import software.amazon.awssdk.crt.CrtRuntimeException;
import software.amazon.awssdk.crt.Log;
import software.amazon.awssdk.crt.http.HttpProxyOptions;
import software.amazon.awssdk.crt.io.*;
import software.amazon.awssdk.crt.mqtt.*;
import software.amazon.awssdk.iot.AwsIotMqttConnectionBuilder;
import software.amazon.awssdk.iot.discovery.DiscoveryClient;
import software.amazon.awssdk.iot.discovery.DiscoveryClientConfig;
import software.amazon.awssdk.iot.discovery.model.ConnectivityInfo;
import software.amazon.awssdk.iot.discovery.model.DiscoverResponse;
import software.amazon.awssdk.iot.discovery.model.GGCore;
import software.amazon.awssdk.iot.discovery.model.GGGroup;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static software.amazon.awssdk.iot.discovery.DiscoveryClient.TLS_EXT_ALPN;

import utils.commandlineutils.CommandLineUtils;

public class BasicDiscovery {
    static String thingName;
    static String rootCaPath;
    static String certPath;
    static String keyPath;
    static String region = "us-east-1";
    static String topic = "test/topic";
    static String mode = "both";

    static String proxyHost;
    static int proxyPort;

    static CommandLineUtils cmdUtils;

    public static void main(String[] args) {
        cmdUtils = new CommandLineUtils();
        cmdUtils.registerProgramName("BasicDiscovery");
        cmdUtils.addCommonMQTTCommands();
        cmdUtils.removeCommand("endpoint");
        cmdUtils.registerCommand("thing_name", "<str>", "The name of the IoT thing.");
        cmdUtils.registerCommand("region", "<str>", "AWS IoT service region (optional, default='us-east-1').");
        cmdUtils.registerCommand("topic", "<str>", "Topic to subscribe/publish to (optional, default='test/topic').");
        cmdUtils.registerCommand("mode", "<str>", "Mode options: 'both', 'publish', or 'subscribe' (optional, default='both').");
        cmdUtils.registerCommand("proxy_host", "<str>", "Websocket proxy host to use (optional, required if --proxy_port is set).");
        cmdUtils.registerCommand("proxy_port", "<int>", "Websocket proxy port to use (optional, required if --proxy_host is set).");
        cmdUtils.registerCommand("help", "", "Prints this message");
        cmdUtils.sendArguments(args);

        if (cmdUtils.hasCommand("verbosity")) {
            cmdUtils.startLogging();
        } else {
            Log.initLoggingFromSystemProperties();
        }

        if (cmdUtils.hasCommand("help")) {
            cmdUtils.printHelp();
            System.exit(1);
        }

        thingName = cmdUtils.getCommandRequired("thing_name", "");
        region = cmdUtils.getCommandOrDefault("region", region);
        rootCaPath = cmdUtils.getCommandOrDefault("ca_file", rootCaPath);
        certPath = cmdUtils.getCommandRequired("cert", "");
        keyPath = cmdUtils.getCommandRequired("key", "");
        topic = cmdUtils.getCommandOrDefault("topic", topic);
        mode = cmdUtils.getCommandOrDefault("mode", mode);
        proxyHost = cmdUtils.getCommandOrDefault("proxy_host", proxyHost);
        proxyPort = Integer.parseInt(cmdUtils.getCommandOrDefault("proxy_port", String.valueOf(proxyPort)));

        // ---- Verify file loads ----
        // Get the absolute CA file path
        final File rootCaFile = new File(rootCaPath);
        if (!rootCaFile.isFile()) {
            throw new RuntimeException("Cannot load root CA from path: " + rootCaFile.getAbsolutePath());
        }
        rootCaPath = rootCaFile.getAbsolutePath();

        final File certFile = new File(certPath);
        if (!certFile.isFile()) {
            throw new RuntimeException("Cannot load certificate from path: " + certFile.getAbsolutePath());
        }
        certPath = certFile.getAbsolutePath();

        final File keyFile = new File(keyPath);
        if (!keyFile.isFile()) {
            throw new RuntimeException("Cannot load private key from path: " + keyFile.getAbsolutePath());
        }
        keyPath = keyFile.getAbsolutePath();
        // ----------------------------

        try(final TlsContextOptions tlsCtxOptions = TlsContextOptions.createWithMtlsFromPath(certPath, keyPath)) {
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

            try(final DiscoveryClientConfig discoveryClientConfig =
                        new DiscoveryClientConfig(tlsCtxOptions,
                        new SocketOptions(), region, 1, proxyOptions);
                final DiscoveryClient discoveryClient = new DiscoveryClient(discoveryClientConfig);
                final MqttClientConnection connection = getClientFromDiscovery(discoveryClient)) {

                if ("subscribe".equals(mode) || "both".equals(mode)) {
                    final CompletableFuture<Integer> subFuture = connection.subscribe(topic, QualityOfService.AT_MOST_ONCE, message -> {
                        System.out.println(String.format("Message received on topic %s: %s",
                                message.getTopic(), new String(message.getPayload(), StandardCharsets.UTF_8)));
                    });

                    subFuture.get();
                }

                final Scanner scanner = new Scanner(System.in);
                while (true) {
                    String input = null;
                    if ("publish".equals(mode) || "both".equals(mode)) {
                        System.out.println("Enter the message you want to publish to topic " + topic + " and press Enter. " +
                                "Type 'exit' or 'quit' to exit this program: ");
                        input = scanner.nextLine();
                    }

                    if ("exit".equals(input) || "quit".equals(input)) {
                        System.out.println("Terminating...");
                        break;
                    }

                    if ("publish".equals(mode) || "both".equals(mode)) {
                        final CompletableFuture<Integer> publishResult = connection.publish(new MqttMessage(topic,
                                input.getBytes(StandardCharsets.UTF_8), QualityOfService.AT_MOST_ONCE, false));
                        Integer result = publishResult.get();
                    }
                }
            }
        } catch (CrtRuntimeException | InterruptedException | ExecutionException ex) {
            System.out.println("Exception thrown: " + ex.toString());
            ex.printStackTrace();
        }
        CrtResource.waitForNoResources();
        System.out.println("Complete!");
    }

    private static MqttClientConnection getClientFromDiscovery(final DiscoveryClient discoveryClient
                                                               ) throws ExecutionException, InterruptedException {
        final CompletableFuture<DiscoverResponse> futureResponse = discoveryClient.discover(thingName);
        final DiscoverResponse response = futureResponse.get();
        if(response.getGGGroups() != null) {
            final Optional<GGGroup> groupOpt = response.getGGGroups().stream().findFirst();
            if(groupOpt.isPresent()) {
                final GGGroup group = groupOpt.get();
                final GGCore core = group.getCores().stream().findFirst().get();

                for (ConnectivityInfo connInfo : core.getConnectivity()) {
                    final String dnsOrIp = connInfo.getHostAddress();
                    final Integer port = connInfo.getPortNumber();

                    System.out.println(String.format("Connecting to group ID %s, with thing arn %s, using endpoint %s:%d",
                            group.getGGGroupId(), core.getThingArn(), dnsOrIp, port));

                    final AwsIotMqttConnectionBuilder connectionBuilder = AwsIotMqttConnectionBuilder.newMtlsBuilderFromPath(certPath, keyPath)
                            .withClientId(thingName)
                            .withPort(port.shortValue())
                            .withEndpoint(dnsOrIp)
                            .withConnectionEventCallbacks(new MqttClientConnectionEvents() {
                                @Override
                                public void onConnectionInterrupted(int errorCode) {
                                    System.out.println("Connection interrupted: " + errorCode);
                                }

                                @Override
                                public void onConnectionResumed(boolean sessionPresent) {
                                    System.out.println("Connection resumed!");
                                }
                            });
                    if (group.getCAs() != null) {
                        connectionBuilder.withCertificateAuthority(group.getCAs().get(0));
                    }

                    try (MqttClientConnection connection = connectionBuilder.build()) {
                        if (connection.connect().get()) {
                            System.out.println("Session resumed");
                        } else {
                            System.out.println("Started a clean session");
                        }

                        /* This lets the connection escape the try block without getting cleaned up */
                        connection.addRef();

                        return connection;
                    } catch (Exception e) {
                        System.out.println(String.format("Connection failed with exception %s", e.toString()));
                    }
                }

                throw new RuntimeException("ThingName " + thingName + " could not connect to the green grass core using any of the endpoint connectivity options");
            }
        }
        throw new RuntimeException("ThingName " + thingName + " does not have a Greengrass group/core configuration");
    }
}
