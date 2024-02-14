/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package androidkeychainpubsub;

import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.CrtResource;
import software.amazon.awssdk.crt.io.*;
import software.amazon.awssdk.crt.mqtt5.*;
import software.amazon.awssdk.crt.mqtt5.Mqtt5ClientOptions.LifecycleEvents;
import software.amazon.awssdk.crt.mqtt5.packets.*;
import software.amazon.awssdk.iot.AwsIotMqtt5ClientBuilder;
import software.amazon.awssdk.iot.AndroidKeyChainHandlerBuilder;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import android.content.Context;

import utils.commandlineutils.CommandLineUtils;

public class AndroidKeyChainPubSub {

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
            throw new RuntimeException("AndroidKeyChainPubSub execution failure", cause);
        } else if (cause != null) {
            System.out.println("Exception encountered: " + cause.toString());
        }
    }

    static final class SampleLifecycleEvents implements Mqtt5ClientOptions.LifecycleEvents {
        CompletableFuture<Void> connectedFuture = new CompletableFuture<>();
        CompletableFuture<Void> stoppedFuture = new CompletableFuture<>();

        @Override
        public void onAttemptingConnect(Mqtt5Client client, OnAttemptingConnectReturn onAttemptingConnectReturn) {
            System.out.println("Mqtt5 Client: Attempting connection...");
        }

        @Override
        public void onConnectionSuccess(Mqtt5Client client, OnConnectionSuccessReturn onConnectionSuccessReturn) {
            System.out.println("Mqtt5 Client: Connection success, client ID: "
                + onConnectionSuccessReturn.getNegotiatedSettings().getAssignedClientID());
            connectedFuture.complete(null);
        }

        @Override
        public void onConnectionFailure(Mqtt5Client client, OnConnectionFailureReturn onConnectionFailureReturn) {
            String errorString = CRT.awsErrorString(onConnectionFailureReturn.getErrorCode());
            System.out.println("Mqtt5 Client: Connection failed with error: " + errorString);
            connectedFuture.completeExceptionally(new Exception("Could not connect: " + errorString));
        }

        @Override
        public void onDisconnection(Mqtt5Client client, OnDisconnectionReturn onDisconnectionReturn) {
            System.out.println("Mqtt5 Client: Disconnected");
            DisconnectPacket disconnectPacket = onDisconnectionReturn.getDisconnectPacket();
            if (disconnectPacket != null) {
                System.out.println("\tDisconnection packet code: " + disconnectPacket.getReasonCode());
                System.out.println("\tDisconnection packet reason: " + disconnectPacket.getReasonString());
            }
        }

        @Override
        public void onStopped(Mqtt5Client client, OnStoppedReturn onStoppedReturn) {
            System.out.println("Mqtt5 Client: Stopped");
            stoppedFuture.complete(null);
        }
    }

    static final class SamplePublishEvents implements Mqtt5ClientOptions.PublishEvents {
        CountDownLatch messagesReceived;

        SamplePublishEvents(int messageCount) {
            messagesReceived = new CountDownLatch(messageCount);
        }

        @Override
        public void onMessageReceived(Mqtt5Client client, PublishReturn publishReturn) {
            PublishPacket publishPacket = publishReturn.getPublishPacket();

            System.out.println("Publish received on topic: " + publishPacket.getTopic());
            System.out.println("Message: " + new String(publishPacket.getPayload()));

            List<UserProperty> packetProperties = publishPacket.getUserProperties();
            if (packetProperties != null) {
                for (int i = 0; i < packetProperties.size(); i++) {
                    UserProperty property = packetProperties.get(i);
                    System.out.println("\twith UserProperty: (" + property.key + ", " + property.value + ")");
                }
            }
            messagesReceived.countDown();
        }
    }

    public static void main(String[] args, Context context) {
        /**
         * cmdData is the arguments/input from the command line placed into a single struct for
         * use in this sample. This handles all of the command line parsing, validating, etc.
         * See the Utils/CommandLineUtils for more information.
         */
        CommandLineUtils.SampleCommandLineData cmdData = CommandLineUtils.getInputForIoTSample("AndroidKeyChainPubSub", args);

        try {
            SampleLifecycleEvents lifecycleEvents = new SampleLifecycleEvents();
            SamplePublishEvents publishEvents = new SamplePublishEvents(cmdData.input_count);
            Mqtt5Client client;

            /*
             * AndroidKeyChainHandlerBuilder is used to handle PrivateKey extraction from Android KeyChain.
             * If you have a PrivateKey, you may pass it directly into the builder instead of providing a
             * context and alias.
             */
            AndroidKeyChainHandlerBuilder keyChainHandlerBuilder =
                AndroidKeyChainHandlerBuilder.newKeyChainHandlerWithAlias(context, cmdData.input_KeyChainAlias);

            AwsIotMqtt5ClientBuilder builder = AwsIotMqtt5ClientBuilder.newDirectMtlsCustomKeyOperationsBuilder(
                cmdData.input_endpoint, keyChainHandlerBuilder.build());

            ConnectPacket.ConnectPacketBuilder connectProperties = new ConnectPacket.ConnectPacketBuilder();
            connectProperties.withClientId(cmdData.input_clientId);
            builder.withConnectProperties(connectProperties);
            builder.withLifeCycleEvents(lifecycleEvents);
            builder.withPublishEvents(publishEvents);
            client = builder.build();
            builder.close();

            /* Connect */
            client.start();
            try {
                lifecycleEvents.connectedFuture.get(60, TimeUnit.SECONDS);
            } catch (Exception ex) {
                throw new RuntimeException("Exception occurred during connect", ex);
            }

            /* Subscribe */
            SubscribePacket.SubscribePacketBuilder subscribeBuilder = new SubscribePacket.SubscribePacketBuilder();
            subscribeBuilder.withSubscription("test_topic_android", QOS.AT_LEAST_ONCE, false, false, SubscribePacket.RetainHandlingType.DONT_SEND);
            try {
                client.subscribe(subscribeBuilder.build()).get(60, TimeUnit.SECONDS);
            } catch (Exception ex) {
                onApplicationFailure(ex);
            }

            /* Publish */
            PublishPacket.PublishPacketBuilder publishBuilder = new PublishPacket.PublishPacketBuilder();
            publishBuilder.withTopic("test_topic_android").withQOS(QOS.AT_LEAST_ONCE);
            int count = 0;
            try {
                while (count++ < cmdData.input_count) {
                    publishBuilder.withPayload(("\"" + cmdData.input_message + ": " + String.valueOf(count) + "\"").getBytes());
                    CompletableFuture<PublishResult> published = client.publish(publishBuilder.build());
                    published.get(60, TimeUnit.SECONDS);
                    Thread.sleep(1000);
                }
            } catch (Exception ex) {
                onApplicationFailure(ex);
            }
            publishEvents.messagesReceived.await(120, TimeUnit.SECONDS);

            /* Disconnect */
            DisconnectPacket.DisconnectPacketBuilder disconnectBuilder = new DisconnectPacket.DisconnectPacketBuilder();
            disconnectBuilder.withReasonCode(DisconnectPacket.DisconnectReasonCode.NORMAL_DISCONNECTION);
            client.stop(disconnectBuilder.build());
            try {
                lifecycleEvents.stoppedFuture.get(60, TimeUnit.SECONDS);
            } catch (Exception ex) {
                onApplicationFailure(ex);
            }

            /* Close the client to free memory */
            client.close();

        } catch (Exception ex) {
            onApplicationFailure(ex);
        }

        CrtResource.waitForNoResources();
        System.out.println("Complete!");
     }
}
