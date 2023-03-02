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

    static String topic = "test/topic";
    static String message = "Hello World!";
    static int    messagesToPublish = 10;
    static int runQueueTests = 0;

    static CommandLineUtils cmdUtils;

    static MqttOperationQueue operationQueue;
    static CompletableFuture<Void> onQueueEmptyFuture = new CompletableFuture<>();

    static class SampleQueueCallbacks implements MqttOperationQueue.QueueCallbacks {
        @Override
        public void OnQueueEmpty() {
            System.out.println("Operation queue is completely empty");
            onQueueEmptyFuture.complete(null);
        }

        @Override
        public void OnQueueFull() {
            System.out.println("Operation queue is full and will start dropping messages should new messages come in");
        }

        @Override
        public void OnQueuedOperationSent(MqttOperationQueue.QueueOperation operation, CompletableFuture<Integer> operationFuture) {
            System.out.println("Sending operation of type " + operation.type + " from the operation queue");
            // TODO - test waiting on the future...
        }

        @Override
        public void OnQueuedOperationSentFailure(MqttOperationQueue.QueueOperation operation, MqttOperationQueue.QueueResult error) {
            System.out.println("ERROR: Operation from queue failed with error: " + error);
        }

        @Override
        public void OnQueuedOperationDropped(MqttOperationQueue.QueueOperation operation) {
            System.out.println("Operation of type " + operation.type + " was dropped from the operation queue");
        }
    }
    static SampleQueueCallbacks sampleCallbacks = new SampleQueueCallbacks();

    static void onRejectedError(RejectedError error) {
        System.out.println("Request rejected: " + error.code.toString() + ": " + error.message);
    }

    /*
     * When called during a CI run, throw an exception that will escape and fail the exec:java task
     * When called otherwise, print what went wrong (if anything) and just continue (return from main)
     */
    static void onApplicationFailure(Throwable cause) {
        if (isCI) {
            throw new RuntimeException("BasicPubSub execution failure", cause);
        } else if (cause != null) {
            System.out.println("Exception encountered: " + cause.toString());
        }
    }

    public static void main(String[] args) {

        cmdUtils = new CommandLineUtils();
        cmdUtils.registerProgramName("PubSub");
        cmdUtils.addCommonMQTTCommands();
        cmdUtils.addCommonTopicMessageCommands();
        cmdUtils.registerCommand("key", "<path>", "Path to your key in PEM format.");
        cmdUtils.registerCommand("cert", "<path>", "Path to your client certificate in PEM format.");
        cmdUtils.registerCommand("client_id", "<int>", "Client id to use (optional, default='test-*').");
        cmdUtils.registerCommand("port", "<int>", "Port to connect to on the endpoint (optional, default='8883').");
        cmdUtils.registerCommand("count", "<int>", "Number of messages to publish (optional, default='10').");
        cmdUtils.registerCommand(
            "run_tests", "<int>",
            "If set to True (1 or greater), then queue tests will be run instead of the sample (optional, default=0)");
        cmdUtils.sendArguments(args);

        topic = cmdUtils.getCommandOrDefault("topic", topic);
        message = cmdUtils.getCommandOrDefault("message", message);
        messagesToPublish = Integer.parseInt(cmdUtils.getCommandOrDefault("count", String.valueOf(messagesToPublish)));
        runQueueTests = Integer.parseInt(cmdUtils.getCommandOrDefault("run_tests", "0"));

        // If running the queue tests, do it immediately and exit without running the sample
        if (runQueueTests > 0) {
            // TODO - add tests
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

        try {

            MqttClientConnection connection = cmdUtils.buildMQTTConnection(callbacks);
            if (connection == null)
            {
                onApplicationFailure(new RuntimeException("MQTT connection creation failed!"));
            }

            MqttOperationQueue.MqttOperationQueueBuilder queueBuilder = new MqttOperationQueue.MqttOperationQueueBuilder();
            queueBuilder.withConnection(connection).withQueueCallbacks(sampleCallbacks);
            queueBuilder.withEnableLogging(true);
            operationQueue = queueBuilder.build();

            CompletableFuture<Boolean> connected = connection.connect();
            try {
                boolean sessionPresent = connected.get();
                System.out.println("Connected to " + (!sessionPresent ? "new" : "existing") + " session!");
            } catch (Exception ex) {
                throw new RuntimeException("Exception occurred during connect", ex);
            }

            // Start the queue
            operationQueue.start();

            CountDownLatch countDownLatch = new CountDownLatch(messagesToPublish);
            operationQueue.subscribe(topic, QualityOfService.AT_LEAST_ONCE, (message) -> {
                String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                System.out.println("MESSAGE: " + payload);
                countDownLatch.countDown();
            });
            onQueueEmptyFuture.get(60, TimeUnit.SECONDS);

            onQueueEmptyFuture = new CompletableFuture<>();
            int count = 0;
            while (count++ < messagesToPublish) {
                operationQueue.publish(new MqttMessage(topic, message.getBytes(), QualityOfService.AT_LEAST_ONCE, false));
            }
            onQueueEmptyFuture.get(60, TimeUnit.SECONDS);

            countDownLatch.await();

            // Stop the queue
            operationQueue.stop();

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
