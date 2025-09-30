/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package greengrass;

import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import software.amazon.awssdk.aws.greengrass.GreengrassCoreIPCClientV2;
import software.amazon.awssdk.aws.greengrass.model.PublishToIoTCoreRequest;
import software.amazon.awssdk.aws.greengrass.model.PublishToIoTCoreResponse;
import software.amazon.awssdk.aws.greengrass.model.QOS;

/**
 * This sample uses AWS IoT Greengrass V2 to publish messages from the Greengrass device to the
 * AWS IoT MQTT broker.
 * <p>
 * This sample can be deployed as a Greengrass V2 component and it will publish 10 MQTT messages
 * over the course of 10 seconds. The IPC integration with Greengrass V2 allows this code to run
 * without additional IoT certificates or secrets, because it directly communicates with the
 * Greengrass core running on the device. As such, to run this sample you need Greengrass Core running.
 * <p>
 * For more information, see the samples <a href="https://github.com/aws/aws-iot-device-sdk-java-v2/blob/main/samples/GreengrassIPC/README.md">README.md</a> file.
 */
public class GreengrassIPC {
    // When run normally, we want to exit nicely even if something goes wrong
    // When run from CI, we want to let an exception escape which in turn causes the
    // exec:java task to return a non-zero exit code
    static String ciPropValue = System.getProperty("aws.crt.ci");
    static boolean isCI = ciPropValue != null && Boolean.valueOf(ciPropValue);

    // Some constants for the payload data we send via IPC
    static double payloadBatteryStateCharge = 42.5;
    static double payloadLocationLongitude = 48.15743;
    static double payloadLocationLatitude = 11.57549;
    // The number of IPC messages to send
    static int sampleMessageCount = 10;
    static int sampleMessagesSent = 0;

    /*
     * When called during a CI run, throw an exception that will escape and fail the exec:java task
     * When called otherwise, print what went wrong (if anything) and just continue (return from main)
     */
    static void onApplicationFailure(Throwable cause) {
        if (isCI) {
            throw new RuntimeException("BasicPubSub execution failure", cause);
        } else if (cause != null) {
            System.out.println("Exception encountered: " + cause.toString());
        }
    }

    /**
     * A simple helper function to print a message when running locally (will not print in CI)
     */
    static void logMessage(String message) {
        if (!isCI) {
            System.out.println(message);
        }
    }

    /**
     * A helper function to generate a JSON payload to send via IPC to simplify/separate sample code.
     */
    public static String getIpcPayloadString() {
        // Get the current time as a formatted string
        String timestamp = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss").format(LocalDateTime.now());
        // Construct a JSON string with the data
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append("\"timestamp\":\"" + timestamp + "\",");
        builder.append("\"battery_state_of_charge\":" + payloadBatteryStateCharge + ",");
        builder.append("\"location\": {");
        builder.append("\"longitude\":" + payloadLocationLongitude + ",");
        builder.append("\"latitude\":" + payloadLocationLatitude);
        builder.append("}");
        builder.append("}");
        return builder.toString();
    }

    public static void main(String[] args) {
        logMessage("Greengrass IPC sample start");

        // Create the Greengrass IPC client
        GreengrassCoreIPCClientV2 ipcClient = null;
        try {
            ipcClient = GreengrassCoreIPCClientV2.builder().build();
        } catch (Exception ex) {
            logMessage("Failed to create Greengrass IPC client!");
            onApplicationFailure(ex);
            System.exit(-1);
        }
        if (ipcClient == null) {
            logMessage("Failed to create Greengrass IPC client!");
            onApplicationFailure(new Throwable("Error - IPC client not initialized!"));
            System.exit(-1);
        }

        // Create the topic name
        String topicNameFromEnv = System.getenv("AWS_IOT_THING_NAME");
        if (topicNameFromEnv == null) {
            logMessage("Could not get IoT Thing name from AWS_IOT_THING_NAME. Using name 'TestThing'...");
            topicNameFromEnv = "TestThing";
        }
        String topicName = String.format("my/iot/%s/telemetry", topicNameFromEnv);

        // Create the IPC request, except the payload. The payload will be created right before sending.
        PublishToIoTCoreRequest publishRequest = new PublishToIoTCoreRequest();
        publishRequest.setQos(QOS.AT_LEAST_ONCE);
        publishRequest.setTopicName(topicName);

        try {
            logMessage("Will attempt to send " + sampleMessageCount + " IPC publishes to IoT Core");
            while (sampleMessagesSent < sampleMessageCount) {
                logMessage("Sending message " + sampleMessagesSent++ + "...");

                // Get the new IPC payload
                publishRequest.withPayload(getIpcPayloadString().getBytes(StandardCharsets.UTF_8));
                CompletableFuture<PublishToIoTCoreResponse> publishFuture = ipcClient.publishToIoTCoreAsync(publishRequest);

                // Try to send the IPC message
                try {
                    publishFuture.get(60, TimeUnit.SECONDS);
                    logMessage("Successfully published IPC message to IoT Core");
                } catch (Exception ex) {
                    logMessage("Failed to publish IPC message to IoT Core");
                }

                // Sleep for a second
                Thread.sleep(1000);
            }
            logMessage("All publishes sent. Finishing sample...");
            ipcClient.close();

        } catch (Exception ex) {
            logMessage("Something in Greengrass IPC sample failed by throwing an exception! Shutting down sample...");
            onApplicationFailure(ex);
            try {
                ipcClient.close();
            } catch (Exception closeEx) {
                onApplicationFailure(closeEx);
            }
            logMessage("Greengrass IPC sample finished with error");
            System.exit(-1);
        }

        logMessage("Greengrass IPC sample finished");
        System.exit(0);
    }
}
