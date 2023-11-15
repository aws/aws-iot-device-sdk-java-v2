/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package fleetProvisioning;

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
import software.amazon.awssdk.iot.iotidentity.IotIdentityClient;
import software.amazon.awssdk.iot.iotidentity.model.CreateKeysAndCertificateRequest;
import software.amazon.awssdk.iot.iotidentity.model.CreateKeysAndCertificateResponse;
import software.amazon.awssdk.iot.iotidentity.model.CreateKeysAndCertificateSubscriptionRequest;
import software.amazon.awssdk.iot.iotidentity.model.ErrorResponse;
import software.amazon.awssdk.iot.iotidentity.model.RegisterThingRequest;
import software.amazon.awssdk.iot.iotidentity.model.RegisterThingResponse;
import software.amazon.awssdk.iot.iotidentity.model.RegisterThingSubscriptionRequest;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.HashMap;
import com.google.gson.Gson;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import utils.commandlineutils.CommandLineUtils;
import utils.mqttclientconnectionwrapper.*;
import ServiceTestLifecycleEvents.ServiceTestLifecycleEvents;

public class FleetProvisioning {

    static CompletableFuture<Void> gotResponse;
    static IotIdentityClient iotIdentityClient;

    static CreateKeysAndCertificateResponse createKeysAndCertificateResponse = null;
    static RegisterThingResponse registerThingResponse = null;

    static long responseWaitTimeMs = 5000L; // 5 seconds

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

    static void onRejectedKeys(ErrorResponse response) {
        System.out.println("CreateKeysAndCertificate Request rejected, errorCode: " + response.errorCode +
                ", errorMessage: " + response.errorMessage +
                ", statusCode: " + response.statusCode);
        gotResponse.complete(null);
    }

    static void onRejectedRegister(ErrorResponse response) {
        System.out.println("RegisterThing Request rejected, errorCode: " + response.errorCode +
                ", errorMessage: " + response.errorMessage +
                ", statusCode: " + response.statusCode);
        gotResponse.complete(null);
    }

    static void onCreateKeysAndCertificateAccepted(CreateKeysAndCertificateResponse response) {
        if (response != null) {
            System.out.println("CreateKeysAndCertificate response certificateId: " + response.certificateId);
            if (createKeysAndCertificateResponse == null) {
                createKeysAndCertificateResponse = response;
            } else {
                System.out.println("CreateKeysAndCertificate response received after having already gotten a response!");
            }
        } else {
            System.out.println("CreateKeysAndCertificate response is null");
        }
        gotResponse.complete(null);
    }

    static void onRegisterThingAccepted(RegisterThingResponse response) {
        if (response != null) {
            System.out.println("RegisterThing response thingName: " + response.thingName);
            if (registerThingResponse == null) {
                registerThingResponse = response;
            } else {
                System.out.println("RegisterThing response received after having already gotten a response!");
            }
        } else {
            System.out.println("RegisterThing response is null");
        }
        gotResponse.complete(null);
    }

    static void onException(Exception e) {
        e.printStackTrace();
        System.out.println("Exception occurred " + e);
    }

    public static void main(String[] args) {
        CommandLineUtils.SampleCommandLineData cmdData = CommandLineUtils.getInputForIoTSample("FleetProvisioningSample", args);

        boolean exitWithError = false;

        if (cmdData.input_mqtt_version < 10) {
            throw new RuntimeException("Invalid MQTT version specified: " + mqttVersion);
        }

        try (MqttClientConnectionWrapper connection = createConnection(cmdData, cmdData.input_mqtt_version)) {
            // Create the identity client (Identity = Fleet Provisioning)
            iotIdentityClient = new IotIdentityClient(connection.getConnection());

            // Connect
            CompletableFuture<Boolean> connected = connection.start();
            boolean sessionPresent = connected.get(responseWaitTimeMs, TimeUnit.MILLISECONDS);
            System.out.println("Connected to " + (!sessionPresent ? "new" : "existing") + " session!");

            createKeysAndCertificateWorkflow(cmdData.input_templateName, cmdData.input_templateParameters);

            // Disconnect
            CompletableFuture<Void> disconnected = connection.stop();
            disconnected.get(responseWaitTimeMs, TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
            System.out.println("Exception encountered! " + "\n");
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

    private static void SubscribeToRegisterThing(String input_templateName) throws Exception {
        RegisterThingSubscriptionRequest registerThingSubscriptionRequest = new RegisterThingSubscriptionRequest();
        registerThingSubscriptionRequest.templateName = input_templateName;

        CompletableFuture<Integer> subscribedRegisterAccepted = iotIdentityClient.SubscribeToRegisterThingAccepted(
                registerThingSubscriptionRequest,
                QualityOfService.AT_LEAST_ONCE,
                FleetProvisioning::onRegisterThingAccepted,
                FleetProvisioning::onException);

        subscribedRegisterAccepted.get(responseWaitTimeMs, TimeUnit.MILLISECONDS);
        System.out.println("Subscribed to SubscribeToRegisterThingAccepted");

        CompletableFuture<Integer> subscribedRegisterRejected = iotIdentityClient.SubscribeToRegisterThingRejected(
                registerThingSubscriptionRequest,
                QualityOfService.AT_LEAST_ONCE,
                FleetProvisioning::onRejectedRegister,
                FleetProvisioning::onException);

        subscribedRegisterRejected.get(responseWaitTimeMs, TimeUnit.MILLISECONDS);
        System.out.println("Subscribed to SubscribeToRegisterThingRejected");
    }

    private static void createKeysAndCertificateWorkflow(String input_templateName, String input_templateParameters) throws Exception {
        CreateKeysAndCertificateSubscriptionRequest createKeysAndCertificateSubscriptionRequest = new CreateKeysAndCertificateSubscriptionRequest();
        CompletableFuture<Integer> keysSubscribedAccepted = iotIdentityClient.SubscribeToCreateKeysAndCertificateAccepted(
                createKeysAndCertificateSubscriptionRequest,
                QualityOfService.AT_LEAST_ONCE,
                FleetProvisioning::onCreateKeysAndCertificateAccepted);

        keysSubscribedAccepted.get(responseWaitTimeMs, TimeUnit.MILLISECONDS);
        System.out.println("Subscribed to CreateKeysAndCertificateAccepted");

        CompletableFuture<Integer> keysSubscribedRejected = iotIdentityClient.SubscribeToCreateKeysAndCertificateRejected(
                createKeysAndCertificateSubscriptionRequest,
                QualityOfService.AT_LEAST_ONCE,
                FleetProvisioning::onRejectedKeys);

        keysSubscribedRejected.get(responseWaitTimeMs, TimeUnit.MILLISECONDS);
        System.out.println("Subscribed to CreateKeysAndCertificateRejected");

        // Subscribes to the register thing accepted and rejected topics
        SubscribeToRegisterThing(input_templateName);

        CompletableFuture<Integer> publishKeys = iotIdentityClient.PublishCreateKeysAndCertificate(
                new CreateKeysAndCertificateRequest(),
                QualityOfService.AT_LEAST_ONCE);

        gotResponse = new CompletableFuture<>();
        publishKeys.get(responseWaitTimeMs, TimeUnit.MILLISECONDS);
        System.out.println("Published to CreateKeysAndCertificate");
        gotResponse.get(responseWaitTimeMs, TimeUnit.MILLISECONDS);
        System.out.println("Got response at CreateKeysAndCertificate");

        // Verify the response is good
        if (createKeysAndCertificateResponse == null) {
            throw new Exception("Got invalid/error createKeysAndCertificateResponse");
        }

        gotResponse = new CompletableFuture<>();
        System.out.println("RegisterThing now....");
        RegisterThingRequest registerThingRequest = new RegisterThingRequest();
        registerThingRequest.certificateOwnershipToken = createKeysAndCertificateResponse.certificateOwnershipToken;
        registerThingRequest.templateName = input_templateName;

        if (input_templateParameters != null && input_templateParameters != "") {
            registerThingRequest.parameters = new Gson().fromJson(input_templateParameters, HashMap.class);
        }

        CompletableFuture<Integer> publishRegister = iotIdentityClient.PublishRegisterThing(
                registerThingRequest,
                QualityOfService.AT_LEAST_ONCE);

        publishRegister.get(responseWaitTimeMs, TimeUnit.MILLISECONDS);
        System.out.println("Published to RegisterThing");
        gotResponse.get(responseWaitTimeMs, TimeUnit.MILLISECONDS);
        System.out.println("Got response at RegisterThing");
    }
}
