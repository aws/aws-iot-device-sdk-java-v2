/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

import static org.junit.jupiter.api.Assertions.fail;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt5.*;
import software.amazon.awssdk.iot.AwsIotMqtt5ClientBuilder;
import software.amazon.awssdk.iot.AwsIotMqttConnectionBuilder;

public class V2ServiceClientTestFixture {

    private String host;
    private String certificatePath;
    private String keyPath;

    Mqtt5Client mqtt5Client;
    MqttClientConnection mqtt311Client;

    void populateTestingEnvironmentVariables() {
        host = System.getenv("AWS_TEST_MQTT5_IOT_CORE_HOST");
        certificatePath = System.getenv("AWS_TEST_MQTT5_IOT_CERTIFICATE_PATH");
        keyPath = System.getenv("AWS_TEST_MQTT5_IOT_KEY_PATH");
    }

    V2ServiceClientTestFixture() {}

    boolean hasTestEnvironment() {
        return host != null && certificatePath != null && keyPath != null;
    }

    void setupMqtt5Client() {
        try (AwsIotMqtt5ClientBuilder builder = AwsIotMqtt5ClientBuilder.newDirectMqttBuilderWithMtlsFromPath(
                host, certificatePath, keyPath)) {

            CompletableFuture<Boolean> connected = new CompletableFuture<>();

            Mqtt5ClientOptions.LifecycleEvents eventHandler = new Mqtt5ClientOptions.LifecycleEvents() {
                @Override
                public void onAttemptingConnect(Mqtt5Client client, OnAttemptingConnectReturn onAttemptingConnectReturn) {}

                @Override
                public void onConnectionSuccess(Mqtt5Client client, OnConnectionSuccessReturn onConnectionSuccessReturn) {
                    connected.complete(true);
                }

                @Override
                public void onConnectionFailure(Mqtt5Client client, OnConnectionFailureReturn onConnectionFailureReturn) {
                    connected.completeExceptionally(new Exception("Could not connect! Failure code: " + CRT.awsErrorString(onConnectionFailureReturn.getErrorCode())));
                }

                @Override
                public void onDisconnection(Mqtt5Client client, OnDisconnectionReturn onDisconnectionReturn) {}

                @Override
                public void onStopped(Mqtt5Client client, OnStoppedReturn onStoppedReturn) {}
            };

            builder.withLifeCycleEvents(eventHandler);

            this.mqtt5Client = builder.build();

            try {
                this.mqtt5Client.start();
                connected.get(10, TimeUnit.SECONDS);
            } catch (Exception ex) {
                fail("Exception in connecting: " + ex.toString());
            }
        }
    }

    void setupMqtt311Client() {
        try (AwsIotMqttConnectionBuilder builder = AwsIotMqttConnectionBuilder.newMtlsBuilderFromPath(certificatePath, keyPath)) {
            builder.withEndpoint(host);
            String clientId = "test-" + UUID.randomUUID().toString();
            builder.withClientId(clientId);

            this.mqtt311Client = builder.build();

            try {
                this.mqtt311Client.connect().get();
            } catch (Exception ex) {
                fail("Exception in connecting: " + ex.toString());
            }
        }
    }

    @AfterEach
    public void tearDown() {
        if (mqtt311Client != null) {
            mqtt311Client.disconnect();
            mqtt311Client.close();
            mqtt311Client = null;
        }

        if (mqtt5Client != null) {
            mqtt5Client.stop();
            mqtt5Client.close();
            mqtt5Client = null;
        }
    }


}
