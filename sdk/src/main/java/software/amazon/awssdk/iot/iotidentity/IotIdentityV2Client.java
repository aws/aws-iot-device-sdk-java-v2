/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.iot.iotidentity;

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
import software.amazon.awssdk.iot.iotidentity.model.*;

/**
 * An AWS IoT service that assists with provisioning a device and installing unique client certificates on it
 *
 * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/provision-wo-cert.html
 *
*/
public class IotIdentityV2Client implements AutoCloseable {

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
    }

    private IotIdentityV2Client(MqttRequestResponseClient rrClient) {
        this.rrClient = rrClient;
        this.gson = createGson();
    }

    /**
     * Constructs a new IotIdentityV2Client, using an MQTT5 client as transport
     *
     * @param protocolClient the MQTT5 client to use
     * @param options configuration options to use
     */
    static public IotIdentityV2Client newFromMqtt5(Mqtt5Client protocolClient, MqttRequestResponseClientOptions options) {
        MqttRequestResponseClient rrClient = new MqttRequestResponseClient(protocolClient, options);
        return new IotIdentityV2Client(rrClient);
    }

    /**
     * Constructs a new IotIdentityV2Client, using an MQTT311 client as transport
     *
     * @param protocolClient the MQTT311 client to use
     * @param options configuration options to use
     */
    static public IotIdentityV2Client newFromMqtt311(MqttClientConnection protocolClient, MqttRequestResponseClientOptions options) {
        MqttRequestResponseClient rrClient = new MqttRequestResponseClient(protocolClient, options);
        return new IotIdentityV2Client(rrClient);
    }

    /**
     * Releases all resources used by the client.  It is not valid to invoke operations
     * on the client after it has been closed.
     */
    public void close() {
        this.rrClient.decRef();
        this.rrClient = null;
    }

    /**
     * Creates a certificate from a certificate signing request (CSR). AWS IoT provides client certificates that are signed by the Amazon Root certificate authority (CA). The new certificate has a PENDING_ACTIVATION status. When you call RegisterThing to provision a thing with this certificate, the certificate status changes to ACTIVE or INACTIVE as described in the template.
     *
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/provision-wo-cert.html#fleet-provision-api
     *
     * @param request modeled request to perform
     *
     * @return a future that will complete with the corresponding response
     */
    public CompletableFuture<CreateCertificateFromCsrResponse> createCertificateFromCsr(CreateCertificateFromCsrRequest request) {
        V2ClientFuture<CreateCertificateFromCsrResponse> responseFuture = new V2ClientFuture<>();

        try {
            RequestResponseOperation.RequestResponseOperationBuilder builder = RequestResponseOperation.builder();


            // Publish Topic
            String publishTopic = "$aws/certificates/create-from-csr/json";
            builder.withPublishTopic(publishTopic);

            // Payload
            String payloadJson = gson.toJson(request);
            builder.withPayload(payloadJson.getBytes(StandardCharsets.UTF_8));

            // Subscriptions
            String subscription0 = "$aws/certificates/create-from-csr/json/accepted";
            builder.withSubscription(subscription0);
            String subscription1 = "$aws/certificates/create-from-csr/json/rejected";
            builder.withSubscription(subscription1);

            // Response paths
            ResponsePath.ResponsePathBuilder pathBuilder1 = ResponsePath.builder();
            String responseTopic1 = publishTopic + "/accepted";
            pathBuilder1.withResponseTopic(publishTopic + "/accepted");
            builder.withResponsePath(pathBuilder1.build());

            ResponsePath.ResponsePathBuilder pathBuilder2 = ResponsePath.builder();
            String responseTopic2 = publishTopic + "/rejected";
            pathBuilder2.withResponseTopic(publishTopic + "/rejected");
            builder.withResponsePath(pathBuilder2.build());

            // Submit
            submitOperation(responseFuture, builder.build(), responseTopic1, CreateCertificateFromCsrResponse.class, responseTopic2, V2ErrorResponse.class, IotIdentityV2Client::createV2ErrorResponseException);
        } catch (Exception e) {
            responseFuture.completeExceptionally(createV2ErrorResponseException(e.getMessage(), null));
        }

        return responseFuture;
    }

    /**
     * Creates new keys and a certificate. AWS IoT provides client certificates that are signed by the Amazon Root certificate authority (CA). The new certificate has a PENDING_ACTIVATION status. When you call RegisterThing to provision a thing with this certificate, the certificate status changes to ACTIVE or INACTIVE as described in the template.
     *
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/provision-wo-cert.html#fleet-provision-api
     *
     * @param request modeled request to perform
     *
     * @return a future that will complete with the corresponding response
     */
    public CompletableFuture<CreateKeysAndCertificateResponse> createKeysAndCertificate(CreateKeysAndCertificateRequest request) {
        V2ClientFuture<CreateKeysAndCertificateResponse> responseFuture = new V2ClientFuture<>();

        try {
            RequestResponseOperation.RequestResponseOperationBuilder builder = RequestResponseOperation.builder();


            // Publish Topic
            String publishTopic = "$aws/certificates/create/json";
            builder.withPublishTopic(publishTopic);

            // Payload
            String payloadJson = gson.toJson(request);
            builder.withPayload(payloadJson.getBytes(StandardCharsets.UTF_8));

            // Subscriptions
            String subscription0 = "$aws/certificates/create/json/accepted";
            builder.withSubscription(subscription0);
            String subscription1 = "$aws/certificates/create/json/rejected";
            builder.withSubscription(subscription1);

            // Response paths
            ResponsePath.ResponsePathBuilder pathBuilder1 = ResponsePath.builder();
            String responseTopic1 = publishTopic + "/accepted";
            pathBuilder1.withResponseTopic(publishTopic + "/accepted");
            builder.withResponsePath(pathBuilder1.build());

            ResponsePath.ResponsePathBuilder pathBuilder2 = ResponsePath.builder();
            String responseTopic2 = publishTopic + "/rejected";
            pathBuilder2.withResponseTopic(publishTopic + "/rejected");
            builder.withResponsePath(pathBuilder2.build());

            // Submit
            submitOperation(responseFuture, builder.build(), responseTopic1, CreateKeysAndCertificateResponse.class, responseTopic2, V2ErrorResponse.class, IotIdentityV2Client::createV2ErrorResponseException);
        } catch (Exception e) {
            responseFuture.completeExceptionally(createV2ErrorResponseException(e.getMessage(), null));
        }

        return responseFuture;
    }

    /**
     * Provisions an AWS IoT thing using a pre-defined template.
     *
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/provision-wo-cert.html#fleet-provision-api
     *
     * @param request modeled request to perform
     *
     * @return a future that will complete with the corresponding response
     */
    public CompletableFuture<RegisterThingResponse> registerThing(RegisterThingRequest request) {
        V2ClientFuture<RegisterThingResponse> responseFuture = new V2ClientFuture<>();

        try {
            if (request.templateName == null) {
                throw new CrtRuntimeException("RegisterThingRequest.templateName cannot be null");
            }

            RequestResponseOperation.RequestResponseOperationBuilder builder = RequestResponseOperation.builder();


            // Publish Topic
            String publishTopic = "$aws/provisioning-templates/{templateName}/provision/json";
            publishTopic = publishTopic.replace("{templateName}", request.templateName);
            builder.withPublishTopic(publishTopic);

            // Payload
            String payloadJson = gson.toJson(request);
            builder.withPayload(payloadJson.getBytes(StandardCharsets.UTF_8));

            // Subscriptions
            String subscription0 = "$aws/provisioning-templates/{templateName}/provision/json/accepted";
            subscription0 = subscription0.replace("{templateName}", request.templateName);
            builder.withSubscription(subscription0);
            String subscription1 = "$aws/provisioning-templates/{templateName}/provision/json/rejected";
            subscription1 = subscription1.replace("{templateName}", request.templateName);
            builder.withSubscription(subscription1);

            // Response paths
            ResponsePath.ResponsePathBuilder pathBuilder1 = ResponsePath.builder();
            String responseTopic1 = publishTopic + "/accepted";
            pathBuilder1.withResponseTopic(publishTopic + "/accepted");
            builder.withResponsePath(pathBuilder1.build());

            ResponsePath.ResponsePathBuilder pathBuilder2 = ResponsePath.builder();
            String responseTopic2 = publishTopic + "/rejected";
            pathBuilder2.withResponseTopic(publishTopic + "/rejected");
            builder.withResponsePath(pathBuilder2.build());

            // Submit
            submitOperation(responseFuture, builder.build(), responseTopic1, RegisterThingResponse.class, responseTopic2, V2ErrorResponse.class, IotIdentityV2Client::createV2ErrorResponseException);
        } catch (Exception e) {
            responseFuture.completeExceptionally(createV2ErrorResponseException(e.getMessage(), null));
        }

        return responseFuture;
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
