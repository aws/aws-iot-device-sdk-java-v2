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
import software.amazon.awssdk.crt.mqtt5.packets.ConnectPacket;
import software.amazon.awssdk.iot.AwsIotMqtt5ClientBuilder;
import software.amazon.awssdk.iot.AwsIotMqttConnectionBuilder;

public class V2ServiceClientTestFixture {

    private String baseHost;
    private String baseCertificatePath;
    private String baseKeyPath;

    private String provisioningHost;
    private String provisioningCertificatePath;
    private String provisioningKeyPath;

    Mqtt5Client mqtt5Client;
    MqttClientConnection mqtt311Client;

    void populateTestingEnvironmentVariables() {
        baseHost = System.getenv("AWS_TEST_MQTT5_IOT_CORE_HOST");
        baseCertificatePath = System.getenv("AWS_TEST_MQTT5_IOT_CERTIFICATE_PATH");
        baseKeyPath = System.getenv("AWS_TEST_MQTT5_IOT_KEY_PATH");

        provisioningHost = System.getenv("AWS_TEST_IOT_CORE_PROVISIONING_HOST");
        provisioningCertificatePath = System.getenv("AWS_TEST_IOT_CORE_PROVISIONING_CERTIFICATE_PATH");
        provisioningKeyPath = System.getenv("AWS_TEST_IOT_CORE_PROVISIONING_KEY_PATH");
    }

    V2ServiceClientTestFixture() {}

    boolean hasBaseTestEnvironment() {
        return baseHost != null && baseCertificatePath != null && baseKeyPath != null;
    }

    boolean hasProvisioningTestEnvironment() {
        return provisioningHost != null && provisioningCertificatePath != null && provisioningKeyPath != null;
    }

    private void setupMqtt5Client(String host, String certificatePath, String keyPath, String clientId) {
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

            ConnectPacket.ConnectPacketBuilder connectBuilder = new ConnectPacket.ConnectPacketBuilder();
            if (clientId != null) {
                connectBuilder.withClientId(clientId);
            } else {
                connectBuilder.withClientId("test-" + UUID.randomUUID().toString());
            }
            builder.withConnectProperties(connectBuilder);

            this.mqtt5Client = builder.build();

            try {
                this.mqtt5Client.start();
                connected.get(10, TimeUnit.SECONDS);
            } catch (Exception ex) {
                fail("Exception in connecting: " + ex.toString());
            }
        }
    }

    void setupBaseMqtt5Client() {
        setupMqtt5Client(baseHost, baseCertificatePath, baseKeyPath, null);
    }

    void setupBaseMqtt5Client(String clientId) {
        setupMqtt5Client(baseHost, baseCertificatePath, baseKeyPath, clientId);
    }

    void setupProvisioningMqtt5Client() {
        setupMqtt5Client(provisioningHost, provisioningCertificatePath, provisioningKeyPath, null);
    }

    private void setupMqtt311Client(String host, String certificatePath, String keyPath, String clientId) {
        try (AwsIotMqttConnectionBuilder builder = AwsIotMqttConnectionBuilder.newMtlsBuilderFromPath(certificatePath, keyPath)) {
            builder.withEndpoint(host);
            if (clientId != null) {
                builder.withClientId(clientId);
            } else {
                builder.withClientId("test-" + UUID.randomUUID().toString());
            }

            this.mqtt311Client = builder.build();

            try {
                this.mqtt311Client.connect().get();
            } catch (Exception ex) {
                fail("Exception in connecting: " + ex.toString());
            }
        }
    }

    void setupBaseMqtt311Client() {
        setupMqtt311Client(baseHost, baseCertificatePath, baseKeyPath, null);
    }

    void setupBaseMqtt311Client(String clientId) {
        setupMqtt311Client(baseHost, baseCertificatePath, baseKeyPath, clientId);
    }

    void setupProvisioningMqtt311Client() {
        setupMqtt311Client(provisioningHost, provisioningCertificatePath, provisioningKeyPath, null);
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
