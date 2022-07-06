/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package MQTTConnect;

import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.CrtResource;
import software.amazon.awssdk.crt.CrtRuntimeException;
import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt.MqttClientConnectionEvents;
import software.amazon.awssdk.iot.AwsIotMqttConnectionBuilder;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import DATestUtils.DATestUtils;

public class MQTTConnect {

    // When run normally, we want to exit nicely even if something goes wrong
    // When run from CI, we want to let an exception escape which in turn causes the
    // exec:java task to return a non-zero exit code
    static String ciPropValue = System.getProperty("aws.crt.ci");
    static boolean isCI = ciPropValue != null && Boolean.valueOf(ciPropValue);

    static String clientId = "test-" + UUID.randomUUID().toString();
    static int port = 8883;

    /*
     * When called during a CI run, throw an exception that will escape and fail the exec:java task
     * When called otherwise, print what went wrong (if anything) and just continue (return from main)
     */
    static void onApplicationFailure(Throwable cause) {
        if (isCI) {
            throw new RuntimeException("BasicPubSub execution failure", cause);
        }
    }

    public static void main(String[] args) {

        if(!DATestUtils.init(DATestUtils.TestType.CONNECT))
        {
            throw new RuntimeException("Failed to initialize environment variables.");
        }

        try(AwsIotMqttConnectionBuilder builder = AwsIotMqttConnectionBuilder.newMtlsBuilderFromPath(DATestUtils.certificatePath, DATestUtils.keyPath)) {
            builder.withClientId(clientId)
                .withEndpoint(DATestUtils.endpoint)
                .withPort((short)port)
                .withCleanSession(true)
                .withPingTimeoutMs(60000)
                .withProtocolOperationTimeoutMs(60000);
            try(MqttClientConnection connection = builder.build()) {
                CompletableFuture<Boolean> connected = connection.connect();
                try {
                    boolean sessionPresent = connected.get();
                } catch (Exception ex) {
                    throw new RuntimeException("Exception occurred during connect", ex);
                }

                CompletableFuture<Void> disconnected = connection.disconnect();
                disconnected.get();
            }
        } catch (CrtRuntimeException | InterruptedException | ExecutionException ex) {
            System.out.print("failed: " + ex.getMessage());
        }

        System.exit(0);
    }
}
