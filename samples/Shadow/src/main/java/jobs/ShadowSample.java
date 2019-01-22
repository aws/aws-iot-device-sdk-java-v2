/* Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package jobs;

import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.CrtRuntimeException;
import software.amazon.awssdk.crt.io.ClientBootstrap;
import software.amazon.awssdk.crt.io.EventLoopGroup;
import software.amazon.awssdk.crt.io.TlsContext;
import software.amazon.awssdk.crt.io.TlsContextOptions;
import software.amazon.awssdk.crt.mqtt.MqttClient;
import software.amazon.awssdk.crt.mqtt.MqttConnection;
import software.amazon.awssdk.crt.mqtt.MqttConnectionEvents;
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

public class ShadowSample {
    static String clientId = "samples-client-id";
    static String thingName;
    static String rootCaPath;
    static String certPath;
    static String keyPath;
    static String endpoint;
    static boolean showHelp = false;
    static int port = 8883;

    final static String SHADOW_PROPERTY = "color";
    final static String SHADOW_VALUE_DEFAULT = "off";

    static MqttConnection connection;
    static IotShadowClient shadow;
    static String localValue = null;
    static int shadowVersion = 1;
    static CompletableFuture<Void> gotResponse;

    static void printUsage() {
        System.out.println(
                "Usage:\n"+
                "  --help        This message\n"+
                "  --thingName   The name of the IoT thing\n"+
                "  --clientId    The Client ID to use when connecting\n"+
                "  -e|--endpoint AWS IoT service endpoint hostname\n"+
                "  -p|--port     Port to connect to on the endpoint\n"+
                "  -r|--rootca   Path to the root certificate\n"+
                "  -c|--cert     Path to the IoT thing certificate\n"+
                "  -k|--key      Path to the IoT thing public key"
        );
    }

    static void parseCommandLine(String[] args) {
        for (int idx = 0; idx < args.length; ++idx) {
            switch (args[idx]) {
                case "--help":
                    showHelp = true;
                    break;
                case "--clientId":
                    clientId = args[++idx];
                    break;
                case "--thingName":
                    thingName = args[++idx];
                    break;
                case "-e":
                case "--endpoint":
                    if (idx + 1 < args.length) {
                        endpoint = args[++idx];
                    }
                    break;
                case "-p":
                case "--port":
                    if (idx + 1 < args.length) {
                        port = Integer.parseInt(args[++idx]);
                    }
                    break;
                case "-r":
                case "--rootca":
                    if (idx + 1 < args.length) {
                        rootCaPath = args[++idx];
                    }
                    break;
                case "-c":
                case "--cert":
                    if (idx + 1 < args.length) {
                        certPath = args[++idx];
                    }
                    break;
                case "-k":
                case "--key":
                    if (idx + 1 < args.length) {
                        keyPath = args[++idx];
                    }
                    break;
                default:
                    System.out.println("Unrecognized argument: " + args[idx]);
            }
        }
    }

    static void onGetShadowAccepted(GetShadowResponse response) {
        System.out.println("Received initial shadow state");

        shadowVersion = response.version;
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
        shadowVersion = response.version;
        if (response.state != null && response.state.containsKey(SHADOW_PROPERTY)) {
            String value = response.state.get(SHADOW_PROPERTY).toString();
            System.out.println("  Delta wants to change value to '" + value + "'. Changing local value...");
            changeShadowValue(value);
        } else {
            System.out.println("  Delta did not report a change in " + SHADOW_PROPERTY);
        }
    }

    static void onUpdateShadowAccepted(UpdateShadowResponse response) {
        String value = response.state.reported.get(SHADOW_PROPERTY).toString();
        System.out.println("Shadow updated, value is " + value);
        shadowVersion = response.version;
        gotResponse.complete(null);
    }

    static void onUpdateShadowRejected(ErrorResponse response) {
        System.out.println("Shadow update was rejected: code: " + response.code + " message: " + response.message);
        System.exit(2);
    }

    static CompletableFuture<Void> changeShadowValue(String value) {
        if (localValue == value) {
            System.out.println("Local value is already " + value);
            CompletableFuture<Void> result = new CompletableFuture<>();
            result.complete(null);
            return result;
        }

        System.out.println("Changed local value to " + value);
        localValue = value;

        System.out.println("Updating shadow value to " + value);
        // build a request to let the service know our current value and desired value, and that we only want
        // to update if the version matches the version we know about
        UpdateShadowRequest request = new UpdateShadowRequest();
        request.version = shadowVersion;
        request.thingName = thingName;
        request.state = new ShadowState();
        request.state.reported = new HashMap<String, Object>() {{
           put(SHADOW_PROPERTY, value);
        }};
        request.state.desired = new HashMap<String, Object>() {{
            put(SHADOW_PROPERTY, value);
        }};

        // Publish the request
        return shadow.PublishUpdateShadow(request).thenRun(() -> {
            System.out.println("Update request published");
        }).exceptionally((ex) -> {
            System.out.println("Update request failed: " + ex.getMessage());
            System.exit(3);
            return null;
        });
    }

    public static void main(String[] args) {
        parseCommandLine(args);
        if (thingName == null || endpoint == null || rootCaPath == null || certPath == null || keyPath == null) {
            printUsage();
            return;
        }

        try {
            EventLoopGroup eventLoopGroup = new EventLoopGroup(1);
            ClientBootstrap clientBootstrap = new ClientBootstrap(eventLoopGroup);
            TlsContextOptions tlsContextOptions = TlsContextOptions.createWithMTLS(certPath, keyPath);
            tlsContextOptions.overrideDefaultTrustStore(null, rootCaPath);
            TlsContext tlsContext = new TlsContext(tlsContextOptions);
            MqttClient client = new MqttClient(clientBootstrap, tlsContext);

            connection = new MqttConnection(client, new MqttConnectionEvents() {
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
            });
            shadow = new IotShadowClient(connection);

            CompletableFuture<Boolean> connected = connection.connect(
                    clientId,
                    endpoint, port,
                    null, tlsContext, true, 0)
                    .exceptionally((ex) -> {
                        System.out.println("Exception occurred during connect: " + ex.toString());
                        return null;
                    });
            boolean sessionPresent = connected.get();
            System.out.println("Connected to " + (!sessionPresent ? "clean" : "existing") + " session!");

            System.out.println("Subscribing to shadow delta events...");
            ShadowDeltaUpdatedSubscriptionRequest requestShadowDeltaUpdated = new ShadowDeltaUpdatedSubscriptionRequest();
            requestShadowDeltaUpdated.thingName = thingName;
            CompletableFuture<Integer> subscribedToDeltas =
                    shadow.SubscribeToShadowDeltaUpdatedEvents(requestShadowDeltaUpdated, ShadowSample::onShadowDeltaUpdated);
            subscribedToDeltas.get();

            System.out.println("Subscribing to update respones...");
            UpdateShadowSubscriptionRequest requestUpdateShadow = new UpdateShadowSubscriptionRequest();
            requestUpdateShadow.thingName = thingName;
            CompletableFuture<Integer> subscribedToUpdateAccepted =
                    shadow.SubscribeToUpdateShadowAccepted(requestUpdateShadow, ShadowSample::onUpdateShadowAccepted);
            CompletableFuture<Integer> subscribedToUpdateRejected =
                    shadow.SubscribeToUpdateShadowRejected(requestUpdateShadow, ShadowSample::onUpdateShadowRejected);
            subscribedToUpdateAccepted.get();
            subscribedToUpdateRejected.get();

            System.out.println("Subscribing to get responses...");
            GetShadowSubscriptionRequest requestGetShadow = new GetShadowSubscriptionRequest();
            requestGetShadow.thingName = thingName;
            CompletableFuture<Integer> subscribedToGetShadowAccepted =
                    shadow.SubscribeToGetShadowAccepted(requestGetShadow, ShadowSample::onGetShadowAccepted);
            CompletableFuture<Integer> subscribedToGetShadowRejected =
                    shadow.SubscribeToGetShadowRejected(requestGetShadow, ShadowSample::onGetShadowRejected);
            subscribedToGetShadowAccepted.get();
            subscribedToGetShadowRejected.get();

            gotResponse = new CompletableFuture<>();

            System.out.println("Requesting current shadow state...");
            GetShadowRequest getShadowRequest = new GetShadowRequest();
            getShadowRequest.thingName = thingName;
            CompletableFuture<Integer> publishedGetShadow = shadow.PublishGetShadow(getShadowRequest);
            publishedGetShadow.get();
            gotResponse.get();

            String newValue = "";
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print(SHADOW_PROPERTY + "(" + shadowVersion + ")> ");
                System.out.flush();
                newValue = scanner.next();
                if (newValue.compareToIgnoreCase("quit") == 0) {
                    break;
                }
                gotResponse = new CompletableFuture<>();
                changeShadowValue(newValue).get();
                gotResponse.get();
            }

            CompletableFuture<Void> disconnected = connection.disconnect();
            disconnected.get();
        } catch (CrtRuntimeException | InterruptedException | ExecutionException ex) {
            System.out.println("Exception encountered: " + ex.toString());
        }

        System.out.println("Complete!");
    }
}
