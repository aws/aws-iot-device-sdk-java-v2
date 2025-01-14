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

    static public IotShadowV2Client newFromMqtt5(Mqtt5Client protocolClient, MqttRequestResponseClientOptions options) {
        MqttRequestResponseClient rrClient = new MqttRequestResponseClient(protocolClient, options);
        return new IotShadowV2Client(rrClient);
    }

    static public IotShadowV2Client newFromMqtt311(MqttClientConnection protocolClient, MqttRequestResponseClientOptions options) {
        MqttRequestResponseClient rrClient = new MqttRequestResponseClient(protocolClient, options);
        return new IotShadowV2Client(rrClient);
    }

    public void close() {
        this.rrClient.decRef();
        this.rrClient = null;
    }

    public CompletableFuture<GetShadowResponse> getShadow(GetShadowRequest request) {
        V2ClientFuture<GetShadowResponse> finalFuture = new V2ClientFuture<>();

        try {
            if (request.thingName == null) {
                throw new CrtRuntimeException("thingName cannot be null");
            }

            RequestResponseOperation.RequestResponseOperationBuilder builder = RequestResponseOperation.builder();

            String correlationToken = UUID.randomUUID().toString();
            request.clientToken = correlationToken;
            builder.withCorrelationToken(correlationToken);

            String topic = "$aws/things/{thingName}/shadow/get";
            topic = topic.replace("{thingName}", request.thingName);
            builder.withPublishTopic(topic);

            String payloadJson = gson.toJson(request);
            builder.withPayload(payloadJson.getBytes(StandardCharsets.UTF_8));

            String subscription1 = "$aws/things/{thingName}/shadow/get/+";
            subscription1 = subscription1.replace("{thingName}", request.thingName);
            builder.withSubscription(subscription1);

            ResponsePath.ResponsePathBuilder pathBuilder1 = ResponsePath.builder();
            String responseTopic1 = "$aws/things/{thingName}/shadow/get/accepted";
            responseTopic1 = responseTopic1.replace("{thingName}", request.thingName);

            pathBuilder1.withResponseTopic(responseTopic1);
            pathBuilder1.withCorrelationTokenJsonPath("clientToken");
            builder.withResponsePath(pathBuilder1.build());

            ResponsePath.ResponsePathBuilder pathBuilder2 = ResponsePath.builder();
            String responseTopic2 = "$aws/things/{thingName}/shadow/get/rejected";
            responseTopic2 = responseTopic2.replace("{thingName}", request.thingName);

            pathBuilder2.withResponseTopic(responseTopic2);
            pathBuilder2.withCorrelationTokenJsonPath("clientToken");
            builder.withResponsePath(pathBuilder2.build());

            submitOperation(finalFuture, builder.build(), responseTopic1, GetShadowResponse.class, responseTopic2, V2ErrorResponse.class, IotShadowV2Client::createV2ErrorResponseException);
        } catch (Exception e) {
            finalFuture.completeExceptionally(createV2ErrorResponseException(e.getMessage(), null));
        }

        return finalFuture;
    }

    public CompletableFuture<DeleteShadowResponse> deleteShadow(DeleteShadowRequest request) {
        V2ClientFuture<DeleteShadowResponse> finalFuture = new V2ClientFuture<>();

        try {
            if (request.thingName == null) {
                throw new CrtRuntimeException("thingName cannot be null");
            }

            RequestResponseOperation.RequestResponseOperationBuilder builder = RequestResponseOperation.builder();

            String correlationToken = UUID.randomUUID().toString();
            request.clientToken = correlationToken;
            builder.withCorrelationToken(correlationToken);

            String topic = "$aws/things/{thingName}/shadow/delete";
            topic = topic.replace("{thingName}", request.thingName);
            builder.withPublishTopic(topic);

            String payloadJson = gson.toJson(request);
            builder.withPayload(payloadJson.getBytes(StandardCharsets.UTF_8));

            String subscription1 = "$aws/things/{thingName}/shadow/delete/+";
            subscription1 = subscription1.replace("{thingName}", request.thingName);
            builder.withSubscription(subscription1);

            ResponsePath.ResponsePathBuilder pathBuilder1 = ResponsePath.builder();
            String responseTopic1 = "$aws/things/{thingName}/shadow/delete/accepted";
            responseTopic1 = responseTopic1.replace("{thingName}", request.thingName);

            pathBuilder1.withResponseTopic(responseTopic1);
            pathBuilder1.withCorrelationTokenJsonPath("clientToken");
            builder.withResponsePath(pathBuilder1.build());

            ResponsePath.ResponsePathBuilder pathBuilder2 = ResponsePath.builder();
            String responseTopic2 = "$aws/things/{thingName}/shadow/delete/rejected";
            responseTopic2 = responseTopic2.replace("{thingName}", request.thingName);

            pathBuilder2.withResponseTopic(responseTopic2);
            pathBuilder2.withCorrelationTokenJsonPath("clientToken");
            builder.withResponsePath(pathBuilder2.build());

            submitOperation(finalFuture, builder.build(), responseTopic1, DeleteShadowResponse.class, responseTopic2, V2ErrorResponse.class, IotShadowV2Client::createV2ErrorResponseException);
        } catch (Exception e) {
            finalFuture.completeExceptionally(createV2ErrorResponseException(e.getMessage(), null));
        }

        return finalFuture;
    }

    public CompletableFuture<UpdateShadowResponse> updateShadow(UpdateShadowRequest request) {
        V2ClientFuture<UpdateShadowResponse> finalFuture = new V2ClientFuture<>();

        try {
            if (request.thingName == null) {
                throw new CrtRuntimeException("thingName cannot be null");
            }

            RequestResponseOperation.RequestResponseOperationBuilder builder = RequestResponseOperation.builder();

            String correlationToken = UUID.randomUUID().toString();
            request.clientToken = correlationToken;
            builder.withCorrelationToken(correlationToken);

            String publishTopic = "$aws/things/{thingName}/shadow/update";
            publishTopic = publishTopic.replace("{thingName}", request.thingName);
            builder.withPublishTopic(publishTopic);

            String payloadJson = gson.toJson(request);
            builder.withPayload(payloadJson.getBytes(StandardCharsets.UTF_8));

            String subscription1 = "$aws/things/{thingName}/shadow/update/accepted";
            subscription1 = subscription1.replace("{thingName}", request.thingName);
            builder.withSubscription(subscription1);

            String subscription2 = "$aws/things/{thingName}/shadow/update/rejected";
            subscription2 = subscription2.replace("{thingName}", request.thingName);
            builder.withSubscription(subscription2);

            ResponsePath.ResponsePathBuilder pathBuilder1 = ResponsePath.builder();
            String responseTopic1 = "$aws/things/{thingName}/shadow/update/accepted";
            responseTopic1 = responseTopic1.replace("{thingName}", request.thingName);

            pathBuilder1.withResponseTopic(responseTopic1);
            pathBuilder1.withCorrelationTokenJsonPath("clientToken");
            builder.withResponsePath(pathBuilder1.build());

            ResponsePath.ResponsePathBuilder pathBuilder2 = ResponsePath.builder();
            String responseTopic2 = "$aws/things/{thingName}/shadow/update/rejected";
            responseTopic2 = responseTopic2.replace("{thingName}", request.thingName);

            pathBuilder2.withResponseTopic(responseTopic2);
            pathBuilder2.withCorrelationTokenJsonPath("clientToken");
            builder.withResponsePath(pathBuilder2.build());

            submitOperation(finalFuture, builder.build(), responseTopic1, UpdateShadowResponse.class, responseTopic2, V2ErrorResponse.class, IotShadowV2Client::createV2ErrorResponseException);
        } catch (Exception e) {
            finalFuture.completeExceptionally(createV2ErrorResponseException(e.getMessage(), null));
        }

        return finalFuture;
    }

    public StreamingOperation createShadowUpdatedEventStream(ShadowUpdatedSubscriptionRequest request, V2ClientStreamOptions<ShadowUpdatedEvent> options) {
        if (request.thingName == null) {
            throw new CrtRuntimeException("thingName cannot be null");
        }

        String topic = "$aws/things/{thingName}/shadow/update/documents";
        topic = topic.replace("{thingName}", request.thingName);
        StreamingOperationOptions innerOptions = StreamingOperationOptions.builder()
                .withTopic(topic)
                .withSubscriptionStatusEventCallback(options.subscriptionEventHandler())
                .withIncomingPublishEventCallback((event) -> {
                    try {
                        String payload = new String(event.getPayload(), StandardCharsets.UTF_8);
                        ShadowUpdatedEvent response = this.gson.fromJson(payload, ShadowUpdatedEvent.class);
                        options.streamEventHandler().accept(response);
                    } catch (Exception e) {
                        V2DeserializationFailureEvent failureEvent = V2DeserializationFailureEvent.builder()
                            .withCause(e)
                            .withPayload(event.getPayload())
                            .build();
                        options.deserializationFailureHandler().accept(failureEvent);
                    }
                })
                .build();

        return this.rrClient.createStream(innerOptions);
    }

    public StreamingOperation createShadowDeltaUpdatedEventStream(ShadowDeltaUpdatedSubscriptionRequest request, V2ClientStreamOptions<ShadowDeltaUpdatedEvent> options) {
        if (request.thingName == null) {
            throw new CrtRuntimeException("thingName cannot be null");
        }

        String topic = "$aws/things/{thingName}/shadow/update/delta";
        topic = topic.replace("{thingName}", request.thingName);
        StreamingOperationOptions innerOptions = StreamingOperationOptions.builder()
            .withTopic(topic)
            .withSubscriptionStatusEventCallback(options.subscriptionEventHandler())
            .withIncomingPublishEventCallback((event) -> {
                try {
                    String payload = new String(event.getPayload(), StandardCharsets.UTF_8);
                    ShadowDeltaUpdatedEvent response = this.gson.fromJson(payload, ShadowDeltaUpdatedEvent.class);
                    options.streamEventHandler().accept(response);
                } catch (Exception e) {
                    V2DeserializationFailureEvent failureEvent = V2DeserializationFailureEvent.builder()
                        .withCause(e)
                        .withPayload(event.getPayload())
                        .build();
                    options.deserializationFailureHandler().accept(failureEvent);
                }
            })
            .build();

        return this.rrClient.createStream(innerOptions);
    }

    static private Throwable createV2ErrorResponseException(String message, V2ErrorResponse errorResponse) {
        if (errorResponse != null) {
            return new V2ErrorResponseException(message, errorResponse);
        } else {
            return new V2ErrorResponseException(message);
        }
    }

    private <T, E> void submitOperation(V2ClientFuture<T> finalFuture, RequestResponseOperation operation, String responseTopic, Class<T> responseClass, String errorTopic, Class<E> errorClass, BiFunction<String, E, Throwable> exceptionFactory) {
        try {
            CompletableFuture<MqttRequestResponse> responseFuture = this.rrClient.submitRequest(operation);
            CompletableFuture<MqttRequestResponse> compositeFuture = responseFuture.whenComplete((res, ex) -> {
                if (ex != null) {
                    finalFuture.completeExceptionally(exceptionFactory.apply(ex.getMessage(), null));
                } else if (res.getTopic().equals(responseTopic)){
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
