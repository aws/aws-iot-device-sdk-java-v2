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
import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.CrtResource;
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
import java.util.concurrent.ExecutionException;
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

    static CommandLineUtils cmdUtils;

    public static void main(String[] args) {
        cmdUtils = new CommandLineUtils();
        cmdUtils.registerProgramName("SBA_Sample");
        cmdUtils.registerCommand("iterations", "<int>", "The name of iterations to run (default=6000)");
        cmdUtils.registerCommand("thread_size", "<int>", "The maximum number of threads (default=8)");
        cmdUtils.registerCommand("task_factor", "<int>", "The number of tasks run in parallel (default=10)");
        cmdUtils.registerCommand("task_length", "<int>", "The amount of times a task does work (default=100)");
        cmdUtils.registerCommand("task_delay", "<int>", "The amount of time (in ms) to sleep before repeating a tasks' implementation (default=5)");
        cmdUtils.registerCommand("max_time", "<int>", "The maximum amount of time (in seconds) the sample can run for (default=300 or 5 minutes)");
        cmdUtils.registerCommand("payload_size", "<int>", "The size of the payload sent in kb (default=256)");
        cmdUtils.sendArguments(args);

        int iterations = Integer.parseInt(cmdUtils.getCommandOrDefault("iterations", String.valueOf(6000)));
        int thread_size = Integer.parseInt(cmdUtils.getCommandOrDefault("thread_size", String.valueOf(8)));
        int task_factor = Integer.parseInt(cmdUtils.getCommandOrDefault("task_factor", String.valueOf(10)));
        int task_length = Integer.parseInt(cmdUtils.getCommandOrDefault("task_length", String.valueOf(100)));
        long task_delay = Long.parseLong(cmdUtils.getCommandOrDefault("task_delay", String.valueOf(5)));
        long max_time = Long.parseLong(cmdUtils.getCommandOrDefault("max_time", String.valueOf(500)));
        int payload_size = Integer.parseInt(cmdUtils.getCommandOrDefault("payload_size", String.valueOf(256)));

        System.out.println("Starting sample...");
        try {
            testLongRunningServerOperations(iterations, thread_size, task_factor, task_length, task_delay, max_time, payload_size);
        } catch (Exception e) {
            System.out.println("ERROR - something went wrong running the sample itself! Exception found!");
            System.out.println(e);
            System.exit(1);
        }
        System.out.println("Complete!");
        System.exit(0);
    }

    // ======================================

    static final BiConsumer<EchoTestRPC, MessageData> DO_ECHO_FN = (client, data) -> {
        final EchoMessageRequest request = new EchoMessageRequest();
        request.setMessage(data);
        final EchoMessageResponseHandler responseHandler = client.echoMessage(request, Optional.empty());
        EchoMessageResponse response = null;
        try {
            response = responseHandler.getResponse().get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            System.out.println("ERROR - something went wrong! Exception found!");
            System.out.println(e);
            System.exit(1);
        }
    };

    public static void futureCausesOperationError(final CompletableFuture<?> future, Class<? extends EventStreamOperationError> clazz, String code) {
        try {
            future.get(60, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            final Throwable t = e.getCause();
            if (t == null) {
                System.out.println("ExecutionException thrown has no CausedBy exception. Something else went wrong with future completion");
                System.exit(1);
            } else if(!clazz.isInstance(t)) {
                System.out.println("ExecutionException thrown has unexpected caused type: " + t);
                System.exit(1);
            } else {
                // Do nothing!
            }
        } catch (InterruptedException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

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

        tasks.add((connection, client) -> {
            final MessageData data = new MessageData();
            // payload_size sized string
            data.setStringMessage(new String(characters));
            DO_ECHO_FN.accept(client, data);
        });

        tasks.add((connection, client) -> {
            final CauseServiceErrorResponseHandler responseHandler = client.causeServiceError(new CauseServiceErrorRequest(), Optional.empty());
            futureCausesOperationError(responseHandler.getResponse(), ServiceError.class, "ServiceError");
        });
        tasks.add((connection, client) -> {
            final CompletableFuture<Throwable> exceptionReceivedFuture = new CompletableFuture<>();
            final CauseStreamServiceToErrorResponseHandler streamErrorResponseHandler = client.causeStreamServiceToError(EchoStreamingRequest.VOID, Optional.of(new StreamResponseHandler<EchoStreamingMessage>() {
                @Override
                public void onStreamEvent(EchoStreamingMessage streamEvent) {
                    exceptionReceivedFuture.completeExceptionally(new RuntimeException("Stream event received when expecting error!"));
                }

                @Override
                public boolean onStreamError(Throwable error) {
                    //this is normal, but we are looking for a specific one
                    exceptionReceivedFuture.complete(error);
                    return true;
                }

                @Override
                public void onStreamClosed() {
                    if (!exceptionReceivedFuture.isDone()) {
                        exceptionReceivedFuture.completeExceptionally(new RuntimeException("Stream closed before exception thrown!"));
                    }
                }
            }));

            try {
                final EchoStreamingMessage msg = new EchoStreamingMessage();
                final MessageData data = new MessageData();
                data.setStringMessage("basicStringMessage");
                msg.setStreamMessage(data);
                streamErrorResponseHandler.sendStreamEvent(msg);   //sends message, exception should be is the response
                exceptionReceivedFuture.get(10, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                System.out.println("ERROR - something went wrong! Exception found!");
                System.out.println(e);
                System.exit(1);
            }
        });
        int count[] = { 0 };
        EchoTestServiceRunner.runLocalEchoTestServerClientLoopUnixDomain(
                CRT.getOSIdentifier().equals("windows") ? "\\\\.\\pipe\\TestP-" + UUID.randomUUID() : "/tmp/ipc.sock",
                (connection, client) -> {
                    final Collection<Future<?>> taskFutures = new LinkedList<>();

                    for (int i = 0; i < parallelTaskMultiplyFactor; ++i) {  //multiply the tasks evenly
                        taskFutures.addAll(tasks.stream()
                        .map(task -> service.submit(()-> {
                            for (int taskExecIndx = 0; taskExecIndx < taskLengthMultiplyFactor; ++taskExecIndx) {
                                task.accept(connection, client);
                                try {
                                    Thread.sleep(taskRepeatSleepDelayMs);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                                System.out.println("Task repeat...");
                            }
                        }))
                        .collect(Collectors.toList()));

                        // Have we hit the time limit?
                        Instant currentTime = java.time.Instant.now();
                        Duration betweenTime = java.time.Duration.between(startTime, currentTime);
                        if (betweenTime.getSeconds() >= max_time) {
                            System.out.println("\n\nMAXIMUM TIME PASSED! The sample run time (in seconds) is: " + (betweenTime.getSeconds()));
                            System.out.println("\n\n");
                            System.exit(0);
                        }
            }

            taskFutures.forEach(task -> {
                try {
                    task.get(10, TimeUnit.SECONDS);
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    System.out.println("ERROR - something went wrong! Exception found!");
                    System.out.println(e);
                    System.exit(1);
                }
            });
            System.out.println("ALL TASKS finished an ITERATION: " + ++count[0]);
        }, numIterations);
        CrtResource.waitForNoResources();
    }

}
