/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package mqtt5pkcs11;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.CrtResource;
import software.amazon.awssdk.crt.mqtt5.*;
import software.amazon.awssdk.crt.mqtt5.packets.*;
import software.amazon.awssdk.iot.AwsIotMqtt5ClientBuilder;
import software.amazon.awssdk.crt.io.Pkcs11Lib;
import software.amazon.awssdk.crt.io.TlsContextPkcs11Options;

/**
 * MQTT5 X509 Sample (mTLS)
 */
public class Mqtt5Pkcs11 {

    // ------------------------- ARGUMENT PARSING -------------------------
    static class Args {
        String endpoint;
        String certPath;
        String pkcs11LibPath;
        String pin;
        String tokenLabel;
        boolean isSlotIdSet = false;
        int slotId;
        String keyLabel;
        String clientId = "mqtt5-sample-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        String topic = "test/topic";
        String message = "Hello from mqtt5 sample";
        int count = 5;
    }

    private static void printHelpAndExit(int code) {
        System.out.println("MQTT5 PKCS11 Sample\n");
        System.out.println("Required:");
        System.out.println("  --endpoint <ENDPOINT>       IoT endpoint hostname");
        System.out.println("  --cert <CERTIFICATE>        Path to certificate file (PEM)");
        System.out.println("  --pkcs11_path <PKCS11_PATH> Path to PKCS#11 Library");
        System.out.println("  --pin <PIN>                 User PIN for logging into PKCS#11 token");
        System.out.println("\nOptional:");
        System.out.println("  --token_label <TOKEN_LABEL> Label of the PKCS#11 token to use (optional)");
        System.out.println("  --slot_id <SLOT_ID>         Slot ID containing the PKCS#11 token to use (optional)");
        System.out.println("  --key_label <KEY_LABEL>     Label of private key on the PKCS#11 token (optional)");
        System.out.println("  --client_id <CLIENT_ID>     MQTT client ID (default: generated)");
        System.out.println("  --topic <TOPIC>             Topic to use (default: test/topic)");
        System.out.println("  --message <MESSAGE>         Message payload (default: \"Hello from mqtt5 sample\")");
        System.out.println("  --count <N>                 Messages to publish (0 = infinite, default: 5)");
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
                case "--cert": a.certPath = v; i++; break;
                case "--pkcs11_path": a.pkcs11LibPath  = v; i++; break;
                case "--pin": a.pin = v; i++; break;
                case "--token_label": a.tokenLabel = v; i++; break;
                case "--slot_id": a.slotId = Integer.parseInt(v); a.isSlotIdSet = true; i++; break;
                case "--key_label": a.keyLabel = v; i++; break;
                case "--client_id": a.clientId = v; i++; break;
                case "--topic": a.topic = v; i++; break;
                case "--message": a.message = v; i++; break;
                case "--count": a.count = Integer.parseInt(v); i++; break;
                default:
                    System.err.println("Unknown arg: " + k);
                    printHelpAndExit(2);
            }
        }
        if (a.endpoint == null || a.certPath == null || a.pkcs11LibPath == null || a.pin == null) {
            System.err.println("Missing required arguments.");
            printHelpAndExit(2);
        }
        return a;
    }
    // ------------------------- ARGUMENT PARSING END ---------------------

    public static void main(String[] argv) {
        Args args = parseArgs(argv);

        System.out.println("\nStarting MQTT5 X509 Sample\n");
        final int TIMEOUT_SECONDS = 100;

        /*
         * Latches for flow control of Sample
         */
        CountDownLatch connected = new CountDownLatch(1);
        CountDownLatch stopped = new CountDownLatch(1);
        CountDownLatch receivedAll = new CountDownLatch(args.count > 0 ? args.count : 1);

        /*
         * Handle MQTT5 Client lifecycle events
         */
        Mqtt5ClientOptions.LifecycleEvents lifecycleEvents = new Mqtt5ClientOptions.LifecycleEvents() {
            @Override
            public void onAttemptingConnect(Mqtt5Client client, OnAttemptingConnectReturn onAttemptingConnectReturn) {
                System.out.printf("Lifecycle Connection Attempt%nConnecting to endpoint: '%s' with client ID '%s'%n",
                        args.endpoint, args.clientId);
            }

            @Override
            public void onConnectionSuccess(Mqtt5Client client, OnConnectionSuccessReturn onConnectionSuccessReturn) {
                System.out.println("Lifecycle Connection Success with reason code: " + 
                    onConnectionSuccessReturn.getConnAckPacket().getReasonCode() + "\n");
                connected.countDown();
            }

            @Override
            public void onConnectionFailure(Mqtt5Client client, OnConnectionFailureReturn onConnectionFailureReturn) {
                System.out.println("Lifecycle Connection Failure with error code: " + 
                    onConnectionFailureReturn.getErrorCode() + " : " +
                    CRT.awsErrorName(onConnectionFailureReturn.getErrorCode()) + " : " + 
                    CRT.awsErrorString(onConnectionFailureReturn.getErrorCode()) + "\n");
            }

            @Override
            public void onDisconnection(Mqtt5Client client, OnDisconnectionReturn onDisconnectionReturn) {
                System.out.println("Mqtt5 Client: Disconnected");
                DisconnectPacket disconnectPacket = onDisconnectionReturn.getDisconnectPacket();
                if (disconnectPacket != null) {
                    System.out.println("\nDisconnection packet code: " + disconnectPacket.getReasonCode() + 
                        "\nDisconnection packet reason: " + disconnectPacket.getReasonString());
                }
            }

            @Override
            public void onStopped(Mqtt5Client client, OnStoppedReturn onStoppedReturn) {
                System.out.println("Lifecycle Stopped\n");
                stopped.countDown();
            }
        };

        /*
         * Handle Publishes received by the MQTT5 Client
         */
        Mqtt5ClientOptions.PublishEvents publishEvents = new Mqtt5ClientOptions.PublishEvents() {
            @Override
            public void onMessageReceived(Mqtt5Client client, PublishReturn publishReturn) {
                PublishPacket publish = publishReturn.getPublishPacket();
                String payload = publish.getPayload() == null
                        ? ""
                        : new String(publish.getPayload(), StandardCharsets.UTF_8);
                System.out.printf("==== Received message from topic '%s': %s ====%n%n",
                        publish.getTopic(), payload);
                if (args.count > 0) {
                    receivedAll.countDown();
                }
            }
        };

        Mqtt5Client client;

        /**
         * Create MQTT5 client using mutual TLS via X509 Certificate and Private Key
         */
        System.out.println("==== Creating MQTT5 Client ====\n");

        Pkcs11Lib pkcs11Lib = new Pkcs11Lib(args.pkcs11LibPath);
        TlsContextPkcs11Options pkcs11Options = new TlsContextPkcs11Options(pkcs11Lib);
        pkcs11Options.withCertificateFilePath(args.certPath);
        pkcs11Options.withUserPin(args.pin);
        
        /*
         * Pass arguments to help find the correct PKCS#11 token, and the private key on that token. You don't need 
         * to pass any of these arguments if your PKCS#11 device only has one token, or the token only has one 
         * private key. But if there are multiple tokens, or multiple keys to choose from, you must narrow down which 
         * one should be used.
         */
        
        if (args.tokenLabel != null) {
            pkcs11Options.withTokenLabel(args.tokenLabel);
        }
        if (args.isSlotIdSet) {
            pkcs11Options.withSlotId(args.slotId);
        }
        if (args.keyLabel != null) {
            pkcs11Options.withPrivateKeyObjectLabel(args.keyLabel);
        }
        AwsIotMqtt5ClientBuilder builder = AwsIotMqtt5ClientBuilder.newDirectMqttBuilderWithMtlsFromPkcs11(
            args.endpoint, pkcs11Options);
        builder.withLifeCycleEvents(lifecycleEvents);
        builder.withPublishEvents(publishEvents);
        builder.withClientId(args.clientId);
        client = builder.build();
        // You must call `close()` on AwsIotMqtt5ClientBuilder or it will leak memory! Builder is `AutoClosable` and rely on
        // scope-based cleanup via try-with-resources.
        builder.close();

        System.out.println("==== Starting client ====");
        client.start();
        try {
            if (!connected.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                throw new RuntimeException("Connection timeout");
            }
        } catch (InterruptedException ex) {
        throw new RuntimeException("Mqtt5 PKCS11: execution failure", ex);
        }

        /* Subscribe */
        System.out.printf("==== Subscribing to topic '%s' ====%n", args.topic);
        SubscribePacket subscribePacket = SubscribePacket.of(args.topic, QOS.AT_LEAST_ONCE);
        try {
            SubAckPacket subAckPacket  = client.subscribe(subscribePacket).get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            System.out.println("SubAck received with reason code:" + subAckPacket.getReasonCodes() + "\n");
        } catch (Exception ex) {
            throw new RuntimeException("Mqtt5 PKCS11: execution failure", ex);
        }

        /* Publish */
        if (args.count == 0) {
            System.out.println("==== Sending messages until program killed ====\n");
        } else {
            System.out.printf("==== Sending %d message(s) ====%n%n", args.count);
        }
        int publishCount = 1;
        while (args.count == 0 || publishCount <= args.count) {
            String payload = args.message + " [" + publishCount + "]";
            System.out.printf("Publishing message to topic '%s': %s%n", args.topic, payload);
            PublishPacket publishPacket = PublishPacket.of(
                args.topic,
                QOS.AT_LEAST_ONCE,
                payload.getBytes(StandardCharsets.UTF_8));
            try {
            PubAckPacket pubAck = client.publish(publishPacket).get(TIMEOUT_SECONDS, TimeUnit.SECONDS).getResultPubAck();
            System.out.println("PubAck received with reason: " + pubAck.getReasonCode() + "\n");
            } catch (Exception ex) {
                throw new RuntimeException("Mqtt5 PKCS11: execution failure", ex);
            }
            try {
                Thread.sleep(Duration.ofMillis(1500).toMillis());
            } catch (InterruptedException ex) {
                throw new RuntimeException("Mqtt5 PKCS11: execution failure", ex);
            }
            publishCount++;
        }
        if (args.count > 0) {
            long remaining = receivedAll.getCount();
            if (remaining > 0) {
                try {
                    receivedAll.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
                } catch (InterruptedException ex) {
                    throw new RuntimeException("Mqtt5 PKCS11: execution failure", ex);
                }
            }
            long received = (args.count - receivedAll.getCount());
            System.out.printf("%d message(s) received.%n%n", received);
        }

        // ---------- Unsubscribe ----------
        System.out.printf("==== Unsubscribing from topic '%s' ====%n", args.topic);
        UnsubscribePacket unsubscribePacket = UnsubscribePacket.of(args.topic);
        try {
            UnsubAckPacket unsubAckPacket = client.unsubscribe(unsubscribePacket).get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            System.out.println("UnsubAck received with reason code:" + unsubAckPacket.getReasonCodes() + "\n");
        } catch (Exception ex) {
            throw new RuntimeException("Mqtt5 PKCS11: execution failure", ex);
        }

        System.out.println("==== Stopping Client ====");
        client.stop();
        try {
            if (!stopped.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                throw new RuntimeException("Stop timeout");
            }
        } catch (InterruptedException ex) {
            throw new RuntimeException("Mqtt5 PKCS11: execution failure", ex);
        }
        System.out.println("==== Client Stopped! ====");

        /* Close the client to free memory */
        client.close();        

        CrtResource.waitForNoResources();
        System.out.println("Complete!");
    }
}
