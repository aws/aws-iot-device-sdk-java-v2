/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package jobs;

import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.CrtResource;
import software.amazon.awssdk.crt.CrtRuntimeException;
import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt.MqttClientConnectionEvents;
import software.amazon.awssdk.crt.mqtt.QualityOfService;
import software.amazon.awssdk.iot.AwsIotMqttConnectionBuilder;
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

import utils.commandlineutils.CommandLineUtils;

public class JobsSample {

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

    static void onRejectedError(RejectedError error) {
        System.out.println("Request rejected: " + error.code.toString() + ": " + error.message);
        System.exit(1);
    }

    static void onGetPendingJobExecutionsAccepted(GetPendingJobExecutionsResponse response) {
        System.out.println("Pending Jobs: " + (response.queuedJobs.size() + response.inProgressJobs.size() == 0 ? "none" : ""));
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
        System.out.println("Describe Job: " + response.execution.jobId + " version: " + response.execution.versionNumber);
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
         * cmdData is the arguments/input from the command line placed into a single struct for
         * use in this sample. This handles all of the command line parsing, validating, etc.
         * See the Utils/CommandLineUtils for more information.
         */
        CommandLineUtils.SampleCommandLineData cmdData = CommandLineUtils.getInputForIoTSample("Jobs", args);

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

            /**
             * Create the MQTT connection from the builder
             */
            AwsIotMqttConnectionBuilder builder = AwsIotMqttConnectionBuilder.newMtlsBuilderFromPath(cmdData.input_cert, cmdData.input_key);
            if (cmdData.input_ca != "") {
                builder.withCertificateAuthorityFromPath(null, cmdData.input_ca);
            }
            builder.withConnectionEventCallbacks(callbacks)
                .withClientId(cmdData.input_clientId)
                .withEndpoint(cmdData.input_endpoint)
                .withPort((short)cmdData.input_port)
                .withCleanSession(true)
                .withProtocolOperationTimeoutMs(60000);
            MqttClientConnection connection = builder.build();
            builder.close();

            IotJobsClient jobs = new IotJobsClient(connection);

            CompletableFuture<Boolean> connected = connection.connect();
            try {
                boolean sessionPresent = connected.get();
                System.out.println("Connected to " + (!sessionPresent ? "new" : "existing") + " session!");
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
                        JobsSample::onGetPendingJobExecutionsAccepted);
                try {
                    subscribed.get();
                    System.out.println("Subscribed to GetPendingJobExecutionsAccepted");
                } catch (Exception ex) {
                    throw new RuntimeException("Failed to subscribe to GetPendingJobExecutions", ex);
                }

                subscribed = jobs.SubscribeToGetPendingJobExecutionsRejected(
                        subscriptionRequest,
                        QualityOfService.AT_LEAST_ONCE,
                        JobsSample::onRejectedError);
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
                CompletableFuture<Integer> subscribed = jobs.SubscribeToDescribeJobExecutionAccepted(
                        subscriptionRequest,
                        QualityOfService.AT_LEAST_ONCE,
                        JobsSample::onDescribeJobExecutionAccepted);
                subscribed.get();
                subscribed = jobs.SubscribeToDescribeJobExecutionRejected(
                        subscriptionRequest,
                        QualityOfService.AT_LEAST_ONCE,
                        JobsSample::onRejectedError);
                subscribed.get();

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

                        CompletableFuture<Integer> subscribed = jobs.SubscribeToStartNextPendingJobExecutionAccepted(
                                subscriptionRequest,
                                QualityOfService.AT_LEAST_ONCE,
                                JobsSample::onStartNextPendingJobExecutionAccepted);
                        subscribed.get();
                        subscribed = jobs.SubscribeToStartNextPendingJobExecutionRejected(
                                subscriptionRequest,
                                QualityOfService.AT_LEAST_ONCE,
                                JobsSample::onRejectedError);
                        subscribed.get();

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
                        CompletableFuture<Integer> subscribed =  jobs.SubscribeToUpdateJobExecutionAccepted(
                                subscriptionRequest,
                                QualityOfService.AT_LEAST_ONCE,
                                (response) -> {
                                    System.out.println("Marked job " + currentJobId + " IN_PROGRESS");
                                    gotResponse.complete(null);
                                });
                        subscribed.get();
                        subscribed = jobs.SubscribeToUpdateJobExecutionRejected(
                                subscriptionRequest,
                                QualityOfService.AT_LEAST_ONCE,
                                JobsSample::onRejectedError);
                        subscribed.get();

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
                        CompletableFuture<Integer> subscribed = jobs.SubscribeToUpdateJobExecutionAccepted(
                                subscriptionRequest,
                                QualityOfService.AT_LEAST_ONCE,
                                (response) -> {
                                    System.out.println("Marked job " + currentJobId + " SUCCEEDED");
                                    gotResponse.complete(null);
                                });
                        subscribed.get();
                        subscribed = jobs.SubscribeToUpdateJobExecutionRejected(
                                subscriptionRequest,
                                QualityOfService.AT_LEAST_ONCE,
                                JobsSample::onRejectedError);
                        subscribed.get();

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

            CompletableFuture<Void> disconnected = connection.disconnect();
            disconnected.get();

            // Close the connection now that we are completely done with it.
            connection.close();

        } catch (CrtRuntimeException | InterruptedException | ExecutionException ex) {
            System.out.println("Exception encountered: " + ex.toString());
        }

        CrtResource.waitForNoResources();
        System.out.println("Complete!");
    }
}
