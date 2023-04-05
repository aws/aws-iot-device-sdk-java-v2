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

    static String input_thingName;
    static String input_certPath;
    static String input_keyPath;

    static CommandLineUtils cmdUtils;

    public static void main(String[] args) {

        /**
         * Parse the command line data and store the values in cmdData for this sample.
         */
        cmdUtils = new CommandLineUtils();
        cmdUtils.registerProgramName("BasicDiscovery");
        CommandLineUtils.SampleCommandLineData cmdData = cmdUtils.parseSampleInputGreengrassDiscovery(args);

        input_thingName = cmdData.input_thingName;
        input_certPath = cmdData.input_cert;
        input_keyPath = cmdData.input_key;

        // ---- Verify file loads ----
        // Get the absolute CA file path
        final File rootCaFile = new File(cmdData.input_ca);
        if (!rootCaFile.isFile()) {
            throw new RuntimeException("Cannot load root CA from path: " + rootCaFile.getAbsolutePath());
        }
        cmdData.input_ca = rootCaFile.getAbsolutePath();

        final File certFile = new File(cmdData.input_cert);
        if (!certFile.isFile()) {
            throw new RuntimeException("Cannot load certificate from path: " + certFile.getAbsolutePath());
        }
        cmdData.input_cert = certFile.getAbsolutePath();

        final File keyFile = new File(cmdData.input_key);
        if (!keyFile.isFile()) {
            throw new RuntimeException("Cannot load private key from path: " + keyFile.getAbsolutePath());
        }
        cmdData.input_key = keyFile.getAbsolutePath();
        // ----------------------------

        try(final TlsContextOptions tlsCtxOptions = TlsContextOptions.createWithMtlsFromPath(cmdData.input_cert, cmdData.input_key)) {
            if(TlsContextOptions.isAlpnSupported()) {
                tlsCtxOptions.withAlpnList(TLS_EXT_ALPN);
            }
            if(cmdData.input_ca != null) {
                tlsCtxOptions.overrideDefaultTrustStoreFromPath(null, cmdData.input_ca);
            }
            HttpProxyOptions proxyOptions = null;
            if (cmdData.input_proxyHost != null && cmdData.input_proxyPort > 0) {
                proxyOptions = new HttpProxyOptions();
                proxyOptions.setHost(cmdData.input_proxyHost);
                proxyOptions.setPort(cmdData.input_proxyPort);
            }

            try(final DiscoveryClientConfig discoveryClientConfig =
                        new DiscoveryClientConfig(tlsCtxOptions,
                        new SocketOptions(), cmdData.input_signingRegion, 1, proxyOptions);
                final DiscoveryClient discoveryClient = new DiscoveryClient(discoveryClientConfig);
                final MqttClientConnection connection = getClientFromDiscovery(discoveryClient)) {

                if ("subscribe".equals(cmdData.input_mode) || "both".equals(cmdData.input_mode)) {
                    final CompletableFuture<Integer> subFuture = connection.subscribe(cmdData.input_topic, QualityOfService.AT_MOST_ONCE, message -> {
                        System.out.println(String.format("Message received on topic %s: %s",
                                message.getTopic(), new String(message.getPayload(), StandardCharsets.UTF_8)));
                    });

                    subFuture.get();
                }

                final Scanner scanner = new Scanner(System.in);
                while (true) {
                    String input = null;
                    if ("publish".equals(cmdData.input_mode) || "both".equals(cmdData.input_mode)) {
                        System.out.println("Enter the message you want to publish to topic " + cmdData.input_topic + " and press Enter. " +
                                "Type 'exit' or 'quit' to exit this program: ");
                        input = scanner.nextLine();
                    }

                    if ("exit".equals(input) || "quit".equals(input)) {
                        System.out.println("Terminating...");
                        break;
                    }

                    if ("publish".equals(cmdData.input_mode) || "both".equals(cmdData.input_mode)) {
                        final CompletableFuture<Integer> publishResult = connection.publish(new MqttMessage(cmdData.input_topic,
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
        final CompletableFuture<DiscoverResponse> futureResponse = discoveryClient.discover(input_thingName);
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

                    final AwsIotMqttConnectionBuilder connectionBuilder = AwsIotMqttConnectionBuilder.newMtlsBuilderFromPath(input_certPath, input_keyPath)
                            .withClientId(input_thingName)
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

                throw new RuntimeException("ThingName " + input_thingName + " could not connect to the green grass core using any of the endpoint connectivity options");
            }
        }
        throw new RuntimeException("ThingName " + input_thingName + " does not have a Greengrass group/core configuration");
    }
}
