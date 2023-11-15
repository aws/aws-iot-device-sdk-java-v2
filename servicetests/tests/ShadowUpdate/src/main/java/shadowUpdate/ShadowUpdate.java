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
import software.amazon.awssdk.iot.iotshadow.model.ErrorResponse;
import software.amazon.awssdk.iot.iotshadow.model.GetShadowRequest;
import software.amazon.awssdk.iot.iotshadow.model.GetShadowResponse;
import software.amazon.awssdk.iot.iotshadow.model.GetShadowSubscriptionRequest;
import software.amazon.awssdk.iot.iotshadow.model.ShadowDeltaUpdatedEvent;
import software.amazon.awssdk.iot.iotshadow.model.ShadowDeltaUpdatedSubscriptionRequest;
import software.amazon.awssdk.iot.iotshadow.model.ShadowState;
import software.amazon.awssdk.iot.iotshadow.model.UpdateShadowRequest;
import software.amazon.awssdk.iot.iotshadow.model.UpdateShadowResponse;
import software.amazon.awssdk.iot.iotshadow.model.UpdateShadowSubscriptionRequest;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.Scanner;

import utils.commandlineutils.CommandLineUtils;
import utils.mqttclientconnectionwrapper.*;
import ServiceTestLifecycleEvents.ServiceTestLifecycleEvents;

public class ShadowUpdate {

    static String input_thingName;
    final static String SHADOW_PROPERTY = "color";
    final static String SHADOW_VALUE_DEFAULT = "off";

    static IotShadowClient shadow;
    static String localValue = null;
    static CompletableFuture<Void> gotResponse;

    static MqttClientConnectionWrapper createConnection(CommandLineUtils.SampleCommandLineData cmdData, Integer mqttVersion) {
        if (mqttVersion == 3) {
            try (AwsIotMqttConnectionBuilder builder = AwsIotMqttConnectionBuilder
                    .newMtlsBuilderFromPath(cmdData.input_cert, cmdData.input_key)) {
                builder.withClientId(cmdData.input_clientId)
                    .withEndpoint(cmdData.input_endpoint)
                    .withPort((short)cmdData.input_port)
                    .withCleanSession(true)
                    .withProtocolOperationTimeoutMs(60000);
                Mqtt3ClientConnectionWrapper connWrapper = new Mqtt3ClientConnectionWrapper();
                connWrapper.connection = builder.build();
                if (connWrapper.connection == null) {
                    throw new RuntimeException("MQTT311 connection creation failed!");
                }
                return connWrapper;
            } catch (Exception ex) {
                throw new RuntimeException("Failed to create MQTT311 connection", ex);
            }
        } else if (mqttVersion == 5) {
            ServiceTestLifecycleEvents lifecycleEvents = new ServiceTestLifecycleEvents();
            try (AwsIotMqtt5ClientBuilder builder = AwsIotMqtt5ClientBuilder.newDirectMqttBuilderWithMtlsFromPath(
                        cmdData.input_endpoint, cmdData.input_cert, cmdData.input_key)) {
                ConnectPacket.ConnectPacketBuilder connectProperties = new ConnectPacket.ConnectPacketBuilder();
                connectProperties.withClientId(cmdData.input_clientId);
                builder.withConnectProperties(connectProperties);
                builder.withLifeCycleEvents(lifecycleEvents);
                builder.withPort((long)cmdData.input_port);
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
        } else {
            throw new RuntimeException("Invalid MQTT version specified: " + mqttVersion);
        }
    }

    static void onGetShadowAccepted(GetShadowResponse response) {
        System.out.println("Received initial shadow state");
        System.out.println("  Shadow document has no value for " + SHADOW_PROPERTY + ". Setting default...");
        changeShadowValue(SHADOW_VALUE_DEFAULT);
    }

    static void onGetShadowRejected(ErrorResponse response) {
        if (response.code == 404) {
            System.out.println("Thing has no shadow document. Creating with defaults...");
            changeShadowValue(SHADOW_VALUE_DEFAULT);
            return;
        }
        gotResponse.complete(null);
        System.out.println("GetShadow request was rejected: code: " + response.code + " message: " + response.message);
        System.exit(1);
    }

    static void onShadowDeltaUpdated(ShadowDeltaUpdatedEvent response) {
        System.out.println("Shadow delta updated");
        if (response.state != null && response.state.containsKey(SHADOW_PROPERTY)) {
            String value = response.state.get(SHADOW_PROPERTY).toString();
            System.out.println("  Delta wants to change value to '" + value + "'. Changing local value...");
            if (!response.clientToken.isEmpty()) {
                System.out.print("  ClientToken: " + response.clientToken + "\n");
            }
            changeShadowValue(value);
        } else {
            System.out.println("  Delta did not report a change in " + SHADOW_PROPERTY);
        }
    }

    static void onUpdateShadowAccepted(UpdateShadowResponse response) {
        if (response.state.reported != null) {
            if (response.state.reported.containsKey(SHADOW_PROPERTY)) {
                String value = response.state.reported.get(SHADOW_PROPERTY).toString();
                System.out.println("Shadow updated, value is " + value);
            }
            else {
                System.out.println("Shadow updated, value is Null");
            }
        }
        else {
            if (response.state.reportedIsNullable == true) {
                System.out.println("Shadow updated, reported and desired is null");
            }
            else {
                System.out.println("Shadow update, data cleared");
            }
        }
        gotResponse.complete(null);
    }

    static void onUpdateShadowRejected(ErrorResponse response) {
        System.out.println("Shadow update was rejected: code: " + response.code + " message: " + response.message);
        System.exit(2);
    }
    static CompletableFuture<Integer> changeShadowValue(String value) {
        UpdateShadowRequest request = new UpdateShadowRequest();
        request.thingName = input_thingName;
        request.state = new ShadowState();
        request.state.reported = new HashMap<String, Object>() {{
           put(SHADOW_PROPERTY, value);
        }};
        request.state.desired = new HashMap<String, Object>() {{
            put(SHADOW_PROPERTY, value);
        }};

        return shadow.PublishUpdateShadow(request, QualityOfService.AT_LEAST_ONCE);
    }

    static CompletableFuture<Integer> changeNamedShadowValue(String value, String shadowName) {
        UpdateNamedShadowRequest request = new UpdateNamedShadowRequest();
        request.thingName = input_thingName;
        request.state = new ShadowState();
        request.state.reported = new HashMap<String, Object>() {{
           put(SHADOW_PROPERTY, value);
        }};
        request.state.desired = new HashMap<String, Object>() {{
           put(SHADOW_PROPERTY, value);
        }};
        request.shadowName = shadowName;

        return shadow.PublishUpdateNamedShadow(request, QualityOfService.AT_LEAST_ONCE);
    }

    public static void main(String[] args) {
        CommandLineUtils.SampleCommandLineData cmdData = CommandLineUtils.getInputForIoTSample("Shadow", args);
        input_thingName = cmdData.input_thingName;

        boolean exitWithError = false;

        try (MqttClientConnectionWrapper connection = createConnection(cmdData, cmdData.input_mqtt_version)) {
            shadow = new IotShadowClient(connection.getConnection());

            CompletableFuture<Boolean> connected = connection.start();
            try {
                boolean sessionPresent = connected.get();
            } catch (Exception ex) {
                throw new RuntimeException("Exception occurred during connect", ex);
            }

            /**
             * Subscribe to shadow topics
             */
            System.out.println("Subscribing to shadow delta events...");
            ShadowDeltaUpdatedSubscriptionRequest requestShadowDeltaUpdated = new ShadowDeltaUpdatedSubscriptionRequest();
            requestShadowDeltaUpdated.thingName = input_thingName;
            CompletableFuture<Integer> subscribedToDeltas =
                    shadow.SubscribeToShadowDeltaUpdatedEvents(
                            requestShadowDeltaUpdated,
                            QualityOfService.AT_LEAST_ONCE,
                            ShadowUpdate::onShadowDeltaUpdated);
            subscribedToDeltas.get();

            System.out.println("Subscribing to update responses...");
            UpdateShadowSubscriptionRequest requestUpdateShadow = new UpdateShadowSubscriptionRequest();
            requestUpdateShadow.thingName = input_thingName;
            CompletableFuture<Integer> subscribedToUpdateAccepted =
                    shadow.SubscribeToUpdateShadowAccepted(
                            requestUpdateShadow,
                            QualityOfService.AT_LEAST_ONCE,
                            ShadowUpdate::onUpdateShadowAccepted);
            CompletableFuture<Integer> subscribedToUpdateRejected =
                    shadow.SubscribeToUpdateShadowRejected(
                            requestUpdateShadow,
                            QualityOfService.AT_LEAST_ONCE,
                            ShadowUpdate::onUpdateShadowRejected);
            subscribedToUpdateAccepted.get();
            subscribedToUpdateRejected.get();

            System.out.println("Subscribing to get responses...");
            GetShadowSubscriptionRequest requestGetShadow = new GetShadowSubscriptionRequest();
            requestGetShadow.thingName = input_thingName;
            CompletableFuture<Integer> subscribedToGetShadowAccepted =
                    shadow.SubscribeToGetShadowAccepted(
                            requestGetShadow,
                            QualityOfService.AT_LEAST_ONCE,
                            ShadowUpdate::onGetShadowAccepted);
            CompletableFuture<Integer> subscribedToGetShadowRejected =
                    shadow.SubscribeToGetShadowRejected(
                            requestGetShadow,
                            QualityOfService.AT_LEAST_ONCE,
                            ShadowUpdate::onGetShadowRejected);
            subscribedToGetShadowAccepted.get();
            subscribedToGetShadowRejected.get();

            gotResponse = new CompletableFuture<>();

            System.out.println("Requesting current shadow state...");
            GetShadowRequest getShadowRequest = new GetShadowRequest();
            getShadowRequest.thingName = input_thingName;
            CompletableFuture<Integer> publishedGetShadow = shadow.PublishGetShadow(
                    getShadowRequest,
                    QualityOfService.AT_LEAST_ONCE);
            publishedGetShadow.get();
            gotResponse.get();

            boolean isNamedShadow = true;
            if (isNamedShadow) {
                changeNamedShadowValue("on", "myShadow").get();
            } else {
                changeShadowValue("on").get();
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
