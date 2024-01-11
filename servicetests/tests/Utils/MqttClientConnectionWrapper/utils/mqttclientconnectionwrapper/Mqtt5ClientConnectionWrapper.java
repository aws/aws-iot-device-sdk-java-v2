/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package utils.mqttclientconnectionwrapper;

import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt5.Mqtt5Client;
import software.amazon.awssdk.iot.AwsIotMqtt5ClientBuilder;

import java.util.concurrent.CompletableFuture;

final public class Mqtt5ClientConnectionWrapper extends MqttClientConnectionWrapper {
    public static Mqtt5Client client;
    public static MqttClientConnection connection;

    public Mqtt5ClientConnectionWrapper(AwsIotMqtt5ClientBuilder builder) {
        client = builder.build();
        connection = new MqttClientConnection(client, null);
        if (connection == null) {
            throw new RuntimeException("MQTT5 connection creation failed!");
        }
    }

    @Override
    public CompletableFuture<Boolean> start() {
        return connection.connect();
    }

    @Override
    public CompletableFuture<Void> stop() {
        return connection.disconnect();
    }

    @Override
    public void close() {
        connection.close();
        client.close();
    }

    @Override
    public MqttClientConnection getConnection() {
        return connection;
    }
};
