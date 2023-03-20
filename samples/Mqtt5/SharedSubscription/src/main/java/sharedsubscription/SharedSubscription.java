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

/**
 * MQTT5 support is currently in <b>developer preview</b>.  We encourage feedback at all times, but feedback during the
 * preview window is especially valuable in shaping the final product.  During the preview period we may make
 * backwards-incompatible changes to the public API, but in general, this is something we will try our best to avoid.
 */
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

    static final class SampleLifecycleEvents implements Mqtt5ClientOptions.LifecycleEvents {
        SampleMqtt5Client sampleClient;
        CompletableFuture<Void> connectedFuture = new CompletableFuture<>();
        CompletableFuture<Void> stoppedFuture = new CompletableFuture<>();

        SampleLifecycleEvents(SampleMqtt5Client client) {
            sampleClient = client;
        }

        @Override
        public void onAttemptingConnect(Mqtt5Client client, OnAttemptingConnectReturn onAttemptingConnectReturn) {
            if (sampleClient != null && sampleClient.client == client) {
                System.out.println("[" + sampleClient.name + "]: Attempting connection...");
            }
        }

        @Override
        public void onConnectionSuccess(Mqtt5Client client, OnConnectionSuccessReturn onConnectionSuccessReturn) {
            if (sampleClient != null && sampleClient.client == client) {
                System.out.println("[" + sampleClient.name + "]: Connection success, client ID: "
                    + onConnectionSuccessReturn.getNegotiatedSettings().getAssignedClientID());
                connectedFuture.complete(null);
            }
        }

        @Override
        public void onConnectionFailure(Mqtt5Client client, OnConnectionFailureReturn onConnectionFailureReturn) {
            if (sampleClient != null && sampleClient.client == client) {
                String errorString = CRT.awsErrorString(onConnectionFailureReturn.getErrorCode());
                System.out.println("[" + sampleClient.name + "]: Connection failed with error: " + errorString);
                connectedFuture.completeExceptionally(new Exception("Could not connect: " + errorString));
            }
        }

        @Override
        public void onDisconnection(Mqtt5Client client, OnDisconnectionReturn onDisconnectionReturn) {
            if (sampleClient != null && sampleClient.client == client) {
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
        }

        @Override
        public void onStopped(Mqtt5Client client, OnStoppedReturn onStoppedReturn) {
            if (sampleClient != null && sampleClient.client == client) {
                System.out.println("[" + sampleClient.name + "]: Stopped");
                stoppedFuture.complete(null);
            }
        }
    }

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

    static final class SampleMqtt5Client {
        Mqtt5Client client;
        String name;
        SamplePublishEvents publishEvents;
        SampleLifecycleEvents lifecycleEvents;
    }

    public static SampleMqtt5Client createMqtt5Client(
        String input_endpoint, String input_cert, String input_key, String input_ca,
        String input_client_id, int input_count, String input_clientName) {

        SampleMqtt5Client sampleClient = new SampleMqtt5Client();
        SamplePublishEvents publishEvents = new SamplePublishEvents(sampleClient, input_count / 2);
        SampleLifecycleEvents lifecycleEvents = new SampleLifecycleEvents(sampleClient);

        Mqtt5Client client;
        try {
            AwsIotMqtt5ClientBuilder builder = AwsIotMqtt5ClientBuilder.newDirectMqttBuilderWithMtlsFromPath(input_endpoint, input_cert, input_key);

            ConnectPacket.ConnectPacketBuilder connectProperties = new ConnectPacket.ConnectPacketBuilder();
            connectProperties.withClientId(input_client_id);
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

    public static void main(String[] args) {

        cmdUtils = new CommandLineUtils();
        cmdUtils.registerProgramName("Mqtt5SharedSubscription");
        cmdUtils.addCommonMQTTCommands();
        cmdUtils.addCommonTopicMessageCommands();
        cmdUtils.registerCommand("key", "<path>", "Path to your key in PEM format. (will use direct MQTT to connect if defined)");
        cmdUtils.registerCommand("cert", "<path>", "Path to your client certificate in PEM format. (will use direct MQTT to connect if defined)");
        cmdUtils.registerCommand("client_id", "<int>",
            "Client id to use (optional, default='test-*'). Note that '1', '2', and '3' will be added for first, second, and third client");
        cmdUtils.registerCommand("count", "<int>", "Number of messages to publish (optional, default='10').");
        cmdUtils.registerCommand("group_identifier", "<string>",
            "The group identifier to use in the shared subscription (optional, default='java-sample')");
        cmdUtils.sendArguments(args);

        /* Get all the input from the command line */
        String input_endpoint = cmdUtils.getCommandRequired("endpoint", "");
        String input_cert = cmdUtils.getCommandRequired("cert", "");
        String input_key = cmdUtils.getCommandRequired("key", "");
        String input_ca = cmdUtils.getCommandOrDefault("ca_file", "");
        String input_client_id = cmdUtils.getCommandOrDefault("client_id", "test-" + UUID.randomUUID().toString());
        int input_count = Integer.parseInt(cmdUtils.getCommandOrDefault("count", String.valueOf(10)));
        String input_topic = cmdUtils.getCommandOrDefault("topic", "test/topic");
        String input_message = cmdUtils.getCommandOrDefault("message", "Hello World!");
        String input_group_identifier = cmdUtils.getCommandOrDefault("group_identifier", "java-sample");
        String input_shared_topic = "$share/" + input_group_identifier + "/" + input_topic;

        /* This sample uses a publisher and two subscribers */
        SampleMqtt5Client publisher = null;
        SampleMqtt5Client subscriber_one = null;
        SampleMqtt5Client subscriber_two = null;

        try {

            /* Create a publisher and two subscribers */
            publisher = createMqtt5Client(
                input_endpoint, input_cert, input_key, input_ca,
                input_client_id + '1', input_count, "Publisher");
            subscriber_one = createMqtt5Client(
                input_endpoint, input_cert, input_key, input_ca,
                input_client_id + '2', input_count, "Subscriber One");
            subscriber_two = createMqtt5Client(
                input_endpoint, input_cert, input_key, input_ca,
                input_client_id + '3', input_count, "Subscriber Two");

            /* Connect all the clients */
            publisher.client.start();
            publisher.lifecycleEvents.connectedFuture.get(60, TimeUnit.SECONDS);
            System.out.println("[" + publisher.name + "]: Connected");
            subscriber_one.client.start();
            subscriber_one.lifecycleEvents.connectedFuture.get(60, TimeUnit.SECONDS);
            System.out.println("[" + subscriber_one.name + "]: Connected");
            subscriber_two.client.start();
            subscriber_two.lifecycleEvents.connectedFuture.get(60, TimeUnit.SECONDS);
            System.out.println("[" + subscriber_two.name + "]: Connected");

            /* Subscribe to the shared topic on the two subscribers */
            SubscribePacket.SubscribePacketBuilder subscribeBuilder = new SubscribePacket.SubscribePacketBuilder();
            subscribeBuilder.withSubscription(input_shared_topic, QOS.AT_LEAST_ONCE, false, false, SubscribePacket.RetainHandlingType.DONT_SEND);
            subscriber_one.client.subscribe(subscribeBuilder.build()).get(60, TimeUnit.SECONDS);
            System.out.println("[" + subscriber_one.name + "]: Subscribed");
            subscriber_two.client.subscribe(subscribeBuilder.build()).get(60, TimeUnit.SECONDS);
            System.out.println("[" + subscriber_two.name + "]: Subscribed");

            /* Publish using the publisher client */
            PublishPacket.PublishPacketBuilder publishBuilder = new PublishPacket.PublishPacketBuilder();
            publishBuilder.withTopic(input_topic).withQOS(QOS.AT_LEAST_ONCE);
            int count = 0;
            if (input_count > 0) {
                while (count++ < input_count) {
                    publishBuilder.withPayload(("\"" + input_message + ": " + String.valueOf(count) + "\"").getBytes());
                    publisher.client.publish(publishBuilder.build()).get(60, TimeUnit.SECONDS);
                    System.out.println("[" + publisher.name + "]: Sent publish");
                    Thread.sleep(1000);
                }
            } else {
                System.out.println("Skipping publishing messages due to message count being zero...");
            }

            /* Make sure all the messages were gotten on the subscribers */
            subscriber_one.publishEvents.messagesReceived.await(60 * 4, TimeUnit.SECONDS);
            subscriber_two.publishEvents.messagesReceived.await(60 * 4, TimeUnit.SECONDS);

            /* Unsubscribe from the shared topic on the two subscribers */
            UnsubscribePacket.UnsubscribePacketBuilder unsubscribeBuilder = new UnsubscribePacket.UnsubscribePacketBuilder();
            unsubscribeBuilder.withSubscription(input_shared_topic);
            subscriber_one.client.unsubscribe(unsubscribeBuilder.build()).get(60, TimeUnit.SECONDS);
            System.out.println("[" + subscriber_one.name + "]: Unsubscribed");
            subscriber_two.client.unsubscribe(unsubscribeBuilder.build()).get(60, TimeUnit.SECONDS);
            System.out.println("[" + subscriber_two.name + "]: Unsubscribed");

            /* Disconnect all the clients */
            publisher.client.stop(null);
            publisher.lifecycleEvents.stoppedFuture.get(60, TimeUnit.SECONDS);
            System.out.println("[" + publisher.name + "]: Fully stopped");
            subscriber_one.client.stop(null);
            subscriber_one.lifecycleEvents.stoppedFuture.get(60, TimeUnit.SECONDS);
            System.out.println("[" + subscriber_one.name + "]: Fully stopped");
            subscriber_two.client.stop(null);
            subscriber_two.lifecycleEvents.stoppedFuture.get(60, TimeUnit.SECONDS);
            System.out.println("[" + subscriber_two.name + "]: Fully stopped");

        } catch (Exception ex) {
            onApplicationFailure(ex);
        } finally {
            /* Close all the MQTT5 clients to make sure no memory is leaked */
            if (publisher != null && publisher.client != null) {
                publisher.client.close();
            }
            if (subscriber_one != null && subscriber_one.client != null) {
                subscriber_one.client.close();
            }
            if (subscriber_two != null && subscriber_two.client != null) {
                subscriber_two.client.close();
            }
        }

        CrtResource.waitForNoResources();
        System.out.println("Complete!");
    }
}
