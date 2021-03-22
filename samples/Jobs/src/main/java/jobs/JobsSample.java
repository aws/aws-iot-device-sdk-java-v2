/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package jobs;

import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.CrtResource;
import software.amazon.awssdk.crt.CrtRuntimeException;
import software.amazon.awssdk.crt.io.ClientBootstrap;
import software.amazon.awssdk.crt.io.EventLoopGroup;
import software.amazon.awssdk.crt.io.HostResolver;
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
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class JobsSample {
    static String clientId = "test-" + UUID.randomUUID().toString();
    static String thingName;
    static String rootCaPath;
    static String certPath;
    static String keyPath;
    static String endpoint;
    static boolean showHelp = false;
    static int port = 8883;

    static CompletableFuture<Void> gotResponse;
    static List<String> availableJobs = new LinkedList<>();
    static String currentJobId;
    static long currentExecutionNumber = 0;
    static int currentVersionNumber = 0;

    static void printUsage() {
        System.out.println(
                "Usage:\n"+
                "  --help        This message\n"+
                "  --thingName   The name of the IoT thing\n"+
                "  --clientId    Client ID to use when connecting (optional)\n"+
                "  -e|--endpoint AWS IoT service endpoint hostname\n"+
                "  -p|--port     Port to connect to on the endpoint\n"+
                "  -r|--rootca   Path to the root certificate\n"+
                "  -c|--cert     Path to the IoT thing certificate\n"+
                "  -k|--key      Path to the IoT thing private key"
        );
    }

    static void parseCommandLine(String[] args) {
        for (int idx = 0; idx < args.length; ++idx) {
            switch (args[idx]) {
                case "--help":
                    showHelp = true;
                    break;
                case "--clientId":
                    if (idx + 1 < args.length) {
                        clientId = args[++idx];
                    }
                    break;
                case "--thingName":
                    if (idx + 1 < args.length) {
                        thingName = args[++idx];
                    }
                    break;
                case "-e":
                case "--endpoint":
                    if (idx + 1 < args.length) {
                        endpoint = args[++idx];
                    }
                    break;
                case "-p":
                case "--port":
                    if (idx + 1 < args.length) {
                        port = Integer.parseInt(args[++idx]);
                    }
                    break;
                case "-r":
                case "--rootca":
                    if (idx + 1 < args.length) {
                        rootCaPath = args[++idx];
                    }
                    break;
                case "-c":
                case "--cert":
                    if (idx + 1 < args.length) {
                        certPath = args[++idx];
                    }
                    break;
                case "-k":
                case "--key":
                    if (idx + 1 < args.length) {
                        keyPath = args[++idx];
                    }
                    break;
                default:
                    System.out.println("Unrecognized argument: " + args[idx]);
            }
        }
    }

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
        parseCommandLine(args);
        if (showHelp || thingName == null || endpoint == null || rootCaPath == null || certPath == null || keyPath == null) {
            printUsage();
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

        try(EventLoopGroup eventLoopGroup = new EventLoopGroup(1);
            HostResolver resolver = new HostResolver(eventLoopGroup);
            ClientBootstrap clientBootstrap = new ClientBootstrap(eventLoopGroup, resolver);
            AwsIotMqttConnectionBuilder builder = AwsIotMqttConnectionBuilder.newMtlsBuilderFromPath(certPath, keyPath)) {

            builder.withCertificateAuthorityFromPath(null, rootCaPath)
                .withEndpoint(endpoint)
                .withClientId(clientId)
                .withCleanSession(true)
                .withBootstrap(clientBootstrap)
                .withConnectionEventCallbacks(callbacks);

            try(MqttClientConnection connection = builder.build()) {
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
                    subscriptionRequest.thingName = thingName;
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

                if (availableJobs.isEmpty()) {
                    System.out.println("No jobs queued, no further work to do");
                }

                for (String jobId : availableJobs) {
                    gotResponse = new CompletableFuture<>();
                    DescribeJobExecutionSubscriptionRequest subscriptionRequest = new DescribeJobExecutionSubscriptionRequest();
                    subscriptionRequest.thingName = thingName;
                    subscriptionRequest.jobId = jobId;
                    jobs.SubscribeToDescribeJobExecutionAccepted(
                            subscriptionRequest,
                            QualityOfService.AT_LEAST_ONCE,
                            JobsSample::onDescribeJobExecutionAccepted);
                    jobs.SubscribeToDescribeJobExecutionRejected(
                            subscriptionRequest,
                            QualityOfService.AT_LEAST_ONCE,
                            JobsSample::onRejectedError);

                    DescribeJobExecutionRequest publishRequest = new DescribeJobExecutionRequest();
                    publishRequest.thingName = thingName;
                    publishRequest.jobId = jobId;
                    publishRequest.includeJobDocument = true;
                    publishRequest.executionNumber = 1L;
                    jobs.PublishDescribeJobExecution(publishRequest, QualityOfService.AT_LEAST_ONCE);
                    gotResponse.get();
                }

                for (int jobIdx = 0; jobIdx < availableJobs.size(); ++jobIdx) {
                    {
                        gotResponse = new CompletableFuture<>();

                        // Start the next pending job
                        StartNextPendingJobExecutionSubscriptionRequest subscriptionRequest = new StartNextPendingJobExecutionSubscriptionRequest();
                        subscriptionRequest.thingName = thingName;

                        jobs.SubscribeToStartNextPendingJobExecutionAccepted(
                                subscriptionRequest,
                                QualityOfService.AT_LEAST_ONCE,
                                JobsSample::onStartNextPendingJobExecutionAccepted);
                        jobs.SubscribeToStartNextPendingJobExecutionRejected(
                                subscriptionRequest,
                                QualityOfService.AT_LEAST_ONCE,
                                JobsSample::onRejectedError);

                        StartNextPendingJobExecutionRequest publishRequest = new StartNextPendingJobExecutionRequest();
                        publishRequest.thingName = thingName;
                        publishRequest.stepTimeoutInMinutes = 15L;
                        jobs.PublishStartNextPendingJobExecution(publishRequest, QualityOfService.AT_LEAST_ONCE);

                        gotResponse.get();
                    }

                    {
                        // Update the service to let it know we're executing
                        gotResponse = new CompletableFuture<>();

                        UpdateJobExecutionSubscriptionRequest subscriptionRequest = new UpdateJobExecutionSubscriptionRequest();
                        subscriptionRequest.thingName = thingName;
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
                                JobsSample::onRejectedError);

                        UpdateJobExecutionRequest publishRequest = new UpdateJobExecutionRequest();
                        publishRequest.thingName = thingName;
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
                        subscriptionRequest.thingName = thingName;
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
                                JobsSample::onRejectedError);

                        UpdateJobExecutionRequest publishRequest = new UpdateJobExecutionRequest();
                        publishRequest.thingName = thingName;
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
            System.out.println("Exception encountered: " + ex.toString());
        }

        CrtResource.waitForNoResources();
        System.out.println("Complete!");
    }
}
