/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package utils.mqttclientconnectionwrapper;

import software.amazon.awssdk.crt.mqtt.MqttClientConnection;

import java.util.concurrent.CompletableFuture;

/**
 * Auxiliary class hiding differences between MQTT311 and MQTT5 connections.
 */
abstract public class MqttClientConnectionWrapper implements AutoCloseable {
    public abstract CompletableFuture<Boolean> start();
    public abstract CompletableFuture<Void> stop();

    public abstract MqttClientConnection getConnection();
}
