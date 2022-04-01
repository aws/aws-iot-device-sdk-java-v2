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
            throw new RuntimeException("Pkcs11PubSub execution failure", cause);
        } else if (cause != null) {
            System.out.println("Exception encountered: " + cause.toString());
        }
    }

    public static void main(String[] args) {

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
        cmdUtils.registerCommand("help", "", "Prints this message");
        cmdUtils.sendArguments(args);

        if (cmdUtils.hasCommand("help")) {
            cmdUtils.printHelp();
            System.exit(1);
        }

        String endpoint = cmdUtils.getCommandRequired("endpoint", "");
        String certPath = cmdUtils.getCommandRequired("cert", "");
        String CaPath = cmdUtils.getCommandOrDefault("ca_file", "");
        String clientId = cmdUtils.getCommandOrDefault("client_id", "test-" + UUID.randomUUID().toString());
        int port = Integer.parseInt(cmdUtils.getCommandOrDefault("port", "8883"));
        String pkcs11LibPath = cmdUtils.getCommandRequired("pkcs11_lib", "");
        String pkcs11UserPin = cmdUtils.getCommandRequired("pin", "");
        String pkcs11TokenLabel = cmdUtils.getCommandOrDefault("key_label", "");
        Long pkcs11SlotId = null;
        if (cmdUtils.hasCommand("slot_id")) {
            Long.parseLong(cmdUtils.getCommandOrDefault("slot_id", "-1"));
        }
        String pkcs11KeyLabel = cmdUtils.getCommandOrDefault("key_label", "");

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
        try (Pkcs11Lib pkcs11Lib = new Pkcs11Lib(pkcs11LibPath);
                TlsContextPkcs11Options pkcs11Options = new TlsContextPkcs11Options(pkcs11Lib)) {

            pkcs11Options.withCertificateFilePath(certPath);
            pkcs11Options.withUserPin(pkcs11UserPin);

            // Pass arguments to help find the correct PKCS#11 token,
            // and the private key on that token. You don't need to pass
            // any of these arguments if your PKCS#11 device only has one
            // token, or the token only has one private key. But if there
            // are multiple tokens, or multiple keys to choose from, you
            // must narrow down which one should be used.

            if (pkcs11TokenLabel != null && pkcs11TokenLabel != "") {
                pkcs11Options.withTokenLabel(pkcs11TokenLabel);
            }

            if (pkcs11SlotId != null) {
                pkcs11Options.withSlotId(pkcs11SlotId);
            }

            if (pkcs11KeyLabel != null && pkcs11KeyLabel != "") {
                pkcs11Options.withPrivateKeyObjectLabel(pkcs11KeyLabel);
            }

            try (AwsIotMqttConnectionBuilder builder = AwsIotMqttConnectionBuilder
                    .newMtlsPkcs11Builder(pkcs11Options)) {

                if (CaPath != null) {
                    builder.withCertificateAuthorityFromPath(null, CaPath);
                }

                builder.withConnectionEventCallbacks(callbacks)
                        .withClientId(clientId)
                        .withEndpoint(endpoint)
                        .withPort((short) port)
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
                }
            } catch (CrtRuntimeException | InterruptedException | ExecutionException ex) {
                onApplicationFailure(ex);
            }
        }
        CrtResource.waitForNoResources();
        System.out.println("Complete!");
    }
}
