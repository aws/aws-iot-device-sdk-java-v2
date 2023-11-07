/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package utils.mqttclientconnectionwrapper;

import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt5.Mqtt5Client;

import java.util.concurrent.CompletableFuture;

final public class Mqtt5ClientConnectionWrapper extends MqttClientConnectionWrapper {
    public static Mqtt5Client client;
    public static MqttClientConnection connection;

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
        client.close();
        connection.close();
    }

    @Override
    public MqttClientConnection getConnection() {
        return connection;
    }
};
