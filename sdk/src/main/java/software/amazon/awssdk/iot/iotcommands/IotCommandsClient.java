/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.iot.iotcommands;

import software.amazon.awssdk.iot.iotcommands.model.CommandExecutionEvent;
import software.amazon.awssdk.iot.iotcommands.model.CommandExecutionStatus;
import software.amazon.awssdk.iot.iotcommands.model.CommandExecutionsSubscriptionRequest;
import software.amazon.awssdk.iot.iotcommands.model.DeviceType;
import software.amazon.awssdk.iot.iotcommands.model.RejectedErrorCode;
import software.amazon.awssdk.iot.iotcommands.model.StatusReason;
import software.amazon.awssdk.iot.iotcommands.model.UpdateCommandExecutionRequest;
import software.amazon.awssdk.iot.iotcommands.model.UpdateCommandExecutionResponse;
import software.amazon.awssdk.iot.iotcommands.model.V2ErrorResponse;

import java.nio.charset.StandardCharsets;

import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt.QualityOfService;
import software.amazon.awssdk.crt.mqtt.MqttException;
import software.amazon.awssdk.crt.mqtt.MqttMessage;

import software.amazon.awssdk.iot.Timestamp;
import software.amazon.awssdk.iot.EnumSerializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * The AWS IoT commands service is used to send an instruction from the cloud to a device that is connected to AWS IoT.
 *
 * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/iot-remote-command.html
 *
*/
public class IotCommandsClient {
    private MqttClientConnection connection = null;
    private final Gson gson = getGson();

    /**
     * Constructs a new IotCommandsClient
     * @param connection The connection to use
     */
    public IotCommandsClient(MqttClientConnection connection) {
        this.connection = connection;
    }

    private Gson getGson() {
        GsonBuilder gson = new GsonBuilder();
        gson.disableHtmlEscaping();
        gson.registerTypeAdapter(Timestamp.class, new Timestamp.Serializer());
        gson.registerTypeAdapter(Timestamp.class, new Timestamp.Deserializer());
        addTypeAdapters(gson);
        return gson.create();
    }

    private void addTypeAdapters(GsonBuilder gson) {
        gson.registerTypeAdapter(CommandExecutionStatus.class, new EnumSerializer<CommandExecutionStatus>());
        gson.registerTypeAdapter(DeviceType.class, new EnumSerializer<DeviceType>());
        gson.registerTypeAdapter(RejectedErrorCode.class, new EnumSerializer<RejectedErrorCode>());
    }

}
