/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package mqtt5.pubsub;

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
public class PubSub {

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
            throw new RuntimeException("Mqtt5 PubSub: execution failure", cause);
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
            if (publishPacket == null) {
                messagesReceived.countDown();
                return;
            }

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

    public static void main(String[] args) {

        cmdUtils = new CommandLineUtils();
        cmdUtils.registerProgramName("Mqtt5PubSub");
        cmdUtils.addCommonMQTTCommands();
        cmdUtils.addCommonTopicMessageCommands();
        cmdUtils.registerCommand("key", "<path>", "Path to your key in PEM format. (will use direct MQTT to connect if defined)");
        cmdUtils.registerCommand("cert", "<path>", "Path to your client certificate in PEM format. (will use direct MQTT to connect if defined)");
        cmdUtils.registerCommand("signing_region", "<string>", "Websocket region to use (will use websockets to connect if defined).");
        cmdUtils.registerCommand("client_id", "<int>", "Client id to use (optional, default='test-*').");
        cmdUtils.registerCommand("count", "<int>", "Number of messages to publish (optional, default='10').");
        cmdUtils.sendArguments(args);

        /**
         * Gather the input from the command line
         */
        String input_endpoint = cmdUtils.getCommandRequired("endpoint", "");
        String input_cert = cmdUtils.getCommandOrDefault("cert", "");
        String input_key = cmdUtils.getCommandOrDefault("key", "");
        String input_ca = cmdUtils.getCommandOrDefault("ca", "");
        String input_client_id = cmdUtils.getCommandOrDefault("client_id", "test-" + UUID.randomUUID().toString());
        int input_port = Integer.parseInt(cmdUtils.getCommandOrDefault("port", "8883"));
        String input_proxyHost = cmdUtils.getCommandOrDefault("proxy_host", "");
        int input_proxyPort = Integer.parseInt(cmdUtils.getCommandOrDefault("proxy_port", "0"));
        String input_topic = cmdUtils.getCommandOrDefault("topic", "test/topic");
        String input_message = cmdUtils.getCommandOrDefault("message", "Hello World!");
        int input_messagesToPublish = Integer.parseInt(cmdUtils.getCommandOrDefault("count", "10"));
        String input_signingRegion = cmdUtils.getCommandOrDefault("signing_region", null);

        // If running in CI, add a UUID to the topic
        if (isCI == true) {
            input_topic += "/" + UUID.randomUUID().toString();
        }

        try {
            /* Create a client based on desired connection type */
            SampleLifecycleEvents lifecycleEvents = new SampleLifecycleEvents();
            SamplePublishEvents publishEvents = new SamplePublishEvents(input_messagesToPublish);
            Mqtt5Client client;

            /**
             * Create the MQTT connection from the builder
             */
            if (input_cert != "" || input_key != "") {
                AwsIotMqtt5ClientBuilder builder = AwsIotMqtt5ClientBuilder.newDirectMqttBuilderWithMtlsFromPath(
                    input_endpoint, input_cert, input_key);
                ConnectPacket.ConnectPacketBuilder connectProperties = new ConnectPacket.ConnectPacketBuilder();
                connectProperties.withClientId(input_client_id);
                builder.withConnectProperties(connectProperties);
                builder.withLifeCycleEvents(lifecycleEvents);
                builder.withPublishEvents(publishEvents);
                client = builder.build();
                builder.close();
            } else {
                AwsIotMqtt5ClientBuilder.WebsocketSigv4Config websocketConfig = new AwsIotMqtt5ClientBuilder.WebsocketSigv4Config();
                if (input_signingRegion != "") {
                    websocketConfig.region = input_signingRegion;
                }
                AwsIotMqtt5ClientBuilder builder = AwsIotMqtt5ClientBuilder.newWebsocketMqttBuilderWithSigv4Auth(
                    input_endpoint, websocketConfig);
                ConnectPacket.ConnectPacketBuilder connectProperties = new ConnectPacket.ConnectPacketBuilder();
                connectProperties.withClientId(input_client_id);
                builder.withConnectProperties(connectProperties);
                builder.withLifeCycleEvents(lifecycleEvents);
                builder.withPublishEvents(publishEvents);
                client = builder.build();
                builder.close();
            }

            /* Connect */
            client.start();
            try {
                lifecycleEvents.connectedFuture.get(60, TimeUnit.SECONDS);
            } catch (Exception ex) {
                throw new RuntimeException("Exception occurred during connect", ex);
            }

            /* Subscribe */
            SubscribePacket.SubscribePacketBuilder subscribeBuilder = new SubscribePacket.SubscribePacketBuilder();
            subscribeBuilder.withSubscription(input_topic, QOS.AT_LEAST_ONCE, false, false, SubscribePacket.RetainHandlingType.DONT_SEND);
            try {
                client.subscribe(subscribeBuilder.build()).get(60, TimeUnit.SECONDS);
            } catch (Exception ex) {
                onApplicationFailure(ex);
            }

            /* Publish */
            PublishPacket.PublishPacketBuilder publishBuilder = new PublishPacket.PublishPacketBuilder();
            publishBuilder.withTopic(input_topic).withQOS(QOS.AT_LEAST_ONCE);
            int count = 0;
            try {
                while (count++ < input_messagesToPublish) {
                    publishBuilder.withPayload(("\"" + input_message + ": " + String.valueOf(count) + "\"").getBytes());
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

        } catch (CrtRuntimeException | InterruptedException ex) {
            onApplicationFailure(ex);
        }

        CrtResource.waitForNoResources();
        System.out.println("Complete!");
    }
}
