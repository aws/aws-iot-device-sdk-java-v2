/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import software.amazon.awssdk.crt.iot.MqttRequestResponseClientOptions;
import software.amazon.awssdk.crt.iot.StreamingOperation;
import software.amazon.awssdk.crt.iot.SubscriptionStatusEventType;
import software.amazon.awssdk.iot.ShadowStateFactory;
import software.amazon.awssdk.iot.Timestamp;
import software.amazon.awssdk.iot.V2ClientStreamOptions;
import software.amazon.awssdk.iot.iotshadow.IotShadowV2Client;
import software.amazon.awssdk.iot.iotshadow.model.*;

public class ShadowTests extends V2ServiceClientTestFixture {

    private IotShadowV2Client shadowClient;
    private Gson gson = createGson();

    Gson createGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.disableHtmlEscaping();
        builder.registerTypeAdapter(Timestamp.class, new Timestamp.Serializer());
        builder.registerTypeAdapter(Timestamp.class, new Timestamp.Deserializer());
        builder.registerTypeAdapterFactory(new ShadowStateFactory());
        return builder.create();
    }

    boolean hasTestEnvironment() {
        return hasBaseTestEnvironment();
    }

    public ShadowTests() {
        super();
        populateTestingEnvironmentVariables();
    }

    MqttRequestResponseClientOptions createDefaultServiceClientOptions() {
        return MqttRequestResponseClientOptions.builder()
            .withMaxRequestResponseSubscriptions(4)
            .withMaxStreamingSubscriptions(2)
            .withOperationTimeoutSeconds(10)
            .build();
    }

    void setupShadowClient5(MqttRequestResponseClientOptions serviceClientOptions) {
        setupBaseMqtt5Client();

        if (serviceClientOptions == null) {
            serviceClientOptions = createDefaultServiceClientOptions();
        }

        shadowClient = IotShadowV2Client.newFromMqtt5(mqtt5Client, serviceClientOptions);
    }

    void setupShadowClient311(MqttRequestResponseClientOptions serviceClientOptions) {
        setupBaseMqtt311Client();

        if (serviceClientOptions == null) {
            serviceClientOptions = createDefaultServiceClientOptions();
        }

        shadowClient = IotShadowV2Client.newFromMqtt311(mqtt311Client, serviceClientOptions);
    }

    @AfterEach
    public void tearDown() {
        if (shadowClient != null) {
            shadowClient.close();
            shadowClient = null;
        }
    }

    void doGetNonExistentShadow(String thingName, String shadowName) {
        GetNamedShadowRequest request = new GetNamedShadowRequest();
        request.thingName = thingName;
        request.shadowName = shadowName;

        CompletableFuture<GetShadowResponse> getShadowResult = shadowClient.getNamedShadow(request);

        try {
            getShadowResult.get();
            Assertions.fail("getNamedShadow should have completed exceptionally");
        } catch (Exception ex) {
            Throwable source = ex.getCause();
            Assertions.assertNotNull(source);
            Assertions.assertInstanceOf(V2ErrorResponseException.class, source);

            V2ErrorResponseException v2Exception = (V2ErrorResponseException) source;
            V2ErrorResponse modeledError = v2Exception.getModeledError();
            Assertions.assertNotNull(modeledError);
            Assertions.assertEquals(404, modeledError.code.intValue());
        }
    }

    @Test
    public void getNonexistentShadow5()
    {
        assumeTrue(hasTestEnvironment());
        setupShadowClient5(null);

        String thingName = UUID.randomUUID().toString();
        String shadowName = UUID.randomUUID().toString();
        doGetNonExistentShadow(thingName, shadowName);
    }

    @Test
    public void getNonexistentShadow311()
    {
        assumeTrue(hasBaseTestEnvironment());
        setupShadowClient311(null);

        String thingName = UUID.randomUUID().toString();
        String shadowName = UUID.randomUUID().toString();
        doGetNonExistentShadow(thingName, shadowName);
    }

    void createShadow(String thingName, String shadowName, String stateJson) {
        ShadowState state = new ShadowState();
        state.desired = gson.fromJson(stateJson, HashMap.class);
        state.reported = gson.fromJson(stateJson, HashMap.class);

        UpdateNamedShadowRequest request = new UpdateNamedShadowRequest();
        request.thingName = thingName;
        request.shadowName = shadowName;
        request.state = state;

        CompletableFuture<UpdateShadowResponse> updateShadowResult = shadowClient.updateNamedShadow(request);
        try {
            UpdateShadowResponse response = updateShadowResult.get();

            Assertions.assertNotNull(response);
            Assertions.assertNotNull(response.state);
            Assertions.assertNotNull(response.state.desired);
            Assertions.assertNotNull(response.state.reported);

            String reportedState = gson.toJson(response.state.reported);
            String desiredState = gson.toJson(response.state.desired);

            Assertions.assertEquals(stateJson, reportedState);
            Assertions.assertEquals(stateJson, desiredState);
        } catch (Exception ex) {
            Assertions.fail("updateNamedShadow failed");
        }
    }

    void getShadow(String thingName, String shadowName, String expectedStateJson) {
        GetNamedShadowRequest request = new GetNamedShadowRequest();
        request.thingName = thingName;
        request.shadowName = shadowName;

        CompletableFuture<GetShadowResponse> getShadowResult = shadowClient.getNamedShadow(request);

        try {
            GetShadowResponse response = getShadowResult.get();

            Assertions.assertNotNull(response);
            Assertions.assertNotNull(response.state);
            Assertions.assertNotNull(response.state.desired);
            Assertions.assertNotNull(response.state.reported);

            String reportedState = gson.toJson(response.state.reported);
            String desiredState = gson.toJson(response.state.desired);

            Assertions.assertEquals(expectedStateJson, reportedState);
            Assertions.assertEquals(expectedStateJson, desiredState);
        } catch (Exception ex) {
            Assertions.fail("getNamedShadow should have completed successfully");
        }
    }

    void deleteShadow(String thingName, String shadowName) {
        DeleteNamedShadowRequest request = new DeleteNamedShadowRequest();
        request.thingName = thingName;
        request.shadowName = shadowName;

        CompletableFuture<DeleteShadowResponse> deleteShadowResult = shadowClient.deleteNamedShadow(request);

        try {
            deleteShadowResult.get();
        } catch (Exception ex) {
            Assertions.fail("deleteNamedShadow should have completed successfully");
        }
    }

    void doCreateGetDeleteShadowTest() {
        String rawJson = "{\"key\":\"value\"}";
        String thingName = UUID.randomUUID().toString();
        String shadowName = UUID.randomUUID().toString();

        doGetNonExistentShadow(thingName, shadowName);
        createShadow(thingName, shadowName, rawJson);

        try {
            getShadow(thingName, shadowName, rawJson);
        } finally {
            deleteShadow(thingName, shadowName);
        }
    }

    @Test
    public void createGetDeleteShadow5()
    {
        assumeTrue(hasTestEnvironment());
        setupShadowClient5(null);
        doCreateGetDeleteShadowTest();
    }

    @Test
    public void createGetDeleteShadow311()
    {
        assumeTrue(hasTestEnvironment());
        setupShadowClient311(null);
        doCreateGetDeleteShadowTest();
    }

    StreamingOperation createDeltaUpdatedStream(String thingName, String shadowName, CompletableFuture<ShadowDeltaUpdatedEvent> deltaUpdated) {
        CompletableFuture<Boolean> subscribed = new CompletableFuture<>();

        NamedShadowDeltaUpdatedSubscriptionRequest request = new NamedShadowDeltaUpdatedSubscriptionRequest();
        request.thingName = thingName;
        request.shadowName = shadowName;

        V2ClientStreamOptions<ShadowDeltaUpdatedEvent> options = V2ClientStreamOptions.<ShadowDeltaUpdatedEvent>builder()
            .withStreamEventHandler((event) -> deltaUpdated.complete(event))
            .withSubscriptionEventHandler((event) -> {
                if (event.getType() == SubscriptionStatusEventType.SUBSCRIPTION_ESTABLISHED) {
                    subscribed.complete(true);
                }
            })
            .build();

        StreamingOperation stream = shadowClient.createNamedShadowDeltaUpdatedStream(request, options);
        stream.open();
        try {
            subscribed.get();
        } catch (Exception ex) {
            Assertions.fail("createDeltaUpdatedStream should have completed successfully");
        }

        return stream;
    }

    StreamingOperation createUpdatedStream(String thingName, String shadowName, CompletableFuture<ShadowUpdatedEvent> updated) {
        CompletableFuture<Boolean> subscribed = new CompletableFuture<>();

        NamedShadowUpdatedSubscriptionRequest request = new NamedShadowUpdatedSubscriptionRequest();
        request.thingName = thingName;
        request.shadowName = shadowName;

        V2ClientStreamOptions<ShadowUpdatedEvent> options = V2ClientStreamOptions.<ShadowUpdatedEvent>builder()
                .withStreamEventHandler((event) -> updated.complete(event))
                .withSubscriptionEventHandler((event) -> {
                    if (event.getType() == SubscriptionStatusEventType.SUBSCRIPTION_ESTABLISHED) {
                        subscribed.complete(true);
                    }
                })
                .build();

        StreamingOperation stream = shadowClient.createNamedShadowUpdatedStream(request, options);
        stream.open();
        try {
            subscribed.get();
        } catch (Exception ex) {
            Assertions.fail("createUpdatedStream should have completed successfully");
        }

        return stream;
    }

    void update(String thingName, String shadowName, ShadowState newState) {
        UpdateNamedShadowRequest request = new UpdateNamedShadowRequest();
        request.thingName = thingName;
        request.shadowName = shadowName;
        request.state = newState;

        CompletableFuture<UpdateShadowResponse> updateShadowResult = shadowClient.updateNamedShadow(request);
        try {
            UpdateShadowResponse response = updateShadowResult.get();
        } catch (Exception ex) {
            Assertions.fail("updateNamedShadow failed");
        }
    }

    void updateDesired(String thingName, String shadowName, String updateJson) {
        ShadowState state = new ShadowState();
        state.desired = gson.fromJson(updateJson, HashMap.class);
        update(thingName, shadowName, state);
    }

    void updateReported(String thingName, String shadowName, String updateJson) {
        ShadowState state = new ShadowState();
        state.reported = gson.fromJson(updateJson, HashMap.class);
        update(thingName, shadowName, state);
    }

    void doUpdateShadowTest() {
        String rawJson = "{\"color\":\"green\",\"on\":true}";
        String thingName = UUID.randomUUID().toString();
        String shadowName = UUID.randomUUID().toString();

        doGetNonExistentShadow(thingName, shadowName);
        createShadow(thingName, shadowName, rawJson);
        try {
            getShadow(thingName, shadowName, rawJson);

            CompletableFuture<ShadowDeltaUpdatedEvent> deltaUpdated = new CompletableFuture<>();
            CompletableFuture<ShadowUpdatedEvent> updated = new CompletableFuture<>();

            try (StreamingOperation deltaUpdatedStream = createDeltaUpdatedStream(thingName, shadowName, deltaUpdated);
                 StreamingOperation updatedStream = createUpdatedStream(thingName, shadowName, updated)) {

                String updateJson = "{\"color\":\"blue\",\"on\":false}";

                updateDesired(thingName, shadowName, updateJson);

                try {
                    ShadowDeltaUpdatedEvent deltaUpdatedEvent = deltaUpdated.get();

                    String deltaUpdatedStateJson = gson.toJson(deltaUpdatedEvent.state);
                    Assertions.assertEquals(updateJson, deltaUpdatedStateJson);
                } catch (Exception ex) {
                    Assertions.fail("streaming delta update failure");
                }

                try {
                    ShadowUpdatedEvent updatedEvent = updated.get();

                    String updatedStateJson = gson.toJson(updatedEvent.current.state.desired);
                    Assertions.assertEquals(updateJson, updatedStateJson);
                } catch (Exception ex) {
                    Assertions.fail("streaming update failure");
                }

                updateReported(thingName, shadowName, updateJson);
            }
        } finally {
            deleteShadow(thingName, shadowName);
        }
    }

    @Test
    public void updateShadow5()
    {
        assumeTrue(hasTestEnvironment());
        setupShadowClient5(null);
        doUpdateShadowTest();
    }

    @Test
    public void updateShadow311()
    {
        assumeTrue(hasTestEnvironment());
        setupShadowClient311(null);
        doUpdateShadowTest();
    }

}
