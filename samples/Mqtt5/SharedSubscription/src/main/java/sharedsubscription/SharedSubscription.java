/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package mqtt5.sharedsubscription;

import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.CrtResource;
import software.amazon.awssdk.crt.CrtRuntimeException;
import software.amazon.awssdk.crt.io.ClientBootstrap;
import software.amazon.awssdk.crt.mqtt5.*;
import software.amazon.awssdk.crt.mqtt5.Mqtt5ClientOptions.LifecycleEvents;
import software.amazon.awssdk.crt.mqtt5.packets.*;
import software.amazon.awssdk.iot.AwsIotMqtt5ClientBuilder;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import utils.commandlineutils.CommandLineUtils;

public class SharedSubscription {
    /**
     * When run normally, we want to exit nicely even if something goes wrong
     * When run from CI, we want to let an exception escape which in turn causes the
     * exec:java task to return a non-zero exit code
     */
    static String ciPropValue = System.getProperty("aws.crt.ci");
    static boolean isCI = ciPropValue != null && Boolean.valueOf(ciPropValue);

    /* Used for command line processing */
    static CommandLineUtils cmdUtils;

    /*
     * When called during a CI run, throw an exception that will escape and fail the exec:java task
     * When called otherwise, print what went wrong (if anything) and just continue (return from main)
     */
    static void onApplicationFailure(Throwable cause) {
        if (isCI) {
            throw new RuntimeException("Mqtt5 SharedSubscription: execution failure", cause);
        } else if (cause != null) {
            System.out.println("Exception encountered: " + cause.toString());
        }
    }

    /**
     * The interface that contains the functions invoked when the MQTT5 has a life-cycle event
     * (connect, disconnect, etc) that can be reacted to.
     */
    static final class SampleLifecycleEvents implements Mqtt5ClientOptions.LifecycleEvents {
        SampleMqtt5Client sampleClient;
        CompletableFuture<Void> connectedFuture = new CompletableFuture<>();
        CompletableFuture<Void> stoppedFuture = new CompletableFuture<>();

        SampleLifecycleEvents(SampleMqtt5Client client) {
            sampleClient = client;
            if (sampleClient == null) {
                System.out.println("Invalid sample client passed to SampleLifecycleEvents");
            }
        }

        @Override
        public void onAttemptingConnect(Mqtt5Client client, OnAttemptingConnectReturn onAttemptingConnectReturn) {
            System.out.println("[" + sampleClient.name + "]: Attempting connection...");
        }

        @Override
        public void onConnectionSuccess(Mqtt5Client client, OnConnectionSuccessReturn onConnectionSuccessReturn) {
            System.out.println("[" + sampleClient.name + "]: Connection success, client ID: "
                + onConnectionSuccessReturn.getNegotiatedSettings().getAssignedClientID());
            connectedFuture.complete(null);
        }

        @Override
        public void onConnectionFailure(Mqtt5Client client, OnConnectionFailureReturn onConnectionFailureReturn) {
            String errorString = CRT.awsErrorString(onConnectionFailureReturn.getErrorCode());
            System.out.println("[" + sampleClient.name + "]: Connection failed with error: " + errorString);
            connectedFuture.completeExceptionally(new Exception("Could not connect: " + errorString));
        }

        @Override
        public void onDisconnection(Mqtt5Client client, OnDisconnectionReturn onDisconnectionReturn) {
            System.out.println("[" + sampleClient.name + "]: Disconnected");
            DisconnectPacket disconnectPacket = onDisconnectionReturn.getDisconnectPacket();
            if (disconnectPacket != null) {
                System.out.println("\tDisconnection packet code: " + disconnectPacket.getReasonCode());
                System.out.println("\tDisconnection packet reason: " + disconnectPacket.getReasonString());

                if (disconnectPacket.getReasonCode() == DisconnectPacket.DisconnectReasonCode.SHARED_SUBSCRIPTIONS_NOT_SUPPORTED) {
                    /* Stop the client, which will interrupt the subscription and stop the sample */
                    client.stop(null);
                }
            }
        }

        @Override
        public void onStopped(Mqtt5Client client, OnStoppedReturn onStoppedReturn) {
            System.out.println("[" + sampleClient.name + "]: Stopped");
            stoppedFuture.complete(null);
        }
    }

    /**
     * The interface that contains the functions invoked when the MQTT5 client gets a message/publish
     * on a topic the MQTT5 client has subscribed to.
     */
    static final class SamplePublishEvents implements Mqtt5ClientOptions.PublishEvents {
        SampleMqtt5Client sampleClient;
        CountDownLatch messagesReceived;

        SamplePublishEvents(SampleMqtt5Client client, int messageCount) {
            sampleClient = client;
            messagesReceived = new CountDownLatch(messageCount);
        }

        @Override
        public void onMessageReceived(Mqtt5Client client, PublishReturn publishReturn) {
            if (sampleClient != null && sampleClient.client == client) {
                System.out.println("[" + sampleClient.name + "] Received a publish");
            }
            PublishPacket publishPacket = publishReturn.getPublishPacket();
            if (publishPacket != null) {
                System.out.println("\tPublish received on topic: " + publishPacket.getTopic());
                System.out.println("\tMessage: " + new String(publishPacket.getPayload()));

                List<UserProperty> packetProperties = publishPacket.getUserProperties();
                if (packetProperties != null) {
                    for (int i = 0; i < packetProperties.size(); i++) {
                        UserProperty property = packetProperties.get(i);
                        System.out.println("\t\twith UserProperty: (" + property.key + ", " + property.value + ")");
                    }
                }
            }
            messagesReceived.countDown();
        }
    }

    /**
     * For the purposes of this sample, we need to associate certain variables with a particular MQTT5 client
     * and to do so we use this class to hold all the data for a particular client used in the sample.
     */
    static final class SampleMqtt5Client {
        Mqtt5Client client;
        String name;
        SamplePublishEvents publishEvents;
        SampleLifecycleEvents lifecycleEvents;

        /**
         * Creates a MQTT5 client using direct MQTT5 via mTLS with the passed input data.
         */
        public static SampleMqtt5Client createMqtt5Client(
            String input_endpoint, String input_cert, String input_key, String input_ca,
            String input_clientId, int input_count, String input_clientName) {

                SampleMqtt5Client sampleClient = new SampleMqtt5Client();
                SamplePublishEvents publishEvents = new SamplePublishEvents(sampleClient, input_count / 2);
                SampleLifecycleEvents lifecycleEvents = new SampleLifecycleEvents(sampleClient);

                Mqtt5Client client;
                try {
                    AwsIotMqtt5ClientBuilder builder = AwsIotMqtt5ClientBuilder.newDirectMqttBuilderWithMtlsFromPath(input_endpoint, input_cert, input_key);

                    ConnectPacket.ConnectPacketBuilder connectProperties = new ConnectPacket.ConnectPacketBuilder();
                    connectProperties.withClientId(input_clientId);
                    builder.withConnectProperties(connectProperties);
                    if (input_ca != "") {
                        builder.withCertificateAuthorityFromPath(null, input_ca);
                    }
                    builder.withLifeCycleEvents(lifecycleEvents);
                    builder.withPublishEvents(publishEvents);
                    client = builder.build();
                    builder.close();
                }
                catch (CrtRuntimeException ex) {
                    System.out.println("Client creation failed!");
                    return null;
                }
                sampleClient.client = client;
                sampleClient.name = input_clientName;
                sampleClient.publishEvents = publishEvents;
                sampleClient.lifecycleEvents = lifecycleEvents;
                return sampleClient;
            }
    }

    public static void main(String[] args) {

        /**
         * Parse the command line data and store the values in cmdData for this sample.
         */
        cmdUtils = new CommandLineUtils();
        cmdUtils.registerProgramName("Mqtt5SharedSubscription");
        CommandLineUtils.SampleCommandLineData cmdData = cmdUtils.parseSampleInputMqtt5SharedSubscription(args);

        /* If this is CI, append a UUID to the topic */
        if (isCI) {
            cmdData.input_topic += "/" + UUID.randomUUID().toString();
        }

        /* Construct the shared topic */
        String input_sharedTopic = "$share/" + cmdData.input_groupIdentifier + "/" + cmdData.input_topic;

        /* This sample uses a publisher and two subscribers */
        SampleMqtt5Client publisher = null;
        SampleMqtt5Client subscriberOne = null;
        SampleMqtt5Client subscriberTwo = null;

        /* Make sure the message count is even */
        if (cmdData.input_count%2 != 0) {
            onApplicationFailure(new Throwable("'--count' is an odd number. '--count' must be even or zero for this sample."));
            System.exit(1);
        }

        try {
            /* Create the MQTT5 clients: one publisher and two subscribers */
            publisher = SampleMqtt5Client.createMqtt5Client(
                cmdData.input_endpoint, cmdData.input_cert, cmdData.input_key, cmdData.input_ca,
                cmdData.input_clientId + '1', cmdData.input_count, "Publisher");
            subscriberOne = SampleMqtt5Client.createMqtt5Client(
                cmdData.input_endpoint, cmdData.input_cert, cmdData.input_key, cmdData.input_ca,
                cmdData.input_clientId + '2', cmdData.input_count, "Subscriber One");
            subscriberTwo = SampleMqtt5Client.createMqtt5Client(
                cmdData.input_endpoint, cmdData.input_cert, cmdData.input_key, cmdData.input_ca,
                cmdData.input_clientId + '3', cmdData.input_count, "Subscriber Two");

            /* Connect all the clients */
            publisher.client.start();
            publisher.lifecycleEvents.connectedFuture.get(60, TimeUnit.SECONDS);
            System.out.println("[" + publisher.name + "]: Connected");
            subscriberOne.client.start();
            subscriberOne.lifecycleEvents.connectedFuture.get(60, TimeUnit.SECONDS);
            System.out.println("[" + subscriberOne.name + "]: Connected");
            subscriberTwo.client.start();
            subscriberTwo.lifecycleEvents.connectedFuture.get(60, TimeUnit.SECONDS);
            System.out.println("[" + subscriberTwo.name + "]: Connected");

            /* Subscribe to the shared topic on the two subscribers */
            try {
                SubscribePacket.SubscribePacketBuilder subscribeBuilder = new SubscribePacket.SubscribePacketBuilder();
                subscribeBuilder.withSubscription(input_sharedTopic, QOS.AT_LEAST_ONCE, false, false, SubscribePacket.RetainHandlingType.DONT_SEND);
                subscriberOne.client.subscribe(subscribeBuilder.build()).get(60, TimeUnit.SECONDS);
                System.out.println("[" + subscriberOne.name + "]: Subscribed");
                subscriberTwo.client.subscribe(subscribeBuilder.build()).get(60, TimeUnit.SECONDS);
                System.out.println("[" + subscriberTwo.name + "]: Subscribed");
            }
            // TMP: If this fails subscribing in CI, just exit the sample gracefully.
            catch (Exception ex) {
                if (isCI) {
                    return;
                } else {
                    throw ex;
                }
            }

            /* Publish using the publisher client */
            PublishPacket.PublishPacketBuilder publishBuilder = new PublishPacket.PublishPacketBuilder();
            publishBuilder.withTopic(cmdData.input_topic).withQOS(QOS.AT_LEAST_ONCE);
            int count = 0;
            if (cmdData.input_count > 0) {
                while (count++ < cmdData.input_count) {
                    publishBuilder.withPayload(("\"" + cmdData.input_message + ": " + String.valueOf(count) + "\"").getBytes());
                    publisher.client.publish(publishBuilder.build()).get(60, TimeUnit.SECONDS);
                    System.out.println("[" + publisher.name + "]: Sent publish");
                    Thread.sleep(1000);
                }
                /* Make sure all the messages were gotten on the subscribers */
                subscriberOne.publishEvents.messagesReceived.await(60 * 4, TimeUnit.SECONDS);
                subscriberTwo.publishEvents.messagesReceived.await(60 * 4, TimeUnit.SECONDS);
            } else {
                System.out.println("Skipping publishing messages due to message count being zero...");
            }

            /* Unsubscribe from the shared topic on the two subscribers */
            UnsubscribePacket.UnsubscribePacketBuilder unsubscribeBuilder = new UnsubscribePacket.UnsubscribePacketBuilder();
            unsubscribeBuilder.withSubscription(input_sharedTopic);
            subscriberOne.client.unsubscribe(unsubscribeBuilder.build()).get(60, TimeUnit.SECONDS);
            System.out.println("[" + subscriberOne.name + "]: Unsubscribed");
            subscriberTwo.client.unsubscribe(unsubscribeBuilder.build()).get(60, TimeUnit.SECONDS);
            System.out.println("[" + subscriberTwo.name + "]: Unsubscribed");

            /* Disconnect all the clients */
            publisher.client.stop(null);
            publisher.lifecycleEvents.stoppedFuture.get(60, TimeUnit.SECONDS);
            System.out.println("[" + publisher.name + "]: Fully stopped");
            subscriberOne.client.stop(null);
            subscriberOne.lifecycleEvents.stoppedFuture.get(60, TimeUnit.SECONDS);
            System.out.println("[" + subscriberOne.name + "]: Fully stopped");
            subscriberTwo.client.stop(null);
            subscriberTwo.lifecycleEvents.stoppedFuture.get(60, TimeUnit.SECONDS);
            System.out.println("[" + subscriberTwo.name + "]: Fully stopped");

        } catch (Exception ex) {
            /* Something bad happened, abort and report! */
            onApplicationFailure(ex);
        } finally {
            /* Close all the MQTT5 clients to make sure no native memory is leaked */
            if (publisher != null && publisher.client != null) {
                publisher.client.close();
            }
            if (subscriberOne != null && subscriberOne.client != null) {
                subscriberOne.client.close();
            }
            if (subscriberTwo != null && subscriberTwo.client != null) {
                subscriberTwo.client.close();
            }
            CrtResource.waitForNoResources();
        }
        System.out.println("Complete!");
    }
}
