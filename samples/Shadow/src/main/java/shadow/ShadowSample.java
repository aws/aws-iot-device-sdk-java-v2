/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package shadow;

import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.CrtResource;
import software.amazon.awssdk.crt.CrtRuntimeException;
import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt.MqttClientConnectionEvents;
import software.amazon.awssdk.crt.mqtt.QualityOfService;
import software.amazon.awssdk.iot.AwsIotMqttConnectionBuilder;
import software.amazon.awssdk.iot.iotshadow.IotShadowClient;
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
import java.util.UUID;

import utils.commandlineutils.CommandLineUtils;

public class ShadowSample {
    static String clientId = "test-" + UUID.randomUUID().toString();
    static String thingName;
    static String rootCaPath;
    static String certPath;
    static String keyPath;
    static String endpoint;
    static int port = 8883;

    final static String SHADOW_PROPERTY = "color";
    final static String SHADOW_VALUE_DEFAULT = "off";

    static MqttClientConnection connection;
    static IotShadowClient shadow;
    static String localValue = null;
    static CompletableFuture<Void> gotResponse;

    static CommandLineUtils cmdUtils;

    static void onGetShadowAccepted(GetShadowResponse response) {
        System.out.println("Received initial shadow state");

        if (response.state != null && localValue == null) {
            gotResponse.complete(null);
            if (response.state.delta != null) {
                String value = response.state.delta.get(SHADOW_PROPERTY).toString();
                System.out.println("  Shadow delta value: " + value);
                return;
            }
            if (response.state.reported != null) {
                String value = response.state.reported.get(SHADOW_PROPERTY).toString();
                System.out.println("  Shadow reported value: " + value);
                // Initialize local value to match the reported shadow value
                localValue = value;
                return;
            }
        }

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

    static CompletableFuture<Void> changeShadowValue(String value) {
        if (localValue != null) {
            if (localValue.equals(value)) {
                System.out.println("Local value is already " + value);
                CompletableFuture<Void> result = new CompletableFuture<>();
                result.complete(null);
                return result;
            }
        }

        System.out.println("Changed local value to " + value);
        localValue = value;

        System.out.println("Updating shadow value to " + value);
        // build a request to let the service know our current value and desired value, and that we only want
        // to update if the version matches the version we know about
        UpdateShadowRequest request = new UpdateShadowRequest();
        request.thingName = thingName;
        request.state = new ShadowState();

        if (value.compareToIgnoreCase("clear_shadow") == 0) {
            request.state.desiredIsNullable = true;
            request.state.reportedIsNullable = true;
            request.state.desired = null;
            request.state.reported = null;
        }
        else if (value.compareToIgnoreCase("null") == 0) {
            // A bit of a hack - we have to set reportedNullIsValid OR desiredNullIsValid
            // so the JSON formatter will allow null , otherwise null will always be
            // be converted to "null"
            // As long as we're passing a Hashmap that is NOT assigned to null, it will not
            // clear the data - so we pass an empty HashMap to avoid clearing data we want to keep
            request.state.desiredIsNullable = true;
            request.state.reportedIsNullable = false;

            // We will only clear desired, so we need to pass an empty HashMap for reported
            request.state.reported = new HashMap<String, Object>() {{}};
            request.state.desired = new HashMap<String, Object>() {{
                 put(SHADOW_PROPERTY, null);
             }};
        }
        else
        {
            request.state.reported = new HashMap<String, Object>() {{
                put(SHADOW_PROPERTY, value);
            }};
            request.state.desired = new HashMap<String, Object>() {{
                put(SHADOW_PROPERTY, value);
            }};
        }

        // Publish the request
        return shadow.PublishUpdateShadow(request, QualityOfService.AT_LEAST_ONCE).thenRun(() -> {
            System.out.println("Update request published");
        }).exceptionally((ex) -> {
            System.out.println("Update request failed: " + ex.getMessage());
            System.exit(3);
            return null;
        });
    }

    public static void main(String[] args) {
        cmdUtils = new CommandLineUtils();
        cmdUtils.registerProgramName("ShadowSample");
        cmdUtils.addCommonMQTTCommands();
        cmdUtils.registerCommand("port", "<int>", "Port to use (optional, default='8883').");
        cmdUtils.registerCommand("thing_name", "<str>", "The name of the IoT thing.");
        cmdUtils.registerCommand("client_id", "<int>", "Client id to use (optional, default='test-*')");
        cmdUtils.registerCommand("help", "", "Prints this message");
        cmdUtils.sendArguments(args);

        thingName = cmdUtils.getCommandRequired("thing_name", "");
        endpoint = cmdUtils.getCommandRequired("endpoint", "");
        certPath = cmdUtils.getCommandRequired("cert", "");
        keyPath = cmdUtils.getCommandRequired("key", "");
        rootCaPath = cmdUtils.getCommandOrDefault("root_ca", rootCaPath);
        clientId = cmdUtils.getCommandOrDefault("client_id", clientId);
        port = Integer.parseInt(cmdUtils.getCommandOrDefault("port", String.valueOf(port)));

        if (cmdUtils.hasCommand("help")) {
            cmdUtils.printHelp();
            System.exit(1);
        }

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

        try(
            AwsIotMqttConnectionBuilder builder = AwsIotMqttConnectionBuilder.newMtlsBuilderFromPath(certPath, keyPath)) {

            if (rootCaPath != null) {
                builder.withCertificateAuthorityFromPath(null, rootCaPath);
            }

            builder.withClientId(clientId)
                    .withEndpoint(endpoint)
                    .withCleanSession(true)
                    .withConnectionEventCallbacks(callbacks);

            try(MqttClientConnection connection = builder.build()) {
                shadow = new IotShadowClient(connection);

                CompletableFuture<Boolean> connected = connection.connect();
                try {
                    boolean sessionPresent = connected.get();
                    System.out.println("Connected to " + (!sessionPresent ? "clean" : "existing") + " session!");
                } catch (Exception ex) {
                    throw new RuntimeException("Exception occurred during connect", ex);
                }

                System.out.println("Subscribing to shadow delta events...");
                ShadowDeltaUpdatedSubscriptionRequest requestShadowDeltaUpdated = new ShadowDeltaUpdatedSubscriptionRequest();
                requestShadowDeltaUpdated.thingName = thingName;
                CompletableFuture<Integer> subscribedToDeltas =
                        shadow.SubscribeToShadowDeltaUpdatedEvents(
                                requestShadowDeltaUpdated,
                                QualityOfService.AT_LEAST_ONCE,
                                ShadowSample::onShadowDeltaUpdated);
                subscribedToDeltas.get();

                System.out.println("Subscribing to update respones...");
                UpdateShadowSubscriptionRequest requestUpdateShadow = new UpdateShadowSubscriptionRequest();
                requestUpdateShadow.thingName = thingName;
                CompletableFuture<Integer> subscribedToUpdateAccepted =
                        shadow.SubscribeToUpdateShadowAccepted(
                                requestUpdateShadow,
                                QualityOfService.AT_LEAST_ONCE,
                                ShadowSample::onUpdateShadowAccepted);
                CompletableFuture<Integer> subscribedToUpdateRejected =
                        shadow.SubscribeToUpdateShadowRejected(
                                requestUpdateShadow,
                                QualityOfService.AT_LEAST_ONCE,
                                ShadowSample::onUpdateShadowRejected);
                subscribedToUpdateAccepted.get();
                subscribedToUpdateRejected.get();

                System.out.println("Subscribing to get responses...");
                GetShadowSubscriptionRequest requestGetShadow = new GetShadowSubscriptionRequest();
                requestGetShadow.thingName = thingName;
                CompletableFuture<Integer> subscribedToGetShadowAccepted =
                        shadow.SubscribeToGetShadowAccepted(
                                requestGetShadow,
                                QualityOfService.AT_LEAST_ONCE,
                                ShadowSample::onGetShadowAccepted);
                CompletableFuture<Integer> subscribedToGetShadowRejected =
                        shadow.SubscribeToGetShadowRejected(
                                requestGetShadow,
                                QualityOfService.AT_LEAST_ONCE,
                                ShadowSample::onGetShadowRejected);
                subscribedToGetShadowAccepted.get();
                subscribedToGetShadowRejected.get();

                gotResponse = new CompletableFuture<>();

                System.out.println("Requesting current shadow state...");
                GetShadowRequest getShadowRequest = new GetShadowRequest();
                getShadowRequest.thingName = thingName;
                CompletableFuture<Integer> publishedGetShadow = shadow.PublishGetShadow(
                        getShadowRequest,
                        QualityOfService.AT_LEAST_ONCE);
                publishedGetShadow.get();
                gotResponse.get();

                String newValue = "";
                Scanner scanner = new Scanner(System.in);
                while (true) {
                    System.out.print(SHADOW_PROPERTY + "> ");
                    System.out.flush();
                    newValue = scanner.next();
                    if (newValue.compareToIgnoreCase("quit") == 0) {
                        break;
                    }
                    gotResponse = new CompletableFuture<>();
                    changeShadowValue(newValue).get();
                    gotResponse.get();
                }
                scanner.close();

                CompletableFuture<Void> disconnected = connection.disconnect();
                disconnected.get();
            }
        } catch (CrtRuntimeException | InterruptedException | ExecutionException ex) {
            System.out.println("Exception encountered: " + ex.toString());
        }

        System.out.println("Complete!");
        CrtResource.waitForNoResources();
    }
}
