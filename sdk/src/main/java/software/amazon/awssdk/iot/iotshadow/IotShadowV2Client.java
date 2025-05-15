/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.iot.iotshadow;

import java.lang.AutoCloseable;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.UUID;
import java.util.function.BiFunction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import software.amazon.awssdk.crt.CrtRuntimeException;
import software.amazon.awssdk.crt.iot.*;
import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt5.Mqtt5Client;
import software.amazon.awssdk.iot.*;
import software.amazon.awssdk.iot.iotshadow.model.*;

/**
 * The AWS IoT Device Shadow service adds shadows to AWS IoT thing objects. Shadows are a simple data store for device properties and state.  Shadows can make a device’s state available to apps and other services whether the device is connected to AWS IoT or not.
 *
 * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html
 *
*/
public class IotShadowV2Client implements AutoCloseable {

    private MqttRequestResponseClient rrClient;
    private final Gson gson;

    private Gson createGson() {
        GsonBuilder gson = new GsonBuilder();
        gson.disableHtmlEscaping();
        gson.registerTypeAdapter(Timestamp.class, new Timestamp.Serializer());
        gson.registerTypeAdapter(Timestamp.class, new Timestamp.Deserializer());
        addTypeAdapters(gson);
        return gson.create();
    }

    private void addTypeAdapters(GsonBuilder gson) {
        ShadowStateFactory shadowStateFactory = new ShadowStateFactory();
        gson.registerTypeAdapterFactory(shadowStateFactory);
    }

    private IotShadowV2Client(MqttRequestResponseClient rrClient) {
        this.rrClient = rrClient;
        this.gson = createGson();
    }

    /**
     * Constructs a new IotShadowV2Client, using an MQTT5 client as transport
     *
     * @param protocolClient the MQTT5 client to use
     * @param options configuration options to use
     */
    static public IotShadowV2Client newFromMqtt5(Mqtt5Client protocolClient, MqttRequestResponseClientOptions options) {
        MqttRequestResponseClient rrClient = new MqttRequestResponseClient(protocolClient, options);
        return new IotShadowV2Client(rrClient);
    }

    /**
     * Constructs a new IotShadowV2Client, using an MQTT311 client as transport
     *
     * @param protocolClient the MQTT311 client to use
     * @param options configuration options to use
     */
    static public IotShadowV2Client newFromMqtt311(MqttClientConnection protocolClient, MqttRequestResponseClientOptions options) {
        MqttRequestResponseClient rrClient = new MqttRequestResponseClient(protocolClient, options);
        return new IotShadowV2Client(rrClient);
    }

    /**
     * Releases all resources used by the client.  It is not valid to invoke operations
     * on the client after it has been closed.
     */
    public void close() {
        this.rrClient.decRef();
        this.rrClient = null;
    }

    private ShadowUpdatedEvent createShadowUpdatedEvent(IncomingPublishEvent publishEvent) {
        String payload = new String(publishEvent.getPayload(), StandardCharsets.UTF_8);
        return this.gson.fromJson(payload, ShadowUpdatedEvent.class);
    }

    private ShadowDeltaUpdatedEvent createShadowDeltaUpdatedEvent(IncomingPublishEvent publishEvent) {
        String payload = new String(publishEvent.getPayload(), StandardCharsets.UTF_8);
        return this.gson.fromJson(payload, ShadowDeltaUpdatedEvent.class);
    }

    /**
     * Deletes a named shadow for an AWS IoT thing.
     *
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#delete-pub-sub-topic
     *
     * @param request modeled request to perform
     *
     * @return a future that will complete with the corresponding response
     */
    public CompletableFuture<DeleteShadowResponse> deleteNamedShadow(DeleteNamedShadowRequest request) {
        V2ClientFuture<DeleteShadowResponse> responseFuture = new V2ClientFuture<>();

        try {
            if (request.thingName == null) {
                throw new CrtRuntimeException("DeleteNamedShadowRequest.thingName cannot be null");
            }

            if (request.shadowName == null) {
                throw new CrtRuntimeException("DeleteNamedShadowRequest.shadowName cannot be null");
            }

            RequestResponseOperation.RequestResponseOperationBuilder builder = RequestResponseOperation.builder();

            // Correlation Token
            String correlationToken = UUID.randomUUID().toString();
            request.clientToken = correlationToken;
            builder.withCorrelationToken(correlationToken);

            // Publish Topic
            String publishTopic = "$aws/things/{thingName}/shadow/name/{shadowName}/delete";
            publishTopic = publishTopic.replace("{thingName}", request.thingName);
            publishTopic = publishTopic.replace("{shadowName}", request.shadowName);
            builder.withPublishTopic(publishTopic);

            // Payload
            String payloadJson = gson.toJson(request);
            builder.withPayload(payloadJson.getBytes(StandardCharsets.UTF_8));

            // Subscriptions
            String subscription0 = "$aws/things/{thingName}/shadow/name/{shadowName}/delete/+";
            subscription0 = subscription0.replace("{thingName}", request.thingName);
            subscription0 = subscription0.replace("{shadowName}", request.shadowName);
            builder.withSubscription(subscription0);

            // Response paths
            ResponsePath.ResponsePathBuilder pathBuilder1 = ResponsePath.builder();
            String responseTopic1 = publishTopic + "/accepted";
            pathBuilder1.withResponseTopic(publishTopic + "/accepted");
            pathBuilder1.withCorrelationTokenJsonPath("clientToken");
            builder.withResponsePath(pathBuilder1.build());

            ResponsePath.ResponsePathBuilder pathBuilder2 = ResponsePath.builder();
            String responseTopic2 = publishTopic + "/rejected";
            pathBuilder2.withResponseTopic(publishTopic + "/rejected");
            pathBuilder2.withCorrelationTokenJsonPath("clientToken");
            builder.withResponsePath(pathBuilder2.build());

            // Submit
            submitOperation(responseFuture, builder.build(), responseTopic1, DeleteShadowResponse.class, responseTopic2, V2ErrorResponse.class, IotShadowV2Client::createV2ErrorResponseException);
        } catch (Exception e) {
            responseFuture.completeExceptionally(createV2ErrorResponseException(e.getMessage(), null));
        }

        return responseFuture;
    }

    /**
     * Deletes the (classic) shadow for an AWS IoT thing.
     *
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#delete-pub-sub-topic
     *
     * @param request modeled request to perform
     *
     * @return a future that will complete with the corresponding response
     */
    public CompletableFuture<DeleteShadowResponse> deleteShadow(DeleteShadowRequest request) {
        V2ClientFuture<DeleteShadowResponse> responseFuture = new V2ClientFuture<>();

        try {
            if (request.thingName == null) {
                throw new CrtRuntimeException("DeleteShadowRequest.thingName cannot be null");
            }

            RequestResponseOperation.RequestResponseOperationBuilder builder = RequestResponseOperation.builder();

            // Correlation Token
            String correlationToken = UUID.randomUUID().toString();
            request.clientToken = correlationToken;
            builder.withCorrelationToken(correlationToken);

            // Publish Topic
            String publishTopic = "$aws/things/{thingName}/shadow/delete";
            publishTopic = publishTopic.replace("{thingName}", request.thingName);
            builder.withPublishTopic(publishTopic);

            // Payload
            String payloadJson = gson.toJson(request);
            builder.withPayload(payloadJson.getBytes(StandardCharsets.UTF_8));

            // Subscriptions
            String subscription0 = "$aws/things/{thingName}/shadow/delete/+";
            subscription0 = subscription0.replace("{thingName}", request.thingName);
            builder.withSubscription(subscription0);

            // Response paths
            ResponsePath.ResponsePathBuilder pathBuilder1 = ResponsePath.builder();
            String responseTopic1 = publishTopic + "/accepted";
            pathBuilder1.withResponseTopic(publishTopic + "/accepted");
            pathBuilder1.withCorrelationTokenJsonPath("clientToken");
            builder.withResponsePath(pathBuilder1.build());

            ResponsePath.ResponsePathBuilder pathBuilder2 = ResponsePath.builder();
            String responseTopic2 = publishTopic + "/rejected";
            pathBuilder2.withResponseTopic(publishTopic + "/rejected");
            pathBuilder2.withCorrelationTokenJsonPath("clientToken");
            builder.withResponsePath(pathBuilder2.build());

            // Submit
            submitOperation(responseFuture, builder.build(), responseTopic1, DeleteShadowResponse.class, responseTopic2, V2ErrorResponse.class, IotShadowV2Client::createV2ErrorResponseException);
        } catch (Exception e) {
            responseFuture.completeExceptionally(createV2ErrorResponseException(e.getMessage(), null));
        }

        return responseFuture;
    }

    /**
     * Gets a named shadow for an AWS IoT thing.
     *
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#get-pub-sub-topic
     *
     * @param request modeled request to perform
     *
     * @return a future that will complete with the corresponding response
     */
    public CompletableFuture<GetShadowResponse> getNamedShadow(GetNamedShadowRequest request) {
        V2ClientFuture<GetShadowResponse> responseFuture = new V2ClientFuture<>();

        try {
            if (request.thingName == null) {
                throw new CrtRuntimeException("GetNamedShadowRequest.thingName cannot be null");
            }

            if (request.shadowName == null) {
                throw new CrtRuntimeException("GetNamedShadowRequest.shadowName cannot be null");
            }

            RequestResponseOperation.RequestResponseOperationBuilder builder = RequestResponseOperation.builder();

            // Correlation Token
            String correlationToken = UUID.randomUUID().toString();
            request.clientToken = correlationToken;
            builder.withCorrelationToken(correlationToken);

            // Publish Topic
            String publishTopic = "$aws/things/{thingName}/shadow/name/{shadowName}/get";
            publishTopic = publishTopic.replace("{thingName}", request.thingName);
            publishTopic = publishTopic.replace("{shadowName}", request.shadowName);
            builder.withPublishTopic(publishTopic);

            // Payload
            String payloadJson = gson.toJson(request);
            builder.withPayload(payloadJson.getBytes(StandardCharsets.UTF_8));

            // Subscriptions
            String subscription0 = "$aws/things/{thingName}/shadow/name/{shadowName}/get/+";
            subscription0 = subscription0.replace("{thingName}", request.thingName);
            subscription0 = subscription0.replace("{shadowName}", request.shadowName);
            builder.withSubscription(subscription0);

            // Response paths
            ResponsePath.ResponsePathBuilder pathBuilder1 = ResponsePath.builder();
            String responseTopic1 = publishTopic + "/accepted";
            pathBuilder1.withResponseTopic(publishTopic + "/accepted");
            pathBuilder1.withCorrelationTokenJsonPath("clientToken");
            builder.withResponsePath(pathBuilder1.build());

            ResponsePath.ResponsePathBuilder pathBuilder2 = ResponsePath.builder();
            String responseTopic2 = publishTopic + "/rejected";
            pathBuilder2.withResponseTopic(publishTopic + "/rejected");
            pathBuilder2.withCorrelationTokenJsonPath("clientToken");
            builder.withResponsePath(pathBuilder2.build());

            // Submit
            submitOperation(responseFuture, builder.build(), responseTopic1, GetShadowResponse.class, responseTopic2, V2ErrorResponse.class, IotShadowV2Client::createV2ErrorResponseException);
        } catch (Exception e) {
            responseFuture.completeExceptionally(createV2ErrorResponseException(e.getMessage(), null));
        }

        return responseFuture;
    }

    /**
     * Gets the (classic) shadow for an AWS IoT thing.
     *
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#get-pub-sub-topic
     *
     * @param request modeled request to perform
     *
     * @return a future that will complete with the corresponding response
     */
    public CompletableFuture<GetShadowResponse> getShadow(GetShadowRequest request) {
        V2ClientFuture<GetShadowResponse> responseFuture = new V2ClientFuture<>();

        try {
            if (request.thingName == null) {
                throw new CrtRuntimeException("GetShadowRequest.thingName cannot be null");
            }

            RequestResponseOperation.RequestResponseOperationBuilder builder = RequestResponseOperation.builder();

            // Correlation Token
            String correlationToken = UUID.randomUUID().toString();
            request.clientToken = correlationToken;
            builder.withCorrelationToken(correlationToken);

            // Publish Topic
            String publishTopic = "$aws/things/{thingName}/shadow/get";
            publishTopic = publishTopic.replace("{thingName}", request.thingName);
            builder.withPublishTopic(publishTopic);

            // Payload
            String payloadJson = gson.toJson(request);
            builder.withPayload(payloadJson.getBytes(StandardCharsets.UTF_8));

            // Subscriptions
            String subscription0 = "$aws/things/{thingName}/shadow/get/+";
            subscription0 = subscription0.replace("{thingName}", request.thingName);
            builder.withSubscription(subscription0);

            // Response paths
            ResponsePath.ResponsePathBuilder pathBuilder1 = ResponsePath.builder();
            String responseTopic1 = publishTopic + "/accepted";
            pathBuilder1.withResponseTopic(publishTopic + "/accepted");
            pathBuilder1.withCorrelationTokenJsonPath("clientToken");
            builder.withResponsePath(pathBuilder1.build());

            ResponsePath.ResponsePathBuilder pathBuilder2 = ResponsePath.builder();
            String responseTopic2 = publishTopic + "/rejected";
            pathBuilder2.withResponseTopic(publishTopic + "/rejected");
            pathBuilder2.withCorrelationTokenJsonPath("clientToken");
            builder.withResponsePath(pathBuilder2.build());

            // Submit
            submitOperation(responseFuture, builder.build(), responseTopic1, GetShadowResponse.class, responseTopic2, V2ErrorResponse.class, IotShadowV2Client::createV2ErrorResponseException);
        } catch (Exception e) {
            responseFuture.completeExceptionally(createV2ErrorResponseException(e.getMessage(), null));
        }

        return responseFuture;
    }

    /**
     * Update a named shadow for a device.
     *
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#update-pub-sub-topic
     *
     * @param request modeled request to perform
     *
     * @return a future that will complete with the corresponding response
     */
    public CompletableFuture<UpdateShadowResponse> updateNamedShadow(UpdateNamedShadowRequest request) {
        V2ClientFuture<UpdateShadowResponse> responseFuture = new V2ClientFuture<>();

        try {
            if (request.thingName == null) {
                throw new CrtRuntimeException("UpdateNamedShadowRequest.thingName cannot be null");
            }

            if (request.shadowName == null) {
                throw new CrtRuntimeException("UpdateNamedShadowRequest.shadowName cannot be null");
            }

            RequestResponseOperation.RequestResponseOperationBuilder builder = RequestResponseOperation.builder();

            // Correlation Token
            String correlationToken = UUID.randomUUID().toString();
            request.clientToken = correlationToken;
            builder.withCorrelationToken(correlationToken);

            // Publish Topic
            String publishTopic = "$aws/things/{thingName}/shadow/name/{shadowName}/update";
            publishTopic = publishTopic.replace("{thingName}", request.thingName);
            publishTopic = publishTopic.replace("{shadowName}", request.shadowName);
            builder.withPublishTopic(publishTopic);

            // Payload
            String payloadJson = gson.toJson(request);
            builder.withPayload(payloadJson.getBytes(StandardCharsets.UTF_8));

            // Subscriptions
            String subscription0 = "$aws/things/{thingName}/shadow/name/{shadowName}/update/accepted";
            subscription0 = subscription0.replace("{thingName}", request.thingName);
            subscription0 = subscription0.replace("{shadowName}", request.shadowName);
            builder.withSubscription(subscription0);
            String subscription1 = "$aws/things/{thingName}/shadow/name/{shadowName}/update/rejected";
            subscription1 = subscription1.replace("{thingName}", request.thingName);
            subscription1 = subscription1.replace("{shadowName}", request.shadowName);
            builder.withSubscription(subscription1);

            // Response paths
            ResponsePath.ResponsePathBuilder pathBuilder1 = ResponsePath.builder();
            String responseTopic1 = publishTopic + "/accepted";
            pathBuilder1.withResponseTopic(publishTopic + "/accepted");
            pathBuilder1.withCorrelationTokenJsonPath("clientToken");
            builder.withResponsePath(pathBuilder1.build());

            ResponsePath.ResponsePathBuilder pathBuilder2 = ResponsePath.builder();
            String responseTopic2 = publishTopic + "/rejected";
            pathBuilder2.withResponseTopic(publishTopic + "/rejected");
            pathBuilder2.withCorrelationTokenJsonPath("clientToken");
            builder.withResponsePath(pathBuilder2.build());

            // Submit
            submitOperation(responseFuture, builder.build(), responseTopic1, UpdateShadowResponse.class, responseTopic2, V2ErrorResponse.class, IotShadowV2Client::createV2ErrorResponseException);
        } catch (Exception e) {
            responseFuture.completeExceptionally(createV2ErrorResponseException(e.getMessage(), null));
        }

        return responseFuture;
    }

    /**
     * Update a device's (classic) shadow.
     *
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#update-pub-sub-topic
     *
     * @param request modeled request to perform
     *
     * @return a future that will complete with the corresponding response
     */
    public CompletableFuture<UpdateShadowResponse> updateShadow(UpdateShadowRequest request) {
        V2ClientFuture<UpdateShadowResponse> responseFuture = new V2ClientFuture<>();

        try {
            if (request.thingName == null) {
                throw new CrtRuntimeException("UpdateShadowRequest.thingName cannot be null");
            }

            RequestResponseOperation.RequestResponseOperationBuilder builder = RequestResponseOperation.builder();

            // Correlation Token
            String correlationToken = UUID.randomUUID().toString();
            request.clientToken = correlationToken;
            builder.withCorrelationToken(correlationToken);

            // Publish Topic
            String publishTopic = "$aws/things/{thingName}/shadow/update";
            publishTopic = publishTopic.replace("{thingName}", request.thingName);
            builder.withPublishTopic(publishTopic);

            // Payload
            String payloadJson = gson.toJson(request);
            builder.withPayload(payloadJson.getBytes(StandardCharsets.UTF_8));

            // Subscriptions
            String subscription0 = "$aws/things/{thingName}/shadow/update/accepted";
            subscription0 = subscription0.replace("{thingName}", request.thingName);
            builder.withSubscription(subscription0);
            String subscription1 = "$aws/things/{thingName}/shadow/update/rejected";
            subscription1 = subscription1.replace("{thingName}", request.thingName);
            builder.withSubscription(subscription1);

            // Response paths
            ResponsePath.ResponsePathBuilder pathBuilder1 = ResponsePath.builder();
            String responseTopic1 = publishTopic + "/accepted";
            pathBuilder1.withResponseTopic(publishTopic + "/accepted");
            pathBuilder1.withCorrelationTokenJsonPath("clientToken");
            builder.withResponsePath(pathBuilder1.build());

            ResponsePath.ResponsePathBuilder pathBuilder2 = ResponsePath.builder();
            String responseTopic2 = publishTopic + "/rejected";
            pathBuilder2.withResponseTopic(publishTopic + "/rejected");
            pathBuilder2.withCorrelationTokenJsonPath("clientToken");
            builder.withResponsePath(pathBuilder2.build());

            // Submit
            submitOperation(responseFuture, builder.build(), responseTopic1, UpdateShadowResponse.class, responseTopic2, V2ErrorResponse.class, IotShadowV2Client::createV2ErrorResponseException);
        } catch (Exception e) {
            responseFuture.completeExceptionally(createV2ErrorResponseException(e.getMessage(), null));
        }

        return responseFuture;
    }

    /**
     * Create a stream for NamedShadowDelta events for a named shadow of an AWS IoT thing.
     *
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#update-delta-pub-sub-topic
     *
     * @param request modeled streaming operation subscription configuration
     * @param options set of callbacks that the operation should invoke in response to related events
     *
     * @return a streaming operation which will invoke a callback every time a message is received on the
     *    associated MQTT topic
     */
    public StreamingOperation createNamedShadowDeltaUpdatedStream(NamedShadowDeltaUpdatedSubscriptionRequest request, V2ClientStreamOptions<ShadowDeltaUpdatedEvent> options) {
        String topic = "$aws/things/{thingName}/shadow/name/{shadowName}/update/delta";

        if (request.thingName == null) {
            throw new CrtRuntimeException("NamedShadowDeltaUpdatedSubscriptionRequest.thingName cannot be null");
        }
        topic = topic.replace("{thingName}", request.thingName);

        if (request.shadowName == null) {
            throw new CrtRuntimeException("NamedShadowDeltaUpdatedSubscriptionRequest.shadowName cannot be null");
        }
        topic = topic.replace("{shadowName}", request.shadowName);

        StreamingOperationOptions innerOptions = StreamingOperationOptions.builder()
            .withTopic(topic)
            .withSubscriptionStatusEventCallback(options.subscriptionEventHandler())
            .withIncomingPublishEventCallback((event) -> {
                try {
                    ShadowDeltaUpdatedEvent response = createShadowDeltaUpdatedEvent(event);
                    options.streamEventHandler().accept(response);
                } catch (Exception e) {
                    V2DeserializationFailureEvent failureEvent = V2DeserializationFailureEvent.builder()
                        .withCause(e)
                        .withPayload(event.getPayload())
                        .withTopic(event.getTopic())
                        .build();
                    options.deserializationFailureHandler().accept(failureEvent);
                }
            })
            .build();

        return this.rrClient.createStream(innerOptions);
    }

    /**
     * Create a stream for ShadowUpdated events for a named shadow of an AWS IoT thing.
     *
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#update-documents-pub-sub-topic
     *
     * @param request modeled streaming operation subscription configuration
     * @param options set of callbacks that the operation should invoke in response to related events
     *
     * @return a streaming operation which will invoke a callback every time a message is received on the
     *    associated MQTT topic
     */
    public StreamingOperation createNamedShadowUpdatedStream(NamedShadowUpdatedSubscriptionRequest request, V2ClientStreamOptions<ShadowUpdatedEvent> options) {
        String topic = "$aws/things/{thingName}/shadow/name/{shadowName}/update/documents";

        if (request.thingName == null) {
            throw new CrtRuntimeException("NamedShadowUpdatedSubscriptionRequest.thingName cannot be null");
        }
        topic = topic.replace("{thingName}", request.thingName);

        if (request.shadowName == null) {
            throw new CrtRuntimeException("NamedShadowUpdatedSubscriptionRequest.shadowName cannot be null");
        }
        topic = topic.replace("{shadowName}", request.shadowName);

        StreamingOperationOptions innerOptions = StreamingOperationOptions.builder()
            .withTopic(topic)
            .withSubscriptionStatusEventCallback(options.subscriptionEventHandler())
            .withIncomingPublishEventCallback((event) -> {
                try {
                    ShadowUpdatedEvent response = createShadowUpdatedEvent(event);
                    options.streamEventHandler().accept(response);
                } catch (Exception e) {
                    V2DeserializationFailureEvent failureEvent = V2DeserializationFailureEvent.builder()
                        .withCause(e)
                        .withPayload(event.getPayload())
                        .withTopic(event.getTopic())
                        .build();
                    options.deserializationFailureHandler().accept(failureEvent);
                }
            })
            .build();

        return this.rrClient.createStream(innerOptions);
    }

    /**
     * Create a stream for ShadowDelta events for the (classic) shadow of an AWS IoT thing.
     *
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#update-delta-pub-sub-topic
     *
     * @param request modeled streaming operation subscription configuration
     * @param options set of callbacks that the operation should invoke in response to related events
     *
     * @return a streaming operation which will invoke a callback every time a message is received on the
     *    associated MQTT topic
     */
    public StreamingOperation createShadowDeltaUpdatedStream(ShadowDeltaUpdatedSubscriptionRequest request, V2ClientStreamOptions<ShadowDeltaUpdatedEvent> options) {
        String topic = "$aws/things/{thingName}/shadow/update/delta";

        if (request.thingName == null) {
            throw new CrtRuntimeException("ShadowDeltaUpdatedSubscriptionRequest.thingName cannot be null");
        }
        topic = topic.replace("{thingName}", request.thingName);

        StreamingOperationOptions innerOptions = StreamingOperationOptions.builder()
            .withTopic(topic)
            .withSubscriptionStatusEventCallback(options.subscriptionEventHandler())
            .withIncomingPublishEventCallback((event) -> {
                try {
                    ShadowDeltaUpdatedEvent response = createShadowDeltaUpdatedEvent(event);
                    options.streamEventHandler().accept(response);
                } catch (Exception e) {
                    V2DeserializationFailureEvent failureEvent = V2DeserializationFailureEvent.builder()
                        .withCause(e)
                        .withPayload(event.getPayload())
                        .withTopic(event.getTopic())
                        .build();
                    options.deserializationFailureHandler().accept(failureEvent);
                }
            })
            .build();

        return this.rrClient.createStream(innerOptions);
    }

    /**
     * Create a stream for ShadowUpdated events for the (classic) shadow of an AWS IoT thing.
     *
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#update-documents-pub-sub-topic
     *
     * @param request modeled streaming operation subscription configuration
     * @param options set of callbacks that the operation should invoke in response to related events
     *
     * @return a streaming operation which will invoke a callback every time a message is received on the
     *    associated MQTT topic
     */
    public StreamingOperation createShadowUpdatedStream(ShadowUpdatedSubscriptionRequest request, V2ClientStreamOptions<ShadowUpdatedEvent> options) {
        String topic = "$aws/things/{thingName}/shadow/update/documents";

        if (request.thingName == null) {
            throw new CrtRuntimeException("ShadowUpdatedSubscriptionRequest.thingName cannot be null");
        }
        topic = topic.replace("{thingName}", request.thingName);

        StreamingOperationOptions innerOptions = StreamingOperationOptions.builder()
            .withTopic(topic)
            .withSubscriptionStatusEventCallback(options.subscriptionEventHandler())
            .withIncomingPublishEventCallback((event) -> {
                try {
                    ShadowUpdatedEvent response = createShadowUpdatedEvent(event);
                    options.streamEventHandler().accept(response);
                } catch (Exception e) {
                    V2DeserializationFailureEvent failureEvent = V2DeserializationFailureEvent.builder()
                        .withCause(e)
                        .withPayload(event.getPayload())
                        .withTopic(event.getTopic())
                        .build();
                    options.deserializationFailureHandler().accept(failureEvent);
                }
            })
            .build();

        return this.rrClient.createStream(innerOptions);
    }

    static private Throwable createV2ErrorResponseException(String message, V2ErrorResponse errorResponse) {
        return new V2ErrorResponseException(message, errorResponse);
    }

    private <T, E> void submitOperation(V2ClientFuture<T> finalFuture, RequestResponseOperation operation, String responseTopic, Class<T> responseClass, String errorTopic, Class<E> errorClass, BiFunction<String, E, Throwable> exceptionFactory) {
        try {
            CompletableFuture<MqttRequestResponse> responseFuture = this.rrClient.submitRequest(operation);
            CompletableFuture<MqttRequestResponse> compositeFuture = responseFuture.whenComplete((res, ex) -> {
                if (ex != null) {
                    finalFuture.completeExceptionally(exceptionFactory.apply(ex.getMessage(), null));
                } else if (res.getTopic().equals(responseTopic)) {
                    try {
                        String payload = new String(res.getPayload(), StandardCharsets.UTF_8);
                        T response = this.gson.fromJson(payload, responseClass);
                        finalFuture.complete(response);
                    } catch (Exception e) {
                        finalFuture.completeExceptionally(exceptionFactory.apply(e.getMessage(), null));
                    }
                } else if (res.getTopic().equals(errorTopic)) {
                    try {
                        String payload = new String(res.getPayload(), StandardCharsets.UTF_8);
                        E error = this.gson.fromJson(payload, errorClass);
                        finalFuture.completeExceptionally(exceptionFactory.apply("Request-response operation failure", error));
                    } catch (Exception e) {
                        finalFuture.completeExceptionally(exceptionFactory.apply(e.getMessage(), null));
                    }
                } else {
                    finalFuture.completeExceptionally(exceptionFactory.apply("Request-response operation completed on unknown topic: " + res.getTopic(), null));
                }
            });
            finalFuture.setTriggeringFuture(compositeFuture);
        } catch (Exception ex) {
            finalFuture.completeExceptionally(exceptionFactory.apply(ex.getMessage(), null));
        }
    }

}
