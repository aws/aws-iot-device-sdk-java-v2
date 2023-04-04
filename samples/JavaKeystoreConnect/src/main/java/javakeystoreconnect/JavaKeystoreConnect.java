/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package javakeystoreconnect;

import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.CrtResource;
import software.amazon.awssdk.crt.CrtRuntimeException;
import software.amazon.awssdk.crt.io.ClientBootstrap;
import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt.MqttClientConnectionEvents;
import software.amazon.awssdk.iot.iotjobs.model.RejectedError;
import software.amazon.awssdk.crt.http.HttpProxyOptions;
import software.amazon.awssdk.iot.AwsIotMqttConnectionBuilder;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.CompletableFuture;
import java.util.UUID;

import utils.commandlineutils.CommandLineUtils;

public class JavaKeystoreConnect {

    // When run normally, we want to exit nicely even if something goes wrong
    // When run from CI, we want to let an exception escape which in turn causes the
    // exec:java task to return a non-zero exit code
    static String ciPropValue = System.getProperty("aws.crt.ci");
    static boolean isCI = ciPropValue != null && Boolean.valueOf(ciPropValue);

    static CommandLineUtils cmdUtils;

    static void onRejectedError(RejectedError error) {
        System.out.println("Request rejected: " + error.code.toString() + ": " + error.message);
    }

    /*
     * When called during a CI run, throw an exception that will escape and fail the exec:java task
     * When called otherwise, print what went wrong (if anything) and just continue (return from main)
     */
    static void onApplicationFailure(Throwable cause) {
        if (isCI) {
            throw new RuntimeException("JavaKeystoreConnect execution failure", cause);
        } else if (cause != null) {
            System.out.println("Exception encountered: " + cause.toString());
        }
    }

    public static void main(String[] args) {

        /**
         * Register the command line inputs
         */
        cmdUtils = new CommandLineUtils();
        cmdUtils.registerProgramName("JavaKeystoreConnect");
        cmdUtils.addCommonMQTTCommands();
        cmdUtils.addCommonProxyCommands();
        cmdUtils.registerCommand("keystore", "<file>", "The path to the Java keystore to use");
        cmdUtils.registerCommand("keystore_password", "<str>", "The password for the Java keystore");
        cmdUtils.registerCommand("keystore_format", "<str>", "The format of the Java keystore (optional, default='PKCS12')");
        cmdUtils.registerCommand("certificate_alias", "<str>", "The certificate alias to use to access the key and certificate in the Java keystore");
        cmdUtils.registerCommand("certificate_password", "<str>", "The password associated with the key and certificate in the Java keystore");
        cmdUtils.registerCommand("client_id", "<int>", "Client id to use (optional, default='test-*').");
        cmdUtils.registerCommand("port", "<int>", "Port to connect to on the endpoint (optional, default='8883').");
        cmdUtils.sendArguments(args);

        /**
         * Gather the input from the command line
         */
        String input_endpoint = cmdUtils.getCommandRequired("endpoint", "");
        String input_ca = cmdUtils.getCommandOrDefault("ca", "");
        String input_client_id = cmdUtils.getCommandOrDefault("client_id", "test-" + UUID.randomUUID().toString());
        int input_port = Integer.parseInt(cmdUtils.getCommandOrDefault("port", "8883"));
        String input_proxyHost = cmdUtils.getCommandOrDefault("proxy_host", "");
        int input_proxyPort = Integer.parseInt(cmdUtils.getCommandOrDefault("proxy_port", "0"));
        String input_keystore = cmdUtils.getCommandRequired("keystore", "");
        String input_keystorePassword = cmdUtils.getCommandRequired("keystore_password", "");
        String input_keystoreFormat = cmdUtils.getCommandOrDefault("keystore_format", "PKCS12");
        String input_certificateAlias = cmdUtils.getCommandRequired("certificate_alias", "");
        String input_certificatePassword = cmdUtils.getCommandRequired("certificate_password", "");

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

        try {

            /**
             * Create the MQTT connection from the builder
             */
            java.security.KeyStore keyStore;
            try {
                keyStore = java.security.KeyStore.getInstance(input_keystoreFormat);
            } catch (java.security.KeyStoreException ex) {
                throw new CrtRuntimeException("Could not get instance of Java keystore with format " + input_keystoreFormat);
            }
            try (java.io.FileInputStream fileInputStream = new java.io.FileInputStream(input_keystore)) {
                keyStore.load(fileInputStream, input_keystorePassword.toCharArray());
            } catch (java.io.FileNotFoundException ex) {
                throw new CrtRuntimeException("Could not open Java keystore file");
            } catch (java.io.IOException | java.security.NoSuchAlgorithmException | java.security.cert.CertificateException ex) {
                throw new CrtRuntimeException("Could not load Java keystore");
            }
            AwsIotMqttConnectionBuilder builder = AwsIotMqttConnectionBuilder.newJavaKeystoreBuilder(
                keyStore,
                input_cert,
                input_key);
            if (input_ca != "") {
                builder.withCertificateAuthorityFromPath(null, input_ca);
            }
            builder.withConnectionEventCallbacks(callbacks)
                .withClientId(input_client_id)
                .withEndpoint(input_endpoint)
                .withPort((short)input_port)
                .withCleanSession(true)
                .withProtocolOperationTimeoutMs(60000);
            if (input_proxyHost != "" && input_proxyPort > 0) {
                HttpProxyOptions proxyOptions = new HttpProxyOptions();
                proxyOptions.setHost(input_proxyHost);
                proxyOptions.setPort(input_proxyPort);
                builder.withHttpProxyOptions(proxyOptions);
            }
            MqttClientConnection connection = builder.build();
            builder.close();

            /**
             * Verify the connection was created
             */
            if (connection == null)
            {
                onApplicationFailure(new RuntimeException("MQTT connection creation failed!"));
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

            /**
             * Close the connection now that it is complete
             */
            connection.close();

        } catch (CrtRuntimeException | InterruptedException | ExecutionException ex) {
            onApplicationFailure(ex);
        }

        CrtResource.waitForNoResources();
        System.out.println("Complete!");
    }

}
