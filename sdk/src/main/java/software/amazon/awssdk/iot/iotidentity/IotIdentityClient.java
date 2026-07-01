/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
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
import software.amazon.awssdk.iot.iotidentity.model.V2ErrorResponse;

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

/**
 * An AWS IoT service that assists with provisioning a device and installing unique client certificates on it
 *
 * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/provision-wo-cert.html
 *
*/
public class IotIdentityClient {
    private MqttClientConnection connection = null;
    private final Gson gson = getGson();

    /**
     * Constructs a new IotIdentityClient
     * @param connection The connection to use
     */
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

    /**
     * Creates a certificate from a certificate signing request (CSR). AWS IoT provides client certificates that are signed by the Amazon Root certificate authority (CA). The new certificate has a PENDING_ACTIVATION status. When you call RegisterThing to provision a thing with this certificate, the certificate status changes to ACTIVE or INACTIVE as described in the template.
     *
     * If the device is offline, the PUBLISH packet will be sent once the connection resumes.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/provision-wo-cert.html#fleet-provision-api
     *
     * @param request Message to be serialized and sent
     * @param qos Quality of Service for delivering this message
     * @return a future containing the MQTT packet id used to perform the publish operation
     *
     * * For QoS 0, completes as soon as the packet is sent.
     * * For QoS 1, completes when PUBACK is received.
     * * QoS 2 is not supported by AWS IoT.
     */
    public CompletableFuture<Integer> PublishCreateCertificateFromCsr(
        CreateCertificateFromCsrRequest request,
        QualityOfService qos) {
        String topic = "$aws/certificates/create-from-csr/json";
        String payloadJson = gson.toJson(request);
        MqttMessage message = new MqttMessage(topic, payloadJson.getBytes(StandardCharsets.UTF_8));
        return connection.publish(message, qos, false);
    }

    /**
     * Creates new keys and a certificate. AWS IoT provides client certificates that are signed by the Amazon Root certificate authority (CA). The new certificate has a PENDING_ACTIVATION status. When you call RegisterThing to provision a thing with this certificate, the certificate status changes to ACTIVE or INACTIVE as described in the template.
     *
     * If the device is offline, the PUBLISH packet will be sent once the connection resumes.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/provision-wo-cert.html#fleet-provision-api
     *
     * @param request Message to be serialized and sent
     * @param qos Quality of Service for delivering this message
     * @return a future containing the MQTT packet id used to perform the publish operation
     *
     * * For QoS 0, completes as soon as the packet is sent.
     * * For QoS 1, completes when PUBACK is received.
     * * QoS 2 is not supported by AWS IoT.
     */
    public CompletableFuture<Integer> PublishCreateKeysAndCertificate(
        CreateKeysAndCertificateRequest request,
        QualityOfService qos) {
        String topic = "$aws/certificates/create/json";
        String payloadJson = gson.toJson(request);
        MqttMessage message = new MqttMessage(topic, payloadJson.getBytes(StandardCharsets.UTF_8));
        return connection.publish(message, qos, false);
    }

    /**
     * Provisions an AWS IoT thing using a pre-defined template.
     *
     * If the device is offline, the PUBLISH packet will be sent once the connection resumes.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/provision-wo-cert.html#fleet-provision-api
     *
     * @param request Message to be serialized and sent
     * @param qos Quality of Service for delivering this message
     * @return a future containing the MQTT packet id used to perform the publish operation
     *
     * * For QoS 0, completes as soon as the packet is sent.
     * * For QoS 1, completes when PUBACK is received.
     * * QoS 2 is not supported by AWS IoT.
     */
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

    /**
     * Subscribes to the accepted topic of the CreateCertificateFromCsr operation.
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/provision-wo-cert.html#fleet-provision-api
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     * @param exceptionHandler callback function to invoke if an exception occurred deserializing a message
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
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

    /**
     * Subscribes to the accepted topic of the CreateCertificateFromCsr operation.
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/provision-wo-cert.html#fleet-provision-api
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToCreateCertificateFromCsrAccepted(
        CreateCertificateFromCsrSubscriptionRequest request,
        QualityOfService qos,
        Consumer<CreateCertificateFromCsrResponse> handler) {
        return SubscribeToCreateCertificateFromCsrAccepted(request, qos, handler, null);
    }

    /**
     * Subscribes to the rejected topic of the CreateCertificateFromCsr operation.
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/provision-wo-cert.html#fleet-provision-api
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     * @param exceptionHandler callback function to invoke if an exception occurred deserializing a message
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
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

    /**
     * Subscribes to the rejected topic of the CreateCertificateFromCsr operation.
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/provision-wo-cert.html#fleet-provision-api
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToCreateCertificateFromCsrRejected(
        CreateCertificateFromCsrSubscriptionRequest request,
        QualityOfService qos,
        Consumer<ErrorResponse> handler) {
        return SubscribeToCreateCertificateFromCsrRejected(request, qos, handler, null);
    }

    /**
     * Subscribes to the accepted topic of the CreateKeysAndCertificate operation.
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/provision-wo-cert.html#fleet-provision-api
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     * @param exceptionHandler callback function to invoke if an exception occurred deserializing a message
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
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

    /**
     * Subscribes to the accepted topic of the CreateKeysAndCertificate operation.
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/provision-wo-cert.html#fleet-provision-api
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToCreateKeysAndCertificateAccepted(
        CreateKeysAndCertificateSubscriptionRequest request,
        QualityOfService qos,
        Consumer<CreateKeysAndCertificateResponse> handler) {
        return SubscribeToCreateKeysAndCertificateAccepted(request, qos, handler, null);
    }

    /**
     * Subscribes to the rejected topic of the CreateKeysAndCertificate operation.
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/provision-wo-cert.html#fleet-provision-api
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     * @param exceptionHandler callback function to invoke if an exception occurred deserializing a message
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
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

    /**
     * Subscribes to the rejected topic of the CreateKeysAndCertificate operation.
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/provision-wo-cert.html#fleet-provision-api
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToCreateKeysAndCertificateRejected(
        CreateKeysAndCertificateSubscriptionRequest request,
        QualityOfService qos,
        Consumer<ErrorResponse> handler) {
        return SubscribeToCreateKeysAndCertificateRejected(request, qos, handler, null);
    }

    /**
     * Subscribes to the accepted topic of the RegisterThing operation.
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/provision-wo-cert.html#fleet-provision-api
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     * @param exceptionHandler callback function to invoke if an exception occurred deserializing a message
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
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

    /**
     * Subscribes to the accepted topic of the RegisterThing operation.
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/provision-wo-cert.html#fleet-provision-api
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToRegisterThingAccepted(
        RegisterThingSubscriptionRequest request,
        QualityOfService qos,
        Consumer<RegisterThingResponse> handler) {
        return SubscribeToRegisterThingAccepted(request, qos, handler, null);
    }

    /**
     * Subscribes to the rejected topic of the RegisterThing operation.
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/provision-wo-cert.html#fleet-provision-api
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     * @param exceptionHandler callback function to invoke if an exception occurred deserializing a message
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
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

    /**
     * Subscribes to the rejected topic of the RegisterThing operation.
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/provision-wo-cert.html#fleet-provision-api
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToRegisterThingRejected(
        RegisterThingSubscriptionRequest request,
        QualityOfService qos,
        Consumer<ErrorResponse> handler) {
        return SubscribeToRegisterThingRejected(request, qos, handler, null);
    }

}
