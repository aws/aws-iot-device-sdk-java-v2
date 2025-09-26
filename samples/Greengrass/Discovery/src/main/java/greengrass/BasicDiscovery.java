/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package greengrass;

import software.amazon.awssdk.crt.CrtResource;
import software.amazon.awssdk.crt.CrtRuntimeException;
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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static software.amazon.awssdk.iot.discovery.DiscoveryClient.TLS_EXT_ALPN;

public class BasicDiscovery {

    // ------------------------- ARGUMENT PARSING -------------------------
    static class Args {
        String certPath;
        String keyPath;
        String region;
        String thingName;
        Boolean printDiscoveryRespOnly = false;
        String mode;
        String proxyHost;
        String caPath;
        int proxyPort = 0;
        String topic = "test/topic";
    }

    private static void printHelpAndExit(int code) {
        System.out.println("Basic Discovery Sample\n");
        System.out.println("Required:");
        System.out.println("  --cert <CERTIFICATE>      Path to certificate file (PEM)");
        System.out.println("  --key <PRIVATE_KEY>       Path to private key file (PEM)");
        System.out.println("  --region <REGION>         The region to connect through");
        System.out.println("  --thing_name <THING_NAME> The name assigned to your IoT Thing");
        System.out.println("\nOptional:");
        System.out.println("  --print_discover_resp_only <PRINT_DISC_RESPONSE> (optional, default='False')");
        System.out.println("  --mode <MODE>                                    The operation mode can be set to 'subscribe' 'publish' or 'both'(default)");
        System.out.println("  --ca_file <CA_FILE>                              Path to optional CA bundle (PEM)");
        System.out.println("  --proxy_host <PROXY_HOST>                        HTTP proxy host");
        System.out.println("  --proxy_port <PROXY_PORT>                        HTTP proxy port");
        System.out.println("  --topic <TOPIC>                                  Topic to use (default: test/topic)");
        System.exit(code);
    }

    private static Args parseArgs(String[] argv) {
        if (argv.length == 0 || Arrays.asList(argv).contains("--help")) {
            printHelpAndExit(0);
        }
        Args a = new Args();
        for (int i = 0; i < argv.length; i++) {
            String k = argv[i];
            String v = (i + 1 < argv.length) ? argv[i + 1] : null;

            switch (k) {
                case "--cert":                     a.certPath = v; i++; break;
                case "--key":                      a.keyPath  = v; i++; break;
                case "--region":                   a.region   = v; i++; break;
                case "--thing_name":               a.thingName = v; i++; break;
                case "--print_discover_resp_only": a.printDiscoveryRespOnly = Boolean.valueOf(v);
                case "--mode":                     a.mode = v; i++; break;
                case "--proxy_host":               a.proxyHost = v; i++; break;
                case "--proxy_port":               a.proxyPort = Integer.parseInt(v); i++; break;
                case "--ca_file":                  a.caPath = v; i++; break;
                case "--topic":                    a.topic = v; i++; break;
                default:
                    System.err.println("Unknown arg: " + k);
                    printHelpAndExit(2);
            }
        }
        if (a.certPath == null || a.keyPath == null || a.region == null || a.thingName == null) {
            System.err.println("Missing required arguments.");
            printHelpAndExit(2);
        }
        return a;
    }
    // ------------------------- ARGUMENT PARSING END ---------------------

    static Args args;

    public static void main(String[] argv) {
        args = parseArgs(argv);

        try (final TlsContextOptions tlsCtxOptions = TlsContextOptions.createWithMtlsFromPath(args.certPath, args.keyPath)) {
            if (TlsContextOptions.isAlpnSupported()) {
                tlsCtxOptions.withAlpnList(TLS_EXT_ALPN);
            }
            if (args.caPath != null) {
                tlsCtxOptions.overrideDefaultTrustStoreFromPath(null, args.caPath);
            }
            HttpProxyOptions proxyOptions = null;
            if (args.proxyHost != null && args.proxyPort > 0) {
                proxyOptions = new HttpProxyOptions();
                proxyOptions.setHost(args.proxyHost);
                proxyOptions.setPort(args.proxyPort);
            }

            try (
                    final SocketOptions socketOptions = new SocketOptions();
                    final DiscoveryClientConfig discoveryClientConfig =
                            new DiscoveryClientConfig(tlsCtxOptions, socketOptions, args.region, 1, proxyOptions);
                    final DiscoveryClient discoveryClient = new DiscoveryClient(discoveryClientConfig)) {

                DiscoverResponse response = discoveryClient.discover(args.thingName).get(60, TimeUnit.SECONDS);
                printGreengrassGroupList(response.getGGGroups(), "");

                if (args.printDiscoveryRespOnly == false) {
                    try (final MqttClientConnection connection = getClientFromDiscovery(discoveryClient)) {
                        if ("subscribe".equals(args.mode) || "both".equals(args.mode)) {
                            final CompletableFuture<Integer> subFuture = connection.subscribe(args.topic, QualityOfService.AT_MOST_ONCE, message -> {
                                System.out.println(String.format("Message received on topic %s: %s",
                                        message.getTopic(), new String(message.getPayload(), StandardCharsets.UTF_8)));
                            });
                            subFuture.get();
                        }

                        final Scanner scanner = new Scanner(System.in);
                        while (true) {
                            String input = null;
                            if ("publish".equals(args.mode) || "both".equals(args.mode)) {
                                System.out.println("Enter the message you want to publish to topic " + args.topic + " and press Enter. " +
                                        "Type 'exit' or 'quit' to exit this program: ");
                                input = scanner.nextLine();
                            }

                            if ("exit".equals(input) || "quit".equals(input)) {
                                System.out.println("Terminating...");
                                break;
                            }

                            if ("publish".equals(args.mode) || "both".equals(args.mode)) {
                                final CompletableFuture<Integer> publishResult = connection.publish(new MqttMessage(args.topic,
                                        input.getBytes(StandardCharsets.UTF_8), QualityOfService.AT_MOST_ONCE, false));
                                Integer result = publishResult.get();
                            }
                        }
                    }
                }
            }
        } catch (CrtRuntimeException | InterruptedException | ExecutionException | TimeoutException ex) {
            System.out.println("Exception thrown: " + ex.toString());
            ex.printStackTrace();
        }
        CrtResource.waitForNoResources();
        System.out.println("Complete!");
    }

    private static void printGreengrassGroupList(List<GGGroup> groupList, String prefix) {
        for (int i = 0; i < groupList.size(); i++) {
            GGGroup group = groupList.get(i);
            System.out.println(prefix + "Group ID: " + group.getGGGroupId());
            printGreengrassCoreList(group.getCores(), "  ");
        }
    }

    private static void printGreengrassCoreList(List<GGCore> coreList, String prefix) {
        for (int i = 0; i < coreList.size(); i++) {
            GGCore core = coreList.get(i);
            System.out.println(prefix + "Thing ARN: " + core.getThingArn());
            printGreengrassConnectivityList(core.getConnectivity(), prefix + "  ");
        }
    }

    private static void printGreengrassConnectivityList(List<ConnectivityInfo> connectivityList, String prefix) {
        for (int i = 0; i < connectivityList.size(); i++) {
            ConnectivityInfo connectivityInfo = connectivityList.get(i);
            System.out.println(prefix + "Connectivity ID: " + connectivityInfo.getId());
            System.out.println(prefix + "Connectivity Host Address: " + connectivityInfo.getHostAddress());
            System.out.println(prefix + "Connectivity Port: " + connectivityInfo.getPortNumber());
        }
    }

    private static MqttClientConnection getClientFromDiscovery(final DiscoveryClient discoveryClient
    ) throws ExecutionException, InterruptedException {
        final CompletableFuture<DiscoverResponse> futureResponse = discoveryClient.discover(args.thingName);
        final DiscoverResponse response = futureResponse.get();

        if (response.getGGGroups() == null) {
            throw new RuntimeException("ThingName " + args.thingName + " does not have a Greengrass group/core configuration");
        }
        final Optional<GGGroup> groupOpt = response.getGGGroups().stream().findFirst();
        if (!groupOpt.isPresent()) {
            throw new RuntimeException("ThingName " + args.thingName + " does not have a Greengrass group/core configuration");
        }

        final GGGroup group = groupOpt.get();
        final GGCore core = group.getCores().stream().findFirst().get();

        for (ConnectivityInfo connInfo : core.getConnectivity()) {
            final String dnsOrIp = connInfo.getHostAddress();
            final Integer port = connInfo.getPortNumber();

            System.out.printf("Connecting to group ID %s, with thing arn %s, using endpoint %s:%d%n",
                    group.getGGGroupId(), core.getThingArn(), dnsOrIp, port);

            try (final AwsIotMqttConnectionBuilder connectionBuilder = AwsIotMqttConnectionBuilder.newMtlsBuilderFromPath(args.certPath, args.keyPath)
                    .withClientId(args.thingName)
                    .withPort(port)
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
                    })) {
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
        }

        throw new RuntimeException("ThingName " + args.thingName + " could not connect to the green grass core using any of the endpoint connectivity options");
    }
}
