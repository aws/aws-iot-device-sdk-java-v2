/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.iot.iotidentity;

import software.amazon.awssdk.iot.iotidentity.model.CreateCertificateFromCsrRequest;
import software.amazon.awssdk.iot.iotidentity.model.CreateCertificateFromCsrResponse;
import software.amazon.awssdk.iot.iotidentity.model.CreateCertificateFromCsrSubscriptionRequest;
import software.amazon.awssdk.iot.iotidentity.model.CreateKeysAndCertificateRequest;
import software.amazon.awssdk.iot.iotidentity.model.CreateKeysAndCertificateResponse;
import software.amazon.awssdk.iot.iotidentity.model.CreateKeysAndCertificateSubscriptionRequest;
import software.amazon.awssdk.iot.iotidentity.model.ErrorResponse;
import software.amazon.awssdk.iot.iotidentity.model.RegisterThingRequest;
import software.amazon.awssdk.iot.iotidentity.model.RegisterThingResponse;
import software.amazon.awssdk.iot.iotidentity.model.RegisterThingSubscriptionRequest;

import java.nio.charset.StandardCharsets;

import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt.QualityOfService;
import software.amazon.awssdk.crt.mqtt.MqttException;
import software.amazon.awssdk.crt.mqtt.MqttMessage;

import software.amazon.awssdk.iot.Timestamp;
import software.amazon.awssdk.iot.EnumSerializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class IotIdentityClient {
    private MqttClientConnection connection = null;
    private final Gson gson = getGson();

    public IotIdentityClient(MqttClientConnection connection) {
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
    }

    public CompletableFuture<Integer> PublishCreateKeysAndCertificate(
        CreateKeysAndCertificateRequest request,
        QualityOfService qos) {
        String topic = "$aws/certificates/create/json";
        String payloadJson = gson.toJson(request);
        MqttMessage message = new MqttMessage(topic, payloadJson.getBytes(StandardCharsets.UTF_8));
        return connection.publish(message, qos, false);
    }

    public CompletableFuture<Integer> SubscribeToCreateKeysAndCertificateAccepted(
        CreateKeysAndCertificateSubscriptionRequest request,
        QualityOfService qos,
        Consumer<CreateKeysAndCertificateResponse> handler,
        Consumer<Exception> exceptionHandler) {
        String topic = "$aws/certificates/create/json/accepted";
        Consumer<MqttMessage> messageHandler = (message) -> {
            try {
                String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                CreateKeysAndCertificateResponse response = gson.fromJson(payload, CreateKeysAndCertificateResponse.class);
                handler.accept(response);
            } catch (Exception e) {
                if (exceptionHandler != null) {
                    exceptionHandler.accept(e);
                }
            }
        };
        return connection.subscribe(topic, qos, messageHandler);
    }

    public CompletableFuture<Integer> SubscribeToCreateKeysAndCertificateAccepted(
        CreateKeysAndCertificateSubscriptionRequest request,
        QualityOfService qos,
        Consumer<CreateKeysAndCertificateResponse> handler) {
        return SubscribeToCreateKeysAndCertificateAccepted(request, qos, handler, null);
    }

    public CompletableFuture<Integer> SubscribeToCreateKeysAndCertificateRejected(
        CreateKeysAndCertificateSubscriptionRequest request,
        QualityOfService qos,
        Consumer<ErrorResponse> handler,
        Consumer<Exception> exceptionHandler) {
        String topic = "$aws/certificates/create/json/rejected";
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

    public CompletableFuture<Integer> SubscribeToCreateKeysAndCertificateRejected(
        CreateKeysAndCertificateSubscriptionRequest request,
        QualityOfService qos,
        Consumer<ErrorResponse> handler) {
        return SubscribeToCreateKeysAndCertificateRejected(request, qos, handler, null);
    }

    public CompletableFuture<Integer> SubscribeToRegisterThingRejected(
        RegisterThingSubscriptionRequest request,
        QualityOfService qos,
        Consumer<ErrorResponse> handler,
        Consumer<Exception> exceptionHandler) {
        String topic = "$aws/provisioning-templates/{templateName}/provision/json/rejected";
        if (request.templateName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("RegisterThingSubscriptionRequest must have a non-null templateName"));
            return result;
        }
        topic = topic.replace("{templateName}", request.templateName);
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

    public CompletableFuture<Integer> SubscribeToRegisterThingRejected(
        RegisterThingSubscriptionRequest request,
        QualityOfService qos,
        Consumer<ErrorResponse> handler) {
        return SubscribeToRegisterThingRejected(request, qos, handler, null);
    }

    public CompletableFuture<Integer> SubscribeToCreateCertificateFromCsrAccepted(
        CreateCertificateFromCsrSubscriptionRequest request,
        QualityOfService qos,
        Consumer<CreateCertificateFromCsrResponse> handler,
        Consumer<Exception> exceptionHandler) {
        String topic = "$aws/certificates/create-from-csr/json/accepted";
        Consumer<MqttMessage> messageHandler = (message) -> {
            try {
                String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                CreateCertificateFromCsrResponse response = gson.fromJson(payload, CreateCertificateFromCsrResponse.class);
                handler.accept(response);
            } catch (Exception e) {
                if (exceptionHandler != null) {
                    exceptionHandler.accept(e);
                }
            }
        };
        return connection.subscribe(topic, qos, messageHandler);
    }

    public CompletableFuture<Integer> SubscribeToCreateCertificateFromCsrAccepted(
        CreateCertificateFromCsrSubscriptionRequest request,
        QualityOfService qos,
        Consumer<CreateCertificateFromCsrResponse> handler) {
        return SubscribeToCreateCertificateFromCsrAccepted(request, qos, handler, null);
    }

    public CompletableFuture<Integer> PublishRegisterThing(
        RegisterThingRequest request,
        QualityOfService qos) {
        String topic = "$aws/provisioning-templates/{templateName}/provision/json";
        if (request.templateName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("RegisterThingRequest must have a non-null templateName"));
            return result;
        }
        topic = topic.replace("{templateName}", request.templateName);
        String payloadJson = gson.toJson(request);
        MqttMessage message = new MqttMessage(topic, payloadJson.getBytes(StandardCharsets.UTF_8));
        return connection.publish(message, qos, false);
    }

    public CompletableFuture<Integer> SubscribeToRegisterThingAccepted(
        RegisterThingSubscriptionRequest request,
        QualityOfService qos,
        Consumer<RegisterThingResponse> handler,
        Consumer<Exception> exceptionHandler) {
        String topic = "$aws/provisioning-templates/{templateName}/provision/json/accepted";
        if (request.templateName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("RegisterThingSubscriptionRequest must have a non-null templateName"));
            return result;
        }
        topic = topic.replace("{templateName}", request.templateName);
        Consumer<MqttMessage> messageHandler = (message) -> {
            try {
                String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                RegisterThingResponse response = gson.fromJson(payload, RegisterThingResponse.class);
                handler.accept(response);
            } catch (Exception e) {
                if (exceptionHandler != null) {
                    exceptionHandler.accept(e);
                }
            }
        };
        return connection.subscribe(topic, qos, messageHandler);
    }

    public CompletableFuture<Integer> SubscribeToRegisterThingAccepted(
        RegisterThingSubscriptionRequest request,
        QualityOfService qos,
        Consumer<RegisterThingResponse> handler) {
        return SubscribeToRegisterThingAccepted(request, qos, handler, null);
    }

    public CompletableFuture<Integer> SubscribeToCreateCertificateFromCsrRejected(
        CreateCertificateFromCsrSubscriptionRequest request,
        QualityOfService qos,
        Consumer<ErrorResponse> handler,
        Consumer<Exception> exceptionHandler) {
        String topic = "$aws/certificates/create-from-csr/json/rejected";
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

    public CompletableFuture<Integer> SubscribeToCreateCertificateFromCsrRejected(
        CreateCertificateFromCsrSubscriptionRequest request,
        QualityOfService qos,
        Consumer<ErrorResponse> handler) {
        return SubscribeToCreateCertificateFromCsrRejected(request, qos, handler, null);
    }

    public CompletableFuture<Integer> PublishCreateCertificateFromCsr(
        CreateCertificateFromCsrRequest request,
        QualityOfService qos) {
        String topic = "$aws/certificates/create-from-csr/json";
        String payloadJson = gson.toJson(request);
        MqttMessage message = new MqttMessage(topic, payloadJson.getBytes(StandardCharsets.UTF_8));
        return connection.publish(message, qos, false);
    }

}
