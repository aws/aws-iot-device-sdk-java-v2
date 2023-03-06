/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */
package operationqueue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt.MqttClientConnectionOperationStatistics;
import software.amazon.awssdk.crt.mqtt.MqttMessage;
import software.amazon.awssdk.crt.mqtt.QualityOfService;

public class MqttOperationQueue extends software.amazon.awssdk.crt.CrtResource {

    private List<QueueOperation> operationQueue;
    private Lock operationQueueLock;
    private ScheduledExecutorService executorService;
    private QueueRunnable runnable;

    // Configuration options/settings
    private MqttClientConnection connection;
    private int queueLimitSize;
    private QueueLimitBehavior queueLimitBehavior;
    private QueueInsertBehavior queueInsertBehavior;
    private int incompleteLimit;
    private int inflightLimit;
    private QueueCallbacks queueCallbacks;
    private long queueLoopTimeMs;
    private boolean enableLogging;

    /**
     * Creates a new MqttOperationQueue from the given builder.
     * @param builder The MqttOperationQueueBuilder containing the options you wish to use.
     * @throws RuntimeException When there is a missing required configuration in the builder.
     */
    public MqttOperationQueue(MqttOperationQueueBuilder builder) throws RuntimeException {
        this.operationQueue = new ArrayList<QueueOperation>();
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        this.operationQueueLock = new ReentrantLock(true);

        this.connection = builder.connection;
        this.queueLimitSize = builder.queueLimitSize;
        this.queueLimitBehavior = builder.queueLimitBehavior;
        this.queueInsertBehavior = builder.queueInsertBehavior;
        this.incompleteLimit = builder.incompleteLimit;
        this.inflightLimit = builder.inflightLimit;
        this.queueCallbacks = builder.queueCallbacks;
        this.queueLoopTimeMs = builder.queueLoopTimeMs;
        this.enableLogging = builder.enableLogging;

        if (this.connection == null) {
            throw new RuntimeException("connection is not defined in MqttOperationQueueBuilder!");
        }

        // Keep the MQTT connection alive for at least as long as this MqttOperationQueue is alive
        addReferenceTo(this.connection);
    }

    protected boolean canReleaseReferencesImmediately() {
        return true;
    }
    protected void releaseNativeHandle() {}

    /**
     * Helper function: Reschedules the runnable to run queueLoopTimeMs in the future.
     */
    private void scheduleRunnableLoop() {
        PrintLogMessage("Scheduling queue loop for " + this.queueLoopTimeMs + " Milliseconds in the future...");
        this.executorService.schedule(this.runnable, this.queueLoopTimeMs, TimeUnit.MILLISECONDS);
    }

    /**
     * Helper function: Adds the given QueueOperation to the queue of operations to be processed.
     * @param operation The operation to add to the queue.
     * @return An enum indicating the result of adding the operation to the queue.
     */
    private QueueResult addOperationToQueue(QueueOperation operation) {
        QueueResult result = QueueResult.SUCCESS;
        QueueOperation droppedOperation = null;

        if (operation == null) {
            result = QueueResult.ERROR_INVALID_ARGUMENT;
            return result;
        }

        // CRITICAL SECTION
        operationQueueLock.lock();
        try {
            if (queueLimitSize <= 0) {
                result = addOperationToQueueInsert(operation);
            } else {
                if (this.operationQueue.size() + 1 <= queueLimitSize) {
                    addOperationToQueueInsert(operation);
                } else {
                    QueueOperationAndResult returnData = addOperationToQueueOverflow(operation);
                    result = returnData.result;
                    droppedOperation = returnData.operation;
                }
            }
        } finally {
            operationQueueLock.unlock();
        }
        // END CRITICAL SECTION

        if (result == QueueResult.SUCCESS) {
            PrintLogMessage("Added operation of type " + operation.type + " successfully to queue");
            if (this.operationQueue.size() == this.queueLimitSize && droppedOperation == null) {
                if (this.queueCallbacks != null) {
                    this.queueCallbacks.OnQueueFull();
                }
            }
        }

        // Note: We invoke the dropped callback outside of the critical section to avoid deadlocks
        if (droppedOperation != null) {
            if (this.queueCallbacks != null) {
                this.queueCallbacks.OnQueuedOperationDropped(droppedOperation);
            }
        }

        return result;
    }

    /**
     * Helper function: Adds the given QueueOperation to the queue when the queue is full.
     * Used to make separate the logic for when the queue is full from when it is not yet full.
     * Called by addOperationToQueue.
     * @param operation The operation to add to the queue.
     * @return A struct containing both an enum and (possibly) the dropped operation.
     */
    private QueueOperationAndResult addOperationToQueueOverflow(QueueOperation operation) {
        QueueOperationAndResult returnData = new QueueOperationAndResult();
        returnData.result = QueueResult.SUCCESS;

        if (queueLimitBehavior == QueueLimitBehavior.RETURN_ERROR) {
            PrintLogMessage("Did not drop any operation, instead returning error...");
            returnData.result = QueueResult.ERROR_QUEUE_FULL;
        } else if (queueLimitBehavior == QueueLimitBehavior.DROP_FRONT) {
            returnData.operation = this.operationQueue.remove(0);
            PrintLogMessage("Dropped operation of type " + returnData.operation.type + " from the front...");
            returnData.result = addOperationToQueueInsert(operation);
        } else if (queueLimitBehavior == QueueLimitBehavior.DROP_BACK) {
            returnData.operation = this.operationQueue.remove(this.operationQueue.size()-1);
            PrintLogMessage("Dropped operation of type " + returnData.operation.type + " from the back...");
            returnData.result = addOperationToQueueInsert(operation);
        } else {
            returnData.result = QueueResult.UNKNOWN_QUEUE_LIMIT_BEHAVIOR;
        }
        return returnData;
    }

    /**
     * Helper function: Inserts the given QueueOperation into the queue/list directly.
     * Used to simplify inserting in front/back based on configuration options.
     * Called by both addOperationToQueue and addOperationToQueueOverflow.
     * @param operation The operation to insert into the queue/list.
     * @return An enum indicating the result of adding the operation to the queue.
     */
    private QueueResult addOperationToQueueInsert(QueueOperation operation) {
        QueueResult result = QueueResult.SUCCESS;
        if (queueInsertBehavior == QueueInsertBehavior.INSERT_BACK) {
            this.operationQueue.add(operation);
        } else if (queueInsertBehavior == QueueInsertBehavior.INSERT_FRONT) {
            this.operationQueue.add(0, operation);
        } else {
            result = QueueResult.UNKNOWN_QUEUE_INSERT_BEHAVIOR;
        }
        return result;
    }

    /**
     * Helper function: Prints to the console if logging is enabled.
     * Just makes code a little cleaner and easier to process.
     * @param message The message to print
     */
    private void PrintLogMessage(String message) {
        if (this.enableLogging) {
            System.out.println("[MqttOperationQueue]: " + message);
        }
    }

    // ==================================================
    // OPERATIONS
    // ==================================================

    /**
     * Starts the MqttOperationQueue running so it can process the queue.
     * Every queueLoopTimeMs milliseconds it will check the queue to see if there is at least a single
     * operation waiting. If there is, it will check the MQTT client statistics to determine if
     * the MQTT connection has the bandwidth for the next operation (based on incompleteLimit and inflightLimit)
     * and, if the MQTT connection has bandwidth, will start a next operation from the queue.
     */
    public void start() {
        if (this.runnable != null) {
            PrintLogMessage("Cannot start because queue is already started!");
            return;
        }
        this.runnable = new QueueRunnable();
        this.runnable.queue = this;
        this.runnable.isShutdown = false;
        scheduleRunnableLoop();
        PrintLogMessage("Started successfully");
    }

    /**
     * Stops the MqttOperationQueue from running and processing operations that may be left in the queue.
     * Once stopped, the MqttOperationQueue can be restarted by calling start() again.
     */
    public void stop() {
        if (this.runnable == null) {
            PrintLogMessage("Cannot stop because queue is already stopped!");
            return;
        }
        this.runnable.isShutdown = true;
        // We can ignore/drop the returned list, it will only be trying to loop QueueRunnable
        this.executorService.shutdownNow();
        this.runnable = null;
        PrintLogMessage("Stopped successfully");
    }

    /**
     * Creates a new Publish operation and adds it to the queue to be run.
     *
     * Note that the inputs to this function are exactly the same as the publish() function in
     * the MqttClientConnection, but instead of executing the operation as soon as possible, it
     * will be added to the queue based on the queueInsertBehavior and processed accordingly.
     *
     * The OnQueuedOperationSent callback function will be invoked when the operation is
     * processed and sent by the client.
     *
     * @param message The message you want to publish in the future.
     * @return The result of adding the publish operation to the queue.
     */
    public QueueResult publish(MqttMessage message) {
        QueueOperation newOperation = new QueueOperation();
        newOperation.type = QueueOperationType.PUBLISH;
        newOperation.message = message;
        return addOperationToQueue(newOperation);
    }

    /**
     * Creates a new subscribe operation and adds it to the queue to be run.
     *
     * Note that the inputs to this function are exactly the same as the subscribe() function in
     * the MqttClientConnection, but instead of executing the operation as soon as possible, it
     * will be added to the queue based on the queueInsertBehavior and processed accordingly.
     *
     * The OnQueuedOperationSent callback function will be invoked when the operation is
     * processed and sent by the client.
     *
     * @param topic The topic to subscribe to.
     * @param qos The Quality of Service (QOS) level for the subscribe operation.
     * @return The result of adding the subscribe operation to the queue.
     */
    public QueueResult subscribe(String topic, QualityOfService qos) {
        QueueOperation newOperation = new QueueOperation();
        newOperation.type = QueueOperationType.SUBSCRIBE;
        newOperation.topic = topic;
        newOperation.qos = qos;
        return addOperationToQueue(newOperation);
    }

    /**
     * Creates a new subscribe operation and adds it to the queue to be run.
     *
     * Note that the inputs to this function are exactly the same as the subscribe() function in
     * the MqttClientConnection, but instead of executing the operation as soon as possible, it
     * will be added to the queue based on the queueInsertBehavior and processed accordingly.
     *
     * The OnQueuedOperationSent callback function will be invoked when the operation is
     * processed and sent by the client.
     *
     * @param topic The topic to subscribe to.
     * @param qos The Quality of Service (QOS) level for the subscribe operation.
     * @param messageConsumer The consumer to call when a message is received on the topic
     * @return The result of adding the subscribe operation to the queue
     */
    public QueueResult subscribe(String topic, QualityOfService qos, Consumer<MqttMessage> messageConsumer) {
        QueueOperation newOperation = new QueueOperation();
        newOperation.type = QueueOperationType.SUBSCRIBE;
        newOperation.topic = topic;
        newOperation.qos = qos;
        newOperation.messageConsumer = messageConsumer;
        return addOperationToQueue(newOperation);
    }

    /**
     * Creates a new unsubscribe operation and adds it to the queue to be run.
     *
     * Note that the inputs to this function are exactly the same as the unsubscribe() function in
     * the MqttClientConnection, but instead of executing the operation as soon as possible, it
     * will be added to the queue based on the queueInsertBehavior and processed accordingly.
     *
     * The OnQueuedOperationSent callback function will be invoked when the operation is
     * processed and sent by the client.
     *
     * @param topic The topic to unsubscribe to
     * @return The result of adding the subscribe operation to the queue
     */
    public QueueResult unsubscribe(String topic) {
        QueueOperation newOperation = new QueueOperation();
        newOperation.type = QueueOperationType.UNSUBSCRIBE;
        newOperation.topic = topic;
        return addOperationToQueue(newOperation);
    }

    /**
     * Adds a new queue operation (publish, subscribe, unsubscribe) to the queue to be run.
     *
     * Note: This function provides only basic validation of the operation data. It is primarily
     * intended to be used with the OnQueuedOperationDropped callback for when you may want to
     * add a dropped message back to the queue.
     * (for example, say it's an important message you know you want to send)
     *
     * @param operation The operation you want to add to the queue
     * @return The result of adding the operation to the queue
     */
    public QueueResult addQueueOperation(QueueOperation operation) {
        /* Basic validation */
        if (operation.type == QueueOperationType.NONE) {
            return QueueResult.ERROR_INVALID_ARGUMENT;
        } else if (operation.type == QueueOperationType.PUBLISH) {
            if (operation.message == null) {
                return QueueResult.ERROR_INVALID_ARGUMENT;
            }
        } else if (operation.type == QueueOperationType.SUBSCRIBE) {
            if (operation.topic == null || operation.qos == null) {
                return QueueResult.ERROR_INVALID_ARGUMENT;
            }
        } else if (operation.type == QueueOperationType.UNSUBSCRIBE) {
            if (operation.topic == null) {
                return QueueResult.ERROR_INVALID_ARGUMENT;
            }
        } else {
            return QueueResult.UNKNOWN_ERROR;
        }
        return addOperationToQueue(operation);
    }

    /**
     * Gets the current size of the operation queue
     * @return The current size of the operation queue
     */
    public int getQueueSize() {
        // CRITICAL SECTION
        operationQueueLock.lock();
        int size = operationQueue.size();
        operationQueueLock.unlock();
        // END CRITICAL SECTION
        return size;
    }

    /**
     * Returns the maximum size of this operation queue.
     * @return The maximum size of this operation queue.
     */
    public int getQueueLimit() {
        return this.queueLimitSize;
    }

    // ==================================================
    // HELPER CLASSES
    // ==================================================

    /**
     * The result of attempting to perform an operation on the MqttOperationQueue.
     * The value indicates either success or what type of issue was encountered.
     */
    public enum QueueResult {
        SUCCESS,
        ERROR_QUEUE_FULL,
        ERROR_INVALID_ARGUMENT,
        UNKNOWN_QUEUE_LIMIT_BEHAVIOR,
        UNKNOWN_QUEUE_INSERT_BEHAVIOR,
        UNKNOWN_OPERATION,
        UNKNOWN_ERROR
    }

    /**
     * A helper class. This class is what is run at periodic intervals and actually
     * performs taking an operation off the queue (if one exists) and performing it
     * on the MqttClientConnection object held by the queue.
     */
    private class QueueRunnable implements Runnable {
        public MqttOperationQueue queue;
        public boolean isShutdown;

        /**
         * This function is called every queueLoopTimeMs milliseconds. This is where the logic for handling
         * the queue resides.
         */
        public void run() {
            if (isShutdown == true || this.queue == null) {
                return; // Do nothing - it's shutdown
            }
            this.queue.PrintLogMessage("Performing operation loop...");
            if (runCheckOperationStatistics()) {
                runOperation();
            }
            this.queue.scheduleRunnableLoop();
        }

        /**
         * Helper function: Checks the MQTT connection operation statistics to see if their values are higher than the maximum
         * values set in MqttOperationQueue. If the value is higher than the maximum in MqttOperationQueue, then it returns false
         * so an operation on the queue will not be processed.
         * Called by the run() function
         * @return False if the operation should NOT be executed because the statistics indicate the MQTT client is at desired capacity
         */
        private boolean runCheckOperationStatistics() {
            MqttClientConnectionOperationStatistics statistics = queue.connection.getOperationStatistics();
            if (statistics.getIncompleteOperationCount() >= queue.incompleteLimit) {
                if (queue.incompleteLimit > 0) {
                    this.queue.PrintLogMessage("Skipping running operation due to incomplete operation count being equal or higher than maximum");
                    return false;
                }
            }
            if (statistics.getUnackedOperationCount() >= queue.inflightLimit) {
                if (queue.inflightLimit > 0) {
                    this.queue.PrintLogMessage("Skipping running operation due to inflight operation count being equal or higher than maximum");
                    return false;
                }
            }
            return true;
        }

        /**
         * Helper function: Takes the operation off the queue, checks what operation it is, and passes
         * it to the MQTT connection to be run.
         * Called by the run() function
         */
        private void runOperation() {
            // CRITICAL SECTION
            queue.operationQueueLock.lock();
            try {
                if (queue.operationQueue.size() > 0) {
                    QueueOperation operation = queue.operationQueue.get(0);
                    queue.operationQueue.remove(0);

                    this.queue.PrintLogMessage("Starting to perform operation of type " + operation.type);
                    PerformOperation(operation);

                    if (queue.operationQueue.size() <= 0) {
                        if (queue.queueCallbacks != null) {
                            queue.queueCallbacks.OnQueueEmpty();
                        }
                    }
                }
                else {
                    this.queue.PrintLogMessage("No operations to perform");
                }
            } finally {
                queue.operationQueueLock.unlock();
            }
            // END CRITICAL SECTION
        }

        /**
         * Helper function: Based on the operation type, calls the appropriate helper function
         * @param operation The operation to be run
         */
        private void PerformOperation(QueueOperation operation) {
            if (operation.type == QueueOperationType.PUBLISH) {
                PerformOperationPublish(operation);
            } else if (operation.type == QueueOperationType.SUBSCRIBE) {
                PerformOperationSubscribe(operation);
            } else if (operation.type == QueueOperationType.UNSUBSCRIBE) {
                PerformOperationUnsubscribe(operation);
            } else {
                PerformOperationUnknown(operation);
            }
        }

        /**
         * Helper function: Takes the publish operation and passes it to the MQTT connection
         * @param operation The publish operation to be run
         */
        private void PerformOperationPublish(QueueOperation operation) {
            CompletableFuture<Integer> future = this.queue.connection.publish(operation.message);
            if (queue.queueCallbacks != null) {
                queue.queueCallbacks.OnQueuedOperationSent(operation, future);
            }
        }

        /**
         * Helper function: Takes the subscribe operation and passes it to the MQTT connection
         * @param operation The subscribe operation to be run
         */
        private void PerformOperationSubscribe(QueueOperation operation) {
            CompletableFuture<Integer> future = null;
            if (operation.messageConsumer != null) {
                future = this.queue.connection.subscribe(operation.topic, operation.qos, operation.messageConsumer);
            } else {
                future = this.queue.connection.subscribe(operation.topic, operation.qos);
            }
            if (queue.queueCallbacks != null) {
                queue.queueCallbacks.OnQueuedOperationSent(operation, future);
            }
        }

        /**
         * Helper function: Takes the unsubscribe operation and passes it to the MQTT connection
         * @param operation The unsubscribe operation to be run
         */
        private void PerformOperationUnsubscribe(QueueOperation operation) {
            CompletableFuture<Integer> future = this.queue.connection.unsubscribe(operation.topic);
            if (queue.queueCallbacks != null) {
                queue.queueCallbacks.OnQueuedOperationSent(operation, future);
            }
        }

        /**
         * Helper function: Takes the operation if it is unknown and sends it as a failure to the callback.
         * @param operation The unknown operation
         */
        private void PerformOperationUnknown(QueueOperation operation) {
            this.queue.PrintLogMessage("ERROR - got unknown operation to perform!");
            if (queue.queueCallbacks != null) {
                queue.queueCallbacks.OnQueuedOperationSentFailure(operation, QueueResult.UNKNOWN_OPERATION);
            }
        }
    }

    /**
     * An enum to indicate the type of data the QueueOperation contains. Used
     * to differentiate between different operations in a common blob object.
     */
    public static enum QueueOperationType {
        NONE,
        PUBLISH,
        SUBSCRIBE,
        UNSUBSCRIBE
    }

    /**
     * A blob class containing all of the data an operation can possibly possess, as well as
     * an enum to indicate what type of operation should be stored within. Used to provide
     * a common base that all operations can be derived from.
     */
    public static class QueueOperation {
        public QueueOperationType type;
        public MqttMessage message;
        public String topic;
        public QualityOfService qos;
        public Consumer<MqttMessage> messageConsumer;
    }

    /**
     * Unfortunate, but Java doesn't allow passing by reference and so we cannot both return a
     * value and set/override a value that is passed in as an argument. However, there is a function
     * (addOperationToQueueOverflow) where we need to return two values, and to do so, we need to
     * make a class for this purpose...
     */
    private static class QueueOperationAndResult {
        public QueueOperation operation;
        public QueueResult result;
    }

    /**
     * An enum to indicate what happens when the MqttOperationQueue is completely full but a new
     * operation is requested to be added to the queue.
     */
    public static enum QueueLimitBehavior {
        /** Drops/Removes the oldest (but soonest to be run) operation added to the queue */
        DROP_FRONT,

        /** Drops/Removes the newest (but further to be run) operation added to the queue */
        DROP_BACK,

        /** Does not add the new operation at all and instead returns ERROR_QUEUE_FULL */
        RETURN_ERROR
    }

    /**
     * An enum to indicate what happens when the MqttOperationQueue has a new operation it
     * needs to add to the queue, configuring where the new operation is added.
     */
    public static enum QueueInsertBehavior {
        /** Adds the new operation to the front, so it will be executed the soonest */
        INSERT_FRONT,
        /** Adds the new operation to the back, so it will be executed last */
        INSERT_BACK
    }

    /**
     * A set of callbacks that will be invoked by the MqttOperationQueue when certain conditions
     * are met or the MqttOperationQueue performs an action.
     */
    public static interface QueueCallbacks {
        /**
         * Invoked when an operation was removed from the queue and was just sent to the MQTT connection.
         * Note: At this point the operation was JUST sent, it has not necessarily been seen by the MQTT server/broker
         * nor has it necessarily gone out onto the socket, but rather the operation (like publish()) was just invoked.
         * @param operation The operation that was just sent.
         * @param operationFuture The CompletableFuture tied to the operation that was just invoked.
         */
        public void OnQueuedOperationSent(QueueOperation operation, CompletableFuture<Integer> operationFuture);

        /**
         * Invoked when an operation was removed from the queue and attempted to be sent to the MQTT connection but it failed.
         * @param operation The operation that failed to be sent
         * @param error The reason the operation failed
         */
        public void OnQueuedOperationSentFailure(QueueOperation operation, QueueResult error);

        /**
         * Invoked when an operation was dropped/removed from the queue because the queue is full and a new operation
         * was just added to the queue.
         * @param operation The operation that was just dropped/removed.
         */
        public void OnQueuedOperationDropped(QueueOperation operation);

        /**
         * Invoked when the operation queue in the MqttOperationQueue has just become full
         */
        public void OnQueueFull();

        /**
         * Invoked when the operation queue in the MqttOperationQueue has just become empty
         */
        public void OnQueueEmpty();
    }

    // ==================================================
    // BUILDER
    // ==================================================

    /**
     * A builder that contains all of the options of the MqttOperationQueue.
     * This is where you can configure how the operations queue works prior to making the final
     * MqttOperationQueue with the build() function.
     */
    public static class MqttOperationQueueBuilder {
        private MqttClientConnection connection;
        private int queueLimitSize = 10;
        private QueueLimitBehavior queueLimitBehavior = QueueLimitBehavior.DROP_BACK;
        private QueueInsertBehavior queueInsertBehavior = QueueInsertBehavior.INSERT_BACK;
        private int incompleteLimit = 1;
        private int inflightLimit = 1;
        private QueueCallbacks queueCallbacks;
        private long queueLoopTimeMs = 1000;
        private boolean enableLogging = false;

        /**
         * Sets the MqttClientConnection that will be used by the MqttOperationQueue.
         * This is a REQUIRED argument that has to be set in order for the MqttOperationQueue to function.
         * @param connection The MqttClientConnection that will be used by the MqttOperationQueue.
         * @return The MqttOperationQueueBuilder.
         */
        public MqttOperationQueueBuilder withConnection(MqttClientConnection connection) {
            this.connection = connection;
            return this;
        }

        /**
         * Returns the MqttClientConnection that will be used by the MqttOperationQueue.
         * @return The MqttClientConnection that will be used by the MqttOperationQueue.
         */
        public MqttClientConnection getConnection() {
            return this.connection;
        }

        /**
         * Sets the maximum size of the operation queue in the MqttOperationQueue.
         * Default operation queue size is 10.
         *
         * If the number of operations exceeds this number, then the queue will be adjusted
         * based on the queueLimitBehavior.
         * @param queueLimitSize The maximum size of the operation queue.
         * @return The MqttOperationQueueBuilder.
         */
        public MqttOperationQueueBuilder withQueueLimitSize(int queueLimitSize) {
            this.queueLimitSize = queueLimitSize;
            return this;
        }
        /**
         * Returns the maximum size of the operation queue in the MqttOperationQueue.
         * @return The maximum size of the operation queue in the MqttOperationQueue.
         */
        public int getQueueLimitSize() {
            return this.queueLimitSize;
        }

        /**
         * Sets how the MqttOperationQueue will behave when the operation queue is full but a
         * new operation is requested to be added to the queue.
         * The default is DROP_BACK, which will drop the newest (but last to be executed) operation at the back of the queue.
         * @param queueLimitBehavior How the MqttOperationQueue will behave when the operation queue is full.
         * @return The MqttOperationQueueBuilder.
         */
        public MqttOperationQueueBuilder withQueueLimitBehavior(QueueLimitBehavior queueLimitBehavior) {
            this.queueLimitBehavior = queueLimitBehavior;
            return this;
        }

        /**
         * Returns how the MqttOperationQueue will behave when the operation queue is full.
         * @return How the MqttOperationQueue will behave when the operation queue is full.
         */
        public QueueLimitBehavior getQueueLimitBehavior() {
            return this.queueLimitBehavior;
        }

        /**
         * Sets how the MqttOperationQueue will behave when inserting a new operation into the queue.
         * The default is INSERT_BACK, which will add the new operation to the back (last to be executed) of the queue.
         * @param queueInsertBehavior How the MqttOperationQueue will behave when inserting a new operation into the queue.
         * @return The MqttOperationQueueBuilder.
         */
        public MqttOperationQueueBuilder withQueueInsertBehavior(QueueInsertBehavior queueInsertBehavior) {
            this.queueInsertBehavior = queueInsertBehavior;
            return this;
        }

        /**
         * Returns how the MqttOperationQueue will behave when inserting a new operation into the queue.
         * @return How the MqttOperationQueue will behave when inserting a new operation into the queue.
         */
        public QueueInsertBehavior getQueueInsertBehavior() {
            return this.queueInsertBehavior;
        }

        /**
         * Sets the maximum number of incomplete operations that the MQTT connection can have before the
         * MqttOperationQueue will wait for them to be complete. Incomplete operations are those that have been
         * sent to the MqttClientConnection but have not been fully processed and responded to from the MQTT server/broker.
         *
         * Once the maximum number of incomplete operations is met, the MqttOperationQueue will wait until the number
         * of incomplete operations is below the set maximum.
         *
         * Default is set to 1. Set to 0 for no limit.
         *
         * @param incompleteLimit The maximum number of incomplete operations before waiting.
         * @return
         */
        public MqttOperationQueueBuilder withIncompleteLimit(int incompleteLimit) {
            this.incompleteLimit = incompleteLimit;
            return this;
        }

        /**
         * Returns the maximum number of incomplete operations the MqttOperationQueue will check for.
         * @return The maximum number of incomplete operations the MqttOperationQueue will check for.
         */
        public int getIncompleteLimit() {
            return this.incompleteLimit;
        }

        /**
         * Sets the maximum number of inflight operations that
         *
         * Sets the maximum number of inflight operations that the MQTT connection can have before the
         * MqttOperationQueue will wait for them to be complete. inflight operations are those that have been
         * sent to the MqttClientConnection and sent out to the MQTT server/broker, but an acknowledgement from
         * the MQTT server/broker has not yet been received.
         *
         * Once the maximum number of inflight operations is met, the MqttOperationQueue will wait until the number
         * of inflight operations is below the set maximum.
         *
         * Default is set to 1. Set to 0 for no limit.
         *
         * @param inflightLimit The maximum number of inflight operations before waiting.
         * @return
         */
        public MqttOperationQueueBuilder withInflightLimit(int inflightLimit) {
            this.inflightLimit = inflightLimit;
            return this;
        }

        /**
         * Returns the maximum number of inflight operations the MqttOperationQueue will check for.
         * @return The maximum number of inflight operations the MqttOperationQueue will check for.
         */
        public int getInflightLimit() {
            return this.inflightLimit;
        }

        /**
         * Sets the callbacks that the MqttOperationQueue will invoke when certain conditions are met.
         * See QueueCallbacks for more information on the callbacks, when they are invoked, and what data they contain.
         * @param queueCallbacks the callbacks that the MqttOperationQueue will invoke when certain conditions are met.
         * @return The MqttOperationQueueBuilder.
         */
        public MqttOperationQueueBuilder withQueueCallbacks(QueueCallbacks queueCallbacks) {
            this.queueCallbacks = queueCallbacks;
            return this;
        }

        /**
         * Returns the callbacks that the MqttOperationQueue will invoke when certain conditions are met.
         * @return The callbacks that the MqttOperationQueue will invoke when certain conditions are met.
         */
        public QueueCallbacks getQueueCallbacks() {
            return this.queueCallbacks;
        }

        /**
         * Sets the interval, in milliseconds, that the MqttOperationQueue will wait before checking the queue and (possibly)
         * processing an operation based on the statistics and state of the MqttClientConnection assigned to the MqttOperationQueue.
         * The default is every second.
         * @param queueLoopTime The interval, in milliseconds, that the MqttOperationQueue will wait before checking the queue.
         * @return The MqttOperationQueueBuilder.
         */
        public MqttOperationQueueBuilder withQueueLoopTime(long queueLoopTimeMs) {
            this.queueLoopTimeMs = queueLoopTimeMs;
            return this;
        }

        /**
         * Returns the interval, in milliseconds, that the MqttOperationQueue will wait before checking the queue.
         * @return The interval, in milliseconds, that the MqttOperationQueue will wait before checking the queue.
         */
        public long getQueueLoopTime() {
            return this.queueLoopTimeMs;
        }

        /**
         * Sets whether the MqttOperationQueue will print logging statements to help debug and determine how the
         * MqttOperationQueue is functioning.
         * @param enableLogging Whether the MqttOperationQueue will print logging statements.
         * @return The MqttOperationQueueBuilder.
         */
        public MqttOperationQueueBuilder withEnableLogging(boolean enableLogging) {
            this.enableLogging = enableLogging;
            return this;
        }

        /**
         * Returns whether the MqttOperationQueue will print logging statements.
         * @return Whether the MqttOperationQueue will print logging statements.
         */
        public boolean getEnableLogging() {
            return this.enableLogging;
        }

        /**
         * Returns a new MqttOperationQueue with the options set in the builder
         * @return A new configured MqttOperationQueue
         */
        public MqttOperationQueue build() {
            return new MqttOperationQueue(this);
        }
    }
}
