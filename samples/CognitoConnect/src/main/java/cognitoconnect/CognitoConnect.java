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
import software.amazon.awssdk.iot.iotjobs.model.RejectedError;

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

        cmdUtils = new CommandLineUtils();
        cmdUtils.registerProgramName("CognitoConnect");
        cmdUtils.addCommonMQTTCommands();
        cmdUtils.addCommonProxyCommands();
        cmdUtils.registerCommand("signing_region", "<str>", "AWS IoT service region.");
        cmdUtils.registerCommand("client_id", "<int>", "Client id to use (optional, default='test-*').");
        cmdUtils.registerCommand("cognito_identity", "<str>", "The Cognito identity ID to use to connect via Cognito");
        cmdUtils.sendArguments(args);

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
             * Note: The data for the connection is gotten from cmdUtils.
             * (see buildCognitoMQTTConnection for implementation)
             *
             * Note: This sample and code assumes that you are using a Cognito identity
             * in the same region as you pass to "--signing_region".
             * If not, you may need to adjust the Cognito endpoint in the cmdUtils.
             * See https://docs.aws.amazon.com/general/latest/gr/cognito_identity.html
             * for all Cognito endpoints.
             */
            MqttClientConnection connection = cmdUtils.buildCognitoMQTTConnection(callbacks);
            if (connection == null)
            {
                onApplicationFailure(new RuntimeException("MQTT connection creation failed!"));
            }

            // Connect and disconnect using the connection we created
            // (see sampleConnectAndDisconnect for implementation)
            cmdUtils.sampleConnectAndDisconnect(connection);

            // Close the connection now that we are completely done with it.
            connection.close();

        } catch (CrtRuntimeException | InterruptedException | ExecutionException ex) {
            onApplicationFailure(ex);
        }

        CrtResource.waitForNoResources();

        System.out.println("Complete!");
    }
}
