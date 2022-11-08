/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package sba_sample;

import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.CrtResource;
import software.amazon.awssdk.crt.CrtRuntimeException;
import software.amazon.awssdk.crt.io.ClientBootstrap;
import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt.MqttClientConnectionEvents;
import software.amazon.awssdk.iot.iotjobs.model.RejectedError;

import java.util.concurrent.ExecutionException;

import utils.commandlineutils.CommandLineUtils;

// ==================
import software.amazon.awssdk.awstest.CauseServiceErrorResponseHandler;
import software.amazon.awssdk.awstest.CauseStreamServiceToErrorResponseHandler;
import software.amazon.awssdk.awstest.EchoMessageResponseHandler;
import software.amazon.awssdk.awstest.EchoStreamMessagesResponseHandler;
import software.amazon.awssdk.awstest.EchoTestRPC;
import software.amazon.awssdk.awstest.EchoTestRPCServiceModel;
import software.amazon.awssdk.awstest.model.CauseServiceErrorRequest;
import software.amazon.awssdk.awstest.model.EchoMessageRequest;
import software.amazon.awssdk.awstest.model.EchoMessageResponse;
import software.amazon.awssdk.awstest.model.EchoStreamingMessage;
import software.amazon.awssdk.awstest.model.EchoStreamingRequest;
import software.amazon.awssdk.awstest.model.FruitEnum;
import software.amazon.awssdk.awstest.model.MessageData;
import software.amazon.awssdk.awstest.model.Pair;
import software.amazon.awssdk.awstest.model.ServiceError;
import software.amazon.awssdk.crt.Log;
import software.amazon.awssdk.eventstreamrpc.echotest.EchoTestServiceRunner;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamOperationError;

import java.time.Instant;
import java.time.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import software.amazon.awssdk.eventstreamrpc.*;
// ==================

public class SBA_Sample {

    public static void PrintExceptionAndExit(Exception ex, String msg) {
        if (msg != null) {
            System.out.println("ERROR: " + msg + " " + ex.toString());
        } else {
            System.out.println("ERROR: Exception found! Exception: " + ex.toString());
        }
        System.exit(1);
    }
    static CommandLineUtils cmdUtils;
    static int iterationCount = 0;

    public static void main(String[] args) {
        cmdUtils = new CommandLineUtils();
        cmdUtils.registerProgramName("SBA_Sample");
        cmdUtils.registerCommand("iterations", "<int>", "The name of iterations to run (default=10000)");
        cmdUtils.registerCommand("thread_size", "<int>", "The maximum number of threads (default=1)");
        cmdUtils.registerCommand("task_factor", "<int>", "The number of tasks run in parallel (default=10)");
        cmdUtils.registerCommand("task_length", "<int>", "The amount of times a task does work (default=6000)");
        cmdUtils.registerCommand("task_delay", "<int>", "The amount of time (in ms) to sleep before repeating a tasks' implementation (default=5)");
        cmdUtils.registerCommand("max_time", "<int>", "The maximum amount of time (in seconds) the sample can run for (default=300 or 5 minutes)");
        cmdUtils.registerCommand("payload_size", "<int>", "The size of the payload sent in kb (default=256)");
        cmdUtils.sendArguments(args);

        int iterations = Integer.parseInt(cmdUtils.getCommandOrDefault("iterations", String.valueOf(10000)));
        int thread_size = Integer.parseInt(cmdUtils.getCommandOrDefault("thread_size", String.valueOf(1)));
        int task_factor = Integer.parseInt(cmdUtils.getCommandOrDefault("task_factor", String.valueOf(10)));
        int task_length = Integer.parseInt(cmdUtils.getCommandOrDefault("task_length", String.valueOf(6000)));
        long task_delay = Long.parseLong(cmdUtils.getCommandOrDefault("task_delay", String.valueOf(1)));
        long max_time = Long.parseLong(cmdUtils.getCommandOrDefault("max_time", String.valueOf(500)));
        int payload_size = Integer.parseInt(cmdUtils.getCommandOrDefault("payload_size", String.valueOf(256)));

        System.out.println("Starting sample...");
        try {
            testLongRunningServerOperations(iterations, thread_size, task_factor, task_length, task_delay, max_time, payload_size);
        } catch (Exception e) {
            PrintExceptionAndExit(e, "Running sample itself!");
        }
        System.out.println("Complete!");
        System.exit(0);
    }

    // ======================================

    static final BiConsumer<EchoTestRPC, MessageData> DO_ECHO_FN = (client, data) -> {
        final EchoMessageRequest request = new EchoMessageRequest().setMessage(data);
        final EchoMessageResponseHandler responseHandler = client.echoMessage(request, Optional.empty());
        try {
            responseHandler.getResponse().get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            PrintExceptionAndExit(e, "Running DO_ECHO_FN!");
        }
    };

    public static void testLongRunningServerOperations(
        int iterations, int thread_size, int task_factor, int task_length, long task_delay, long max_time, int payload_size) throws Exception {

        Instant startTime = java.time.Instant.now();

        final int numIterations = iterations;
        final int threadPoolSize = thread_size; //max threads, since tasks are IO intense, doesn't need to be large
        final int parallelTaskMultiplyFactor = task_factor; //however many tasks to run in parallel
        final int taskLengthMultiplyFactor = task_length; //whatever work each task does (very small), do it this many times within a single run with a short sleep in between
        final long taskRepeatSleepDelayMs = task_delay; //time to sleep before repeating a tasks' impl

        final ArrayList<BiConsumer<EventStreamRPCConnection, EchoTestRPC>> tasks = new ArrayList<>();
        final ExecutorService service = Executors.newFixedThreadPool(threadPoolSize);

        final char[] characters = new char[payload_size * 1024];
        Arrays.fill(characters, 'f');

        // ONLY have a single task to send large payloads!
        tasks.add((connection, client) -> {
            final MessageData data = new MessageData();
            data.setStringMessage(new String(characters));
            DO_ECHO_FN.accept(client, data);
        });

        EchoTestServiceRunner.runLocalEchoTestServerClientLoopUnixDomain(
                CRT.getOSIdentifier().equals("windows") ? "\\\\.\\pipe\\TestP-" + UUID.randomUUID() : "/tmp/ipc.sock",
                (connection, client) -> {
                    final Collection<Future<?>> taskFutures = new LinkedList<>();
                    for (int i = 0; i < parallelTaskMultiplyFactor; ++i) {
                        // Spread tasks evenly across threads (if threads are being used)
                        taskFutures.addAll(tasks.stream()
                        .map(task -> service.submit(()-> {
                            for (int taskExecIndx = 0; taskExecIndx < taskLengthMultiplyFactor; ++taskExecIndx) {
                                task.accept(connection, client);
                                try {
                                    Thread.sleep(taskRepeatSleepDelayMs);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }))
                        .collect(Collectors.toList()));

                        // Have we hit the time limit? If so, then stop
                        Instant currentTime = java.time.Instant.now();
                        Duration betweenTime = java.time.Duration.between(startTime, currentTime);
                        if (betweenTime.getSeconds() >= max_time) {
                            System.out.println("\n\nMAXIMUM TIME PASSED! The sample run time (in seconds) is: " + (betweenTime.getSeconds()) + "\n\n");
                            System.exit(0);
                        }
            }
            taskFutures.forEach(task -> {
                try {
                    // No timeout - we just want to run the massive amount of operations/iterations
                    task.get();
                } catch (InterruptedException | ExecutionException e) {
                    PrintExceptionAndExit(e, "Running taskFutures.forEach!");
                }
            });
            iterationCount += 1;
            System.out.println("Finished iteration: " + iterationCount);
        }, numIterations);
        CrtResource.waitForNoResources();
    }
}
