/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package cognitoconnect;

import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.CrtResource;
import software.amazon.awssdk.crt.CrtRuntimeException;
import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt.MqttClientConnectionEvents;
import software.amazon.awssdk.crt.http.HttpProxyOptions;
import software.amazon.awssdk.crt.auth.credentials.CognitoCredentialsProvider;
import software.amazon.awssdk.crt.io.ClientBootstrap;
import software.amazon.awssdk.crt.io.TlsContextOptions;
import software.amazon.awssdk.crt.io.ClientTlsContext;
import software.amazon.awssdk.iot.AwsIotMqttConnectionBuilder;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import utils.commandlineutils.CommandLineUtils;

public class CognitoConnect {
    // When run normally, we want to exit nicely even if something goes wrong
    // When run from CI, we want to let an exception escape which in turn causes the
    // exec:java task to return a non-zero exit code
    static String ciPropValue = System.getProperty("aws.crt.ci");
    static boolean isCI = ciPropValue != null && Boolean.valueOf(ciPropValue);

    static CommandLineUtils cmdUtils;

    /*
     * When called during a CI run, throw an exception that will escape and fail the exec:java task
     * When called otherwise, print what went wrong (if anything) and just continue (return from main)
     */
    static void onApplicationFailure(Throwable cause) {
        if (isCI) {
            throw new RuntimeException("CognitoConnect execution failure", cause);
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
        CommandLineUtils.SampleCommandLineData cmdData = CommandLineUtils.getInputForIoTSample("CognitoConnect", args);

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
             * Creates a connection using Cognito credentials.
             *
             * Note: This sample and code assumes that you are using a Cognito identity
             * in the same region as you pass to "--signing_region".
             * If not, you may need to adjust the Cognito endpoint in the cmdUtils.
             * See https://docs.aws.amazon.com/general/latest/gr/cognito_identity.html
             * for all Cognito endpoints.
             */
            // =================================
            AwsIotMqttConnectionBuilder builder = AwsIotMqttConnectionBuilder.newMtlsBuilderFromPath(null, null);
            builder.withConnectionEventCallbacks(callbacks)
                .withClientId(cmdData.input_clientId)
                .withEndpoint(cmdData.input_endpoint)
                .withCleanSession(true)
                .withProtocolOperationTimeoutMs(60000);

            builder.withWebsockets(true);
            builder.withWebsocketSigningRegion(cmdData.input_signingRegion);

            CognitoCredentialsProvider.CognitoCredentialsProviderBuilder cognitoBuilder = new CognitoCredentialsProvider.CognitoCredentialsProviderBuilder();
            String cognitoEndpoint = "cognito-identity." + cmdData.input_signingRegion + ".amazonaws.com";
            cognitoBuilder.withEndpoint(cognitoEndpoint).withIdentity(cmdData.input_cognitoIdentity);
            cognitoBuilder.withClientBootstrap(ClientBootstrap.getOrCreateStaticDefault());

            TlsContextOptions cognitoTlsContextOptions = TlsContextOptions.createDefaultClient();
            ClientTlsContext cognitoTlsContext = new ClientTlsContext(cognitoTlsContextOptions);
            cognitoTlsContextOptions.close();
            cognitoBuilder.withTlsContext(cognitoTlsContext);

            CognitoCredentialsProvider cognitoCredentials = cognitoBuilder.build();
            builder.withWebsocketCredentialsProvider(cognitoCredentials);

            MqttClientConnection connection = builder.build();
            builder.close();
            cognitoCredentials.close();
            cognitoTlsContext.close();

            if (connection == null)
            {
                onApplicationFailure(new RuntimeException("MQTT connection creation failed!"));
            }
            // =================================

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
