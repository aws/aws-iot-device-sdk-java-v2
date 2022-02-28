/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package pkcs11pubsub;

import software.amazon.awssdk.crt.*;
import software.amazon.awssdk.crt.io.*;
import software.amazon.awssdk.crt.mqtt.*;
import software.amazon.awssdk.iot.AwsIotMqttConnectionBuilder;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

public class Pkcs11PubSub {

    // When run normally, we want to exit nicely even if something goes wrong
    // When run from CI, we want to let an exception escape which in turn causes the
    // exec:java task to return a non-zero exit code
    static String ciPropValue = System.getProperty("aws.crt.ci");
    static boolean isCI = ciPropValue != null && Boolean.valueOf(ciPropValue);

    static String clientId = "test-" + UUID.randomUUID().toString();
    static String rootCaPath;
    static String certPath;
    static String endpoint;
    static String pkcs11LibPath;
    static String pkcs11UserPin;
    static String pkcs11TokenLabel;
    static Long pkcs11SlotId;
    static String pkcs11KeyLabel;
    static String topic = "test/topic";
    static String message = "Hello World!";
    static int messagesToPublish = 10;
    static boolean showHelp = false;
    static int port = 8883;

    static void printUsage() {
        System.out.println("Usage:\n"
                + "  --help            This message\n"
                + "  --clientId        Client ID to use when connecting (optional)\n"
                + "  -e|--endpoint     AWS IoT service endpoint hostname\n"
                + "  -p|--port         Port to connect to on the endpoint (optional)\n"
                + "  -r|--rootca       Path to the root certificate (optional)\n"
                + "  -c|--cert         Path to the IoT thing certificate\n"
                + "  --pkcs11Lib       Path to PKCS#11 library\n"
                + "  --pin             User PIN for logging into PKCS#11 token\n"
                + "  --tokenLabel      Label of PKCS#11 token to use (optional)\n"
                + "  --slotId          Slot ID containing PKCS#11 token to use (optional)\n"
                + "  --keyLabel        Label of private key on the PKCS#11 token (optional)"
                + "  -t|--topic        Topic to subscribe/publish to (optional)\n"
                + "  -m|--message      Message to publish (optional)\n"
                + "  -n|--count        Number of messages to publish (optional)\n");
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
            case "--pkcs11Lib":
                if (idx + 1 < args.length) {
                    pkcs11LibPath = args[++idx];
                }
                break;
            case "--pin":
                if (idx + 1 < args.length) {
                    pkcs11UserPin = args[++idx];
                }
                break;
            case "--tokenLabel":
                if (idx + 1 < args.length) {
                    pkcs11TokenLabel = args[++idx];
                }
                break;
            case "--slotId":
                if (idx + 1 < args.length) {
                    pkcs11SlotId = Long.parseLong(args[++idx]);
                }
                break;
            case "--keyLabel":
                if (idx + 1 < args.length) {
                    pkcs11KeyLabel = args[++idx];
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
            default:
                System.out.println("Unrecognized argument: " + args[idx]);
            }
        }
    }

    /*
     * When called during a CI run, throw an exception that will escape and fail the
     * exec:java task When called otherwise, print what went wrong (if anything) and
     * just continue (return from main)
     */
    static void onApplicationFailure(Throwable cause) {
        if (isCI) {
            throw new RuntimeException("BasicPubSub execution failure", cause);
        } else if (cause != null) {
            System.out.println("Exception encountered: " + cause.toString());
        }
    }

    public static void main(String[] args) {

        parseCommandLine(args);
        if (showHelp || endpoint == null || certPath == null || pkcs11LibPath == null || pkcs11UserPin == null) {
            printUsage();
            onApplicationFailure(null);
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

        // Load PKCS#11 library
        try (Pkcs11Lib pkcs11Lib = new Pkcs11Lib(pkcs11LibPath);
                TlsContextPkcs11Options pkcs11Options = new TlsContextPkcs11Options(pkcs11Lib)) {

            pkcs11Options.withCertificateFilePath(certPath);
            pkcs11Options.withUserPin(pkcs11UserPin);

            // Pass arguments to help find the correct PKCS#11 token,
            // and the private key on that token. You don't need to pass
            // any of these arguments if your PKCS#11 device only has one
            // token, or the token only has one private key. But if there
            // are multiple tokens, or multiple keys to choose from, you
            // must narrow down which one should be used.

            if (pkcs11TokenLabel != null) {
                pkcs11Options.withTokenLabel(pkcs11TokenLabel);
            }

            if (pkcs11SlotId != null) {
                pkcs11Options.withSlotId(pkcs11SlotId);
            }

            if (pkcs11KeyLabel != null) {
                pkcs11Options.withPrivateKeyObjectLabel(pkcs11KeyLabel);
            }

            try (
                    AwsIotMqttConnectionBuilder builder = AwsIotMqttConnectionBuilder
                            .newMtlsPkcs11Builder(pkcs11Options)) {

                if (rootCaPath != null) {
                    builder.withCertificateAuthorityFromPath(null, rootCaPath);
                }

                builder.withConnectionEventCallbacks(callbacks).withClientId(clientId)
                        .withEndpoint(endpoint).withPort((short) port).withCleanSession(true)
                        .withProtocolOperationTimeoutMs(60000);

                try (MqttClientConnection connection = builder.build()) {

                    CompletableFuture<Boolean> connected = connection.connect();
                    try {
                        boolean sessionPresent = connected.get();
                        System.out.println("Connected to " + (!sessionPresent ? "new" : "existing") + " session!");
                    } catch (Exception ex) {
                        throw new RuntimeException("Exception occurred during connect", ex);
                    }

                    CountDownLatch countDownLatch = new CountDownLatch(messagesToPublish);

                    CompletableFuture<Integer> subscribed = connection.subscribe(topic, QualityOfService.AT_LEAST_ONCE,
                            (message) -> {
                                String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                                System.out.println("MESSAGE: " + payload);
                                countDownLatch.countDown();
                            });

                    subscribed.get();

                    int count = 0;
                    while (count++ < messagesToPublish) {
                        CompletableFuture<Integer> published = connection.publish(
                                new MqttMessage(topic, message.getBytes(), QualityOfService.AT_LEAST_ONCE, false));
                        published.get();
                        Thread.sleep(1000);
                    }

                    countDownLatch.await();

                    CompletableFuture<Void> disconnected = connection.disconnect();
                    disconnected.get();
                }
            } catch (CrtRuntimeException | InterruptedException | ExecutionException ex) {
                onApplicationFailure(ex);
            }
        }

        CrtResource.waitForNoResources();

        System.out.println("Complete!");
    }
}
