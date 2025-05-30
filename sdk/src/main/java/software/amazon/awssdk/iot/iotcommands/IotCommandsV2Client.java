/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.iot.iotcommands;

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
import software.amazon.awssdk.iot.iotcommands.model.*;

/**
 * The AWS IoT commands service is used to send an instruction from the cloud to a device that is connected to AWS IoT.
 *
 * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/iot-remote-command.html
 *
*/
public class IotCommandsV2Client implements AutoCloseable {

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
        gson.registerTypeAdapter(CommandExecutionStatus.class, new EnumSerializer<CommandExecutionStatus>());
        gson.registerTypeAdapter(DeviceType.class, new EnumSerializer<DeviceType>());
        gson.registerTypeAdapter(RejectedErrorCode.class, new EnumSerializer<RejectedErrorCode>());
    }

    private IotCommandsV2Client(MqttRequestResponseClient rrClient) {
        this.rrClient = rrClient;
        this.gson = createGson();
    }

    /**
     * Constructs a new IotCommandsV2Client, using an MQTT5 client as transport
     *
     * @param protocolClient the MQTT5 client to use
     * @param options configuration options to use
     */
    static public IotCommandsV2Client newFromMqtt5(Mqtt5Client protocolClient, MqttRequestResponseClientOptions options) {
        MqttRequestResponseClient rrClient = new MqttRequestResponseClient(protocolClient, options);
        return new IotCommandsV2Client(rrClient);
    }

    /**
     * Constructs a new IotCommandsV2Client, using an MQTT311 client as transport
     *
     * @param protocolClient the MQTT311 client to use
     * @param options configuration options to use
     */
    static public IotCommandsV2Client newFromMqtt311(MqttClientConnection protocolClient, MqttRequestResponseClientOptions options) {
        MqttRequestResponseClient rrClient = new MqttRequestResponseClient(protocolClient, options);
        return new IotCommandsV2Client(rrClient);
    }

    /**
     * Releases all resources used by the client.  It is not valid to invoke operations
     * on the client after it has been closed.
     */
    public void close() {
        this.rrClient.decRef();
        this.rrClient = null;
    }

    private CommandExecutionEvent createCommandExecutionEvent(IncomingPublishEvent publishEvent) {
        CommandExecutionEvent event = new CommandExecutionEvent();
        event.executionId = publishEvent.getTopic().split("/")[5];
        event.payload = publishEvent.getPayload();
        String contentType = publishEvent.getContentType();
        if (contentType != null) {
            event.contentType = contentType;
        }
        Long messageExpiryIntervalSeconds = publishEvent.getMessageExpiryIntervalSeconds();
        if (messageExpiryIntervalSeconds != null) {
            event.timeout = Math.toIntExact(messageExpiryIntervalSeconds);
        }
        return event;
    }

    /**
     * Update the status of a command execution.
     *
     *
     * @param request modeled request to perform
     *
     * @return a future that will complete with the corresponding response
     */
    public CompletableFuture<UpdateCommandExecutionResponse> updateCommandExecution(UpdateCommandExecutionRequest request) {
        V2ClientFuture<UpdateCommandExecutionResponse> responseFuture = new V2ClientFuture<>();

        try {
            if (request.deviceType == null) {
                throw new CrtRuntimeException("UpdateCommandExecutionRequest.deviceType cannot be null");
            }

            if (request.deviceId == null) {
                throw new CrtRuntimeException("UpdateCommandExecutionRequest.deviceId cannot be null");
            }

            if (request.executionId == null) {
                throw new CrtRuntimeException("UpdateCommandExecutionRequest.executionId cannot be null");
            }

            RequestResponseOperation.RequestResponseOperationBuilder builder = RequestResponseOperation.builder();


            // Publish Topic
            String publishTopic = "$aws/commands/{deviceType}/{deviceId}/executions/{executionId}/response/json";
            publishTopic = publishTopic.replace("{deviceType}", request.deviceType.toString());
            publishTopic = publishTopic.replace("{deviceId}", request.deviceId);
            publishTopic = publishTopic.replace("{executionId}", request.executionId);
            builder.withPublishTopic(publishTopic);

            // Payload
            String payloadJson = gson.toJson(request);
            builder.withPayload(payloadJson.getBytes(StandardCharsets.UTF_8));

            // Subscriptions
            String subscription0 = "$aws/commands/{deviceType}/{deviceId}/executions/{executionId}/response/accepted/json";
            subscription0 = subscription0.replace("{deviceType}", request.deviceType.toString());
            subscription0 = subscription0.replace("{deviceId}", request.deviceId);
            subscription0 = subscription0.replace("{executionId}", request.executionId);
            builder.withSubscription(subscription0);
            String subscription1 = "$aws/commands/{deviceType}/{deviceId}/executions/{executionId}/response/rejected/json";
            subscription1 = subscription1.replace("{deviceType}", request.deviceType.toString());
            subscription1 = subscription1.replace("{deviceId}", request.deviceId);
            subscription1 = subscription1.replace("{executionId}", request.executionId);
            builder.withSubscription(subscription1);

            // Response paths
            ResponsePath.ResponsePathBuilder pathBuilder1 = ResponsePath.builder();
            String responseTopic1 = "$aws/commands/{deviceType}/{deviceId}/executions/{executionId}/response/accepted/json";
            responseTopic1 = responseTopic1.replace("{deviceType}", request.deviceType.toString());
            responseTopic1 = responseTopic1.replace("{deviceId}", request.deviceId);
            responseTopic1 = responseTopic1.replace("{executionId}", request.executionId);
            pathBuilder1.withResponseTopic(responseTopic1);
            builder.withResponsePath(pathBuilder1.build());

            ResponsePath.ResponsePathBuilder pathBuilder2 = ResponsePath.builder();
            String responseTopic2 = "$aws/commands/{deviceType}/{deviceId}/executions/{executionId}/response/rejected/json";
            responseTopic2 = responseTopic2.replace("{deviceType}", request.deviceType.toString());
            responseTopic2 = responseTopic2.replace("{deviceId}", request.deviceId);
            responseTopic2 = responseTopic2.replace("{executionId}", request.executionId);
            pathBuilder2.withResponseTopic(responseTopic2);
            builder.withResponsePath(pathBuilder2.build());

            // Submit
            submitOperation(responseFuture, builder.build(), responseTopic1, UpdateCommandExecutionResponse.class, responseTopic2, V2ErrorResponse.class, IotCommandsV2Client::createV2ErrorResponseException);
        } catch (Exception e) {
            responseFuture.completeExceptionally(createV2ErrorResponseException(e.getMessage(), null));
        }

        return responseFuture;
    }

    /**
     * Creates a stream of CommandExecution notifications for a given IoT thing.
     *
     *
     * @param request modeled streaming operation subscription configuration
     * @param options set of callbacks that the operation should invoke in response to related events
     *
     * @return a streaming operation which will invoke a callback every time a message is received on the
     *    associated MQTT topic
     */
    public StreamingOperation createCommandExecutionsCborPayloadStream(CommandExecutionsSubscriptionRequest request, V2ClientStreamOptions<CommandExecutionEvent> options) {
        String topic = "$aws/commands/{deviceType}/{deviceId}/executions/+/request/cbor";

        if (request.deviceType == null) {
            throw new CrtRuntimeException("CommandExecutionsSubscriptionRequest.deviceType cannot be null");
        }
        topic = topic.replace("{deviceType}", request.deviceType.toString());

        if (request.deviceId == null) {
            throw new CrtRuntimeException("CommandExecutionsSubscriptionRequest.deviceId cannot be null");
        }
        topic = topic.replace("{deviceId}", request.deviceId);

        StreamingOperationOptions innerOptions = StreamingOperationOptions.builder()
            .withTopic(topic)
            .withSubscriptionStatusEventCallback(options.subscriptionEventHandler())
            .withIncomingPublishEventCallback((event) -> {
                try {
                    CommandExecutionEvent response = createCommandExecutionEvent(event);
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
     * Creates a stream of CommandExecution notifications for a given IoT thing.
     *
     *
     * @param request modeled streaming operation subscription configuration
     * @param options set of callbacks that the operation should invoke in response to related events
     *
     * @return a streaming operation which will invoke a callback every time a message is received on the
     *    associated MQTT topic
     */
    public StreamingOperation createCommandExecutionsGenericPayloadStream(CommandExecutionsSubscriptionRequest request, V2ClientStreamOptions<CommandExecutionEvent> options) {
        String topic = "$aws/commands/{deviceType}/{deviceId}/executions/+/request";

        if (request.deviceType == null) {
            throw new CrtRuntimeException("CommandExecutionsSubscriptionRequest.deviceType cannot be null");
        }
        topic = topic.replace("{deviceType}", request.deviceType.toString());

        if (request.deviceId == null) {
            throw new CrtRuntimeException("CommandExecutionsSubscriptionRequest.deviceId cannot be null");
        }
        topic = topic.replace("{deviceId}", request.deviceId);

        StreamingOperationOptions innerOptions = StreamingOperationOptions.builder()
            .withTopic(topic)
            .withSubscriptionStatusEventCallback(options.subscriptionEventHandler())
            .withIncomingPublishEventCallback((event) -> {
                try {
                    CommandExecutionEvent response = createCommandExecutionEvent(event);
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
     * Creates a stream of CommandExecution notifications for a given IoT thing.
     *
     *
     * @param request modeled streaming operation subscription configuration
     * @param options set of callbacks that the operation should invoke in response to related events
     *
     * @return a streaming operation which will invoke a callback every time a message is received on the
     *    associated MQTT topic
     */
    public StreamingOperation createCommandExecutionsJsonPayloadStream(CommandExecutionsSubscriptionRequest request, V2ClientStreamOptions<CommandExecutionEvent> options) {
        String topic = "$aws/commands/{deviceType}/{deviceId}/executions/+/request/json";

        if (request.deviceType == null) {
            throw new CrtRuntimeException("CommandExecutionsSubscriptionRequest.deviceType cannot be null");
        }
        topic = topic.replace("{deviceType}", request.deviceType.toString());

        if (request.deviceId == null) {
            throw new CrtRuntimeException("CommandExecutionsSubscriptionRequest.deviceId cannot be null");
        }
        topic = topic.replace("{deviceId}", request.deviceId);

        StreamingOperationOptions innerOptions = StreamingOperationOptions.builder()
            .withTopic(topic)
            .withSubscriptionStatusEventCallback(options.subscriptionEventHandler())
            .withIncomingPublishEventCallback((event) -> {
                try {
                    CommandExecutionEvent response = createCommandExecutionEvent(event);
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
