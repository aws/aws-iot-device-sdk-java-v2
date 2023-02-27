/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package identity;

import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.CrtResource;
import software.amazon.awssdk.crt.CrtRuntimeException;
import software.amazon.awssdk.crt.Log;
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
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import utils.commandlineutils.CommandLineUtils;

public class FleetProvisioningSample {
    static String templateName;
    static String templateParameters;
    static String csrPath;

    static CompletableFuture<Void> gotResponse;
    static IotIdentityClient iotIdentityClient;

    static CreateKeysAndCertificateResponse createKeysAndCertificateResponse = null;
    static CreateCertificateFromCsrResponse createCertificateFromCsrResponse = null;
    static RegisterThingResponse registerThingResponse = null;

    static long responseWaitTimeMs = 5000L; // 5 seconds

    static CommandLineUtils cmdUtils;

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

    public static void main(String[] args) {

        cmdUtils = new CommandLineUtils();
        cmdUtils.registerProgramName("FleetProvisioningSample");
        cmdUtils.addCommonMQTTCommands();
        cmdUtils.registerCommand("key", "<path>", "Path to your key in PEM format.");
        cmdUtils.registerCommand("cert", "<path>", "Path to your client certificate in PEM format.");
        cmdUtils.registerCommand("client_id", "<int>", "Client id to use (optional, default='test-*').");
        cmdUtils.registerCommand("port", "<int>", "Port to connect to on the endpoint (optional, default='8883').");
        cmdUtils.registerCommand("template_name", "<str>", "Provisioning template name.");
        cmdUtils.registerCommand("template_parameters", "<json>", "Provisioning template parameters.");
        cmdUtils.registerCommand("csr", "<path>", "Path to the CSR file (optional).");
        cmdUtils.sendArguments(args);

        templateName = cmdUtils.getCommandRequired("template_name", "");
        templateParameters = cmdUtils.getCommandRequired("template_parameters", "");
        csrPath = cmdUtils.getCommandOrDefault("csr", csrPath);

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

        MqttClientConnection connection = null;
        boolean exitWithError = false;

        try {
            connection = cmdUtils.buildMQTTConnection(callbacks);
            iotIdentityClient = new IotIdentityClient(connection);

            CompletableFuture<Boolean> connected = connection.connect();
            boolean sessionPresent = connected.get(responseWaitTimeMs, TimeUnit.MILLISECONDS);
            System.out.println("Connected to " + (!sessionPresent ? "new" : "existing") + " session!");

            if (csrPath == null) {
                createKeysAndCertificateWorkflow();
            } else {
                createCertificateFromCsrWorkflow();
            }

            CompletableFuture<Void> disconnected = connection.disconnect();
            disconnected.get(responseWaitTimeMs, TimeUnit.MILLISECONDS);

        } catch (Exception ex) {
            System.out.println("Exception encountered! " + "\n");
            ex.printStackTrace();
            exitWithError = true;
        }

        if (connection != null) {
            // Close the connection now that we are completely done with it.
            connection.close();
        }

        CrtResource.waitForNoResources();
        System.out.println("Sample complete!");

        if (exitWithError) {
            System.exit(1);
        } else {
            System.exit(0);
        }
    }

    private static void SubscribeToRegisterThing() throws Exception {
        RegisterThingSubscriptionRequest registerThingSubscriptionRequest = new RegisterThingSubscriptionRequest();
        registerThingSubscriptionRequest.templateName = templateName;

        CompletableFuture<Integer> subscribedRegisterAccepted = iotIdentityClient.SubscribeToRegisterThingAccepted(
                registerThingSubscriptionRequest,
                QualityOfService.AT_LEAST_ONCE,
                FleetProvisioningSample::onRegisterThingAccepted,
                FleetProvisioningSample::onException);

        subscribedRegisterAccepted.get(responseWaitTimeMs, TimeUnit.MILLISECONDS);
        System.out.println("Subscribed to SubscribeToRegisterThingAccepted");

        CompletableFuture<Integer> subscribedRegisterRejected = iotIdentityClient.SubscribeToRegisterThingRejected(
                registerThingSubscriptionRequest,
                QualityOfService.AT_LEAST_ONCE,
                FleetProvisioningSample::onRejectedRegister,
                FleetProvisioningSample::onException);

        subscribedRegisterRejected.get(responseWaitTimeMs, TimeUnit.MILLISECONDS);
        System.out.println("Subscribed to SubscribeToRegisterThingRejected");
    }

    private static void createKeysAndCertificateWorkflow() throws Exception {
        CreateKeysAndCertificateSubscriptionRequest createKeysAndCertificateSubscriptionRequest = new CreateKeysAndCertificateSubscriptionRequest();
        CompletableFuture<Integer> keysSubscribedAccepted = iotIdentityClient.SubscribeToCreateKeysAndCertificateAccepted(
                createKeysAndCertificateSubscriptionRequest,
                QualityOfService.AT_LEAST_ONCE,
                FleetProvisioningSample::onCreateKeysAndCertificateAccepted);

        keysSubscribedAccepted.get(responseWaitTimeMs, TimeUnit.MILLISECONDS);
        System.out.println("Subscribed to CreateKeysAndCertificateAccepted");

        CompletableFuture<Integer> keysSubscribedRejected = iotIdentityClient.SubscribeToCreateKeysAndCertificateRejected(
                createKeysAndCertificateSubscriptionRequest,
                QualityOfService.AT_LEAST_ONCE,
                FleetProvisioningSample::onRejectedKeys);

        keysSubscribedRejected.get(responseWaitTimeMs, TimeUnit.MILLISECONDS);
        System.out.println("Subscribed to CreateKeysAndCertificateRejected");

        // Subscribes to the register thing accepted and rejected topics
        SubscribeToRegisterThing();

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
        registerThingRequest.templateName = templateName;

        if (templateParameters != null && templateParameters != "") {
            registerThingRequest.parameters = new Gson().fromJson(templateParameters, HashMap.class);
        }

        CompletableFuture<Integer> publishRegister = iotIdentityClient.PublishRegisterThing(
                registerThingRequest,
                QualityOfService.AT_LEAST_ONCE);

        publishRegister.get(responseWaitTimeMs, TimeUnit.MILLISECONDS);
        System.out.println("Published to RegisterThing");
        gotResponse.get(responseWaitTimeMs, TimeUnit.MILLISECONDS);
        System.out.println("Got response at RegisterThing");
    }

    private static void createCertificateFromCsrWorkflow() throws Exception {
        CreateCertificateFromCsrSubscriptionRequest createCertificateFromCsrSubscriptionRequest = new CreateCertificateFromCsrSubscriptionRequest();
        CompletableFuture<Integer> csrSubscribedAccepted = iotIdentityClient.SubscribeToCreateCertificateFromCsrAccepted(
                createCertificateFromCsrSubscriptionRequest,
                QualityOfService.AT_LEAST_ONCE,
                FleetProvisioningSample::onCreateCertificateFromCsrResponseAccepted);

        csrSubscribedAccepted.get(responseWaitTimeMs, TimeUnit.MILLISECONDS);
        System.out.println("Subscribed to CreateCertificateFromCsrAccepted");

        CompletableFuture<Integer> csrSubscribedRejected = iotIdentityClient.SubscribeToCreateCertificateFromCsrRejected(
                createCertificateFromCsrSubscriptionRequest,
                QualityOfService.AT_LEAST_ONCE,
                FleetProvisioningSample::onRejectedCsr);

        csrSubscribedRejected.get(responseWaitTimeMs, TimeUnit.MILLISECONDS);
        System.out.println("Subscribed to CreateCertificateFromCsrRejected");

        // Subscribes to the register thing accepted and rejected topics
        SubscribeToRegisterThing();

        String csrContents = new String(Files.readAllBytes(Paths.get(csrPath)));
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
        registerThingRequest.templateName = templateName;
        registerThingRequest.parameters = new Gson().fromJson(templateParameters, HashMap.class);
        CompletableFuture<Integer> publishRegister = iotIdentityClient.PublishRegisterThing(
                registerThingRequest,
                QualityOfService.AT_LEAST_ONCE);

        publishRegister.get(responseWaitTimeMs, TimeUnit.MILLISECONDS);
        System.out.println("Published to RegisterThing");
        gotResponse.get(responseWaitTimeMs, TimeUnit.MILLISECONDS);
        System.out.println("Got response at RegisterThing");
    }
}
