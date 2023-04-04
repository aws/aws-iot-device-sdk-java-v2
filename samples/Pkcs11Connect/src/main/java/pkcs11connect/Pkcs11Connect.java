/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package pkcs11connect;

import software.amazon.awssdk.crt.*;
import software.amazon.awssdk.crt.io.*;
import software.amazon.awssdk.crt.mqtt.*;
import software.amazon.awssdk.iot.AwsIotMqttConnectionBuilder;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import utils.commandlineutils.CommandLineUtils;

public class Pkcs11Connect {
    // When run normally, we want to exit nicely even if something goes wrong
    // When run from CI, we want to let an exception escape which in turn causes the
    // exec:java task to return a non-zero exit code
    static String ciPropValue = System.getProperty("aws.crt.ci");
    static boolean isCI = ciPropValue != null && Boolean.valueOf(ciPropValue);

    static CommandLineUtils cmdUtils;

    /*
     * When called during a CI run, throw an exception that will escape and fail the
     * exec:java task When called otherwise, print what went wrong (if anything) and
     * just continue (return from main)
     */
    static void onApplicationFailure(Throwable cause) {
        if (isCI) {
            throw new RuntimeException("Pkcs11Connect execution failure", cause);
        } else if (cause != null) {
            System.out.println("Exception encountered: " + cause.toString());
        }
    }

    public static void main(String[] args) {

        /**
         * Register the command line inputs
         */
        cmdUtils = new CommandLineUtils();
        cmdUtils.registerProgramName("Pkcs11PubSub");
        cmdUtils.addCommonMQTTCommands();
        cmdUtils.registerCommand("cert", "<path>", "Path to your client certificate in PEM format.");
        cmdUtils.registerCommand("client_id", "<int>", "Client id to use (optional, default='test-*').");
        cmdUtils.registerCommand("port", "<int>", "Port to connect to on the endpoint (optional, default='8883').");
        cmdUtils.registerCommand("pkcs11_lib", "<path>", "Path to PKCS#11 library.");
        cmdUtils.registerCommand("pin", "<int>", "User PIN for logging into PKCS#11 token.");
        cmdUtils.registerCommand("token_label", "<str>", "Label of PKCS#11 token to use (optional).");
        cmdUtils.registerCommand("slot_id", "<int>", "Slot ID containing PKCS#11 token to use (optional).");
        cmdUtils.registerCommand("key_label", "<str>", "Label of private key on the PKCS#11 token (optional).");
        cmdUtils.sendArguments(args);

        /**
         * Gather the input from the command line
         */
        String input_endpoint = cmdUtils.getCommandRequired("endpoint", "");
        String input_certPath = cmdUtils.getCommandRequired("cert", "");
        String input_CaPath = cmdUtils.getCommandOrDefault("ca_file", "");
        String input_clientId = cmdUtils.getCommandOrDefault("client_id", "test-" + UUID.randomUUID().toString());
        int input_port = Integer.parseInt(cmdUtils.getCommandOrDefault("port", "8883"));
        String input_pkcs11LibPath = cmdUtils.getCommandRequired("pkcs11_lib", "");
        String input_pkcs11UserPin = cmdUtils.getCommandRequired("pin", "");
        String input_pkcs11TokenLabel = cmdUtils.getCommandOrDefault("token_label", "");
        Long input_pkcs11SlotId = null;
        if (cmdUtils.hasCommand("slot_id")) {
            input_pkcs11SlotId = Long.parseLong(cmdUtils.getCommandOrDefault("slot_id", "-1"));
        }
        String input_pkcs11KeyLabel = cmdUtils.getCommandOrDefault("key_label", "");

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

        // Load PKCS#11 library
        try (Pkcs11Lib pkcs11Lib = new Pkcs11Lib(input_pkcs11LibPath);
                TlsContextPkcs11Options pkcs11Options = new TlsContextPkcs11Options(pkcs11Lib)) {

            pkcs11Options.withCertificateFilePath(input_certPath);
            pkcs11Options.withUserPin(input_pkcs11UserPin);

            // Pass arguments to help find the correct PKCS#11 token,
            // and the private key on that token. You don't need to pass
            // any of these arguments if your PKCS#11 device only has one
            // token, or the token only has one private key. But if there
            // are multiple tokens, or multiple keys to choose from, you
            // must narrow down which one should be used.

            if (input_pkcs11TokenLabel != null && input_pkcs11TokenLabel != "") {
                pkcs11Options.withTokenLabel(input_pkcs11TokenLabel);
            }

            if (input_pkcs11SlotId != null) {
                pkcs11Options.withSlotId(input_pkcs11SlotId);
            }

            if (input_pkcs11KeyLabel != null && input_pkcs11KeyLabel != "") {
                pkcs11Options.withPrivateKeyObjectLabel(input_pkcs11KeyLabel);
            }

            try (AwsIotMqttConnectionBuilder builder = AwsIotMqttConnectionBuilder
                    .newMtlsPkcs11Builder(pkcs11Options)) {

                if (input_CaPath != null && input_CaPath != "") {
                    builder.withCertificateAuthorityFromPath(null, input_CaPath);
                }

                builder.withConnectionEventCallbacks(callbacks)
                        .withClientId(input_clientId)
                        .withEndpoint(input_endpoint)
                        .withPort((short) input_port)
                        .withCleanSession(true)
                        .withProtocolOperationTimeoutMs(60000);

                try (MqttClientConnection connection = builder.build()) {

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
            } catch (CrtRuntimeException | InterruptedException | ExecutionException ex) {
                onApplicationFailure(ex);
            }
        }
        CrtResource.waitForNoResources();
        System.out.println("Complete!");
    }
}
