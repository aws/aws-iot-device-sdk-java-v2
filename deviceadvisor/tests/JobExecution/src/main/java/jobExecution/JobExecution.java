/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package JobExecution;

import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.CrtResource;
import software.amazon.awssdk.crt.CrtRuntimeException;
import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
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
import software.amazon.awssdk.iot.iotjobs.model.NextJobExecutionChangedSubscriptionRequest;
import software.amazon.awssdk.iot.iotjobs.model.NextJobExecutionChangedEvent;
import software.amazon.awssdk.iot.iotjobs.model.JobExecutionsChangedSubscriptionRequest;
import software.amazon.awssdk.iot.iotjobs.model.JobExecutionsChangedEvent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.UUID;

import DATestUtils.DATestUtils;

public class JobExecution {
    static String clientId = "test-" + UUID.randomUUID().toString();
    static short port = 8883;

    static MqttClientConnection connection;
    static IotJobsClient jobs;
    static CompletableFuture<Void> gotResponse;
    static List<String> availableJobs = new LinkedList<>();
    static String currentJobId;
    static long currentExecutionNumber = 0;
    static int currentVersionNumber = 0;

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
        // Set vars
        if (!DATestUtils.init(DATestUtils.TestType.JOBS)) {
            throw new RuntimeException("Failed to initialize environment variables.");
        }

        try (AwsIotMqttConnectionBuilder builder = AwsIotMqttConnectionBuilder
                .newMtlsBuilderFromPath(DATestUtils.certificatePath, DATestUtils.keyPath)) {
            builder.withClientId(clientId)
                    .withEndpoint(DATestUtils.endpoint)
                    .withPort(port)
                    .withCleanSession(true)
                    .withProtocolOperationTimeoutMs(60000);

            try (MqttClientConnection connection = builder.build()) {
                jobs = new IotJobsClient(connection);

                CompletableFuture<Boolean> connected = connection.connect();
                try {
                    boolean sessionPresent = connected.get();
                    System.out.println("Connected to " + (!sessionPresent ? "new" : "existing") + " session!");
                } catch (Exception ex) {
                    throw new RuntimeException("Exception occurred during connect", ex);
                }

                if (true) {
                    gotResponse = new CompletableFuture<>();
                    GetPendingJobExecutionsSubscriptionRequest subscriptionRequest = new GetPendingJobExecutionsSubscriptionRequest();
                    subscriptionRequest.thingName = DATestUtils.thing_name;
                    System.out.println("Subscribing to GetPendingJobExecutionsAccepted for thing '"
                            + DATestUtils.thing_name + "'");
                    CompletableFuture<Integer> subscribed = jobs.SubscribeToGetPendingJobExecutionsAccepted(
                            subscriptionRequest,
                            QualityOfService.AT_LEAST_ONCE,
                            JobExecution::onGetPendingJobExecutionsAccepted);
                    try {
                        subscribed.get();
                        System.out.println("Subscribed to GetPendingJobExecutionsAccepted");
                    } catch (Exception ex) {
                        throw new RuntimeException("Failed to subscribe to GetPendingJobExecutions", ex);
                    }

                    subscribed = jobs.SubscribeToGetPendingJobExecutionsRejected(
                            subscriptionRequest,
                            QualityOfService.AT_LEAST_ONCE,
                            JobExecution::onRejectedError);
                    subscribed.get();
                    System.out.println("Subscribed to GetPendingJobExecutionsRejected");

                    GetPendingJobExecutionsRequest publishRequest = new GetPendingJobExecutionsRequest();
                    publishRequest.thingName = DATestUtils.thing_name;
                    CompletableFuture<Integer> published = jobs.PublishGetPendingJobExecutions(
                            publishRequest,
                            QualityOfService.AT_LEAST_ONCE);
                    try {
                        published.get();
                        System.out.println("Published to GetPendingJobExecutions");
                        gotResponse.get();
                    } catch (Exception ex) {
                        throw new RuntimeException("Exception occurred during publish", ex);
                    }
                }

                for (String jobId : availableJobs) {
                    gotResponse = new CompletableFuture<>();
                    DescribeJobExecutionSubscriptionRequest subscriptionRequest = new DescribeJobExecutionSubscriptionRequest();
                    subscriptionRequest.thingName = DATestUtils.thing_name;
                    subscriptionRequest.jobId = jobId;
                    jobs.SubscribeToDescribeJobExecutionAccepted(
                            subscriptionRequest,
                            QualityOfService.AT_LEAST_ONCE,
                            JobExecution::onDescribeJobExecutionAccepted);
                    jobs.SubscribeToDescribeJobExecutionRejected(
                            subscriptionRequest,
                            QualityOfService.AT_LEAST_ONCE,
                            JobExecution::onRejectedError);

                    DescribeJobExecutionRequest publishRequest = new DescribeJobExecutionRequest();
                    publishRequest.thingName = DATestUtils.thing_name;
                    publishRequest.jobId = jobId;
                    publishRequest.includeJobDocument = true;
                    publishRequest.executionNumber = 1L;
                    jobs.PublishDescribeJobExecution(publishRequest, QualityOfService.AT_LEAST_ONCE);
                    gotResponse.get();
                }

                // If sample is not running in CI, then process the available jobs.
                for (int jobIdx = 0; jobIdx < availableJobs.size(); ++jobIdx) {
                    {
                        gotResponse = new CompletableFuture<>();

                        // Start the next pending job
                        StartNextPendingJobExecutionSubscriptionRequest subscriptionRequest = new StartNextPendingJobExecutionSubscriptionRequest();
                        subscriptionRequest.thingName = DATestUtils.thing_name;

                        jobs.SubscribeToStartNextPendingJobExecutionAccepted(
                                subscriptionRequest,
                                QualityOfService.AT_LEAST_ONCE,
                                JobExecution::onStartNextPendingJobExecutionAccepted);
                        jobs.SubscribeToStartNextPendingJobExecutionRejected(
                                subscriptionRequest,
                                QualityOfService.AT_LEAST_ONCE,
                                JobExecution::onRejectedError);

                        StartNextPendingJobExecutionRequest publishRequest = new StartNextPendingJobExecutionRequest();
                        publishRequest.thingName = DATestUtils.thing_name;
                        publishRequest.stepTimeoutInMinutes = 15L;
                        jobs.PublishStartNextPendingJobExecution(publishRequest, QualityOfService.AT_LEAST_ONCE);

                        gotResponse.get();
                    }

                    {
                        // Update the service to let it know we're executing
                        gotResponse = new CompletableFuture<>();

                        UpdateJobExecutionSubscriptionRequest subscriptionRequest = new UpdateJobExecutionSubscriptionRequest();
                        subscriptionRequest.thingName = DATestUtils.thing_name;
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
                                JobExecution::onRejectedError);

                        UpdateJobExecutionRequest publishRequest = new UpdateJobExecutionRequest();
                        publishRequest.thingName = DATestUtils.thing_name;
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
                        subscriptionRequest.thingName = DATestUtils.thing_name;
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
                                JobExecution::onRejectedError);

                        UpdateJobExecutionRequest publishRequest = new UpdateJobExecutionRequest();
                        publishRequest.thingName = DATestUtils.thing_name;
                        publishRequest.jobId = currentJobId;
                        publishRequest.executionNumber = currentExecutionNumber;
                        publishRequest.status = JobStatus.SUCCEEDED;
                        publishRequest.expectedVersion = currentVersionNumber++;
                        jobs.PublishUpdateJobExecution(publishRequest, QualityOfService.AT_LEAST_ONCE);

                        gotResponse.get();
                    }
                }

                CompletableFuture<Void> disconnected = connection.disconnect();
                disconnected.get();
            }
        } catch (CrtRuntimeException | InterruptedException | ExecutionException ex) {
            throw new RuntimeException("Builder Connection Failed.", ex);
        }

        System.exit(0);
    }
}
