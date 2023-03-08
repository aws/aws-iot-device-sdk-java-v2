/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package operationqueue;

import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.CrtResource;
import software.amazon.awssdk.crt.CrtRuntimeException;
import software.amazon.awssdk.crt.Log;
import software.amazon.awssdk.crt.auth.credentials.X509CredentialsProvider;
import software.amazon.awssdk.crt.http.HttpProxyOptions;
import software.amazon.awssdk.crt.io.ClientBootstrap;
import software.amazon.awssdk.crt.io.ClientTlsContext;
import software.amazon.awssdk.crt.io.TlsContextOptions;
import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt.MqttClientConnectionEvents;
import software.amazon.awssdk.crt.mqtt.MqttMessage;
import software.amazon.awssdk.crt.mqtt.QualityOfService;
import software.amazon.awssdk.iot.AwsIotMqttConnectionBuilder;
import software.amazon.awssdk.iot.iotjobs.model.RejectedError;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import utils.commandlineutils.CommandLineUtils;

public class OperationQueue {

    // When run normally, we want to exit nicely even if something goes wrong
    // When run from CI, we want to let an exception escape which in turn causes the
    // exec:java task to return a non-zero exit code
    static String ciPropValue = System.getProperty("aws.crt.ci");
    static boolean isCI = ciPropValue != null && Boolean.valueOf(ciPropValue);

    static String topic = "test/topic/" + UUID.randomUUID().toString();;
    static String message = "Hello World:";
    static int    messagesToPublish = 20;
    static int runQueueTests = 0;
    static int queueLimit = 10;
    static int queueMode = 0;

    static CommandLineUtils cmdUtils;

    static CompletableFuture<Void> onQueueEmptyFuture = new CompletableFuture<>();

    /*
     * When called during a CI run, throw an exception that will escape and fail the exec:java task
     * When called otherwise, print what went wrong (if anything) and just continue (return from main)
     */
    static void onApplicationFailure(Throwable cause) {
        if (isCI) {
            throw new RuntimeException("OperationQueue execution failure", cause);
        } else if (cause != null) {
            System.out.println("Exception encountered: " + cause.toString());
        }
    }

    public static void main(String[] args) {

        cmdUtils = new CommandLineUtils();
        cmdUtils.registerProgramName("OperationQueue");
        cmdUtils.addCommonMQTTCommands();
        cmdUtils.addCommonTopicMessageCommands();
        cmdUtils.registerCommand("key", "<path>", "Path to your key in PEM format.");
        cmdUtils.registerCommand("cert", "<path>", "Path to your client certificate in PEM format.");
        cmdUtils.registerCommand("client_id", "<int>", "Client id to use (optional, default='test-*').");
        cmdUtils.registerCommand("port", "<int>", "Port to connect to on the endpoint (optional, default='8883').");
        cmdUtils.registerCommand("count", "<int>", "Number of messages to publish (optional, default='20').");
        cmdUtils.registerCommand("queue_limit", "<int>", "The maximum number of operations for the queue (optional, default=10)");
        cmdUtils.registerCommand("queue_mode", "<int>", "The mode for the queue to use. (optional, default=0)" +
                                "\n\t0 = Overflow removes from queue back and new messages are pushed to queue back" +
                                "\n\t1 = Overflow removes from queue front and new messages are pushed to queue back" +
                                "\n\t2 = Overflow removes from queue front and new messages are pushed to queue front" +
                                "\n\t3 = Overflow removes from queue back and messages are pushed to queue front");
        cmdUtils.registerCommand(
            "run_tests", "<int>",
            "If set to True (1 or greater), then queue tests will be run instead of the sample (optional, default=0)");
        cmdUtils.sendArguments(args);

        topic = cmdUtils.getCommandOrDefault("topic", topic);
        message = cmdUtils.getCommandOrDefault("message", message);
        messagesToPublish = Integer.parseInt(cmdUtils.getCommandOrDefault("count", String.valueOf(messagesToPublish)));
        runQueueTests = Integer.parseInt(cmdUtils.getCommandOrDefault("run_tests", String.valueOf(runQueueTests)));
        queueLimit = Integer.parseInt(cmdUtils.getCommandOrDefault("queue_limit", String.valueOf(queueLimit)));
        queueMode = Integer.parseInt(cmdUtils.getCommandOrDefault("queue_mode", String.valueOf(queueMode)));

        /**
         * If running the queue tests, do it immediately and exit without running the rest of the sample.
         * These tests make sure the MqttOperationQueue works as expected, as opposed to demonstrating how it works.
         */
        if (runQueueTests > 0) {
            MqttOperationQueueTests.RunTests(cmdUtils);
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

        MqttOperationQueue.QueueCallbacks queueCallbacks = new MqttOperationQueue.QueueCallbacks() {
            @Override
            public void OnQueueEmpty() {
                System.out.println("Operation queue is completely empty");
                onQueueEmptyFuture.complete(null);
            }

            @Override
            public void OnQueueFull() {
                System.out.println("Operation queue is full and will start dropping operations should new operations come in");
            }

            @Override
            public void OnOperationSent(MqttOperationQueue.QueueOperation operation, CompletableFuture<Integer> operationFuture) {
                if (operation.type == MqttOperationQueue.QueueOperationType.PUBLISH) {
                    String payload = new String(operation.message.getPayload(), StandardCharsets.UTF_8);
                    System.out.println("Sending publish with payload [" + payload + "] from the operation queue");
                } else {
                    System.out.println("Sending operation of type " + operation.type + " from the operation queue");
                }
            }

            @Override
            public void OnOperationSentFailure(MqttOperationQueue.QueueOperation operation, MqttOperationQueue.QueueResult error) {
                System.out.println("ERROR: Operation from queue failed with error: " + error);
            }

            @Override
            public void OnOperationDropped(MqttOperationQueue.QueueOperation operation) {
                if (operation.type == MqttOperationQueue.QueueOperationType.PUBLISH) {
                    String payload = new String(operation.message.getPayload(), StandardCharsets.UTF_8);
                    System.out.println("Publish with payload [" + payload + "] dropped from operation queue");
                } else {
                    System.out.println("Operation of type " + operation.type + " was dropped from the operation queue");
                }
            }
        };

        try {

            MqttClientConnection connection = cmdUtils.buildMQTTConnection(callbacks);
            if (connection == null)
            {
                onApplicationFailure(new RuntimeException("MQTT connection creation failed!"));
            }

            // Build the MQTT operation queue
            MqttOperationQueue.MqttOperationQueueBuilder queueBuilder = new MqttOperationQueue.MqttOperationQueueBuilder();
            queueBuilder.withConnection(connection).withQueueCallbacks(queueCallbacks).withQueueLimitSize(queueLimit);
            // Change the insert and overflow mode based on the CI input
            if (queueMode == 0) {
                queueBuilder.withQueueInsertBehavior(MqttOperationQueue.InsertBehavior.INSERT_BACK)
                            .withQueueLimitBehavior(MqttOperationQueue.LimitBehavior.DROP_BACK);
            } else if (queueMode == 1) {
                queueBuilder.withQueueInsertBehavior(MqttOperationQueue.InsertBehavior.INSERT_BACK)
                            .withQueueLimitBehavior(MqttOperationQueue.LimitBehavior.DROP_FRONT);
            } else if (queueMode == 2) {
                queueBuilder.withQueueInsertBehavior(MqttOperationQueue.InsertBehavior.INSERT_FRONT)
                            .withQueueLimitBehavior(MqttOperationQueue.LimitBehavior.DROP_FRONT);
            } else if (queueMode == 3) {
                queueBuilder.withQueueInsertBehavior(MqttOperationQueue.InsertBehavior.INSERT_FRONT)
                            .withQueueLimitBehavior(MqttOperationQueue.LimitBehavior.DROP_BACK);
            }
            MqttOperationQueue operationQueue = queueBuilder.build();

            // Connect the MQTT client
            CompletableFuture<Boolean> connected = connection.connect();
            try {
                boolean sessionPresent = connected.get();
                System.out.println("Connected to " + (!sessionPresent ? "new" : "existing") + " session!");
            } catch (Exception ex) {
                throw new RuntimeException("Exception occurred during connect", ex);
            }

            // Start the queue
            operationQueue.start();

            // Subscribe to the topic
            CountDownLatch countDownLatch = new CountDownLatch(queueLimit);
            operationQueue.subscribe(topic, QualityOfService.AT_LEAST_ONCE, (message) -> {
                String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                System.out.println("Received message: " + payload);
                countDownLatch.countDown();
            });
            // Wait until the subscribe has gone out of the queue
            onQueueEmptyFuture.get(60, TimeUnit.SECONDS);

            // Publish all the messages (Note: By default, 10 messages will be dropped!)
            onQueueEmptyFuture = new CompletableFuture<>();
            int count = 0;
            while (count++ < messagesToPublish) {
                operationQueue.publish(new MqttMessage(topic, (message + count).getBytes(), QualityOfService.AT_LEAST_ONCE, false));
            }
            // Wait until the messages have all left the queue
            onQueueEmptyFuture.get(60, TimeUnit.SECONDS);

            // Make sure we got all the responses from the server
            countDownLatch.await();

            // Stop the queue
            operationQueue.stop();

            // Disconnect the MQTT client
            CompletableFuture<Void> disconnected = connection.disconnect();
            disconnected.get();

            // Close the queue now that we are completely done with it.
            operationQueue.close();
            // Close the connection now that we are completely done with it.
            connection.close();

        } catch (CrtRuntimeException | InterruptedException | ExecutionException | TimeoutException ex) {
            onApplicationFailure(ex);
        }

        CrtResource.waitForNoResources();
        System.out.println("Complete!");
    }
}
