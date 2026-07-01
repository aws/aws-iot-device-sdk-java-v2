/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.iot.iotshadow;

import software.amazon.awssdk.iot.iotshadow.model.DeleteNamedShadowRequest;
import software.amazon.awssdk.iot.iotshadow.model.DeleteNamedShadowSubscriptionRequest;
import software.amazon.awssdk.iot.iotshadow.model.DeleteShadowRequest;
import software.amazon.awssdk.iot.iotshadow.model.DeleteShadowResponse;
import software.amazon.awssdk.iot.iotshadow.model.DeleteShadowSubscriptionRequest;
import software.amazon.awssdk.iot.iotshadow.model.ErrorResponse;
import software.amazon.awssdk.iot.iotshadow.model.GetNamedShadowRequest;
import software.amazon.awssdk.iot.iotshadow.model.GetNamedShadowSubscriptionRequest;
import software.amazon.awssdk.iot.iotshadow.model.GetShadowRequest;
import software.amazon.awssdk.iot.iotshadow.model.GetShadowResponse;
import software.amazon.awssdk.iot.iotshadow.model.GetShadowSubscriptionRequest;
import software.amazon.awssdk.iot.iotshadow.model.NamedShadowDeltaUpdatedSubscriptionRequest;
import software.amazon.awssdk.iot.iotshadow.model.NamedShadowUpdatedSubscriptionRequest;
import software.amazon.awssdk.iot.iotshadow.model.ShadowDeltaUpdatedEvent;
import software.amazon.awssdk.iot.iotshadow.model.ShadowDeltaUpdatedSubscriptionRequest;
import software.amazon.awssdk.iot.iotshadow.model.ShadowMetadata;
import software.amazon.awssdk.iot.iotshadow.model.ShadowState;
import software.amazon.awssdk.iot.iotshadow.model.ShadowStateWithDelta;
import software.amazon.awssdk.iot.iotshadow.model.ShadowUpdatedEvent;
import software.amazon.awssdk.iot.iotshadow.model.ShadowUpdatedSnapshot;
import software.amazon.awssdk.iot.iotshadow.model.ShadowUpdatedSubscriptionRequest;
import software.amazon.awssdk.iot.iotshadow.model.UpdateNamedShadowRequest;
import software.amazon.awssdk.iot.iotshadow.model.UpdateNamedShadowSubscriptionRequest;
import software.amazon.awssdk.iot.iotshadow.model.UpdateShadowRequest;
import software.amazon.awssdk.iot.iotshadow.model.UpdateShadowResponse;
import software.amazon.awssdk.iot.iotshadow.model.UpdateShadowSubscriptionRequest;
import software.amazon.awssdk.iot.iotshadow.model.V2ErrorResponse;

import java.nio.charset.StandardCharsets;

import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt.QualityOfService;
import software.amazon.awssdk.crt.mqtt.MqttException;
import software.amazon.awssdk.crt.mqtt.MqttMessage;

import software.amazon.awssdk.iot.Timestamp;
import software.amazon.awssdk.iot.EnumSerializer;

import software.amazon.awssdk.iot.ShadowStateFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * The AWS IoT Device Shadow service adds shadows to AWS IoT thing objects. Shadows are a simple data store for device properties and state.  Shadows can make a device’s state available to apps and other services whether the device is connected to AWS IoT or not.
 *
 * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html
 *
*/
public class IotShadowClient {
    private MqttClientConnection connection = null;
    private final Gson gson = getGson();

    /**
     * Constructs a new IotShadowClient
     * @param connection The connection to use
     */
    public IotShadowClient(MqttClientConnection connection) {
        this.connection = connection;
    }

    private Gson getGson() {
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

    /**
     * Deletes a named shadow for an AWS IoT thing.
     *
     * If the device is offline, the PUBLISH packet will be sent once the connection resumes.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#delete-pub-sub-topic
     *
     * @param request Message to be serialized and sent
     * @param qos Quality of Service for delivering this message
     * @return a future containing the MQTT packet id used to perform the publish operation
     *
     * * For QoS 0, completes as soon as the packet is sent.
     * * For QoS 1, completes when PUBACK is received.
     * * QoS 2 is not supported by AWS IoT.
     */
    public CompletableFuture<Integer> PublishDeleteNamedShadow(
        DeleteNamedShadowRequest request,
        QualityOfService qos) {
        String topic = "$aws/things/{thingName}/shadow/name/{shadowName}/delete";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("DeleteNamedShadowRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        if (request.shadowName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("DeleteNamedShadowRequest must have a non-null shadowName"));
            return result;
        }
        topic = topic.replace("{shadowName}", request.shadowName);
        String payloadJson = gson.toJson(request);
        MqttMessage message = new MqttMessage(topic, payloadJson.getBytes(StandardCharsets.UTF_8));
        return connection.publish(message, qos, false);
    }

    /**
     * Deletes the (classic) shadow for an AWS IoT thing.
     *
     * If the device is offline, the PUBLISH packet will be sent once the connection resumes.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#delete-pub-sub-topic
     *
     * @param request Message to be serialized and sent
     * @param qos Quality of Service for delivering this message
     * @return a future containing the MQTT packet id used to perform the publish operation
     *
     * * For QoS 0, completes as soon as the packet is sent.
     * * For QoS 1, completes when PUBACK is received.
     * * QoS 2 is not supported by AWS IoT.
     */
    public CompletableFuture<Integer> PublishDeleteShadow(
        DeleteShadowRequest request,
        QualityOfService qos) {
        String topic = "$aws/things/{thingName}/shadow/delete";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("DeleteShadowRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        String payloadJson = gson.toJson(request);
        MqttMessage message = new MqttMessage(topic, payloadJson.getBytes(StandardCharsets.UTF_8));
        return connection.publish(message, qos, false);
    }

    /**
     * Gets a named shadow for an AWS IoT thing.
     *
     * If the device is offline, the PUBLISH packet will be sent once the connection resumes.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#get-pub-sub-topic
     *
     * @param request Message to be serialized and sent
     * @param qos Quality of Service for delivering this message
     * @return a future containing the MQTT packet id used to perform the publish operation
     *
     * * For QoS 0, completes as soon as the packet is sent.
     * * For QoS 1, completes when PUBACK is received.
     * * QoS 2 is not supported by AWS IoT.
     */
    public CompletableFuture<Integer> PublishGetNamedShadow(
        GetNamedShadowRequest request,
        QualityOfService qos) {
        String topic = "$aws/things/{thingName}/shadow/name/{shadowName}/get";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("GetNamedShadowRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        if (request.shadowName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("GetNamedShadowRequest must have a non-null shadowName"));
            return result;
        }
        topic = topic.replace("{shadowName}", request.shadowName);
        String payloadJson = gson.toJson(request);
        MqttMessage message = new MqttMessage(topic, payloadJson.getBytes(StandardCharsets.UTF_8));
        return connection.publish(message, qos, false);
    }

    /**
     * Gets the (classic) shadow for an AWS IoT thing.
     *
     * If the device is offline, the PUBLISH packet will be sent once the connection resumes.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#get-pub-sub-topic
     *
     * @param request Message to be serialized and sent
     * @param qos Quality of Service for delivering this message
     * @return a future containing the MQTT packet id used to perform the publish operation
     *
     * * For QoS 0, completes as soon as the packet is sent.
     * * For QoS 1, completes when PUBACK is received.
     * * QoS 2 is not supported by AWS IoT.
     */
    public CompletableFuture<Integer> PublishGetShadow(
        GetShadowRequest request,
        QualityOfService qos) {
        String topic = "$aws/things/{thingName}/shadow/get";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("GetShadowRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        String payloadJson = gson.toJson(request);
        MqttMessage message = new MqttMessage(topic, payloadJson.getBytes(StandardCharsets.UTF_8));
        return connection.publish(message, qos, false);
    }

    /**
     * Update a named shadow for a device.
     *
     * If the device is offline, the PUBLISH packet will be sent once the connection resumes.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#update-pub-sub-topic
     *
     * @param request Message to be serialized and sent
     * @param qos Quality of Service for delivering this message
     * @return a future containing the MQTT packet id used to perform the publish operation
     *
     * * For QoS 0, completes as soon as the packet is sent.
     * * For QoS 1, completes when PUBACK is received.
     * * QoS 2 is not supported by AWS IoT.
     */
    public CompletableFuture<Integer> PublishUpdateNamedShadow(
        UpdateNamedShadowRequest request,
        QualityOfService qos) {
        String topic = "$aws/things/{thingName}/shadow/name/{shadowName}/update";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("UpdateNamedShadowRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        if (request.shadowName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("UpdateNamedShadowRequest must have a non-null shadowName"));
            return result;
        }
        topic = topic.replace("{shadowName}", request.shadowName);
        String payloadJson = gson.toJson(request);
        MqttMessage message = new MqttMessage(topic, payloadJson.getBytes(StandardCharsets.UTF_8));
        return connection.publish(message, qos, false);
    }

    /**
     * Update a device's (classic) shadow.
     *
     * If the device is offline, the PUBLISH packet will be sent once the connection resumes.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#update-pub-sub-topic
     *
     * @param request Message to be serialized and sent
     * @param qos Quality of Service for delivering this message
     * @return a future containing the MQTT packet id used to perform the publish operation
     *
     * * For QoS 0, completes as soon as the packet is sent.
     * * For QoS 1, completes when PUBACK is received.
     * * QoS 2 is not supported by AWS IoT.
     */
    public CompletableFuture<Integer> PublishUpdateShadow(
        UpdateShadowRequest request,
        QualityOfService qos) {
        String topic = "$aws/things/{thingName}/shadow/update";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("UpdateShadowRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        String payloadJson = gson.toJson(request);
        MqttMessage message = new MqttMessage(topic, payloadJson.getBytes(StandardCharsets.UTF_8));
        return connection.publish(message, qos, false);
    }

    /**
     * Subscribes to the accepted topic for the DeleteNamedShadow operation.
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#delete-accepted-pub-sub-topic
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     * @param exceptionHandler callback function to invoke if an exception occurred deserializing a message
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToDeleteNamedShadowAccepted(
        DeleteNamedShadowSubscriptionRequest request,
        QualityOfService qos,
        Consumer<DeleteShadowResponse> handler,
        Consumer<Exception> exceptionHandler) {
        String topic = "$aws/things/{thingName}/shadow/name/{shadowName}/delete/accepted";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("DeleteNamedShadowSubscriptionRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        if (request.shadowName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("DeleteNamedShadowSubscriptionRequest must have a non-null shadowName"));
            return result;
        }
        topic = topic.replace("{shadowName}", request.shadowName);
        Consumer<MqttMessage> messageHandler = (message) -> {
            try {
                String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                DeleteShadowResponse response = gson.fromJson(payload, DeleteShadowResponse.class);
                handler.accept(response);
            } catch (Exception e) {
                if (exceptionHandler != null) {
                    exceptionHandler.accept(e);
                }
            }
        };
        return connection.subscribe(topic, qos, messageHandler);
    }

    /**
     * Subscribes to the accepted topic for the DeleteNamedShadow operation.
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#delete-accepted-pub-sub-topic
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToDeleteNamedShadowAccepted(
        DeleteNamedShadowSubscriptionRequest request,
        QualityOfService qos,
        Consumer<DeleteShadowResponse> handler) {
        return SubscribeToDeleteNamedShadowAccepted(request, qos, handler, null);
    }

    /**
     * Subscribes to the rejected topic for the DeleteNamedShadow operation.
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#delete-rejected-pub-sub-topic
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     * @param exceptionHandler callback function to invoke if an exception occurred deserializing a message
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToDeleteNamedShadowRejected(
        DeleteNamedShadowSubscriptionRequest request,
        QualityOfService qos,
        Consumer<ErrorResponse> handler,
        Consumer<Exception> exceptionHandler) {
        String topic = "$aws/things/{thingName}/shadow/name/{shadowName}/delete/rejected";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("DeleteNamedShadowSubscriptionRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        if (request.shadowName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("DeleteNamedShadowSubscriptionRequest must have a non-null shadowName"));
            return result;
        }
        topic = topic.replace("{shadowName}", request.shadowName);
        Consumer<MqttMessage> messageHandler = (message) -> {
            try {
                String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                ErrorResponse response = gson.fromJson(payload, ErrorResponse.class);
                handler.accept(response);
            } catch (Exception e) {
                if (exceptionHandler != null) {
                    exceptionHandler.accept(e);
                }
            }
        };
        return connection.subscribe(topic, qos, messageHandler);
    }

    /**
     * Subscribes to the rejected topic for the DeleteNamedShadow operation.
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#delete-rejected-pub-sub-topic
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToDeleteNamedShadowRejected(
        DeleteNamedShadowSubscriptionRequest request,
        QualityOfService qos,
        Consumer<ErrorResponse> handler) {
        return SubscribeToDeleteNamedShadowRejected(request, qos, handler, null);
    }

    /**
     * Subscribes to the accepted topic for the DeleteShadow operation
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#delete-accepted-pub-sub-topic
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     * @param exceptionHandler callback function to invoke if an exception occurred deserializing a message
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToDeleteShadowAccepted(
        DeleteShadowSubscriptionRequest request,
        QualityOfService qos,
        Consumer<DeleteShadowResponse> handler,
        Consumer<Exception> exceptionHandler) {
        String topic = "$aws/things/{thingName}/shadow/delete/accepted";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("DeleteShadowSubscriptionRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        Consumer<MqttMessage> messageHandler = (message) -> {
            try {
                String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                DeleteShadowResponse response = gson.fromJson(payload, DeleteShadowResponse.class);
                handler.accept(response);
            } catch (Exception e) {
                if (exceptionHandler != null) {
                    exceptionHandler.accept(e);
                }
            }
        };
        return connection.subscribe(topic, qos, messageHandler);
    }

    /**
     * Subscribes to the accepted topic for the DeleteShadow operation
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#delete-accepted-pub-sub-topic
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToDeleteShadowAccepted(
        DeleteShadowSubscriptionRequest request,
        QualityOfService qos,
        Consumer<DeleteShadowResponse> handler) {
        return SubscribeToDeleteShadowAccepted(request, qos, handler, null);
    }

    /**
     * Subscribes to the rejected topic for the DeleteShadow operation
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#delete-rejected-pub-sub-topic
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     * @param exceptionHandler callback function to invoke if an exception occurred deserializing a message
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToDeleteShadowRejected(
        DeleteShadowSubscriptionRequest request,
        QualityOfService qos,
        Consumer<ErrorResponse> handler,
        Consumer<Exception> exceptionHandler) {
        String topic = "$aws/things/{thingName}/shadow/delete/rejected";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("DeleteShadowSubscriptionRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        Consumer<MqttMessage> messageHandler = (message) -> {
            try {
                String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                ErrorResponse response = gson.fromJson(payload, ErrorResponse.class);
                handler.accept(response);
            } catch (Exception e) {
                if (exceptionHandler != null) {
                    exceptionHandler.accept(e);
                }
            }
        };
        return connection.subscribe(topic, qos, messageHandler);
    }

    /**
     * Subscribes to the rejected topic for the DeleteShadow operation
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#delete-rejected-pub-sub-topic
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToDeleteShadowRejected(
        DeleteShadowSubscriptionRequest request,
        QualityOfService qos,
        Consumer<ErrorResponse> handler) {
        return SubscribeToDeleteShadowRejected(request, qos, handler, null);
    }

    /**
     * Subscribes to the accepted topic for the GetNamedShadow operation.
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#get-accepted-pub-sub-topic
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     * @param exceptionHandler callback function to invoke if an exception occurred deserializing a message
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToGetNamedShadowAccepted(
        GetNamedShadowSubscriptionRequest request,
        QualityOfService qos,
        Consumer<GetShadowResponse> handler,
        Consumer<Exception> exceptionHandler) {
        String topic = "$aws/things/{thingName}/shadow/name/{shadowName}/get/accepted";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("GetNamedShadowSubscriptionRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        if (request.shadowName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("GetNamedShadowSubscriptionRequest must have a non-null shadowName"));
            return result;
        }
        topic = topic.replace("{shadowName}", request.shadowName);
        Consumer<MqttMessage> messageHandler = (message) -> {
            try {
                String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                GetShadowResponse response = gson.fromJson(payload, GetShadowResponse.class);
                handler.accept(response);
            } catch (Exception e) {
                if (exceptionHandler != null) {
                    exceptionHandler.accept(e);
                }
            }
        };
        return connection.subscribe(topic, qos, messageHandler);
    }

    /**
     * Subscribes to the accepted topic for the GetNamedShadow operation.
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#get-accepted-pub-sub-topic
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToGetNamedShadowAccepted(
        GetNamedShadowSubscriptionRequest request,
        QualityOfService qos,
        Consumer<GetShadowResponse> handler) {
        return SubscribeToGetNamedShadowAccepted(request, qos, handler, null);
    }

    /**
     * Subscribes to the rejected topic for the GetNamedShadow operation.
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#get-rejected-pub-sub-topic
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     * @param exceptionHandler callback function to invoke if an exception occurred deserializing a message
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToGetNamedShadowRejected(
        GetNamedShadowSubscriptionRequest request,
        QualityOfService qos,
        Consumer<ErrorResponse> handler,
        Consumer<Exception> exceptionHandler) {
        String topic = "$aws/things/{thingName}/shadow/name/{shadowName}/get/rejected";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("GetNamedShadowSubscriptionRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        if (request.shadowName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("GetNamedShadowSubscriptionRequest must have a non-null shadowName"));
            return result;
        }
        topic = topic.replace("{shadowName}", request.shadowName);
        Consumer<MqttMessage> messageHandler = (message) -> {
            try {
                String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                ErrorResponse response = gson.fromJson(payload, ErrorResponse.class);
                handler.accept(response);
            } catch (Exception e) {
                if (exceptionHandler != null) {
                    exceptionHandler.accept(e);
                }
            }
        };
        return connection.subscribe(topic, qos, messageHandler);
    }

    /**
     * Subscribes to the rejected topic for the GetNamedShadow operation.
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#get-rejected-pub-sub-topic
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToGetNamedShadowRejected(
        GetNamedShadowSubscriptionRequest request,
        QualityOfService qos,
        Consumer<ErrorResponse> handler) {
        return SubscribeToGetNamedShadowRejected(request, qos, handler, null);
    }

    /**
     * Subscribes to the accepted topic for the GetShadow operation.
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#get-accepted-pub-sub-topic
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     * @param exceptionHandler callback function to invoke if an exception occurred deserializing a message
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToGetShadowAccepted(
        GetShadowSubscriptionRequest request,
        QualityOfService qos,
        Consumer<GetShadowResponse> handler,
        Consumer<Exception> exceptionHandler) {
        String topic = "$aws/things/{thingName}/shadow/get/accepted";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("GetShadowSubscriptionRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        Consumer<MqttMessage> messageHandler = (message) -> {
            try {
                String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                GetShadowResponse response = gson.fromJson(payload, GetShadowResponse.class);
                handler.accept(response);
            } catch (Exception e) {
                if (exceptionHandler != null) {
                    exceptionHandler.accept(e);
                }
            }
        };
        return connection.subscribe(topic, qos, messageHandler);
    }

    /**
     * Subscribes to the accepted topic for the GetShadow operation.
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#get-accepted-pub-sub-topic
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToGetShadowAccepted(
        GetShadowSubscriptionRequest request,
        QualityOfService qos,
        Consumer<GetShadowResponse> handler) {
        return SubscribeToGetShadowAccepted(request, qos, handler, null);
    }

    /**
     * Subscribes to the rejected topic for the GetShadow operation.
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#get-rejected-pub-sub-topic
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     * @param exceptionHandler callback function to invoke if an exception occurred deserializing a message
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToGetShadowRejected(
        GetShadowSubscriptionRequest request,
        QualityOfService qos,
        Consumer<ErrorResponse> handler,
        Consumer<Exception> exceptionHandler) {
        String topic = "$aws/things/{thingName}/shadow/get/rejected";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("GetShadowSubscriptionRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        Consumer<MqttMessage> messageHandler = (message) -> {
            try {
                String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                ErrorResponse response = gson.fromJson(payload, ErrorResponse.class);
                handler.accept(response);
            } catch (Exception e) {
                if (exceptionHandler != null) {
                    exceptionHandler.accept(e);
                }
            }
        };
        return connection.subscribe(topic, qos, messageHandler);
    }

    /**
     * Subscribes to the rejected topic for the GetShadow operation.
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#get-rejected-pub-sub-topic
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToGetShadowRejected(
        GetShadowSubscriptionRequest request,
        QualityOfService qos,
        Consumer<ErrorResponse> handler) {
        return SubscribeToGetShadowRejected(request, qos, handler, null);
    }

    /**
     * Subscribe to NamedShadowDelta events for a named shadow of an AWS IoT thing.
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#update-delta-pub-sub-topic
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     * @param exceptionHandler callback function to invoke if an exception occurred deserializing a message
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToNamedShadowDeltaUpdatedEvents(
        NamedShadowDeltaUpdatedSubscriptionRequest request,
        QualityOfService qos,
        Consumer<ShadowDeltaUpdatedEvent> handler,
        Consumer<Exception> exceptionHandler) {
        String topic = "$aws/things/{thingName}/shadow/name/{shadowName}/update/delta";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("NamedShadowDeltaUpdatedSubscriptionRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        if (request.shadowName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("NamedShadowDeltaUpdatedSubscriptionRequest must have a non-null shadowName"));
            return result;
        }
        topic = topic.replace("{shadowName}", request.shadowName);
        Consumer<MqttMessage> messageHandler = (message) -> {
            try {
                String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                ShadowDeltaUpdatedEvent response = gson.fromJson(payload, ShadowDeltaUpdatedEvent.class);
                handler.accept(response);
            } catch (Exception e) {
                if (exceptionHandler != null) {
                    exceptionHandler.accept(e);
                }
            }
        };
        return connection.subscribe(topic, qos, messageHandler);
    }

    /**
     * Subscribe to NamedShadowDelta events for a named shadow of an AWS IoT thing.
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#update-delta-pub-sub-topic
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToNamedShadowDeltaUpdatedEvents(
        NamedShadowDeltaUpdatedSubscriptionRequest request,
        QualityOfService qos,
        Consumer<ShadowDeltaUpdatedEvent> handler) {
        return SubscribeToNamedShadowDeltaUpdatedEvents(request, qos, handler, null);
    }

    /**
     * Subscribe to ShadowUpdated events for a named shadow of an AWS IoT thing.
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#update-documents-pub-sub-topic
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     * @param exceptionHandler callback function to invoke if an exception occurred deserializing a message
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToNamedShadowUpdatedEvents(
        NamedShadowUpdatedSubscriptionRequest request,
        QualityOfService qos,
        Consumer<ShadowUpdatedEvent> handler,
        Consumer<Exception> exceptionHandler) {
        String topic = "$aws/things/{thingName}/shadow/name/{shadowName}/update/documents";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("NamedShadowUpdatedSubscriptionRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        if (request.shadowName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("NamedShadowUpdatedSubscriptionRequest must have a non-null shadowName"));
            return result;
        }
        topic = topic.replace("{shadowName}", request.shadowName);
        Consumer<MqttMessage> messageHandler = (message) -> {
            try {
                String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                ShadowUpdatedEvent response = gson.fromJson(payload, ShadowUpdatedEvent.class);
                handler.accept(response);
            } catch (Exception e) {
                if (exceptionHandler != null) {
                    exceptionHandler.accept(e);
                }
            }
        };
        return connection.subscribe(topic, qos, messageHandler);
    }

    /**
     * Subscribe to ShadowUpdated events for a named shadow of an AWS IoT thing.
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#update-documents-pub-sub-topic
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToNamedShadowUpdatedEvents(
        NamedShadowUpdatedSubscriptionRequest request,
        QualityOfService qos,
        Consumer<ShadowUpdatedEvent> handler) {
        return SubscribeToNamedShadowUpdatedEvents(request, qos, handler, null);
    }

    /**
     * Subscribe to ShadowDelta events for the (classic) shadow of an AWS IoT thing.
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#update-delta-pub-sub-topic
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     * @param exceptionHandler callback function to invoke if an exception occurred deserializing a message
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToShadowDeltaUpdatedEvents(
        ShadowDeltaUpdatedSubscriptionRequest request,
        QualityOfService qos,
        Consumer<ShadowDeltaUpdatedEvent> handler,
        Consumer<Exception> exceptionHandler) {
        String topic = "$aws/things/{thingName}/shadow/update/delta";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("ShadowDeltaUpdatedSubscriptionRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        Consumer<MqttMessage> messageHandler = (message) -> {
            try {
                String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                ShadowDeltaUpdatedEvent response = gson.fromJson(payload, ShadowDeltaUpdatedEvent.class);
                handler.accept(response);
            } catch (Exception e) {
                if (exceptionHandler != null) {
                    exceptionHandler.accept(e);
                }
            }
        };
        return connection.subscribe(topic, qos, messageHandler);
    }

    /**
     * Subscribe to ShadowDelta events for the (classic) shadow of an AWS IoT thing.
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#update-delta-pub-sub-topic
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToShadowDeltaUpdatedEvents(
        ShadowDeltaUpdatedSubscriptionRequest request,
        QualityOfService qos,
        Consumer<ShadowDeltaUpdatedEvent> handler) {
        return SubscribeToShadowDeltaUpdatedEvents(request, qos, handler, null);
    }

    /**
     * Subscribe to ShadowUpdated events for the (classic) shadow of an AWS IoT thing.
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#update-documents-pub-sub-topic
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     * @param exceptionHandler callback function to invoke if an exception occurred deserializing a message
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToShadowUpdatedEvents(
        ShadowUpdatedSubscriptionRequest request,
        QualityOfService qos,
        Consumer<ShadowUpdatedEvent> handler,
        Consumer<Exception> exceptionHandler) {
        String topic = "$aws/things/{thingName}/shadow/update/documents";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("ShadowUpdatedSubscriptionRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        Consumer<MqttMessage> messageHandler = (message) -> {
            try {
                String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                ShadowUpdatedEvent response = gson.fromJson(payload, ShadowUpdatedEvent.class);
                handler.accept(response);
            } catch (Exception e) {
                if (exceptionHandler != null) {
                    exceptionHandler.accept(e);
                }
            }
        };
        return connection.subscribe(topic, qos, messageHandler);
    }

    /**
     * Subscribe to ShadowUpdated events for the (classic) shadow of an AWS IoT thing.
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#update-documents-pub-sub-topic
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToShadowUpdatedEvents(
        ShadowUpdatedSubscriptionRequest request,
        QualityOfService qos,
        Consumer<ShadowUpdatedEvent> handler) {
        return SubscribeToShadowUpdatedEvents(request, qos, handler, null);
    }

    /**
     * Subscribes to the accepted topic for the UpdateNamedShadow operation
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#update-accepted-pub-sub-topic
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     * @param exceptionHandler callback function to invoke if an exception occurred deserializing a message
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToUpdateNamedShadowAccepted(
        UpdateNamedShadowSubscriptionRequest request,
        QualityOfService qos,
        Consumer<UpdateShadowResponse> handler,
        Consumer<Exception> exceptionHandler) {
        String topic = "$aws/things/{thingName}/shadow/name/{shadowName}/update/accepted";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("UpdateNamedShadowSubscriptionRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        if (request.shadowName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("UpdateNamedShadowSubscriptionRequest must have a non-null shadowName"));
            return result;
        }
        topic = topic.replace("{shadowName}", request.shadowName);
        Consumer<MqttMessage> messageHandler = (message) -> {
            try {
                String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                UpdateShadowResponse response = gson.fromJson(payload, UpdateShadowResponse.class);
                handler.accept(response);
            } catch (Exception e) {
                if (exceptionHandler != null) {
                    exceptionHandler.accept(e);
                }
            }
        };
        return connection.subscribe(topic, qos, messageHandler);
    }

    /**
     * Subscribes to the accepted topic for the UpdateNamedShadow operation
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#update-accepted-pub-sub-topic
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToUpdateNamedShadowAccepted(
        UpdateNamedShadowSubscriptionRequest request,
        QualityOfService qos,
        Consumer<UpdateShadowResponse> handler) {
        return SubscribeToUpdateNamedShadowAccepted(request, qos, handler, null);
    }

    /**
     * Subscribes to the rejected topic for the UpdateNamedShadow operation
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#update-rejected-pub-sub-topic
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     * @param exceptionHandler callback function to invoke if an exception occurred deserializing a message
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToUpdateNamedShadowRejected(
        UpdateNamedShadowSubscriptionRequest request,
        QualityOfService qos,
        Consumer<ErrorResponse> handler,
        Consumer<Exception> exceptionHandler) {
        String topic = "$aws/things/{thingName}/shadow/name/{shadowName}/update/rejected";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("UpdateNamedShadowSubscriptionRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        if (request.shadowName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("UpdateNamedShadowSubscriptionRequest must have a non-null shadowName"));
            return result;
        }
        topic = topic.replace("{shadowName}", request.shadowName);
        Consumer<MqttMessage> messageHandler = (message) -> {
            try {
                String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                ErrorResponse response = gson.fromJson(payload, ErrorResponse.class);
                handler.accept(response);
            } catch (Exception e) {
                if (exceptionHandler != null) {
                    exceptionHandler.accept(e);
                }
            }
        };
        return connection.subscribe(topic, qos, messageHandler);
    }

    /**
     * Subscribes to the rejected topic for the UpdateNamedShadow operation
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#update-rejected-pub-sub-topic
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToUpdateNamedShadowRejected(
        UpdateNamedShadowSubscriptionRequest request,
        QualityOfService qos,
        Consumer<ErrorResponse> handler) {
        return SubscribeToUpdateNamedShadowRejected(request, qos, handler, null);
    }

    /**
     * Subscribes to the accepted topic for the UpdateShadow operation
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#update-accepted-pub-sub-topic
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     * @param exceptionHandler callback function to invoke if an exception occurred deserializing a message
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToUpdateShadowAccepted(
        UpdateShadowSubscriptionRequest request,
        QualityOfService qos,
        Consumer<UpdateShadowResponse> handler,
        Consumer<Exception> exceptionHandler) {
        String topic = "$aws/things/{thingName}/shadow/update/accepted";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("UpdateShadowSubscriptionRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        Consumer<MqttMessage> messageHandler = (message) -> {
            try {
                String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                UpdateShadowResponse response = gson.fromJson(payload, UpdateShadowResponse.class);
                handler.accept(response);
            } catch (Exception e) {
                if (exceptionHandler != null) {
                    exceptionHandler.accept(e);
                }
            }
        };
        return connection.subscribe(topic, qos, messageHandler);
    }

    /**
     * Subscribes to the accepted topic for the UpdateShadow operation
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#update-accepted-pub-sub-topic
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToUpdateShadowAccepted(
        UpdateShadowSubscriptionRequest request,
        QualityOfService qos,
        Consumer<UpdateShadowResponse> handler) {
        return SubscribeToUpdateShadowAccepted(request, qos, handler, null);
    }

    /**
     * Subscribes to the rejected topic for the UpdateShadow operation
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#update-rejected-pub-sub-topic
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     * @param exceptionHandler callback function to invoke if an exception occurred deserializing a message
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToUpdateShadowRejected(
        UpdateShadowSubscriptionRequest request,
        QualityOfService qos,
        Consumer<ErrorResponse> handler,
        Consumer<Exception> exceptionHandler) {
        String topic = "$aws/things/{thingName}/shadow/update/rejected";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("UpdateShadowSubscriptionRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        Consumer<MqttMessage> messageHandler = (message) -> {
            try {
                String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                ErrorResponse response = gson.fromJson(payload, ErrorResponse.class);
                handler.accept(response);
            } catch (Exception e) {
                if (exceptionHandler != null) {
                    exceptionHandler.accept(e);
                }
            }
        };
        return connection.subscribe(topic, qos, messageHandler);
    }

    /**
     * Subscribes to the rejected topic for the UpdateShadow operation
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html#update-rejected-pub-sub-topic
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToUpdateShadowRejected(
        UpdateShadowSubscriptionRequest request,
        QualityOfService qos,
        Consumer<ErrorResponse> handler) {
        return SubscribeToUpdateShadowRejected(request, qos, handler, null);
    }

}
