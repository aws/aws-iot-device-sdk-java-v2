/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package jobs;

import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.CrtResource;
import software.amazon.awssdk.crt.CrtRuntimeException;
import software.amazon.awssdk.crt.mqtt.QualityOfService;
import software.amazon.awssdk.crt.mqtt5.packets.*;
import software.amazon.awssdk.crt.mqtt5.*;
import software.amazon.awssdk.crt.mqtt5.Mqtt5ClientOptions;
import software.amazon.awssdk.crt.mqtt5.Mqtt5Client;
import software.amazon.awssdk.iot.AwsIotMqtt5ClientBuilder;
import software.amazon.awssdk.iot.iotjobs.IotJobsClient;
import software.amazon.awssdk.iot.iotjobs.model.DescribeJobExecutionRequest;
import software.amazon.awssdk.iot.iotjobs.model.DescribeJobExecutionResponse;
import software.amazon.awssdk.iot.iotjobs.model.DescribeJobExecutionSubscriptionRequest;
import software.amazon.awssdk.iot.iotjobs.model.GetPendingJobExecutionsRequest;
import software.amazon.awssdk.iot.iotjobs.model.GetPendingJobExecutionsResponse;
import software.amazon.awssdk.iot.iotjobs.model.GetPendingJobExecutionsSubscriptionRequest;
import software.amazon.awssdk.iot.iotjobs.model.JobExecutionSummary;
import software.amazon.awssdk.iot.iotjobs.model.JobStatus;
import software.amazon.awssdk.iot.iotjobs.model.RejectedError;
import software.amazon.awssdk.iot.iotjobs.model.StartNextJobExecutionResponse;
import software.amazon.awssdk.iot.iotjobs.model.StartNextPendingJobExecutionRequest;
import software.amazon.awssdk.iot.iotjobs.model.StartNextPendingJobExecutionSubscriptionRequest;
import software.amazon.awssdk.iot.iotjobs.model.UpdateJobExecutionRequest;
import software.amazon.awssdk.iot.iotjobs.model.UpdateJobExecutionSubscriptionRequest;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import utils.commandlineutils.CommandLineUtils;

public class Mqtt5JobsSample {

    // When run normally, we want to check for jobs and process them
    // When run from CI, we want to just check for jobs
    static String ciPropValue = System.getProperty("aws.crt.ci");
    static boolean isCI = ciPropValue != null && Boolean.valueOf(ciPropValue);

    static CompletableFuture<Void> gotResponse;
    static List<String> availableJobs = new LinkedList<>();
    static String currentJobId;
    static long currentExecutionNumber = 0;
    static int currentVersionNumber = 0;

    static CommandLineUtils cmdUtils;

    static final class SampleLifecycleEvents implements Mqtt5ClientOptions.LifecycleEvents {
        CompletableFuture<Void> connectedFuture = new CompletableFuture<>();
        CompletableFuture<Void> stoppedFuture = new CompletableFuture<>();

        @Override
        public void onAttemptingConnect(Mqtt5Client client, OnAttemptingConnectReturn onAttemptingConnectReturn) {
            System.out.println("Mqtt5 Client: Attempting connection...");
        }

        @Override
        public void onConnectionSuccess(Mqtt5Client client, OnConnectionSuccessReturn onConnectionSuccessReturn) {
            System.out.println("Mqtt5 Client: Connection success, client ID: "
                    + onConnectionSuccessReturn.getNegotiatedSettings().getAssignedClientID());
            System.out.println("Connected to "
                    + (!onConnectionSuccessReturn.getConnAckPacket().getSessionPresent() ? "new" : "existing")
                    + " session!");
            connectedFuture.complete(null);
        }

        @Override
        public void onConnectionFailure(Mqtt5Client client, OnConnectionFailureReturn onConnectionFailureReturn) {
            String errorString = CRT.awsErrorString(onConnectionFailureReturn.getErrorCode());
            System.out.println("Mqtt5 Client: Connection failed with error: " + errorString);
            connectedFuture.completeExceptionally(new Exception("Could not connect: " + errorString));
        }

        @Override
        public void onDisconnection(Mqtt5Client client, OnDisconnectionReturn onDisconnectionReturn) {
            System.out.println("Mqtt5 Client: Disconnected");
            DisconnectPacket disconnectPacket = onDisconnectionReturn.getDisconnectPacket();
            if (disconnectPacket != null) {
                System.out.println("\tDisconnection packet code: " + disconnectPacket.getReasonCode());
                System.out.println("\tDisconnection packet reason: " + disconnectPacket.getReasonString());
            }
        }

        @Override
        public void onStopped(Mqtt5Client client, OnStoppedReturn onStoppedReturn) {
            System.out.println("Mqtt5 Client: Stopped");
            stoppedFuture.complete(null);
        }
    }

    static void onRejectedError(RejectedError error) {
        System.out.println("Request rejected: " + error.code.toString() + ": " + error.message);
        System.exit(1);
    }

    static void onGetPendingJobExecutionsAccepted(GetPendingJobExecutionsResponse response) {
        System.out.println(
                "Pending Jobs: " + (response.queuedJobs.size() + response.inProgressJobs.size() == 0 ? "none" : ""));
        for (JobExecutionSummary job : response.inProgressJobs) {
            availableJobs.add(job.jobId);
            System.out.println("  In Progress: " + job.jobId + " @  " + job.lastUpdatedAt.toString());
        }
        for (JobExecutionSummary job : response.queuedJobs) {
            availableJobs.add(job.jobId);
            System.out.println("  " + job.jobId + " @ " + job.lastUpdatedAt.toString());
        }
        gotResponse.complete(null);
    }

    static void onDescribeJobExecutionAccepted(DescribeJobExecutionResponse response) {
        System.out
                .println("Describe Job: " + response.execution.jobId + " version: " + response.execution.versionNumber);
        if (response.execution.jobDocument != null) {
            response.execution.jobDocument.forEach((key, value) -> {
                System.out.println("  " + key + ": " + value);
            });
        }
        gotResponse.complete(null);
    }

    static void onStartNextPendingJobExecutionAccepted(StartNextJobExecutionResponse response) {
        System.out.println("Start Job: " + response.execution.jobId);
        currentJobId = response.execution.jobId;
        currentExecutionNumber = response.execution.executionNumber;
        currentVersionNumber = response.execution.versionNumber;
        gotResponse.complete(null);
    }

    public static void main(String[] args) {

        /**
         * cmdData is the arguments/input from the command line placed into a single
         * struct for
         * use in this sample. This handles all of the command line parsing, validating,
         * etc.
         * See the Utils/CommandLineUtils for more information.
         */
        CommandLineUtils.SampleCommandLineData cmdData = CommandLineUtils.getInputForIoTSample("Jobs", args);

        try {

            /**
             * Create the MQTT5 client from the builder
             */
            SampleLifecycleEvents lifecycleEvents = new SampleLifecycleEvents();

            AwsIotMqtt5ClientBuilder builder = AwsIotMqtt5ClientBuilder.newDirectMqttBuilderWithMtlsFromPath(
                    cmdData.input_endpoint, cmdData.input_cert, cmdData.input_key);
            ConnectPacket.ConnectPacketBuilder connectProperties = new ConnectPacket.ConnectPacketBuilder();
            connectProperties.withClientId(cmdData.input_clientId);
            builder.withConnectProperties(connectProperties);
            builder.withLifeCycleEvents(lifecycleEvents);
            Mqtt5Client client = builder.build();
            builder.close();

            // Create the job client, IotJobsClient throws MqttException
            IotJobsClient jobs = new IotJobsClient(client);

            // Connect
            client.start();
            try {
                lifecycleEvents.connectedFuture.get(60, TimeUnit.SECONDS);
            } catch (Exception ex) {
                throw new RuntimeException("Exception occurred during connect", ex);
            }

            {
                gotResponse = new CompletableFuture<>();
                GetPendingJobExecutionsSubscriptionRequest subscriptionRequest = new GetPendingJobExecutionsSubscriptionRequest();
                subscriptionRequest.thingName = cmdData.input_thingName;
                CompletableFuture<Integer> subscribed = jobs.SubscribeToGetPendingJobExecutionsAccepted(
                        subscriptionRequest,
                        QualityOfService.AT_LEAST_ONCE,
                        Mqtt5JobsSample::onGetPendingJobExecutionsAccepted);
                try {
                    subscribed.get();
                    System.out.println("Subscribed to GetPendingJobExecutionsAccepted");
                } catch (Exception ex) {
                    throw new RuntimeException("Failed to subscribe to GetPendingJobExecutions", ex);
                }

                subscribed = jobs.SubscribeToGetPendingJobExecutionsRejected(
                        subscriptionRequest,
                        QualityOfService.AT_LEAST_ONCE,
                        Mqtt5JobsSample::onRejectedError);
                subscribed.get();
                System.out.println("Subscribed to GetPendingJobExecutionsRejected");

                GetPendingJobExecutionsRequest publishRequest = new GetPendingJobExecutionsRequest();
                publishRequest.thingName = cmdData.input_thingName;
                CompletableFuture<Integer> published = jobs.PublishGetPendingJobExecutions(
                        publishRequest,
                        QualityOfService.AT_LEAST_ONCE);
                try {
                    published.get();
                    gotResponse.get();
                } catch (Exception ex) {
                    throw new RuntimeException("Exception occurred during publish", ex);
                }
            }

            if (availableJobs.isEmpty()) {
                System.out.println("No jobs queued, no further work to do");

                // If sample is running in CI, there should be at least one job
                if (isCI == true) {
                    throw new RuntimeException("No jobs queued in CI! At least one job should be queued!");
                }
            }

            for (String jobId : availableJobs) {
                gotResponse = new CompletableFuture<>();
                DescribeJobExecutionSubscriptionRequest subscriptionRequest = new DescribeJobExecutionSubscriptionRequest();
                subscriptionRequest.thingName = cmdData.input_thingName;
                subscriptionRequest.jobId = jobId;
                jobs.SubscribeToDescribeJobExecutionAccepted(
                        subscriptionRequest,
                        QualityOfService.AT_LEAST_ONCE,
                        Mqtt5JobsSample::onDescribeJobExecutionAccepted);
                jobs.SubscribeToDescribeJobExecutionRejected(
                        subscriptionRequest,
                        QualityOfService.AT_LEAST_ONCE,
                        Mqtt5JobsSample::onRejectedError);

                DescribeJobExecutionRequest publishRequest = new DescribeJobExecutionRequest();
                publishRequest.thingName = cmdData.input_thingName;
                publishRequest.jobId = jobId;
                publishRequest.includeJobDocument = true;
                publishRequest.executionNumber = 1L;
                jobs.PublishDescribeJobExecution(publishRequest, QualityOfService.AT_LEAST_ONCE);
                gotResponse.get();
            }

            // If sample is not running in CI, then process the available jobs.
            if (isCI == false) {
                for (int jobIdx = 0; jobIdx < availableJobs.size(); ++jobIdx) {
                    {
                        gotResponse = new CompletableFuture<>();

                        // Start the next pending job
                        StartNextPendingJobExecutionSubscriptionRequest subscriptionRequest = new StartNextPendingJobExecutionSubscriptionRequest();
                        subscriptionRequest.thingName = cmdData.input_thingName;

                        jobs.SubscribeToStartNextPendingJobExecutionAccepted(
                                subscriptionRequest,
                                QualityOfService.AT_LEAST_ONCE,
                                Mqtt5JobsSample::onStartNextPendingJobExecutionAccepted);
                        jobs.SubscribeToStartNextPendingJobExecutionRejected(
                                subscriptionRequest,
                                QualityOfService.AT_LEAST_ONCE,
                                Mqtt5JobsSample::onRejectedError);

                        StartNextPendingJobExecutionRequest publishRequest = new StartNextPendingJobExecutionRequest();
                        publishRequest.thingName = cmdData.input_thingName;
                        publishRequest.stepTimeoutInMinutes = 15L;
                        jobs.PublishStartNextPendingJobExecution(publishRequest, QualityOfService.AT_LEAST_ONCE);

                        gotResponse.get();
                    }

                    {
                        // Update the service to let it know we're executing
                        gotResponse = new CompletableFuture<>();

                        UpdateJobExecutionSubscriptionRequest subscriptionRequest = new UpdateJobExecutionSubscriptionRequest();
                        subscriptionRequest.thingName = cmdData.input_thingName;
                        subscriptionRequest.jobId = currentJobId;
                        jobs.SubscribeToUpdateJobExecutionAccepted(
                                subscriptionRequest,
                                QualityOfService.AT_LEAST_ONCE,
                                (response) -> {
                                    System.out.println("Marked job " + currentJobId + " IN_PROGRESS");
                                    gotResponse.complete(null);
                                });
                        jobs.SubscribeToUpdateJobExecutionRejected(
                                subscriptionRequest,
                                QualityOfService.AT_LEAST_ONCE,
                                Mqtt5JobsSample::onRejectedError);

                        UpdateJobExecutionRequest publishRequest = new UpdateJobExecutionRequest();
                        publishRequest.thingName = cmdData.input_thingName;
                        publishRequest.jobId = currentJobId;
                        publishRequest.executionNumber = currentExecutionNumber;
                        publishRequest.status = JobStatus.IN_PROGRESS;
                        publishRequest.expectedVersion = currentVersionNumber++;
                        jobs.PublishUpdateJobExecution(publishRequest, QualityOfService.AT_LEAST_ONCE);

                        gotResponse.get();
                    }

                    // Fake doing something
                    Thread.sleep(1000);

                    {
                        // Update the service to let it know we're done
                        gotResponse = new CompletableFuture<>();

                        UpdateJobExecutionSubscriptionRequest subscriptionRequest = new UpdateJobExecutionSubscriptionRequest();
                        subscriptionRequest.thingName = cmdData.input_thingName;
                        subscriptionRequest.jobId = currentJobId;
                        jobs.SubscribeToUpdateJobExecutionAccepted(
                                subscriptionRequest,
                                QualityOfService.AT_LEAST_ONCE,
                                (response) -> {
                                    System.out.println("Marked job " + currentJobId + " SUCCEEDED");
                                    gotResponse.complete(null);
                                });
                        jobs.SubscribeToUpdateJobExecutionRejected(
                                subscriptionRequest,
                                QualityOfService.AT_LEAST_ONCE,
                                Mqtt5JobsSample::onRejectedError);

                        UpdateJobExecutionRequest publishRequest = new UpdateJobExecutionRequest();
                        publishRequest.thingName = cmdData.input_thingName;
                        publishRequest.jobId = currentJobId;
                        publishRequest.executionNumber = currentExecutionNumber;
                        publishRequest.status = JobStatus.SUCCEEDED;
                        publishRequest.expectedVersion = currentVersionNumber++;
                        jobs.PublishUpdateJobExecution(publishRequest, QualityOfService.AT_LEAST_ONCE);

                        gotResponse.get();
                    }
                }
            }

            // Disconnect
            client.stop(null);
            try {
                lifecycleEvents.stoppedFuture.get(60, TimeUnit.SECONDS);
            } catch (Exception ex) {
                System.out.println("Exception encountered: " + ex.toString());
                System.exit(1);
            }

            /* Close the client to free memory */
            client.close();
            jobs.close();

        } catch (CrtRuntimeException | InterruptedException | ExecutionException ex) {
            System.out.println("Exception encountered: " + ex.toString());
        }

        CrtResource.waitForNoResources();
        System.out.println("Complete!");
    }
}