/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package jobsExecution;

import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.CrtResource;
import software.amazon.awssdk.crt.CrtRuntimeException;
import software.amazon.awssdk.crt.mqtt.QualityOfService;
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
import utils.mqttclientconnectionwrapper.*;
import ServiceTestLifecycleEvents.ServiceTestLifecycleEvents;

public class JobsExecution {
    static IotJobsClient jobs;

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

    static void getAvailableJobs(String thingName) throws Exception {
        gotResponse = new CompletableFuture<>();
        GetPendingJobExecutionsSubscriptionRequest subscriptionRequest = new GetPendingJobExecutionsSubscriptionRequest();
        subscriptionRequest.thingName = thingName;
        CompletableFuture<Integer> subscribed = jobs.SubscribeToGetPendingJobExecutionsAccepted(
                subscriptionRequest,
                QualityOfService.AT_LEAST_ONCE,
                JobsExecution::onGetPendingJobExecutionsAccepted);
        try {
            subscribed.get();
            System.out.println("Subscribed to GetPendingJobExecutionsAccepted");
        } catch (Exception ex) {
            throw new RuntimeException("Failed to subscribe to GetPendingJobExecutions", ex);
        }

        subscribed = jobs.SubscribeToGetPendingJobExecutionsRejected(
                subscriptionRequest,
                QualityOfService.AT_LEAST_ONCE,
                JobsExecution::onRejectedError);
        subscribed.get();
        System.out.println("Subscribed to GetPendingJobExecutionsRejected");

        GetPendingJobExecutionsRequest publishRequest = new GetPendingJobExecutionsRequest();
        publishRequest.thingName = thingName;
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

    static void describeJob(String thingName, String jobId) throws Exception {
        gotResponse = new CompletableFuture<>();
        DescribeJobExecutionSubscriptionRequest subscriptionRequest = new DescribeJobExecutionSubscriptionRequest();
        subscriptionRequest.thingName = thingName;
        subscriptionRequest.jobId = jobId;
        jobs.SubscribeToDescribeJobExecutionAccepted(
                subscriptionRequest,
                QualityOfService.AT_LEAST_ONCE,
                JobsExecution::onDescribeJobExecutionAccepted);
        jobs.SubscribeToDescribeJobExecutionRejected(
                subscriptionRequest,
                QualityOfService.AT_LEAST_ONCE,
                JobsExecution::onRejectedError);

        DescribeJobExecutionRequest publishRequest = new DescribeJobExecutionRequest();
        publishRequest.thingName = thingName;
        publishRequest.jobId = jobId;
        publishRequest.includeJobDocument = true;
        publishRequest.executionNumber = 1L;
        jobs.PublishDescribeJobExecution(publishRequest, QualityOfService.AT_LEAST_ONCE);
        gotResponse.get();
    }

    static void startNextPendingJob(String thingName) throws RuntimeException {
        gotResponse = new CompletableFuture<>();

        StartNextPendingJobExecutionSubscriptionRequest subscriptionRequest = new StartNextPendingJobExecutionSubscriptionRequest();
        subscriptionRequest.thingName = thingName;

        jobs.SubscribeToStartNextPendingJobExecutionAccepted(
                subscriptionRequest,
                QualityOfService.AT_LEAST_ONCE,
                JobsExecution::onStartNextPendingJobExecutionAccepted);
        jobs.SubscribeToStartNextPendingJobExecutionRejected(
                subscriptionRequest,
                QualityOfService.AT_LEAST_ONCE,
                JobsExecution::onRejectedError);

        StartNextPendingJobExecutionRequest publishRequest = new StartNextPendingJobExecutionRequest();
        publishRequest.thingName = thingName;
        publishRequest.stepTimeoutInMinutes = 15L;
        jobs.PublishStartNextPendingJobExecution(publishRequest, QualityOfService.AT_LEAST_ONCE);

        // Waiting for either onStartNextPendingJobExecutionAccepted or onRejectedError
        // to be called.
        try {
            gotResponse.get();
        } catch (Exception ex) {
            throw new RuntimeException("Exception occurred while waiting for starting next pending Job", ex);
        }
    }

    static void updateCurrentJobStatus(String thingName, JobStatus jobStatus) throws RuntimeException {
        gotResponse = new CompletableFuture<>();

        UpdateJobExecutionSubscriptionRequest subscriptionRequest = new UpdateJobExecutionSubscriptionRequest();
        subscriptionRequest.thingName = thingName;
        subscriptionRequest.jobId = currentJobId;
        jobs.SubscribeToUpdateJobExecutionAccepted(
                subscriptionRequest,
                QualityOfService.AT_LEAST_ONCE,
                (response) -> {
                    System.out.println("Marked job " + currentJobId + " " + jobStatus.toString());
                    gotResponse.complete(null);
                });
        jobs.SubscribeToUpdateJobExecutionRejected(
                subscriptionRequest,
                QualityOfService.AT_LEAST_ONCE,
                JobsExecution::onRejectedError);

        UpdateJobExecutionRequest publishRequest = new UpdateJobExecutionRequest();
        publishRequest.thingName = thingName;
        publishRequest.jobId = currentJobId;
        publishRequest.executionNumber = currentExecutionNumber;
        publishRequest.status = jobStatus;
        publishRequest.expectedVersion = currentVersionNumber++;
        jobs.PublishUpdateJobExecution(publishRequest, QualityOfService.AT_LEAST_ONCE);

        // Waiting for a response to our update.
        try {
            gotResponse.get();
        } catch (Exception ex) {
            throw new RuntimeException("Exception occurred while waiting for updating Job", ex);
        }
    }

    public static void main(String[] args) {
        CommandLineUtils.SampleCommandLineData cmdData = CommandLineUtils.getInputForIoTSample("Jobs", args);

        boolean exitWithError = false;

        try (MqttClientConnectionWrapper connection = MqttClientConnectionWrapperCreator.createConnection(
                    cmdData.input_cert,
                    cmdData.input_key,
                    cmdData.input_clientId,
                    cmdData.input_endpoint,
                    cmdData.input_port,
                    cmdData.input_mqtt_version)) {

            jobs = new IotJobsClient(connection.getConnection());

            CompletableFuture<Boolean> connected = connection.start();
            try {
                connected.get();
            } catch (Exception ex) {
                throw new RuntimeException("Exception occurred during connect", ex);
            }

            String thingName = cmdData.input_thingName;

            getAvailableJobs(thingName);
            if (availableJobs.isEmpty()) {
                throw new RuntimeException("At least one job should be queued!");
            }

            // Optional step, but perform it anyway to check that describing jobs works.
            for (String jobId : availableJobs) {
                describeJob(thingName, jobId);
            }

            for (int jobIdx = 0; jobIdx < availableJobs.size(); ++jobIdx) {
                startNextPendingJob(thingName);
                updateCurrentJobStatus(thingName, JobStatus.IN_PROGRESS);
                // Fake doing something
                Thread.sleep(1000);
                updateCurrentJobStatus(thingName, JobStatus.SUCCEEDED);
            }

            CompletableFuture<Void> disconnected = connection.stop();
            disconnected.get();
        } catch (Exception ex) {
            System.out.println("Exception encountered!\n");
            ex.printStackTrace();
            exitWithError = true;
        }

        CrtResource.waitForNoResources();
        System.out.println("Complete!");

        if (exitWithError) {
            System.exit(1);
        } else {
            System.exit(0);
        }
    }
}
