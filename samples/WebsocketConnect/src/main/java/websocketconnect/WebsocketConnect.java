/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package websocketconnect;

import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.CrtResource;
import software.amazon.awssdk.crt.CrtRuntimeException;
import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt.MqttClientConnectionEvents;
import software.amazon.awssdk.crt.http.HttpProxyOptions;
import software.amazon.awssdk.iot.AwsIotMqttConnectionBuilder;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.CompletableFuture;

import utils.commandlineutils.CommandLineUtils;

public class WebsocketConnect {
    static CommandLineUtils cmdUtils;

    static MqttClientConnection createMqttClientConnection(CommandLineUtils.SampleCommandLineData cmdData) {
        /**
         * Callbacks for various connection events.
         *
         * For a list of supported connection events, see
         * https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt/MqttClientConnectionEvents.html
         */
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

        /**
         * Create a new MQTT connection from the builder.
         *
         * Instantiate a builder in the try-with-resources block, so it will be closed automatically at the end of the
         * block. Otherwise, we must call 'builder.close()' explicitly when the builder is not required anymore.
         */
        try (AwsIotMqttConnectionBuilder builder = AwsIotMqttConnectionBuilder.newMtlsBuilderFromPath(null, null)) {
            if (cmdData.input_ca != "") {
                builder.withCertificateAuthorityFromPath(null, cmdData.input_ca);
            }
            builder.withConnectionEventCallbacks(callbacks)
                .withClientId(cmdData.input_clientId)
                .withEndpoint(cmdData.input_endpoint)
                .withPort(cmdData.input_port)
                .withCleanSession(true)
                .withProtocolOperationTimeoutMs(60000);
            if (cmdData.input_proxyHost != "" && cmdData.input_proxyPort > 0) {
                HttpProxyOptions proxyOptions = new HttpProxyOptions();
                proxyOptions.setHost(cmdData.input_proxyHost);
                proxyOptions.setPort(cmdData.input_proxyPort);
                builder.withHttpProxyOptions(proxyOptions);
            }
            builder.withWebsockets(true);
            builder.withWebsocketSigningRegion(cmdData.input_signingRegion);
            return builder.build();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to create MQTT311 connection", ex);
        }
    }

    public static void main(String[] args) {
        /**
         * cmdData is the arguments/input from the command line placed into a single struct for
         * use in this sample. This handles all of the command line parsing, validating, etc.
         * See the Utils/CommandLineUtils for more information.
         */
        CommandLineUtils.SampleCommandLineData cmdData = CommandLineUtils.getInputForIoTSample("WebsocketConnect", args);

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

        /**
         * Create connection in the try-with-resources block, so it will be closed automatically at the end of the block.
         * Otherwise, we must call 'connection.close()' explicitly when the connection is not required anymore.
         */
        try (MqttClientConnection connection = createMqttClientConnection(cmdData)) {
            /**
             * Verify the connection was created
             */
            if (connection == null) {
                throw new RuntimeException("MQTT connection creation failed!");
            }

            /**
             * Connect and disconnect
             */
            CompletableFuture<Boolean> connected = connection.connect();
            try {
                boolean sessionPresent = connected.get();
                System.out.println("Connected to " + (!sessionPresent ? "new" : "existing") + " session!");
            } catch (Exception ex) {
                throw new RuntimeException("Exception occurred during connect", ex);
            }
            System.out.println("Disconnecting...");
            CompletableFuture<Void> disconnected = connection.disconnect();
            disconnected.get();
            System.out.println("Disconnected.");

        } catch (CrtRuntimeException | InterruptedException | ExecutionException ex) {
            throw new RuntimeException("WebSocketConnect execution failure", ex);
        }

        CrtResource.waitForNoResources();

        System.out.println("Complete!");
    }
}
