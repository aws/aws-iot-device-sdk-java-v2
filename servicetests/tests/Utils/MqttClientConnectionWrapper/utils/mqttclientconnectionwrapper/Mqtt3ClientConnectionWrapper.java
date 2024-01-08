/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package utils.mqttclientconnectionwrapper;

import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.iot.AwsIotMqttConnectionBuilder;

import java.util.concurrent.CompletableFuture;

final public class Mqtt3ClientConnectionWrapper extends MqttClientConnectionWrapper {
    public static MqttClientConnection connection;

    public Mqtt3ClientConnectionWrapper(AwsIotMqttConnectionBuilder builder) {
        connection = builder.build();
        if (connection == null) {
            throw new RuntimeException("MQTT311 connection creation failed!");
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
    }

    @Override
    public MqttClientConnection getConnection() {
        return connection;
    }
};
