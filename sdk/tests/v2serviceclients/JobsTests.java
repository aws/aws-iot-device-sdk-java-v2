/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.crt.iot.MqttRequestResponseClientOptions;
import software.amazon.awssdk.crt.iot.StreamingOperation;
import software.amazon.awssdk.crt.iot.SubscriptionStatusEventType;
import software.amazon.awssdk.iot.V2ClientStreamOptions;
import software.amazon.awssdk.iot.iotjobs.IotJobsV2Client;
import software.amazon.awssdk.iot.iotjobs.model.*;

import software.amazon.awssdk.iot.iotjobs.model.DescribeJobExecutionRequest;
import software.amazon.awssdk.iot.iotjobs.model.DescribeJobExecutionResponse;
import software.amazon.awssdk.iot.iotjobs.model.JobExecutionSummary;
import software.amazon.awssdk.iot.iotjobs.model.JobStatus;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iot.IotClient;
import software.amazon.awssdk.services.iot.model.*;
import software.amazon.awssdk.services.sts.StsClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class JobsTests extends V2ServiceClientTestFixture {

    private static final Logger LOGGER = Logger.getLogger(JobsTests.class.getName());

    private static class TestContext {
        private String thingName = null;
        private String thingGroupName = null;

        private String thingGroupArn = null;
        private String jobId1 = null;

        private final List<JobExecutionsChangedEvent> jobExecutionsChangedEvents = new ArrayList<>();
        private final List<NextJobExecutionChangedEvent> nextJobExecutionChangedEvents = new ArrayList<>();

        private final Lock eventLock = new ReentrantLock();
        private final Condition eventSignal = eventLock.newCondition();
    }

    private IotJobsV2Client jobsClient;
    private IotClient iotClient;

    private String testRegion;

    private TestContext testContext;

    void populateTestingEnvironmentVariables() {
        super.populateTestingEnvironmentVariables();
        testRegion = System.getenv("AWS_TEST_MQTT5_IOT_CORE_REGION");
    }

    boolean hasTestEnvironment() {
        return testRegion != null && super.hasBaseTestEnvironment();
    }

    public JobsTests() {
        super();
        populateTestingEnvironmentVariables();

        if (hasTestEnvironment()) {
            // reference STS to allow STS assume role in ~/.aws/credentials during local testing
            StsClient stsClient = StsClient.builder()
                    .region(Region.of(testRegion))
                    .build();

            iotClient = IotClient.builder()
                    .region(Region.of(testRegion))
                    .build();
        }
    }

    MqttRequestResponseClientOptions createDefaultServiceClientOptions() {
        return MqttRequestResponseClientOptions.builder()
                .withMaxRequestResponseSubscriptions(4)
                .withMaxStreamingSubscriptions(2)
                .withOperationTimeoutSeconds(10)
                .build();
    }

    void setupJobsClient5(MqttRequestResponseClientOptions serviceClientOptions) {
        setupBaseMqtt5Client();

        if (serviceClientOptions == null) {
            serviceClientOptions = createDefaultServiceClientOptions();
        }

        jobsClient = IotJobsV2Client.newFromMqtt5(mqtt5Client, serviceClientOptions);
    }

    void setupJobsClient311(MqttRequestResponseClientOptions serviceClientOptions) {
        setupBaseMqtt311Client();

        if (serviceClientOptions == null) {
            serviceClientOptions = createDefaultServiceClientOptions();
        }

        jobsClient = IotJobsV2Client.newFromMqtt311(mqtt311Client, serviceClientOptions);
    }

    void pause(long millis) {
        try {
            wait(millis);
        } catch (Exception ex) {
            ;
        }
    }

    String createJob(int index) {
        String jobId = "jobid-" + UUID.randomUUID().toString();
        String jobDocumentJson = String.format("{\"test\":\"do-something-%d\"}", index);

        iotClient.createJob(CreateJobRequest.builder()
                .jobId(jobId)
                .document(jobDocumentJson)
                .targets(testContext.thingGroupArn)
                .targetSelection(TargetSelection.CONTINUOUS).build());

        return jobId;
    }

    void sleepOnThrottle() {
        long seed = System.nanoTime();
        Random generator = new Random(seed);
        try {
            // 1 - 10 seconds
            long sleepMillis = (long)(generator.nextDouble() * 9000 + 1000);
            Thread.sleep(sleepMillis);
        } catch (Exception e) {
            ;
        }
    }

    void deleteJob(String jobId) {
        boolean done = false;
        while (!done) {
            try {
                iotClient.deleteJob(DeleteJobRequest.builder().jobId(jobId).force(true).build());
                done = true;
            } catch (ThrottlingException | LimitExceededException ex) {
                // We run more than 10 CI jobs concurrently, causing us to hit a variety of annoying limits.
                sleepOnThrottle();
            }
        }
    }

    @AfterEach
    public void tearDown() {
        if (!hasTestEnvironment()) {
            return;
        }

        if (jobsClient != null) {
            jobsClient.close();
            jobsClient = null;
        }

        pause(1000);

        if (testContext.jobId1 != null) {
            deleteJob(testContext.jobId1);
        }

        pause(1000);

        if (testContext.thingName != null) {
            iotClient.deleteThing(DeleteThingRequest.builder().thingName(testContext.thingName).build());
        }

        pause(1000);

        if (testContext.thingGroupName != null) {
            iotClient.deleteThingGroup(DeleteThingGroupRequest.builder().thingGroupName(testContext.thingGroupName).build());
        }
    }

    @BeforeEach
    public void setup() {
        if (!hasTestEnvironment()) {
            return;
        }

        testContext = new TestContext();

        String thingGroupName = "tgn-" + UUID.randomUUID().toString();

        CreateThingGroupResponse createThingGroupResponse = iotClient.createThingGroup(CreateThingGroupRequest.builder().
                    thingGroupName(thingGroupName).build());

        testContext.thingGroupName = thingGroupName;
        testContext.thingGroupArn = createThingGroupResponse.thingGroupArn();

        String thingName = "thing-" + UUID.randomUUID().toString();

        iotClient.createThing(CreateThingRequest.builder().thingName(thingName).build());

        testContext.thingName = thingName;

        pause(1000);

        testContext.jobId1 = createJob(1);
    }

    StreamingOperation createJobExecutionsChangedStream(String thingName) {
        CompletableFuture<Boolean> subscribed = new CompletableFuture<>();

        JobExecutionsChangedSubscriptionRequest request = new JobExecutionsChangedSubscriptionRequest();
        request.thingName = thingName;

        V2ClientStreamOptions<JobExecutionsChangedEvent> options = V2ClientStreamOptions.<JobExecutionsChangedEvent>builder()
                .withStreamEventHandler((event) -> {
                    this.testContext.eventLock.lock();
                    try {
                        this.testContext.jobExecutionsChangedEvents.add(event);
                    } finally {
                        this.testContext.eventSignal.signalAll();
                        this.testContext.eventLock.unlock();
                    }
                })
                .withSubscriptionEventHandler((event) -> {
                    if (event.getType() == SubscriptionStatusEventType.SUBSCRIPTION_ESTABLISHED) {
                        subscribed.complete(true);
                    }
                })
                .build();

        StreamingOperation stream = jobsClient.createJobExecutionsChangedStream(request, options);
        stream.open();
        try {
            subscribed.get();
        } catch (Exception ex) {
            Assertions.fail("createJobExecutionsChangedStream should have completed successfully");
        }

        return stream;
    }

    StreamingOperation createNextJobExecutionChangedStream(String thingName) {
        CompletableFuture<Boolean> subscribed = new CompletableFuture<>();

        NextJobExecutionChangedSubscriptionRequest request = new NextJobExecutionChangedSubscriptionRequest();
        request.thingName = thingName;

        V2ClientStreamOptions<NextJobExecutionChangedEvent> options = V2ClientStreamOptions.<NextJobExecutionChangedEvent>builder()
                .withStreamEventHandler((event) -> {
                    this.testContext.eventLock.lock();
                    try {
                        this.testContext.nextJobExecutionChangedEvents.add(event);
                    } finally {
                        this.testContext.eventSignal.signalAll();
                        this.testContext.eventLock.unlock();
                    }
                })
                .withSubscriptionEventHandler((event) -> {
                    if (event.getType() == SubscriptionStatusEventType.SUBSCRIPTION_ESTABLISHED) {
                        subscribed.complete(true);
                    }
                })
                .build();

        StreamingOperation stream = jobsClient.createNextJobExecutionChangedStream(request, options);
        stream.open();
        try {
            subscribed.get();
        } catch (Exception ex) {
            Assertions.fail("createNextJobExecutionChangedStream should have completed successfully");
        }

        return stream;
    }

    void waitForInitialStreamEvents() {
        testContext.eventLock.lock();
        try {
            while (testContext.jobExecutionsChangedEvents.isEmpty()) {
                testContext.eventSignal.await();
            }

            JobExecutionsChangedEvent firstEvent = testContext.jobExecutionsChangedEvents.get(0);
            List<JobExecutionSummary> queuedJobs = firstEvent.jobs.get(JobStatus.QUEUED);
            Assertions.assertFalse(queuedJobs.isEmpty());
            Assertions.assertEquals(testContext.jobId1, queuedJobs.get(0).jobId);

            while (testContext.nextJobExecutionChangedEvents.isEmpty()) {
                testContext.eventSignal.await();
            }

            NextJobExecutionChangedEvent firstNextEvent = testContext.nextJobExecutionChangedEvents.get(0);
            Assertions.assertEquals(testContext.jobId1, firstNextEvent.execution.jobId);
            Assertions.assertEquals(JobStatus.QUEUED, firstNextEvent.execution.status);
        } catch (Exception ex) {
            Assertions.fail("waitForInitialStreamEvents should have completed successfully");
        } finally {
            testContext.eventLock.unlock();
        }
    }

    void waitForFinalStreamEvents() {
        testContext.eventLock.lock();
        try {
            while (testContext.jobExecutionsChangedEvents.size() < 2) {
                testContext.eventSignal.await();
            }

            JobExecutionsChangedEvent finalEvent = testContext.jobExecutionsChangedEvents.get(1);
            Assertions.assertTrue(finalEvent.jobs == null || finalEvent.jobs.isEmpty());

            while (testContext.nextJobExecutionChangedEvents.size() < 2) {
                testContext.eventSignal.await();
            }

            NextJobExecutionChangedEvent finalNextEvent = testContext.nextJobExecutionChangedEvents.get(1);
            Assertions.assertNotNull(finalNextEvent.timestamp);
            Assertions.assertNull(finalNextEvent.execution);
        } catch (Exception ex) {
            Assertions.fail("waitForFinalStreamEvents should have completed successfully");
        } finally {
            testContext.eventLock.unlock();
        }
    }

    void verifyNothingInProgress() throws InterruptedException, ExecutionException {
        GetPendingJobExecutionsRequest getPendingRequest = new GetPendingJobExecutionsRequest();
        getPendingRequest.thingName = testContext.thingName;
        GetPendingJobExecutionsResponse getPendingResponse = jobsClient.getPendingJobExecutions(getPendingRequest).get();
        Assertions.assertEquals(0, getPendingResponse.queuedJobs.size());
        Assertions.assertEquals(0, getPendingResponse.inProgressJobs.size());
    }

    void doJobControlTest() {
        // open both streams
        try (StreamingOperation jobExecutionsChangedStream = createJobExecutionsChangedStream(testContext.thingName);
             StreamingOperation nextJobExecutionChangedStream = createNextJobExecutionChangedStream(testContext.thingName)) {

            // verify nothing pending, in-progress
            verifyNothingInProgress();

            // attach thing to thing group; this should cause job1 to immediately be queued for us
            iotClient.addThingToThingGroup(AddThingToThingGroupRequest.builder()
                    .thingName(testContext.thingName)
                    .thingGroupName(testContext.thingGroupName)
                    .build());

            // wait for initial stream events to trigger
            waitForInitialStreamEvents();

            // start the next job
            StartNextPendingJobExecutionRequest startNextRequest = new StartNextPendingJobExecutionRequest();
            startNextRequest.thingName = testContext.thingName;

            StartNextJobExecutionResponse startNextResponse = jobsClient.startNextPendingJobExecution(startNextRequest).get();
            Assertions.assertEquals(testContext.jobId1, startNextResponse.execution.jobId);

            // pretend to work on it
            pause(1000);

            // verify it's in progress
            DescribeJobExecutionRequest describeJobExecutionRequest = new DescribeJobExecutionRequest();
            describeJobExecutionRequest.thingName = testContext.thingName;
            describeJobExecutionRequest.jobId = testContext.jobId1;

            DescribeJobExecutionResponse describeJobExecutionResponse = jobsClient.describeJobExecution(describeJobExecutionRequest).get();
            Assertions.assertEquals(testContext.jobId1, describeJobExecutionResponse.execution.jobId);
            Assertions.assertEquals(JobStatus.IN_PROGRESS, describeJobExecutionResponse.execution.status);

            // notify job complete
            UpdateJobExecutionRequest updateJobExecutionRequest = new UpdateJobExecutionRequest();
            updateJobExecutionRequest.thingName = testContext.thingName;
            updateJobExecutionRequest.jobId = testContext.jobId1;
            updateJobExecutionRequest.status = JobStatus.SUCCEEDED;

            jobsClient.updateJobExecution(updateJobExecutionRequest).get();

            pause(3000);

            // verify nothing left to do
            waitForFinalStreamEvents();
            verifyNothingInProgress();

        } catch (Exception ex) {
            ex.printStackTrace();
            Assertions.fail("doJobControlTest triggered exception");
        }
    }

    @Test
    public void jobControl5() {
        assumeTrue(hasTestEnvironment());
        setupJobsClient5(null);

        doJobControlTest();
    }

    @Test
    public void jobControl311() {
        assumeTrue(hasTestEnvironment());
        setupJobsClient311(null);

        doJobControlTest();
    }
}
