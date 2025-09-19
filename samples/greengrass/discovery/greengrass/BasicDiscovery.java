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

import utils.commandlineutils.CommandLineUtils;
/*

# Required Arguments
required.add_argument("--cert", required=True,  metavar="", dest="input_cert",
                    help="Path to the certificate file to use during mTLS connection establishment")
required.add_argument("--key", required=True,  metavar="", dest="input_key",
                    help="Path to the private key file to use during mTLS connection establishment")
required.add_argument("--region", required=True,  metavar="", dest="input_signing_region",
                      help="The region to connect through.")
required.add_argument("--thing_name", required=True,  metavar="", dest="input_thing_name",
                      help="The name assigned to your IoT Thing.")

# Optional Arguments
optional.add_argument("--ca_file",  metavar="", dest="input_ca",
                      help="Path to optional CA bundle (PEM)")
optional.add_argument("--topic", default=f"test/topic/{uuid.uuid4().hex[:8]}",  metavar="", dest="input_topic",
                      help="Topic")

optional.add_argument("--print_discover_resp_only", type=bool, default=False,  metavar="", dest="input_print_discovery_resp_only",
                    help="(optional, default='False').")
optional.add_argument("--mode", default='both',  metavar="", dest="input_mode",
                    help=f"The operation mode (optional, default='both').\nModes:{allowed_actions}")
optional.add_argument("--proxy_host",  metavar="", dest="input_proxy_host",
                      help="HTTP proxy host")
optional.add_argument("--proxy_port", type=int, default=0,  metavar="", dest="input_proxy_port",
                      help="HTTP proxy port")
optional.add_argument("--client_id",  metavar="", dest="input_clientId", default=f"mqtt5-sample-{uuid.uuid4().hex[:8]}",
                    help="Client ID")


optional.add_argument("--message", default="Hello World!",  metavar="", dest="input_message",
                      help="Message payload")
optional.add_argument("--max_pub_ops", type=int, default=10,  metavar="", dest="input_max_pub_ops", 
                    help="The maximum number of publish operations (optional, default='10').")


input_thingName = cmdData.input_thingName;
        input_certPath = cmdData.input_cert;
        input_keyPath = cmdData.input_key;
if (cmdData.input_ca != null) {
                tlsCtxOptions.overrideDefaultTrustStoreFromPath(null, cmdData.input_ca);


(cmdData.input_proxyHost != null && cmdData.input_proxyPort > 0) {
 cmdData.input_signingRegion

 cmdData.inputPrintDiscoverRespOnly
 cmdData.input_mode
 cmdData.input_topic
 */
    // ------------------------- ARGUMENT PARSING -------------------------
    static class Args {
        String certPath;
        String keyPath;
        String region;
        String thingName;
        Boolean printDiscoveryRespOnly = false;
        String mode;
        String proxyHost;
        Boolean isProxyPortSet = false;
        int proxyPort;
        String clientId = "mqtt5-sample-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        String topic = "test/topic";
    }

    private static void printHelpAndExit(int code) {
        System.out.println("MQTT5 X509 Sample (mTLS)\n");
        System.out.println("Required:");
        System.out.println("  --cert <CERTIFICATE>      Path to certificate file (PEM)");
        System.out.println("  --key <PRIVATE_KEY>       Path to private key file (PEM)");
        System.out.println("  --region <REGION>         The region to connect through");
        System.out.println("  --thing_name <THING_NAME> The name assigned to your IoT Thing");
        System.out.println("\nOptional:");
        System.out.println("  --print_discover_resp_only <PRINT_DISC_RESPONSE> (optional, default='False')");
        System.out.println("  --mode <MODE>                                    The operation mode (optional, default='both').\nModes:{allowed_actions}");
        System.out.println("  --proxy_host <PROXY_HOST>                        HTTP proxy host");
        System.out.println("  --proxy_port <PROXY_PORT>                        HTTP proxy port");
        System.out.println("  --client_id <CLIENT_ID>                          MQTT client ID (default: generated)");
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
                case "--endpoint": a.endpoint = v; i++; break;
                case "--cert":     a.certPath = v; i++; break;
                case "--key":      a.keyPath  = v; i++; break;
                case "--client_id": a.clientId = v; i++; break;
                case "--topic":     a.topic = v; i++; break;
                case "--message":   a.message = v; i++; break;
                case "--count":
                    a.count = Integer.parseInt(v); i++; break;
                default:
                    System.err.println("Unknown arg: " + k);
                    printHelpAndExit(2);
            }
        }
        if (a.endpoint == null || a.certPath == null || a.keyPath == null) {
            System.err.println("Missing required arguments.");
            printHelpAndExit(2);
        }
        return a;
    }
    // ------------------------- ARGUMENT PARSING END ---------------------

public class BasicDiscovery {

    // When run normally, we want to exit nicely even if something goes wrong.
    // When run from CI, we want to let an exception escape which in turn causes the
    // exec:java task to return a non-zero exit code.
    static String ciPropValue = System.getProperty("aws.crt.ci");
    static boolean isCI = ciPropValue != null && Boolean.valueOf(ciPropValue);

    // Needed to access command line input data in getClientFromDiscovery
    static String input_thingName;
    static String input_certPath;
    static String input_keyPath;

    static CommandLineUtils cmdUtils;

    public static void main(String[] args) {

        /*
         * cmdData is the arguments/input from the command line placed into a single struct for
         * use in this sample. This handles all of the command line parsing, validating, etc.
         * See the Utils/CommandLineUtils for more information.
         */
        CommandLineUtils.SampleCommandLineData cmdData = CommandLineUtils.getInputForIoTSample("BasicDiscovery", args);

        input_thingName = cmdData.input_thingName;
        input_certPath = cmdData.input_cert;
        input_keyPath = cmdData.input_key;

        try (final TlsContextOptions tlsCtxOptions = TlsContextOptions.createWithMtlsFromPath(cmdData.input_cert, cmdData.input_key)) {
            if (TlsContextOptions.isAlpnSupported()) {
                tlsCtxOptions.withAlpnList(TLS_EXT_ALPN);
            }
            if (cmdData.input_ca != null) {
                tlsCtxOptions.overrideDefaultTrustStoreFromPath(null, cmdData.input_ca);
            }
            HttpProxyOptions proxyOptions = null;
            if (cmdData.input_proxyHost != null && cmdData.input_proxyPort > 0) {
                proxyOptions = new HttpProxyOptions();
                proxyOptions.setHost(cmdData.input_proxyHost);
                proxyOptions.setPort(cmdData.input_proxyPort);
            }

            try (
                    final SocketOptions socketOptions = new SocketOptions();
                    final DiscoveryClientConfig discoveryClientConfig =
                            new DiscoveryClientConfig(tlsCtxOptions, socketOptions, cmdData.input_signingRegion, 1, proxyOptions);
                    final DiscoveryClient discoveryClient = new DiscoveryClient(discoveryClientConfig)) {

                DiscoverResponse response = discoveryClient.discover(input_thingName).get(60, TimeUnit.SECONDS);
                if (isCI) {
                    System.out.println("Received a greengrass discovery result! Not showing result in CI for possible data sensitivity.");
                } else {
                    printGreengrassGroupList(response.getGGGroups(), "");
                }

                if (cmdData.inputPrintDiscoverRespOnly == false) {
                    try (final MqttClientConnection connection = getClientFromDiscovery(discoveryClient)) {
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
        final CompletableFuture<DiscoverResponse> futureResponse = discoveryClient.discover(input_thingName);
        final DiscoverResponse response = futureResponse.get();

        if (response.getGGGroups() == null) {
            throw new RuntimeException("ThingName " + input_thingName + " does not have a Greengrass group/core configuration");
        }
        final Optional<GGGroup> groupOpt = response.getGGGroups().stream().findFirst();
        if (!groupOpt.isPresent()) {
            throw new RuntimeException("ThingName " + input_thingName + " does not have a Greengrass group/core configuration");
        }

        final GGGroup group = groupOpt.get();
        final GGCore core = group.getCores().stream().findFirst().get();

        for (ConnectivityInfo connInfo : core.getConnectivity()) {
            final String dnsOrIp = connInfo.getHostAddress();
            final Integer port = connInfo.getPortNumber();

            System.out.printf("Connecting to group ID %s, with thing arn %s, using endpoint %s:%d%n",
                    group.getGGGroupId(), core.getThingArn(), dnsOrIp, port);

            try (final AwsIotMqttConnectionBuilder connectionBuilder = AwsIotMqttConnectionBuilder.newMtlsBuilderFromPath(input_certPath, input_keyPath)
                    .withClientId(input_thingName)
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

        throw new RuntimeException("ThingName " + input_thingName + " could not connect to the green grass core using any of the endpoint connectivity options");
    }
}
