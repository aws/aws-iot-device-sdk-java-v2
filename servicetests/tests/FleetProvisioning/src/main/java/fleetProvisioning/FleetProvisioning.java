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
import software.amazon.awssdk.iot.AwsIotMqttConnectionBuilder;
import software.amazon.awssdk.iot.iotidentity.IotIdentityClient;
import software.amazon.awssdk.iot.iotidentity.model.CreateCertificateFromCsrRequest;
import software.amazon.awssdk.iot.iotidentity.model.CreateCertificateFromCsrResponse;
import software.amazon.awssdk.iot.iotidentity.model.CreateCertificateFromCsrSubscriptionRequest;
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

public class FleetProvisioning {

    static CompletableFuture<Void> gotResponse;
    static IotIdentityClient iotIdentityClient;

    static CreateKeysAndCertificateResponse createKeysAndCertificateResponse = null;
    static CreateCertificateFromCsrResponse createCertificateFromCsrResponse = null;
    static RegisterThingResponse registerThingResponse = null;

    static long responseWaitTimeMs = 5000L; // 5 seconds

    static void onRejectedKeys(ErrorResponse response) {
        System.out.println("CreateKeysAndCertificate Request rejected, errorCode: " + response.errorCode +
                ", errorMessage: " + response.errorMessage +
                ", statusCode: " + response.statusCode);

        gotResponse.complete(null);
    }

    static void onRejectedCsr(ErrorResponse response) {
        System.out.println("CreateCertificateFromCsr Request rejected, errorCode: " + response.errorCode +
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

    static void onCreateCertificateFromCsrResponseAccepted(CreateCertificateFromCsrResponse response) {
        if (response != null) {
            System.out.println("CreateCertificateFromCsr response certificateId: " + response.certificateId);
            if (createCertificateFromCsrResponse == null) {
                createCertificateFromCsrResponse = response;
            } else {
                System.out.println("CreateCertificateFromCsr response received after having already gotten a response!");
            }
        } else {
            System.out.println("CreateCertificateFromCsr response is null");
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

    static MqttClientConnection createConnection(Boolean useMqtt5) {
        if (useMqtt5) {
            DALifecycleEvents lifecycleEvents = new DALifecycleEvents();
            try (AwsIotMqtt5ClientBuilder builder = AwsIotMqtt5ClientBuilder.newDirectMqttBuilderWithMtlsFromPath(
                        DATestUtils.endpoint, DATestUtils.certificatePath, DATestUtils.keyPath)) {
                ConnectPacket.ConnectPacketBuilder connectProperties = new ConnectPacket.ConnectPacketBuilder();
                connectProperties.withClientId(clientId);
                builder.withConnectProperties(connectProperties);
                builder.withLifeCycleEvents(lifecycleEvents);
                builder.withPort((long)port);
                Mqtt5Client client = builder.build();
                builder.close();
                return new MqttClientConnection(client, null);
            } catch (Exception ex) {
                throw new RuntimeException("Failed to create MQTT311 connection from MQTT5 client", ex);
            }
        } else {
            try (AwsIotMqttConnectionBuilder builder = AwsIotMqttConnectionBuilder
                    .newMtlsBuilderFromPath(DATestUtils.certificatePath, DATestUtils.keyPath)) {
                builder.withClientId(clientId)
                    .withEndpoint(DATestUtils.endpoint)
                    .withPort(port)
                    .withCleanSession(true)
                    .withProtocolOperationTimeoutMs(60000);

                return builder.build();
            } catch (Exception ex) {
                throw new RuntimeException("Failed to create MQTT311 connection", ex);
            }
        }
    }

    public static void main(String[] args) {
        CommandLineUtils.ServiceTestCommandLineData cmdData = CommandLineUtils.getInputForServiceTest("FleetProvisioning", args);

        MqttClientConnectionEvents callbacks = new MqttClientConnectionEvents() {
            @Override
            public void onConnectionInterrupted(int errorCode) {
                if (errorCode != 0) {
                    System.out.println("Connection interrupted: " + errorCode + ": " + CRT.awsErrorString(errorCode));
                }
            }

            @Override
            public void onConnectionResumed(boolean sessionPresent) {
                System.out.println("Connection resumed: " + (sessionPresent ? "existing session" : "clean session"));
            }
        };

        boolean exitWithError = false;

        try (MqttClientConnection connection = createConnection(true)) {
            /**
             * Create the MQTT connection from the builder
             */
            AwsIotMqttConnectionBuilder builder = AwsIotMqttConnectionBuilder.newMtlsBuilderFromPath(cmdData.input_cert, cmdData.input_key);
            if (cmdData.input_ca != "") {
                builder.withCertificateAuthorityFromPath(null, cmdData.input_ca);
            }
            builder.withConnectionEventCallbacks(callbacks)
                .withClientId(cmdData.input_clientId)
                .withEndpoint(cmdData.input_endpoint)
                .withPort((short)cmdData.input_port)
                .withCleanSession(true)
                .withProtocolOperationTimeoutMs(60000);
            connection = builder.build();
            builder.close();

            /**
             * Verify the connection was created
             */
            if (connection == null) {
                throw new RuntimeException("MQTT connection creation failed!");
            }

            // Create the identity client (Identity = Fleet Provisioning)
            iotIdentityClient = new IotIdentityClient(connection);

            // Connect
            CompletableFuture<Boolean> connected = connection.connect();
            boolean sessionPresent = connected.get(responseWaitTimeMs, TimeUnit.MILLISECONDS);
            System.out.println("Connected to " + (!sessionPresent ? "new" : "existing") + " session!");

            // Fleet Provision based on whether there is a CSR file path or not
            if (cmdData.input_csrPath == null) {
                createKeysAndCertificateWorkflow(cmdData.input_templateName, cmdData.input_templateParameters);
            } else {
                createCertificateFromCsrWorkflow(cmdData.input_templateName, cmdData.input_templateParameters, cmdData.input_csrPath);
            }

            // Disconnect
            CompletableFuture<Void> disconnected = connection.disconnect();
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

    private static void createCertificateFromCsrWorkflow(String input_templateName, String input_templateParameters, String input_csrPath) throws Exception {
        CreateCertificateFromCsrSubscriptionRequest createCertificateFromCsrSubscriptionRequest = new CreateCertificateFromCsrSubscriptionRequest();
        CompletableFuture<Integer> csrSubscribedAccepted = iotIdentityClient.SubscribeToCreateCertificateFromCsrAccepted(
                createCertificateFromCsrSubscriptionRequest,
                QualityOfService.AT_LEAST_ONCE,
                FleetProvisioning::onCreateCertificateFromCsrResponseAccepted);

        csrSubscribedAccepted.get(responseWaitTimeMs, TimeUnit.MILLISECONDS);
        System.out.println("Subscribed to CreateCertificateFromCsrAccepted");

        CompletableFuture<Integer> csrSubscribedRejected = iotIdentityClient.SubscribeToCreateCertificateFromCsrRejected(
                createCertificateFromCsrSubscriptionRequest,
                QualityOfService.AT_LEAST_ONCE,
                FleetProvisioning::onRejectedCsr);

        csrSubscribedRejected.get(responseWaitTimeMs, TimeUnit.MILLISECONDS);
        System.out.println("Subscribed to CreateCertificateFromCsrRejected");

        // Subscribes to the register thing accepted and rejected topics
        SubscribeToRegisterThing(input_templateName);

        String csrContents = new String(Files.readAllBytes(Paths.get(input_csrPath)));
        CreateCertificateFromCsrRequest createCertificateFromCsrRequest = new CreateCertificateFromCsrRequest();
        createCertificateFromCsrRequest.certificateSigningRequest = csrContents;
        CompletableFuture<Integer> publishCsr = iotIdentityClient.PublishCreateCertificateFromCsr(
                createCertificateFromCsrRequest,
                QualityOfService.AT_LEAST_ONCE);

        gotResponse = new CompletableFuture<>();
        publishCsr.get(responseWaitTimeMs, TimeUnit.MILLISECONDS);
        System.out.println("Published to CreateCertificateFromCsr");
        gotResponse.get(responseWaitTimeMs, TimeUnit.MILLISECONDS);
        System.out.println("Got response at CreateCertificateFromCsr");

        // Verify the response is good
        if (createCertificateFromCsrResponse == null) {
            throw new Exception("Got invalid/error createCertificateFromCsrResponse");
        }

        gotResponse = new CompletableFuture<>();
        System.out.println("RegisterThing now....");
        RegisterThingRequest registerThingRequest = new RegisterThingRequest();
        registerThingRequest.certificateOwnershipToken = createCertificateFromCsrResponse.certificateOwnershipToken;
        registerThingRequest.templateName = input_templateName;
        registerThingRequest.parameters = new Gson().fromJson(input_templateParameters, HashMap.class);
        CompletableFuture<Integer> publishRegister = iotIdentityClient.PublishRegisterThing(
                registerThingRequest,
                QualityOfService.AT_LEAST_ONCE);

        publishRegister.get(responseWaitTimeMs, TimeUnit.MILLISECONDS);
        System.out.println("Published to RegisterThing");
        gotResponse.get(responseWaitTimeMs, TimeUnit.MILLISECONDS);
        System.out.println("Got response at RegisterThing");
    }
}
