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

public class MqttOperationQueueTests {

    private CommandLineUtils cmdUtils;
    private MqttClientConnection connection = null;

    private static int OPERATION_WAIT_TIME = 60;
    private static boolean PRINT_QUEUE_LOGS = false;
    private static String TEST_TOPIC = "test/topic/" + UUID.randomUUID().toString();

    static void OnApplicationFailure(Throwable cause) {
        throw new RuntimeException("OperationQueue execution failure", cause);
    }

    static class FutureQueueCallbacks implements MqttOperationQueue.QueueCallbacks {
        public CompletableFuture<Void> onQueueEmptyFuture = new CompletableFuture<Void>();
        public CompletableFuture<Void> onQueueFullFuture = new CompletableFuture<Void>();
        public CompletableFuture<MqttOperationQueue.QueueOperation> onQueueSentFuture = new CompletableFuture<MqttOperationQueue.QueueOperation>();
        public CompletableFuture<MqttOperationQueue.QueueOperation> onQueueDroppedFuture = new CompletableFuture<MqttOperationQueue.QueueOperation>();

        @Override
        public void OnQueueEmpty() {
            onQueueEmptyFuture.complete(null);
        }

        @Override
        public void OnQueueFull() {
            onQueueFullFuture.complete(null);
        }

        @Override
        public void OnOperationSent(MqttOperationQueue.QueueOperation operation, CompletableFuture<Integer> operationFuture) {
            onQueueSentFuture.complete(operation);
        }

        @Override
        public void OnOperationSentFailure(MqttOperationQueue.QueueOperation operation, MqttOperationQueue.QueueResult error) {}

        @Override
        public void OnOperationDropped(MqttOperationQueue.QueueOperation operation) {
            onQueueDroppedFuture.complete(operation);
        }
    }

    public void TestConnectionSetup() {

        MqttClientConnectionEvents callbacks = new MqttClientConnectionEvents() {
            @Override
            public void onConnectionInterrupted(int errorCode) {}

            @Override
            public void onConnectionResumed(boolean sessionPresent) {}
        };
        this.connection = cmdUtils.buildMQTTConnection(callbacks);
        if (this.connection == null)
        {
            OnApplicationFailure(new RuntimeException("TestSetup: MQTT connection creation failed!"));
        }
    }

    public void TestConnectionTeardown() {
        if (this.connection != null) {
            this.connection.close();
            this.connection = null;
        }
    }

    public void TestOperationSuccess(MqttOperationQueue.QueueResult result, String testName) {
        if (result != MqttOperationQueue.QueueResult.SUCCESS) {
            OnApplicationFailure(new RuntimeException(testName + ": operation was not successful. Result: " + result));
        }
    }

    /**
     * Tests that the queue can perform all operations, that they are performed in the right order (not checking overflow),
     * and that they contain the proper data as expected.
     */
    public void TestConnectSubPubUnsub() {
        TestConnectionSetup();

        FutureQueueCallbacks callbacks = new FutureQueueCallbacks();

        MqttOperationQueue.MqttOperationQueueBuilder queueBuilder = new MqttOperationQueue.MqttOperationQueueBuilder();
        queueBuilder.withConnection(this.connection).withQueueCallbacks(callbacks).withEnableLogging(PRINT_QUEUE_LOGS);
        queueBuilder.withQueueLoopTime(2000); // Spend 2 seconds per operation to ensure the last operation had time to run
        MqttOperationQueue operationQueue = queueBuilder.build();

        try {
            // Add the operations to the queue
            TestOperationSuccess(operationQueue.subscribe(TEST_TOPIC, QualityOfService.AT_LEAST_ONCE, (message) -> {
                String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                if (!payload.equals("Hello_World")) {
                    OnApplicationFailure(new RuntimeException("Payload did not contain expected value! '" + payload + "' != 'Hello_World'"));
                }
            }), "TestConnectSubPubUnsub");
            TestOperationSuccess(operationQueue.publish(new MqttMessage(TEST_TOPIC, "Hello_World".getBytes(), QualityOfService.AT_LEAST_ONCE, false)), "TestConnectSubPubUnsub");
            TestOperationSuccess(operationQueue.unsubscribe(TEST_TOPIC), "TestConnectSubPubUnsub");

            if (operationQueue.getQueueSize() != 3) {
                OnApplicationFailure(new RuntimeException("TestConnectSubPubUnsub: Queue size is not 3!"));
            }

            CompletableFuture<Boolean> connected = this.connection.connect();
            connected.get(OPERATION_WAIT_TIME, TimeUnit.SECONDS);

            operationQueue.start();

            // Make sure the order is right. Order should be: Sub, Pub, Unsub
            MqttOperationQueue.QueueOperation returnOperation = null;
            returnOperation = callbacks.onQueueSentFuture.get(OPERATION_WAIT_TIME, TimeUnit.SECONDS);
            if (returnOperation == null || returnOperation.type != MqttOperationQueue.QueueOperationType.SUBSCRIBE) {
                OnApplicationFailure(new RuntimeException("TestConnectSubPubUnsub: First operation is not subscribe!"));
            }
            callbacks.onQueueSentFuture = new CompletableFuture<MqttOperationQueue.QueueOperation>();
            returnOperation = callbacks.onQueueSentFuture.get(OPERATION_WAIT_TIME, TimeUnit.SECONDS);
            if (returnOperation == null || returnOperation.type != MqttOperationQueue.QueueOperationType.PUBLISH) {
                OnApplicationFailure(new RuntimeException("TestConnectSubPubUnsub: Second operation is not publish!"));
            }
            callbacks.onQueueSentFuture = new CompletableFuture<MqttOperationQueue.QueueOperation>();
            returnOperation = callbacks.onQueueSentFuture.get(OPERATION_WAIT_TIME, TimeUnit.SECONDS);
            if (returnOperation == null || returnOperation.type != MqttOperationQueue.QueueOperationType.UNSUBSCRIBE) {
                OnApplicationFailure(new RuntimeException("TestConnectSubPubUnsub: Third operation is not unsubscribe!"));
            }

            // Make sure the queue is reported empty
            callbacks.onQueueEmptyFuture.get(OPERATION_WAIT_TIME, TimeUnit.SECONDS);

            CompletableFuture<Void> disconnected = this.connection.disconnect();
            disconnected.get(OPERATION_WAIT_TIME, TimeUnit.SECONDS);

        } catch (Exception ex) {
            OnApplicationFailure(ex);
        } finally {
            operationQueue.stop();
            operationQueue.close();
        }
        TestConnectionTeardown();
        System.out.println("Test: TestConnectSubPubUnsub Passed");
    }

    /**
     * Tests that when the queue is full it keeps the proper size, that operations are properly dropped from the BACK
     * of the queue rather than from the front (BACK = newest but last to be performed), and the the operations
     * are performed in proper order as expected.
     */
    public void TestDropBack() {
        TestConnectionSetup();

        FutureQueueCallbacks callbacks = new FutureQueueCallbacks();

        MqttOperationQueue.MqttOperationQueueBuilder queueBuilder = new MqttOperationQueue.MqttOperationQueueBuilder();
        queueBuilder.withConnection(this.connection).withQueueCallbacks(callbacks).withEnableLogging(PRINT_QUEUE_LOGS);
        queueBuilder.withQueueLimitSize(2).withQueueLimitBehavior(MqttOperationQueue.LimitBehavior.DROP_BACK);
        queueBuilder.withQueueLoopTime(2000); // Spend 2 seconds per operation to ensure the last operation had time to run
        MqttOperationQueue operationQueue = queueBuilder.build();

        try {
            // Add the operations to the queue
            TestOperationSuccess(operationQueue.subscribe(TEST_TOPIC, QualityOfService.AT_LEAST_ONCE, (message) -> {}), "TestDropBack");
            // Perform 10 publishes
            for (int i = 0; i < 10; i++) {
                TestOperationSuccess(operationQueue.publish(new MqttMessage(TEST_TOPIC, "Hello_World".getBytes(), QualityOfService.AT_LEAST_ONCE, false)), "TestDropBack");
            }
            // Make sure the queue size is correct
            if (operationQueue.getQueueSize() != 2) {
                OnApplicationFailure(new RuntimeException("TestDropBack: Queue size is not 2!"));
            }

            // Add the unsubscribe and make sure the publish is dropped as expected
            callbacks.onQueueDroppedFuture = new CompletableFuture<MqttOperationQueue.QueueOperation>();
            TestOperationSuccess(operationQueue.unsubscribe(TEST_TOPIC), "TestDropBack");
            MqttOperationQueue.QueueOperation droppedOperation = callbacks.onQueueDroppedFuture.get(OPERATION_WAIT_TIME, TimeUnit.SECONDS);
            if (droppedOperation == null || droppedOperation.type != MqttOperationQueue.QueueOperationType.PUBLISH || droppedOperation.message == null) {
                OnApplicationFailure(new RuntimeException("TestDropBack: Dropped operation is not publish like expected!"));
            }

            CompletableFuture<Boolean> connected = this.connection.connect();
            connected.get(OPERATION_WAIT_TIME, TimeUnit.SECONDS);

            operationQueue.start();

            // Make sure the order is right. Order should be: Sub, Unsub
            MqttOperationQueue.QueueOperation returnOperation = null;
            returnOperation = callbacks.onQueueSentFuture.get(OPERATION_WAIT_TIME, TimeUnit.SECONDS);
            if (returnOperation == null || returnOperation.type != MqttOperationQueue.QueueOperationType.SUBSCRIBE) {
                OnApplicationFailure(new RuntimeException("TestDropBack: First operation is not subscribe!"));
            }
            callbacks.onQueueSentFuture = new CompletableFuture<MqttOperationQueue.QueueOperation>();
            returnOperation = callbacks.onQueueSentFuture.get(OPERATION_WAIT_TIME, TimeUnit.SECONDS);
            if (returnOperation == null || returnOperation.type != MqttOperationQueue.QueueOperationType.UNSUBSCRIBE) {
                OnApplicationFailure(new RuntimeException("TestDropBack: Second operation is not publish!"));
            }

            // Make sure the queue is reported empty
            callbacks.onQueueEmptyFuture.get(OPERATION_WAIT_TIME, TimeUnit.SECONDS);

            CompletableFuture<Void> disconnected = this.connection.disconnect();
            disconnected.get(OPERATION_WAIT_TIME, TimeUnit.SECONDS);

        } catch (Exception ex) {
            OnApplicationFailure(ex);
        } finally {
            operationQueue.stop();
            operationQueue.close();
        }
        TestConnectionTeardown();
        System.out.println("Test: TestDropBack Passed");
    }

    /**
     * Tests that when the queue is full it that operations are properly dropped from the FRONT of the queue
     */
    public void TestDropFront() {
        TestConnectionSetup();

        FutureQueueCallbacks callbacks = new FutureQueueCallbacks();

        MqttOperationQueue.MqttOperationQueueBuilder queueBuilder = new MqttOperationQueue.MqttOperationQueueBuilder();
        queueBuilder.withConnection(this.connection).withQueueCallbacks(callbacks).withEnableLogging(PRINT_QUEUE_LOGS);
        queueBuilder.withQueueLimitSize(2).withQueueLimitBehavior(MqttOperationQueue.LimitBehavior.DROP_FRONT);
        MqttOperationQueue operationQueue = queueBuilder.build();

        try {
            // Add subscribe and unsubscribe to the queue
            TestOperationSuccess(operationQueue.subscribe(TEST_TOPIC, QualityOfService.AT_LEAST_ONCE, (message) -> {}), "TestDropFront");
            TestOperationSuccess(operationQueue.unsubscribe(TEST_TOPIC), "TestDropFront");

            // Add two publishes and make sure the drop order is correct
            // First drop
            TestOperationSuccess(operationQueue.publish(new MqttMessage(TEST_TOPIC, "Hello_World".getBytes(), QualityOfService.AT_LEAST_ONCE, false)), "TestDropFront");
            MqttOperationQueue.QueueOperation droppedOperation = callbacks.onQueueDroppedFuture.get(OPERATION_WAIT_TIME, TimeUnit.SECONDS);
            if (droppedOperation == null || droppedOperation.type != MqttOperationQueue.QueueOperationType.SUBSCRIBE) {
                OnApplicationFailure(new RuntimeException("TestDropFront: First dropped operation is not subscribe like expected!"));
            }
            // Second drop
            callbacks.onQueueDroppedFuture = new CompletableFuture<MqttOperationQueue.QueueOperation>();
            TestOperationSuccess(operationQueue.publish(new MqttMessage(TEST_TOPIC, "Hello_World".getBytes(), QualityOfService.AT_LEAST_ONCE, false)), "TestDropFront");
            droppedOperation = callbacks.onQueueDroppedFuture.get(OPERATION_WAIT_TIME, TimeUnit.SECONDS);
            if (droppedOperation == null || droppedOperation.type != MqttOperationQueue.QueueOperationType.UNSUBSCRIBE) {
                OnApplicationFailure(new RuntimeException("TestDropFront: First dropped operation is not unsubscribe like expected!"));
            }
        } catch (Exception ex) {
            OnApplicationFailure(ex);
        } finally {
            operationQueue.stop();
            operationQueue.close();
        }
        TestConnectionTeardown();
        System.out.println("Test: TestDropFront Passed");
    }

    /**
     * Tests that adding/inserting operations to the front of the queue works as expected and the send order
     * is correct and expected.
     */
    public void TestAddFront() {
        TestConnectionSetup();

        FutureQueueCallbacks callbacks = new FutureQueueCallbacks();

        MqttOperationQueue.MqttOperationQueueBuilder queueBuilder = new MqttOperationQueue.MqttOperationQueueBuilder();
        queueBuilder.withConnection(this.connection).withQueueCallbacks(callbacks).withEnableLogging(PRINT_QUEUE_LOGS);
        queueBuilder.withQueueLimitSize(2).withQueueLimitBehavior(MqttOperationQueue.LimitBehavior.DROP_BACK);
        queueBuilder.withQueueInsertBehavior(MqttOperationQueue.InsertBehavior.INSERT_FRONT);
        queueBuilder.withQueueLoopTime(2000); // Spend 2 seconds per operation to ensure the last operation had time to run
        MqttOperationQueue operationQueue = queueBuilder.build();

        try {

            // Fill with publishes
            TestOperationSuccess(operationQueue.publish(new MqttMessage(TEST_TOPIC, "Hello_World".getBytes(), QualityOfService.AT_LEAST_ONCE, false)), "TestAddFront");
            TestOperationSuccess(operationQueue.publish(new MqttMessage(TEST_TOPIC, "Hello_World".getBytes(), QualityOfService.AT_LEAST_ONCE, false)), "TestAddFront");

            // Make sure the queue size is correct
            if (operationQueue.getQueueSize() != 2) {
                OnApplicationFailure(new RuntimeException("TestAddFront: Queue size is not 2!"));
            }

            // Add unsubscribe and then subscribe, which should result in the queue order of subscribe, unsubscribe
            TestOperationSuccess(operationQueue.unsubscribe(TEST_TOPIC), "TestAddFront");
            TestOperationSuccess(operationQueue.subscribe(TEST_TOPIC, QualityOfService.AT_LEAST_ONCE, (message) -> {}), "TestAddFront");

            // Make sure the queue size is correct
            if (operationQueue.getQueueSize() != 2) {
                OnApplicationFailure(new RuntimeException("TestAddFront: Queue size is not 2!"));
            }

            CompletableFuture<Boolean> connected = this.connection.connect();
            connected.get(OPERATION_WAIT_TIME, TimeUnit.SECONDS);

            operationQueue.start();

            // Make sure the order is right. Order should be: Sub, Unsub
            MqttOperationQueue.QueueOperation returnOperation = null;
            returnOperation = callbacks.onQueueSentFuture.get(OPERATION_WAIT_TIME, TimeUnit.SECONDS);
            if (returnOperation == null || returnOperation.type != MqttOperationQueue.QueueOperationType.SUBSCRIBE) {
                OnApplicationFailure(new RuntimeException("TestAddFront: First operation is not subscribe!"));
            }
            callbacks.onQueueSentFuture = new CompletableFuture<MqttOperationQueue.QueueOperation>();
            returnOperation = callbacks.onQueueSentFuture.get(OPERATION_WAIT_TIME, TimeUnit.SECONDS);
            if (returnOperation == null || returnOperation.type != MqttOperationQueue.QueueOperationType.UNSUBSCRIBE) {
                OnApplicationFailure(new RuntimeException("TestAddFront: Second operation is not publish!"));
            }

            // Make sure the queue is reported empty
            callbacks.onQueueEmptyFuture.get(OPERATION_WAIT_TIME, TimeUnit.SECONDS);

            CompletableFuture<Void> disconnected = this.connection.disconnect();
            disconnected.get(OPERATION_WAIT_TIME, TimeUnit.SECONDS);

        } catch (Exception ex) {
            OnApplicationFailure(ex);
        } finally {
            operationQueue.stop();
            operationQueue.close();
        }
        TestConnectionTeardown();
        System.out.println("Test: TestAddFront Passed");
    }

    /**
     * Tests that when the queue is full and set to return an error, that it does so when you try to add more to it
     */
    public void TestAddError() {
        TestConnectionSetup();

        FutureQueueCallbacks callbacks = new FutureQueueCallbacks();

        MqttOperationQueue.MqttOperationQueueBuilder queueBuilder = new MqttOperationQueue.MqttOperationQueueBuilder();
        queueBuilder.withConnection(this.connection).withQueueCallbacks(callbacks).withEnableLogging(PRINT_QUEUE_LOGS);
        queueBuilder.withQueueLimitSize(2).withQueueLimitBehavior(MqttOperationQueue.LimitBehavior.RETURN_ERROR);
        MqttOperationQueue operationQueue = queueBuilder.build();

        try {
            // Fill with unsubscribe
            TestOperationSuccess(operationQueue.unsubscribe(TEST_TOPIC), "TestAddError");
            TestOperationSuccess(operationQueue.unsubscribe(TEST_TOPIC), "TestAddError");

            // Try to add another but it should return an error since the queue is full
            MqttOperationQueue.QueueResult operationResult = operationQueue.unsubscribe(TEST_TOPIC);
            if (operationResult != MqttOperationQueue.QueueResult.ERROR_QUEUE_FULL) {
                OnApplicationFailure(new RuntimeException(
                    "TestAddError: unsubscribe when full did not return queue full error. Result: " + operationResult));
            }
        } catch (Exception ex) {
            OnApplicationFailure(ex);
        } finally {
            operationQueue.stop();
            operationQueue.close();
        }
        TestConnectionTeardown();
        System.out.println("Test: TestDropFront Passed");
    }

    public void PerformTests(CommandLineUtils cmdUtils) {
        this.cmdUtils = cmdUtils;
        TestConnectSubPubUnsub();
        TestDropBack();
        TestDropFront();
        TestAddFront();
        TestAddError();
    }

    public static void RunTests(CommandLineUtils cmdUtils) {
        MqttOperationQueueTests tests = new MqttOperationQueueTests();
        tests.PerformTests(cmdUtils);
    }
}
