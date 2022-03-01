/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package shadowUpdate;

import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.CrtResource;
import software.amazon.awssdk.crt.CrtRuntimeException;
import software.amazon.awssdk.crt.io.ClientBootstrap;
import software.amazon.awssdk.crt.io.EventLoopGroup;
import software.amazon.awssdk.crt.io.HostResolver;
import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt.MqttClientConnectionEvents;
import software.amazon.awssdk.crt.mqtt.QualityOfService;
import software.amazon.awssdk.iot.AwsIotMqttConnectionBuilder;
import software.amazon.awssdk.iot.iotshadow.IotShadowClient;
import software.amazon.awssdk.iot.iotshadow.model.ErrorResponse;
import software.amazon.awssdk.iot.iotshadow.model.ShadowState;
import software.amazon.awssdk.iot.iotshadow.model.UpdateShadowRequest;
import software.amazon.awssdk.iot.iotshadow.model.UpdateShadowResponse;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.UUID;

public class ShadowUpdate {
    static String clientId = "test-" + UUID.randomUUID().toString();
    static String thingName;
    static String rootCaPath;
    static String certPath;
    static String keyPath;
    static String endpoint;
    static String newValue;
    static String shadowProperty;
    static int port = 8883;

    static MqttClientConnection connection;
    static IotShadowClient shadow;
    static CompletableFuture<Void> gotResponse;

    static void onUpdateShadowAccepted(UpdateShadowResponse response) {
        gotResponse.complete(null);
    }

    static void onUpdateShadowRejected(ErrorResponse response) {
        System.exit(2);
    }

    static CompletableFuture<Void> changeShadowValue(String value) {
        // build a request to let the service know our current value and desired value, and that we only want
        // to update if the version matches the version we know about
        UpdateShadowRequest request = new UpdateShadowRequest();
        request.thingName = thingName;
        request.state = new ShadowState();
        
        request.state.reported = new HashMap<String, Object>() {{
           put(shadowProperty, value);
        }};
        request.state.desired = new HashMap<String, Object>() {{
            put(shadowProperty, value);
        }};


        // Publish the request
        return shadow.PublishUpdateShadow(request, QualityOfService.AT_LEAST_ONCE).thenRun(() -> {
        }).exceptionally((ex) -> {
            System.exit(3);
            return null;
        });
    }

    public static void main(String[] args) {
        // Set vars
        endpoint = System.getenv("DA_ENDPOINT");
        certPath = System.getenv("DA_CERTI");
        keyPath = System.getenv("DA_KEY");
        thingName = System.getenv("DA_THING_NAME");
        shadowProperty = System.getenv("DA_SHADOW_PROPERTY");
        newValue = System.getenv("DA_SHADOW_VALUE_SET");

        try(EventLoopGroup eventLoopGroup = new EventLoopGroup(1);
            HostResolver resolver = new HostResolver(eventLoopGroup);
            ClientBootstrap clientBootstrap = new ClientBootstrap(eventLoopGroup, resolver);
            AwsIotMqttConnectionBuilder builder = AwsIotMqttConnectionBuilder.newMtlsBuilderFromPath(certPath, keyPath)) {

            if (rootCaPath != null) {
                builder.withCertificateAuthorityFromPath(null, rootCaPath);
            }

            builder.withClientId(clientId)
                    .withEndpoint(endpoint)
                    .withCleanSession(true)
                    .withBootstrap(clientBootstrap);

            try(MqttClientConnection connection = builder.build()) {
                shadow = new IotShadowClient(connection);

                CompletableFuture<Boolean> connected = connection.connect();
                try {
                    connected.get();
                } catch (Exception ex) {
                    throw new RuntimeException("Exception occurred during connect", ex);
                }


                gotResponse = new CompletableFuture<>();
                changeShadowValue(newValue).get();
                gotResponse.get();

                CompletableFuture<Void> disconnected = connection.disconnect();
                disconnected.get();
            }
        } catch (CrtRuntimeException | InterruptedException | ExecutionException ex) {

        }
        CrtResource.waitForNoResources();
    }
}
