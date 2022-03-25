/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package windowscertpubsub;

import software.amazon.awssdk.crt.*;
import software.amazon.awssdk.crt.io.*;
import software.amazon.awssdk.crt.mqtt.*;
import software.amazon.awssdk.iot.AwsIotMqttConnectionBuilder;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import utils.commandlineutils.CommandLineUtils;

public class WindowsCertPubSub {

    // When run normally, we want to exit nicely even if something goes wrong
    // When run from CI, we want to let an exception escape which in turn causes the
    // exec:java task to return a non-zero exit code
    static String ciPropValue = System.getProperty("aws.crt.ci");
    static boolean isCI = ciPropValue != null && Boolean.valueOf(ciPropValue);

    static String clientId = "test-" + UUID.randomUUID().toString();
    static String rootCaPath;
    static String windowsCertStorePath;
    static String endpoint;
    static String topic = "test/topic";
    static String message = "Hello World!";
    static int messagesToPublish = 10;
    static int port = 8883;

    static CommandLineUtils cmdUtils;

    /*
     * When called during a CI run, throw an exception that will escape and fail the
     * exec:java task When called otherwise, print what went wrong (if anything) and
     * just continue (return from main)
     */
    static void onApplicationFailure(Throwable cause) {
        if (isCI) {
            throw new RuntimeException("execution failure", cause);
        } else if (cause != null) {
            System.out.println("Exception encountered: " + cause.toString());
        }
    }

    public static void main(String[] args) {

        cmdUtils = new CommandLineUtils();
        cmdUtils.registerProgramName("WindowsCertPubSub");
        cmdUtils.addCommonMQTTCommands();
        cmdUtils.removeCommand("cert");
        cmdUtils.removeCommand("key");
        cmdUtils.registerCommand("cert", "<str>", "Path to certificate in Windows cert store. " +
                                                  "e.g. \"CurrentUser\\MY\\6ac133ac58f0a88b83e9c794eba156a98da39b4c\"");
        cmdUtils.registerCommand("client_id", "<int>", "Client id to use (optional, default='test-*').");
        cmdUtils.registerCommand("port", "<int>", "Port to connect to on the endpoint (optional, default='8883').");
        cmdUtils.registerCommand("topic", "<str>", "Topic to subscribe/publish to (optional, default='test/topic').");
        cmdUtils.registerCommand("message", "<str>", "Message to publish (optional, default='Hello World').");
        cmdUtils.registerCommand("count", "<int>", "Number of messages to publish (optional, default='10').");
        cmdUtils.registerCommand("help", "", "Prints this message");
        cmdUtils.sendArguments(args);

        if (cmdUtils.hasCommand("help")) {
            cmdUtils.printHelp();
            System.exit(1);
        }

        endpoint = cmdUtils.getCommandRequired("endpoint", "");
        windowsCertStorePath = cmdUtils.getCommandRequired("cert", "");
        rootCaPath = cmdUtils.getCommandOrDefault("root_ca", rootCaPath);
        clientId = cmdUtils.getCommandOrDefault("client_id", clientId);
        port = Integer.parseInt(cmdUtils.getCommandOrDefault("port", String.valueOf(port)));
        topic = cmdUtils.getCommandOrDefault("topic", topic);
        message = cmdUtils.getCommandOrDefault("message", message);
        messagesToPublish = Integer.parseInt(cmdUtils.getCommandOrDefault("count", String.valueOf(messagesToPublish)));

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

        try (AwsIotMqttConnectionBuilder builder =
                AwsIotMqttConnectionBuilder.newMtlsWindowsCertStorePathBuilder(windowsCertStorePath)) {

            if (rootCaPath != null) {
                builder.withCertificateAuthorityFromPath(null, rootCaPath);
            }

            builder.withConnectionEventCallbacks(callbacks)
                    .withClientId(clientId)
                    .withEndpoint(endpoint)
                    .withPort((short) port)
                    .withCleanSession(true)
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

                CompletableFuture<Integer> subscribed = connection.subscribe(topic, QualityOfService.AT_LEAST_ONCE, (message) -> {
                    String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                    System.out.println("MESSAGE: " + payload);
                    countDownLatch.countDown();
                });

                subscribed.get();

                int count = 0;
                while (count++ < messagesToPublish) {
                    CompletableFuture<Integer> published = connection.publish(new MqttMessage(topic, message.getBytes(), QualityOfService.AT_LEAST_ONCE, false));
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

        CrtResource.waitForNoResources();

        System.out.println("Complete!");
    }
}
