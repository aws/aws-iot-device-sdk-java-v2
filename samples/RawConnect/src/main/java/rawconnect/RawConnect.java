/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package rawconnect;

import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.CrtResource;
import software.amazon.awssdk.crt.CrtRuntimeException;
import software.amazon.awssdk.crt.Log;
import software.amazon.awssdk.crt.io.ClientBootstrap;
import software.amazon.awssdk.crt.io.TlsContext;
import software.amazon.awssdk.crt.io.TlsContextOptions;
import software.amazon.awssdk.crt.mqtt.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import utils.commandlineutils.CommandLineUtils;

public class RawConnect {
    // When run normally, we want to exit nicely even if something goes wrong
    // When run from CI, we want to let an exception escape which in turn causes the
    // exec:java task to return a non-zero exit code
    static String ciPropValue = System.getProperty("aws.crt.ci");
    static boolean isCI = ciPropValue != null && Boolean.valueOf(ciPropValue);

    static CommandLineUtils cmdUtils;

    public static void main(String[] args) {

        /**
         * Parse the command line data and store the values in cmdData for this sample.
         */
        cmdUtils = new CommandLineUtils();
        cmdUtils.registerProgramName("RawConnect");
        CommandLineUtils.SampleCommandLineData cmdData = cmdUtils.parseSampleInputPkcs11Connect(args);

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

        if (cmdData.input_authParams != null && cmdData.input_authParams.size() > 0) {
            if (cmdData.input_username.length() > 0) {
                StringBuilder usernameBuilder = new StringBuilder();

                usernameBuilder.append(cmdData.input_username);
                usernameBuilder.append("?");
                for (int i = 0; i < cmdData.input_authParams.size(); ++i) {
                    usernameBuilder.append(cmdData.input_authParams.get(i));
                    if (i + 1 < cmdData.input_authParams.size()) {
                        usernameBuilder.append("&");
                    }
                }
                cmdData.input_username = usernameBuilder.toString();
            }
        }

        try(
            TlsContextOptions tlsContextOptions = TlsContextOptions.createWithMtlsFromPath(cmdData.input_cert, cmdData.input_key)) {

            if (cmdData.input_ca != null) {
                tlsContextOptions.overrideDefaultTrustStoreFromPath(null, cmdData.input_ca);
            }

            int port = 8883;
            if (TlsContextOptions.isAlpnSupported()) {
                port = 443;
                tlsContextOptions.withAlpnList(cmdData.input_protocolName);
            }

            try(TlsContext tlsContext = new TlsContext(tlsContextOptions);
                MqttClient client = new MqttClient(tlsContext);
                MqttConnectionConfig config = new MqttConnectionConfig()) {

                config.setMqttClient(client);
                config.setClientId(cmdData.input_clientId);
                config.setConnectionCallbacks(callbacks);
                config.setCleanSession(true);
                config.setEndpoint(cmdData.input_endpoint);
                config.setPort(port);

                if (cmdData.input_username != null && cmdData.input_username.length() > 0) {
                    config.setLogin(cmdData.input_username, cmdData.input_password);
                }

                try (MqttClientConnection connection = new MqttClientConnection(config)) {

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

                    // Close the connection now that we are completely done with it.
                    connection.close();
                }
            }
        } catch (CrtRuntimeException | InterruptedException | ExecutionException ex) {
            System.out.println("Exception encountered: " + ex.toString());
        }

        CrtResource.waitForNoResources();
        System.out.println("Complete!");
    }
}
