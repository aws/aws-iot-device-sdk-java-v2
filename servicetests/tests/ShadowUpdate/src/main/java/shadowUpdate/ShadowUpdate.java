/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package shadowUpdate;

import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.CrtResource;
import software.amazon.awssdk.crt.CrtRuntimeException;
import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt.MqttClientConnectionEvents;
import software.amazon.awssdk.crt.mqtt.QualityOfService;
import software.amazon.awssdk.crt.mqtt5.Mqtt5ClientOptions;
import software.amazon.awssdk.crt.mqtt5.Mqtt5Client;
import software.amazon.awssdk.crt.mqtt5.packets.ConnectPacket;
import software.amazon.awssdk.iot.AwsIotMqttConnectionBuilder;
import software.amazon.awssdk.iot.AwsIotMqtt5ClientBuilder;
import software.amazon.awssdk.iot.iotshadow.IotShadowClient;
import software.amazon.awssdk.iot.iotshadow.model.ShadowState;
import software.amazon.awssdk.iot.iotshadow.model.UpdateShadowRequest;
import software.amazon.awssdk.iot.iotshadow.model.UpdateNamedShadowRequest;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.Scanner;

import utils.commandlineutils.CommandLineUtils;
import utils.mqttclientconnectionwrapper.*;
import ServiceTestLifecycleEvents.ServiceTestLifecycleEvents;

public class ShadowUpdate {
    static IotShadowClient shadow;

    static CompletableFuture<Integer> changeShadowValue(String thingName, String property, String value) {
        UpdateShadowRequest request = new UpdateShadowRequest();
        request.thingName = thingName;
        request.state = new ShadowState();
        request.state.reported = new HashMap<String, Object>() {{
           put(property, value);
        }};
        request.state.desired = new HashMap<String, Object>() {{
            put(property, value);
        }};

        return shadow.PublishUpdateShadow(request, QualityOfService.AT_LEAST_ONCE);
    }

    static CompletableFuture<Integer> changeNamedShadowValue(String thingName, String property, String value, String shadowName) {
        UpdateNamedShadowRequest request = new UpdateNamedShadowRequest();
        request.thingName = thingName;
        request.state = new ShadowState();
        request.state.reported = new HashMap<String, Object>() {{
           put(property, value);
        }};
        request.state.desired = new HashMap<String, Object>() {{
           put(property, value);
        }};
        request.shadowName = shadowName;

        return shadow.PublishUpdateNamedShadow(request, QualityOfService.AT_LEAST_ONCE);
    }

    public static void main(String[] args) {
        CommandLineUtils.SampleCommandLineData cmdData = CommandLineUtils.getInputForIoTSample("Shadow", args);

        boolean exitWithError = false;

        try (MqttClientConnectionWrapper connection = MqttClientConnectionWrapperCreator.createConnection(
                    cmdData.input_cert,
                    cmdData.input_key,
                    cmdData.input_clientId,
                    cmdData.input_endpoint,
                    cmdData.input_port,
                    cmdData.input_mqtt_version)) {
            shadow = new IotShadowClient(connection.getConnection());

            CompletableFuture<Boolean> connected = connection.start();
            try {
                connected.get();
            } catch (Exception ex) {
                throw new RuntimeException("Exception occurred during connect", ex);
            }

            if (cmdData.input_shadowName.isEmpty()) {
                changeShadowValue(
                        cmdData.input_thingName,
                        cmdData.input_shadowProperty,
                        cmdData.input_shadowValue
                ).get();
            } else {
                changeNamedShadowValue(
                        cmdData.input_thingName,
                        cmdData.input_shadowProperty,
                        cmdData.input_shadowValue,
                        cmdData.input_shadowName
                ).get();
            }

            CompletableFuture<Void> disconnected = connection.stop();
            disconnected.get();
        } catch (Exception ex) {
            System.out.println("Exception encountered!\n");
            ex.printStackTrace();
            exitWithError = true;
        }

        CrtResource.waitForNoResources();
        System.out.println("Service test complete!");

        if (exitWithError) {
            System.exit(1);
        } else {
            System.exit(0);
        }
    }
}
