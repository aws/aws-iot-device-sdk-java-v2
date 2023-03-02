/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package shadow;

import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.CrtResource;
import software.amazon.awssdk.crt.CrtRuntimeException;
import software.amazon.awssdk.crt.Log;
import software.amazon.awssdk.crt.mqtt5.*;
import software.amazon.awssdk.crt.mqtt5.packets.*;
import software.amazon.awssdk.crt.mqtt5.Mqtt5Client;
import software.amazon.awssdk.crt.mqtt5.QOS;
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

import java.util.List;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.Scanner;
import java.util.UUID;

import utils.commandlineutils.CommandLineUtils;

public class ShadowSample {

    // When run normally, we want to get input from the console
    // When run from CI, we want to automatically make changes to the shadow document
    static String ciPropValue = System.getProperty("aws.crt.ci");
    static boolean isCI = ciPropValue != null && Boolean.valueOf(ciPropValue);

    static String thingName;
    final static String SHADOW_PROPERTY = "color";
    final static String SHADOW_VALUE_DEFAULT = "off";

    static Mqtt5Client client;
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
        return shadow.PublishUpdateShadow(request, QOS.AT_LEAST_ONCE).thenRun(() -> {
            System.out.println("Update request published");
        }).exceptionally((ex) -> {
            System.out.println("Update request failed: " + ex.getMessage());
            System.exit(3);
            return null;
        });
    }

    static final class SampleLifecycleEvents implements Mqtt5ClientOptions.LifecycleEvents {
        CompletableFuture<Void> connectedFuture = new CompletableFuture<>();
        CompletableFuture<Void> stoppedFuture = new CompletableFuture<>();

        @Override
        public void onAttemptingConnect(Mqtt5Client client, OnAttemptingConnectReturn onAttemptingConnectReturn) {
            System.out.println("Mqtt5 Client: Attempting connection...");
        }

        @Override
        public void onConnectionSuccess(Mqtt5Client client, OnConnectionSuccessReturn onConnectionSuccessReturn) {
            System.out.println("Mqtt5 Client: Connection success, client ID: "
                + onConnectionSuccessReturn.getNegotiatedSettings().getAssignedClientID());
            connectedFuture.complete(null);
        }

        @Override
        public void onConnectionFailure(Mqtt5Client client, OnConnectionFailureReturn onConnectionFailureReturn) {
            String errorString = CRT.awsErrorString(onConnectionFailureReturn.getErrorCode());
            System.out.println("Mqtt5 Client: Connection failed with error: " + errorString);
            connectedFuture.completeExceptionally(new Exception("Could not connect: " + errorString));
        }

        @Override
        public void onDisconnection(Mqtt5Client client, OnDisconnectionReturn onDisconnectionReturn) {
            System.out.println("Mqtt5 Client: Disconnected");
            DisconnectPacket disconnectPacket = onDisconnectionReturn.getDisconnectPacket();
            if (disconnectPacket != null) {
                System.out.println("\tDisconnection packet code: " + disconnectPacket.getReasonCode());
                System.out.println("\tDisconnection packet reason: " + disconnectPacket.getReasonString());
            }
        }

        @Override
        public void onStopped(Mqtt5Client client, OnStoppedReturn onStoppedReturn) {
            System.out.println("Mqtt5 Client: Stopped");
            stoppedFuture.complete(null);
        }
    }

    static final class SamplePublishEvents implements Mqtt5ClientOptions.PublishEvents {
        @Override
        public void onMessageReceived(Mqtt5Client client, PublishReturn publishReturn) {
            PublishPacket publishPacket = publishReturn.getPublishPacket();
            if (publishPacket == null) {
                return;
            }

            System.out.println("Publish received on topic: " + publishPacket.getTopic());
            System.out.println("Message: " + new String(publishPacket.getPayload()));

            List<UserProperty> packetProperties = publishPacket.getUserProperties();
            if (packetProperties != null) {
                for (int i = 0; i < packetProperties.size(); i++) {
                    UserProperty property = packetProperties.get(i);
                    System.out.println("\twith UserProperty: (" + property.key + ", " + property.value + ")");
                }
            }
        }
    }

    public static void main(String[] args) {
        cmdUtils = new CommandLineUtils();
        cmdUtils.registerProgramName("ShadowSample");
        cmdUtils.addCommonMQTTCommands();
        cmdUtils.registerCommand("key", "<path>", "Path to your key in PEM format.");
        cmdUtils.registerCommand("cert", "<path>", "Path to your client certificate in PEM format.");
        cmdUtils.registerCommand("port", "<int>", "Port to use (optional, default='8883').");
        cmdUtils.registerCommand("thing_name", "<str>", "The name of the IoT thing.");
        cmdUtils.registerCommand("client_id", "<int>", "Client id to use (optional, default='test-*')");
        cmdUtils.sendArguments(args);

        thingName = cmdUtils.getCommandRequired("thing_name", "");

        try {

            /* Create a client based on desired connection type */
            SampleLifecycleEvents lifecycleEvents = new SampleLifecycleEvents();
            SamplePublishEvents publishEvents = new SamplePublishEvents();
            Mqtt5Client client;
            if (cmdUtils.hasCommand("cert") || cmdUtils.hasCommand("key")) {
                client = cmdUtils.buildDirectMQTT5Connection(lifecycleEvents, publishEvents);
            } else {
                client = cmdUtils.buildWebsocketMQTT5Connection(lifecycleEvents, publishEvents);
            }


            shadow = new IotShadowClient(client);


            /* Connect */
            client.start();

            System.out.println("Subscribing to shadow delta events...");
            ShadowDeltaUpdatedSubscriptionRequest requestShadowDeltaUpdated = new ShadowDeltaUpdatedSubscriptionRequest();
            requestShadowDeltaUpdated.thingName = thingName;
            CompletableFuture<SubAckPacket> subscribedToDeltas =
                    shadow.Mqtt5SubscribeToShadowDeltaUpdatedEvents(
                            requestShadowDeltaUpdated,
                            QOS.AT_LEAST_ONCE,
                            ShadowSample::onShadowDeltaUpdated);
            subscribedToDeltas.get();

            System.out.println("Subscribing to update responses...");
            UpdateShadowSubscriptionRequest requestUpdateShadow = new UpdateShadowSubscriptionRequest();
            requestUpdateShadow.thingName = thingName;
            CompletableFuture<SubAckPacket> subscribedToUpdateAccepted =
                    shadow.Mqtt5SubscribeToUpdateShadowAccepted(
                            requestUpdateShadow,
                            QOS.AT_LEAST_ONCE,
                            ShadowSample::onUpdateShadowAccepted);
            CompletableFuture<SubAckPacket> subscribedToUpdateRejected =
                    shadow.Mqtt5SubscribeToUpdateShadowRejected(
                            requestUpdateShadow,
                            QOS.AT_LEAST_ONCE,
                            ShadowSample::onUpdateShadowRejected);
            subscribedToUpdateAccepted.get();
            subscribedToUpdateRejected.get();

            System.out.println("Subscribing to get responses...");
            GetShadowSubscriptionRequest requestGetShadow = new GetShadowSubscriptionRequest();
            requestGetShadow.thingName = thingName;
            CompletableFuture<SubAckPacket> subscribedToGetShadowAccepted =
                    shadow.Mqtt5SubscribeToGetShadowAccepted(
                            requestGetShadow,
                            QOS.AT_LEAST_ONCE,
                            ShadowSample::onGetShadowAccepted);
            CompletableFuture<SubAckPacket> subscribedToGetShadowRejected =
                    shadow.Mqtt5SubscribeToGetShadowRejected(
                            requestGetShadow,
                            QOS.AT_LEAST_ONCE,
                            ShadowSample::onGetShadowRejected);
            subscribedToGetShadowAccepted.get();
            subscribedToGetShadowRejected.get();

            gotResponse = new CompletableFuture<>();

            System.out.println("Requesting current shadow state...");
            GetShadowRequest getShadowRequest = new GetShadowRequest();
            getShadowRequest.thingName = thingName;
            CompletableFuture<PublishResult> publishedGetShadow = shadow.PublishGetShadow(
                    getShadowRequest,
                    QOS.AT_LEAST_ONCE);
            publishedGetShadow.get();
            gotResponse.get();

            // If this is not running in CI, then take input from the console
            if (isCI == false) {
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
            }
            // If this is in running in CI, then automatically update the shadow
            else {
                int messages_sent = 0;
                String message_string = "";
                while (messages_sent < 5) {
                    gotResponse = new CompletableFuture<>();
                    message_string = "Shadow_Value_" + String.valueOf(messages_sent);
                    changeShadowValue(message_string).get();
                    gotResponse.get();
                    messages_sent += 1;
                }
            }

            // Close the connection now that we are completely done with it.
            client.close();

        } catch (CrtRuntimeException | InterruptedException | ExecutionException ex) {
            System.out.println("Exception encountered: " + ex.toString());
            System.exit(1);
        }

        System.out.println("Complete!");
        CrtResource.waitForNoResources();
    }
}
