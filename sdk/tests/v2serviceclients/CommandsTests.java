/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

import org.junit.jupiter.api.*;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.crt.iot.MqttRequestResponseClientOptions;
import software.amazon.awssdk.crt.iot.StreamingOperation;
import software.amazon.awssdk.crt.iot.SubscriptionStatusEventType;
import software.amazon.awssdk.iot.V2ClientStreamOptions;
import software.amazon.awssdk.iot.iotcommands.IotCommandsV2Client;
import software.amazon.awssdk.iot.iotcommands.model.*;
import software.amazon.awssdk.iot.iotcommands.model.CommandExecutionStatus;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iot.IotClient;
import software.amazon.awssdk.services.iotjobsdataplane.IotJobsDataPlaneClient;
import software.amazon.awssdk.services.iot.model.*;
import software.amazon.awssdk.services.iotjobsdataplane.model.StartCommandExecutionRequest;
import software.amazon.awssdk.services.iotjobsdataplane.model.StartCommandExecutionResponse;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class CommandsTests extends V2ServiceClientTestFixture {

    private static final Logger LOGGER = Logger.getLogger(CommandsTests.class.getName());

    private static class TestContext {
        private String thingName = null;
        private String thingArn = null;
        private String mqttClientId = null;

        private CreateCommandResponse commandInfo = null;
        private StartCommandExecutionResponse commandExecutionInfo = null;

        private final List<CommandExecutionEvent> commandExecutionEvents = new ArrayList<>();

        private final Lock eventLock = new ReentrantLock();
        private final Condition eventSignal = eventLock.newCondition();
    }

    private IotCommandsV2Client commandsClient;
    private IotClient iotClient;
    private IotJobsDataPlaneClient iotJobsDataPlaneClient;

    private String testRegion;

    private TestContext testContext;

    void populateTestingEnvironmentVariables() {
        super.populateTestingEnvironmentVariables();
        testRegion = System.getenv("AWS_TEST_MQTT5_IOT_CORE_REGION");
    }

    boolean hasTestEnvironment() {
        return testRegion != null && super.hasBaseTestEnvironment();
    }

    public CommandsTests() {
        super();
        populateTestingEnvironmentVariables();

        if (hasTestEnvironment()) {
            iotClient = IotClient.builder()
                    .region(Region.of(testRegion))
                    .build();

            DescribeEndpointRequest describeEndpointRequest =
                    DescribeEndpointRequest.builder().endpointType("iot:Jobs").build();
            DescribeEndpointResponse describeEndpointResponse = iotClient.describeEndpoint(describeEndpointRequest);

            iotJobsDataPlaneClient = IotJobsDataPlaneClient.builder()
                    .region(Region.of(testRegion))
                    .endpointOverride(URI.create("https://" + describeEndpointResponse.endpointAddress()))
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

    void setupCommandsClient5(MqttRequestResponseClientOptions serviceClientOptions) {
        setupBaseMqtt5Client();

        if (serviceClientOptions == null) {
            serviceClientOptions = createDefaultServiceClientOptions();
        }

        commandsClient = IotCommandsV2Client.newFromMqtt5(mqtt5Client, serviceClientOptions);
    }

    void setupCommandsClient311(MqttRequestResponseClientOptions serviceClientOptions) {
        setupBaseMqtt311Client();

        if (serviceClientOptions == null) {
            serviceClientOptions = createDefaultServiceClientOptions();
        }

        commandsClient = IotCommandsV2Client.newFromMqtt311(mqtt311Client, serviceClientOptions);
    }

    void pause(long millis) {
        try {
            wait(millis);
        } catch (Exception ex) {
            ;
        }
    }

    CreateCommandResponse createCommand(int index) {
        String commandId = "commandid-" + UUID.randomUUID().toString();
        String commandDocumentJson = String.format("{\"test\":\"do-something-%d\"}", index);
        SdkBytes commandDocumentBytes = SdkBytes.fromUtf8String(commandDocumentJson);
        CommandPayload commandPayload = CommandPayload.builder()
                .contentType("application/json")
                .content(commandDocumentBytes)
                .build();

        return iotClient.createCommand(CreateCommandRequest.builder()
                .commandId(commandId)
                .payload(commandPayload)
                .build());
    }

    void sleepOnThrottle() {
        long seed = System.nanoTime();
        Random generator = new Random(seed);
        try {
            // 1 - 10 seconds
            long sleepMillis = (long) (generator.nextDouble() * 9000 + 1000);
            Thread.sleep(sleepMillis);
        } catch (Exception e) {
            ;
        }
    }

    void deleteCommand(String commandId) {
        boolean done = false;
        while (!done) {
            try {
                iotClient.deleteCommand(DeleteCommandRequest.builder().commandId(commandId).build());
                done = true;
            } catch (ThrottlingException | LimitExceededException ex) {
                // We run more than 10 CI jobs concurrently, causing us to hit a variety of annoying limits.
                sleepOnThrottle();
            }
        }
    }

    @BeforeEach
    public void setup() {
        if (!hasTestEnvironment()) {
            return;
        }

        testContext = new TestContext();

        String mqttClientId = "test-" + UUID.randomUUID();
        String thingName = "thing-commands-java-" + UUID.randomUUID();

        CreateThingResponse response = iotClient.createThing(CreateThingRequest.builder().thingName(thingName).build());

        testContext.thingName = thingName;
        testContext.thingArn = response.thingArn();
        testContext.mqttClientId = mqttClientId;

        pause(1000);

        testContext.commandInfo = createCommand(1);
    }

    @AfterEach
    public void tearDown() {
        if (!hasTestEnvironment()) {
            return;
        }

        if (commandsClient != null) {
            commandsClient.close();
            commandsClient = null;
        }

        pause(1000);

        if (testContext.commandInfo != null) {
            deleteCommand(testContext.commandInfo.commandId());
        }

        pause(1000);

        if (testContext.thingName != null) {
            iotClient.deleteThing(DeleteThingRequest.builder().thingName(testContext.thingName).build());
        }
    }

    StreamingOperation createCommandExecutionsJsonStream(String thingName) {
        CompletableFuture<Boolean> subscribed = new CompletableFuture<>();

        CommandExecutionsSubscriptionRequest request = new CommandExecutionsSubscriptionRequest();
        request.deviceType = DeviceType.THING;
        request.deviceId = thingName;

        V2ClientStreamOptions<CommandExecutionEvent> options = V2ClientStreamOptions.<CommandExecutionEvent>builder()
                .withStreamEventHandler((event) -> {
                    this.testContext.eventLock.lock();
                    try {
                        this.testContext.commandExecutionEvents.add(event);
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

        StreamingOperation stream = commandsClient.createCommandExecutionsJsonPayloadStream(request, options);
        stream.open();
        try {
            subscribed.get();
        } catch (Exception ex) {
            Assertions.fail("createCommandExecutionsJsonPayloadStream should have completed successfully");
        }

        return stream;
    }

    void waitForStreamEvents() {
        testContext.eventLock.lock();
        try {
            while (testContext.commandExecutionEvents.isEmpty()) {
                testContext.eventSignal.await();
            }

            CommandExecutionEvent event = testContext.commandExecutionEvents.get(0);
            Assertions.assertEquals(testContext.commandExecutionInfo.executionId(), event.executionId);
//            Assertions.assertEquals(testContext.commandExecutionInfo., event.contentType);
        } catch (Exception ex) {
            Assertions.fail("waitForInitialStreamEvents should have completed successfully");
        } finally {
            testContext.eventLock.unlock();
        }
    }

    void doCommandTest() {
        // open both streams
        try (StreamingOperation commandExecutionsJsonStream = createCommandExecutionsJsonStream(testContext.thingName)) {

            {
                StartCommandExecutionRequest request = StartCommandExecutionRequest.builder()
                        .commandArn(testContext.commandInfo.commandArn())
                        .executionTimeoutSeconds(10L)
                        .targetArn(testContext.thingArn)
                        .build();

                testContext.commandExecutionInfo = iotJobsDataPlaneClient.startCommandExecution(request);
            }

            // wait for initial stream events to trigger
            waitForStreamEvents();

            // pretend to work on it
            pause(1000);

            // update to in-progress
            {
                UpdateCommandExecutionRequest updateCommandExecutionRequest = new UpdateCommandExecutionRequest();
                updateCommandExecutionRequest.executionId = testContext.commandExecutionInfo.executionId();
                updateCommandExecutionRequest.deviceType = DeviceType.THING;
                updateCommandExecutionRequest.deviceId = testContext.thingName;
                updateCommandExecutionRequest.status = CommandExecutionStatus.IN_PROGRESS;
                commandsClient.updateCommandExecution(updateCommandExecutionRequest).get();
            }

            // verify it's in-progress
            {
                GetCommandExecutionRequest request = GetCommandExecutionRequest.builder()
                        .executionId(testContext.commandExecutionInfo.executionId())
                        .targetArn(testContext.thingArn)
                        .build();
                GetCommandExecutionResponse response = iotClient.getCommandExecution(request);
                Assertions.assertEquals(
                        software.amazon.awssdk.services.iot.model.CommandExecutionStatus.IN_PROGRESS,
                        response.status());
            }

            // notify command complete
            {
                UpdateCommandExecutionRequest updateCommandExecutionRequest = new UpdateCommandExecutionRequest();
                updateCommandExecutionRequest.executionId = testContext.commandExecutionInfo.executionId();
                updateCommandExecutionRequest.deviceType = DeviceType.THING;
                updateCommandExecutionRequest.deviceId = testContext.thingName;
                updateCommandExecutionRequest.status = CommandExecutionStatus.SUCCEEDED;
                commandsClient.updateCommandExecution(updateCommandExecutionRequest).get();
            }

            // verify it's done
            {
                GetCommandExecutionRequest request = GetCommandExecutionRequest.builder()
                        .executionId(testContext.commandExecutionInfo.executionId())
                        .targetArn(testContext.thingArn)
                        .build();
                GetCommandExecutionResponse response = iotClient.getCommandExecution(request);
                Assertions.assertEquals(
                        software.amazon.awssdk.services.iot.model.CommandExecutionStatus.SUCCEEDED,
                        response.status());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            Assertions.fail("doCommandsControlTest triggered exception");
        }
    }

    @Test
    public void handleCommandExecution5() {
        assumeTrue(hasTestEnvironment());
        setupCommandsClient5(null);
        doCommandTest();
    }

    @Test
    public void handleCommandExecution311() {
        assumeTrue(hasTestEnvironment());
        setupCommandsClient311(null);
        doCommandTest();
    }
}
