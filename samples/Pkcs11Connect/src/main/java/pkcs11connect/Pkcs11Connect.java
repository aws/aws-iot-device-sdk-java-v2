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
         * cmdData is the arguments/input from the command line placed into a single struct for
         * use in this sample. This handles all of the command line parsing, validating, etc.
         * See the Utils/CommandLineUtils for more information.
         */
        CommandLineUtils.SampleCommandLineData cmdData = CommandLineUtils.getInputForIoTSample("Pkcs11Connect", args);

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
        try (Pkcs11Lib pkcs11Lib = new Pkcs11Lib(cmdData.input_pkcs11LibPath);
                TlsContextPkcs11Options pkcs11Options = new TlsContextPkcs11Options(pkcs11Lib)) {

            pkcs11Options.withCertificateFilePath(cmdData.input_cert);
            pkcs11Options.withUserPin(cmdData.input_pkcs11UserPin);

            // Pass arguments to help find the correct PKCS#11 token,
            // and the private key on that token. You don't need to pass
            // any of these arguments if your PKCS#11 device only has one
            // token, or the token only has one private key. But if there
            // are multiple tokens, or multiple keys to choose from, you
            // must narrow down which one should be used.

            if (cmdData.input_pkcs11TokenLabel != null && cmdData.input_pkcs11TokenLabel != "") {
                pkcs11Options.withTokenLabel(cmdData.input_pkcs11TokenLabel);
            }

            if (cmdData.input_pkcs11SlotId != null) {
                pkcs11Options.withSlotId(cmdData.input_pkcs11SlotId);
            }

            if (cmdData.input_pkcs11KeyLabel != null && cmdData.input_pkcs11KeyLabel != "") {
                pkcs11Options.withPrivateKeyObjectLabel(cmdData.input_pkcs11KeyLabel);
            }

            try (AwsIotMqttConnectionBuilder builder = AwsIotMqttConnectionBuilder
                    .newMtlsPkcs11Builder(pkcs11Options)) {

                if (cmdData.input_ca != null && cmdData.input_ca != "") {
                    builder.withCertificateAuthorityFromPath(null, cmdData.input_ca);
                }

                builder.withConnectionEventCallbacks(callbacks)
                        .withClientId(cmdData.input_clientId)
                        .withEndpoint(cmdData.input_endpoint)
                        .withPort((short) cmdData.input_port)
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
