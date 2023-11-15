/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package utils.mqttclientconnectionwrapper;

import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt5.packets.ConnectPacket;
import software.amazon.awssdk.iot.AwsIotMqttConnectionBuilder;
import software.amazon.awssdk.iot.AwsIotMqtt5ClientBuilder;

import ServiceTestLifecycleEvents.ServiceTestLifecycleEvents;

final public class MqttClientConnectionWrapperCreator {
    public static MqttClientConnectionWrapper createConnection(
            String cert, String key, String clientId, String endpoint, int port, Integer mqttVersion) {
        if (mqttVersion == 3) {
            return createMqtt3Connection(cert, key, clientId, endpoint, port);
        } else if (mqttVersion == 5) {
            return createMqtt5Connection(cert, key, clientId, endpoint, port);
        } else {
            throw new RuntimeException("Invalid MQTT version specified: " + mqttVersion);
        }
    }

    static MqttClientConnectionWrapper createMqtt3Connection(
            String cert, String key, String clientId, String endpoint, int port) {
        try (AwsIotMqttConnectionBuilder builder = AwsIotMqttConnectionBuilder
                .newMtlsBuilderFromPath(cert, key)) {
            builder.withClientId(clientId)
                .withEndpoint(endpoint)
                .withPort((short)port)
                .withCleanSession(true);
            Mqtt3ClientConnectionWrapper connWrapper = new Mqtt3ClientConnectionWrapper();
            connWrapper.connection = builder.build();
            if (connWrapper.connection == null) {
                throw new RuntimeException("MQTT311 connection creation failed!");
            }
            return connWrapper;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to create MQTT311 connection", ex);
        }
    }

    static MqttClientConnectionWrapper createMqtt5Connection(
            String cert, String key, String clientId, String endpoint, int port) {
        ServiceTestLifecycleEvents lifecycleEvents = new ServiceTestLifecycleEvents();
        try (AwsIotMqtt5ClientBuilder builder = AwsIotMqtt5ClientBuilder.newDirectMqttBuilderWithMtlsFromPath(
                    endpoint, cert, key)) {
            ConnectPacket.ConnectPacketBuilder connectProperties = new ConnectPacket.ConnectPacketBuilder();
            connectProperties.withClientId(clientId);
            builder.withConnectProperties(connectProperties);
            builder.withLifeCycleEvents(lifecycleEvents);
            builder.withPort((long)port);
            Mqtt5ClientConnectionWrapper connWrapper = new Mqtt5ClientConnectionWrapper();
            connWrapper.client = builder.build();
            connWrapper.connection = new MqttClientConnection(connWrapper.client, null);
            if (connWrapper.connection == null) {
                throw new RuntimeException("MQTT5 connection creation failed!");
            }
            return connWrapper;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to create MQTT311 connection from MQTT5 client", ex);
        }
    }
}
