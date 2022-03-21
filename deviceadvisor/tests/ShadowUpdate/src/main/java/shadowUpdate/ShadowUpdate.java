/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package ShadowUpdate;

import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.CrtResource;
import software.amazon.awssdk.crt.CrtRuntimeException;
import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt.QualityOfService;
import software.amazon.awssdk.iot.AwsIotMqttConnectionBuilder;
import software.amazon.awssdk.iot.iotshadow.IotShadowClient;
import software.amazon.awssdk.iot.iotshadow.model.ShadowState;
import software.amazon.awssdk.iot.iotshadow.model.UpdateShadowRequest;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.UUID;

import DATestUtils.DATestUtils;

public class ShadowUpdate {
    static String clientId = "test-" + UUID.randomUUID().toString();
    static int port = 8883;

    static MqttClientConnection connection;
    static IotShadowClient shadow;
    static CompletableFuture<Void> gotResponse;

    static CompletableFuture<Void> changeShadowValue() {
        // build a request to let the service know our current value and desired value, and that we only want
        // to update if the version matches the version we know about
        UpdateShadowRequest request = new UpdateShadowRequest();
        request.thingName = DATestUtils.thing_name;
        request.state = new ShadowState();
        
        request.state.reported = new HashMap<String, Object>() {{
           put(DATestUtils.shadowProperty, DATestUtils.shadowValue);
        }};
        request.state.desired = new HashMap<String, Object>() {{
            put(DATestUtils.shadowProperty, DATestUtils.shadowValue);
        }};


        // Publish the request
        return shadow.PublishUpdateShadow(request, QualityOfService.AT_MOST_ONCE).thenRun(() -> {
        }).exceptionally((ex) -> {
            System.exit(3);
            return null;
        });
    }

    public static void main(String[] args) {
        // Set vars
        if(!DATestUtils.init(DATestUtils.TestType.SUB_PUB))
        {
            throw new RuntimeException("Failed to initialize environment variables.");
        }

        try(AwsIotMqttConnectionBuilder builder = AwsIotMqttConnectionBuilder.newMtlsBuilderFromPath(DATestUtils.certificatePath, DATestUtils.keyPath)) {


            builder.withClientId(clientId)
                    .withEndpoint(DATestUtils.endpoint)
                    .withPort((short)port)
                    .withCleanSession(true)
                    .withProtocolOperationTimeoutMs(60000);

            try(MqttClientConnection connection = builder.build()) {
                shadow = new IotShadowClient(connection);

                CompletableFuture<Boolean> connected = connection.connect();
                try {
                    connected.get();
                } catch (Exception ex) {
                    throw new RuntimeException("Exception occurred during connect", ex);
                }


                gotResponse = new CompletableFuture<>();
                changeShadowValue().get();
                gotResponse.get();

                CompletableFuture<Void> disconnected = connection.disconnect();
                disconnected.get();
            }
        } catch (CrtRuntimeException | InterruptedException | ExecutionException ex) {

        }
        CrtResource.waitForNoResources();
    }
}
