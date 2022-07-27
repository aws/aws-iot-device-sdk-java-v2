/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package windowscertconnect;

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

public class WindowsCertConnect {

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
            throw new RuntimeException("execution failure", cause);
        } else if (cause != null) {
            System.out.println("Exception encountered: " + cause.toString());
        }
    }

    public static void main(String[] args) {

        cmdUtils = new CommandLineUtils();
        cmdUtils.registerProgramName("WindowsCertConnect");
        cmdUtils.addCommonMQTTCommands();
        cmdUtils.registerCommand("cert", "<str>", "Path to certificate in Windows cert store. " +
                                                  "e.g. \"CurrentUser\\MY\\6ac133ac58f0a88b83e9c794eba156a98da39b4c\"");
        cmdUtils.registerCommand("client_id", "<int>", "Client id to use (optional, default='test-*').");
        cmdUtils.registerCommand("port", "<int>", "Port to connect to on the endpoint (optional, default='8883').");
        cmdUtils.registerCommand("help", "", "Prints this message");
        cmdUtils.sendArguments(args);

        String endpoint = cmdUtils.getCommandRequired("endpoint", "");
        String windowsCertStorePath = cmdUtils.getCommandRequired("cert", "");
        String rootCaPath = cmdUtils.getCommandOrDefault("ca_file", "");
        String clientId = cmdUtils.getCommandOrDefault("client_id", "test-" + UUID.randomUUID().toString());
        int port = Integer.parseInt(cmdUtils.getCommandOrDefault("port", "8883"));

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

        try (AwsIotMqttConnectionBuilder builder =
                AwsIotMqttConnectionBuilder.newMtlsWindowsCertStorePathBuilder(windowsCertStorePath)) {

            if (rootCaPath != null && rootCaPath != "") {
                builder.withCertificateAuthorityFromPath(null, rootCaPath);
            }

            builder.withConnectionEventCallbacks(callbacks)
                    .withClientId(clientId)
                    .withEndpoint(endpoint)
                    .withPort((short) port)
                    .withCleanSession(true)
                    .withProtocolOperationTimeoutMs(60000);

            try (MqttClientConnection connection = builder.build()) {
                // Connect and disconnect using the connection we created
                // (see sampleConnectAndDisconnect for implementation)
                cmdUtils.sampleConnectAndDisconnect(connection);

                // Close the connection now that we are completely done with it.
                connection.close();
            }
        } catch (CrtRuntimeException | InterruptedException | ExecutionException ex) {
            onApplicationFailure(ex);
        }

        CrtResource.waitForNoResources();

        System.out.println("Complete!");
    }
}
