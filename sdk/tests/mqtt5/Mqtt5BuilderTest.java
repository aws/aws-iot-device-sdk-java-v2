/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.mqtt5.Mqtt5Client;
import software.amazon.awssdk.crt.mqtt5.Mqtt5ClientOptions;
import software.amazon.awssdk.crt.mqtt5.NegotiatedSettings;
import software.amazon.awssdk.crt.mqtt5.OnAttemptingConnectReturn;
import software.amazon.awssdk.crt.mqtt5.OnConnectionFailureReturn;
import software.amazon.awssdk.crt.mqtt5.OnConnectionSuccessReturn;
import software.amazon.awssdk.crt.mqtt5.OnDisconnectionReturn;
import software.amazon.awssdk.crt.mqtt5.OnStoppedReturn;
import software.amazon.awssdk.crt.mqtt5.PublishResult;
import software.amazon.awssdk.crt.mqtt5.PublishReturn;
import software.amazon.awssdk.crt.mqtt5.QOS;
import software.amazon.awssdk.crt.mqtt5.packets.ConnAckPacket;
import software.amazon.awssdk.crt.mqtt5.packets.DisconnectPacket;
import software.amazon.awssdk.crt.mqtt5.packets.PublishPacket;
import software.amazon.awssdk.crt.mqtt5.packets.SubscribePacket;
import software.amazon.awssdk.crt.mqtt5.packets.UnsubscribePacket;
import software.amazon.awssdk.iot.AwsIotMqtt5ClientBuilder;
import software.amazon.awssdk.iot.AwsIotMqttConnectionBuilder;

public class Mqtt5BuilderTest {

    private String mqtt5IoTCoreHost;
    private String mqtt5IoTCoreCertificatePath;
    private String mqtt5IoTCoreKeyPath;

    private String mqtt5IoTCoreNoSigningAuthorizerName;
    private String mqtt5IoTCoreNoSigningAuthorizerUsername;
    private String mqtt5IoTCoreNoSigningAuthorizerPassword;

    private String mqtt5IoTCoreSigningAuthorizerName;
    private String mqtt5IoTCoreSigningAuthorizerUsername;
    private String mqtt5IoTCoreSigningAuthorizerPassword;
    private String mqtt5IoTCoreSigningAuthorizerToken;
    private String mqtt5IoTCoreSigningAuthorizerTokenKeyName;
    private String mqtt5IoTCoreSigningAuthorizerTokenSignature;
    private String mqtt5IoTCoreSigningAuthorizerTokenSignatureUnencoded;

    private void populateTestingEnvironmentVariables() {
        mqtt5IoTCoreHost = System.getenv("AWS_TEST_MQTT5_IOT_CORE_HOST");
        mqtt5IoTCoreCertificatePath = System.getenv("AWS_TEST_MQTT5_IOT_CERTIFICATE_PATH");
        mqtt5IoTCoreKeyPath = System.getenv("AWS_TEST_MQTT5_IOT_KEY_PATH");

        mqtt5IoTCoreNoSigningAuthorizerName = System.getenv("AWS_TEST_MQTT5_IOT_CORE_NO_SIGNING_AUTHORIZER_NAME");
        mqtt5IoTCoreNoSigningAuthorizerUsername = System.getenv("AWS_TEST_MQTT5_IOT_CORE_NO_SIGNING_AUTHORIZER_USERNAME");
        mqtt5IoTCoreNoSigningAuthorizerPassword = System.getenv("AWS_TEST_MQTT5_IOT_CORE_NO_SIGNING_AUTHORIZER_PASSWORD");

        mqtt5IoTCoreSigningAuthorizerName = System.getenv("AWS_TEST_MQTT5_IOT_CORE_SIGNING_AUTHORIZER_NAME");
        mqtt5IoTCoreSigningAuthorizerUsername = System.getenv("AWS_TEST_MQTT5_IOT_CORE_SIGNING_AUTHORIZER_USERNAME");
        mqtt5IoTCoreSigningAuthorizerPassword = System.getenv("AWS_TEST_MQTT5_IOT_CORE_SIGNING_AUTHORIZER_PASSWORD");
        mqtt5IoTCoreSigningAuthorizerToken = System.getenv("AWS_TEST_MQTT5_IOT_CORE_SIGNING_AUTHORIZER_TOKEN");
        mqtt5IoTCoreSigningAuthorizerTokenKeyName = System.getenv("AWS_TEST_MQTT5_IOT_CORE_SIGNING_AUTHORIZER_TOKEN_KEY_NAME");
        mqtt5IoTCoreSigningAuthorizerTokenSignature = System.getenv("AWS_TEST_MQTT5_IOT_CORE_SIGNING_AUTHORIZER_TOKEN_SIGNATURE");
        mqtt5IoTCoreSigningAuthorizerTokenSignatureUnencoded = System.getenv("AWS_TEST_MQTT5_IOT_CORE_SIGNING_AUTHORIZER_TOKEN_SIGNATURE_UNENCODED");
    }

    Mqtt5BuilderTest() {
        populateTestingEnvironmentVariables();
    }

    /**
     * ============================================================
     * TEST HELPER FUNCTIONS
     * ============================================================
     */

    static final class LifecycleEvents_Futured implements Mqtt5ClientOptions.LifecycleEvents {
        CompletableFuture<Void> connectedFuture = new CompletableFuture<>();
        CompletableFuture<Void> stopFuture = new CompletableFuture<>();

        ConnAckPacket connectSuccessPacket = null;
        NegotiatedSettings connectSuccessSettings = null;

        int connectFailureCode = 0;
        ConnAckPacket connectFailurePacket = null;

        int disconnectFailureCode = 0;
        DisconnectPacket disconnectPacket = null;

        @Override
        public void onAttemptingConnect(Mqtt5Client client, OnAttemptingConnectReturn onAttemptingConnectReturn) {}

        @Override
        public void onConnectionSuccess(Mqtt5Client client, OnConnectionSuccessReturn onConnectionSuccessReturn) {
            connectSuccessPacket = onConnectionSuccessReturn.getConnAckPacket();
            connectSuccessSettings = onConnectionSuccessReturn.getNegotiatedSettings();
            connectedFuture.complete(null);
        }

        @Override
        public void onConnectionFailure(Mqtt5Client client, OnConnectionFailureReturn onConnectionFailureReturn) {
            connectFailureCode = onConnectionFailureReturn.getErrorCode();
            connectFailurePacket = onConnectionFailureReturn.getConnAckPacket();
            connectedFuture.completeExceptionally(new Exception("Could not connect! Failure code: " + CRT.awsErrorString(connectFailureCode)));
        }

        @Override
        public void onDisconnection(Mqtt5Client client, OnDisconnectionReturn onDisconnectionReturn) {
            disconnectFailureCode = onDisconnectionReturn.getErrorCode();
            disconnectPacket = onDisconnectionReturn.getDisconnectPacket();
        }

        @Override
        public void onStopped(Mqtt5Client client, OnStoppedReturn onStoppedReturn) {
            stopFuture.complete(null);
        }
    }

    static final class PublishEvents_Futured implements Mqtt5ClientOptions.PublishEvents {
        CompletableFuture<Void> publishReceivedFuture = new CompletableFuture<>();
        PublishPacket publishPacket = null;

        @Override
        public void onMessageReceived(Mqtt5Client client, PublishReturn publishReturn) {
            publishPacket = publishReturn.getPublishPacket();
            publishReceivedFuture.complete(null);
        }
    }

    private void TestSubPubUnsub(Mqtt5Client client, LifecycleEvents_Futured lifecycleEvents, PublishEvents_Futured publishEvents) {
        String topic_uuid = UUID.randomUUID().toString();

        // Connect
        try {
            client.start();
            lifecycleEvents.connectedFuture.get(120, TimeUnit.SECONDS);
        } catch (Exception ex) {
            fail("Exception in connecting: " + ex.toString());
        }
        assertTrue(client.getIsConnected() == true);

        // Sub
        SubscribePacket.SubscribePacketBuilder subBuilder = new SubscribePacket.SubscribePacketBuilder();
        subBuilder.withSubscription("test/topic/" + topic_uuid, QOS.AT_LEAST_ONCE);
        try {
            client.subscribe(subBuilder.build()).get(120, TimeUnit.SECONDS);
        } catch (Exception ex) {
            fail("Exception in subscribing: " + ex.toString());
        }

        // Pub
        PublishPacket.PublishPacketBuilder pubBuilder = new PublishPacket.PublishPacketBuilder();
        String publishPayload = "Hello World";
        pubBuilder.withTopic("test/topic/" + topic_uuid).withQOS(QOS.AT_LEAST_ONCE).withPayload(publishPayload.getBytes());
        try {
            PublishResult result = client.publish(pubBuilder.build()).get(120, TimeUnit.SECONDS);
        } catch (Exception ex) {
            fail("Exception in publishing: " + ex.toString());
        }
        try {
            publishEvents.publishReceivedFuture.get(120, TimeUnit.SECONDS);
            String resultStr = new String(publishEvents.publishPacket.getPayload());
            assertTrue(resultStr.equals(publishPayload));
        } catch (Exception ex) {
            fail("Exception in getting publish: " + ex.toString());
        }

        // Unsubscribe
        UnsubscribePacket.UnsubscribePacketBuilder unsubBuilder = new UnsubscribePacket.UnsubscribePacketBuilder();
        unsubBuilder.withSubscription("test/topic/" + topic_uuid);
        try {
            client.unsubscribe(unsubBuilder.build()).get(120, TimeUnit.SECONDS);
        } catch (Exception ex) {
            fail("Exception in unsubscribing: " + ex.toString());
        }

        // Disconnect/Stop
        try {
            client.stop(new DisconnectPacket.DisconnectPacketBuilder().build());
            lifecycleEvents.stopFuture.get(120, TimeUnit.SECONDS);
        } catch (Exception ex) {
            fail("Exception in stopping: " + ex.toString());
        }
        assertTrue(client.getIsConnected() == false);
    }

    /**
     * ============================================================
     * IOT BUILDER TEST CASES
     * ============================================================
     */

    /* Testing direct connect with mTLS (cert and key) */
    @Test
    public void ConnIoT_DirectConnect_UC1()
    {
        assumeTrue(mqtt5IoTCoreHost != null);
        assumeTrue(mqtt5IoTCoreCertificatePath != null);
        assumeTrue(mqtt5IoTCoreKeyPath != null);

        AwsIotMqtt5ClientBuilder builder = AwsIotMqtt5ClientBuilder.newDirectMqttBuilderWithMtlsFromPath(
            mqtt5IoTCoreHost, mqtt5IoTCoreCertificatePath, mqtt5IoTCoreKeyPath);

        LifecycleEvents_Futured lifecycleEvents = new LifecycleEvents_Futured();
        builder.withLifeCycleEvents(lifecycleEvents);

        PublishEvents_Futured publishEvents = new PublishEvents_Futured();
        builder.withPublishEvents(publishEvents);

        Mqtt5Client client = builder.build();
        TestSubPubUnsub(client, lifecycleEvents, publishEvents);
        client.close();
        builder.close();
    }

    /* Testing direct connect with mTLS (cert and key) - but with two clients from same builder */
    @Test
    public void ConnIoT_DirectConnect_UC1_ALT()
    {
        assumeTrue(mqtt5IoTCoreHost != null);
        assumeTrue(mqtt5IoTCoreCertificatePath != null);
        assumeTrue(mqtt5IoTCoreKeyPath != null);

        AwsIotMqtt5ClientBuilder builder = AwsIotMqtt5ClientBuilder.newDirectMqttBuilderWithMtlsFromPath(
            mqtt5IoTCoreHost, mqtt5IoTCoreCertificatePath, mqtt5IoTCoreKeyPath);

        LifecycleEvents_Futured lifecycleEvents = new LifecycleEvents_Futured();
        builder.withLifeCycleEvents(lifecycleEvents);
        PublishEvents_Futured publishEvents = new PublishEvents_Futured();
        builder.withPublishEvents(publishEvents);

        Mqtt5Client client = builder.build();
        TestSubPubUnsub(client, lifecycleEvents, publishEvents);
        client.close();

        // Create a second client using the same builder:
        LifecycleEvents_Futured lifecycleEventsTwo = new LifecycleEvents_Futured();
        builder.withLifeCycleEvents(lifecycleEventsTwo);
        PublishEvents_Futured publishEventsTwo = new PublishEvents_Futured();
        builder.withPublishEvents(publishEventsTwo);
        Mqtt5Client clientTwo = builder.build();
        TestSubPubUnsub(clientTwo, lifecycleEventsTwo, publishEventsTwo);
        clientTwo.close();

        // Builder must be closed to free everything
        builder.close();
    }

    /* Websocket connect */
    @Test
    public void ConnIoT_WebsocketConnect_UC1()
    {
        assumeTrue(mqtt5IoTCoreHost != null);

        AwsIotMqtt5ClientBuilder builder = AwsIotMqtt5ClientBuilder.newWebsocketMqttBuilderWithSigv4Auth(
            mqtt5IoTCoreHost, null);

        LifecycleEvents_Futured lifecycleEvents = new LifecycleEvents_Futured();
        builder.withLifeCycleEvents(lifecycleEvents);

        PublishEvents_Futured publishEvents = new PublishEvents_Futured();
        builder.withPublishEvents(publishEvents);

        Mqtt5Client client = builder.build();
        TestSubPubUnsub(client, lifecycleEvents, publishEvents);
        client.close();
        builder.close();
    }

    /* Custom Auth (no signing) connect */
    @Test
    public void ConnIoT_CustomAuth_UC1()
    {
        assumeTrue(mqtt5IoTCoreHost != null);
        assumeTrue(mqtt5IoTCoreNoSigningAuthorizerName != null);
        assumeTrue(mqtt5IoTCoreNoSigningAuthorizerUsername != null);
        assumeTrue(mqtt5IoTCoreNoSigningAuthorizerPassword != null);

        AwsIotMqtt5ClientBuilder.MqttConnectCustomAuthConfig customAuthConfig = new AwsIotMqtt5ClientBuilder.MqttConnectCustomAuthConfig();
        customAuthConfig.authorizerName = mqtt5IoTCoreNoSigningAuthorizerName;
        customAuthConfig.username = mqtt5IoTCoreNoSigningAuthorizerUsername;
        customAuthConfig.password = mqtt5IoTCoreNoSigningAuthorizerPassword.getBytes();

        AwsIotMqtt5ClientBuilder builder = AwsIotMqtt5ClientBuilder.newDirectMqttBuilderWithCustomAuth(
            mqtt5IoTCoreHost, customAuthConfig);

        LifecycleEvents_Futured lifecycleEvents = new LifecycleEvents_Futured();
        builder.withLifeCycleEvents(lifecycleEvents);

        PublishEvents_Futured publishEvents = new PublishEvents_Futured();
        builder.withPublishEvents(publishEvents);

        Mqtt5Client client = builder.build();
        TestSubPubUnsub(client, lifecycleEvents, publishEvents);
        client.close();
        builder.close();
    }

    /* Custom Auth (with signing) connect */
    @Test
    public void ConnIoT_CustomAuth_UC2()
    {
        assumeTrue(mqtt5IoTCoreHost != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerName != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerUsername != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerPassword != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerToken != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerTokenKeyName != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerTokenSignature != null);

        AwsIotMqtt5ClientBuilder.MqttConnectCustomAuthConfig customAuthConfig = new AwsIotMqtt5ClientBuilder.MqttConnectCustomAuthConfig();
        customAuthConfig.authorizerName = mqtt5IoTCoreSigningAuthorizerName;
        customAuthConfig.username = mqtt5IoTCoreSigningAuthorizerUsername;
        customAuthConfig.password = mqtt5IoTCoreSigningAuthorizerPassword.getBytes();
        customAuthConfig.tokenValue = mqtt5IoTCoreSigningAuthorizerToken;
        customAuthConfig.tokenKeyName = mqtt5IoTCoreSigningAuthorizerTokenKeyName;
        customAuthConfig.tokenSignature = mqtt5IoTCoreSigningAuthorizerTokenSignature;

        AwsIotMqtt5ClientBuilder builder = AwsIotMqtt5ClientBuilder.newDirectMqttBuilderWithCustomAuth(
            mqtt5IoTCoreHost, customAuthConfig);

        LifecycleEvents_Futured lifecycleEvents = new LifecycleEvents_Futured();
        builder.withLifeCycleEvents(lifecycleEvents);

        PublishEvents_Futured publishEvents = new PublishEvents_Futured();
        builder.withPublishEvents(publishEvents);

        Mqtt5Client client = builder.build();
        TestSubPubUnsub(client, lifecycleEvents, publishEvents);
        client.close();
        builder.close();
    }

    /* Custom Auth (with signing and an unencoded signature) connect */
    @Test
    public void ConnIoT_CustomAuth_UC2_unencoded()
    {
        assumeTrue(mqtt5IoTCoreHost != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerName != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerUsername != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerPassword != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerToken != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerTokenKeyName != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerTokenSignatureUnencoded != null);

        AwsIotMqtt5ClientBuilder.MqttConnectCustomAuthConfig customAuthConfig = new AwsIotMqtt5ClientBuilder.MqttConnectCustomAuthConfig();
        customAuthConfig.authorizerName = mqtt5IoTCoreSigningAuthorizerName;
        customAuthConfig.username = mqtt5IoTCoreSigningAuthorizerUsername;
        customAuthConfig.password = mqtt5IoTCoreSigningAuthorizerPassword.getBytes();
        customAuthConfig.tokenValue = mqtt5IoTCoreSigningAuthorizerToken;
        customAuthConfig.tokenKeyName = mqtt5IoTCoreSigningAuthorizerTokenKeyName;
        customAuthConfig.tokenSignature = mqtt5IoTCoreSigningAuthorizerTokenSignatureUnencoded;

        AwsIotMqtt5ClientBuilder builder = AwsIotMqtt5ClientBuilder.newDirectMqttBuilderWithCustomAuth(
                mqtt5IoTCoreHost, customAuthConfig);

        LifecycleEvents_Futured lifecycleEvents = new LifecycleEvents_Futured();
        builder.withLifeCycleEvents(lifecycleEvents);

        PublishEvents_Futured publishEvents = new PublishEvents_Futured();
        builder.withPublishEvents(publishEvents);

        Mqtt5Client client = builder.build();
        TestSubPubUnsub(client, lifecycleEvents, publishEvents);
        client.close();
        builder.close();
    }

    /* Custom Auth (no signing) connect - Websockets */
    @Test
    public void ConnIoT_CustomAuth_UC3()
    {
        assumeTrue(mqtt5IoTCoreHost != null);
        assumeTrue(mqtt5IoTCoreNoSigningAuthorizerName != null);
        assumeTrue(mqtt5IoTCoreNoSigningAuthorizerUsername != null);
        assumeTrue(mqtt5IoTCoreNoSigningAuthorizerPassword != null);

        AwsIotMqtt5ClientBuilder.MqttConnectCustomAuthConfig customAuthConfig = new AwsIotMqtt5ClientBuilder.MqttConnectCustomAuthConfig();
        customAuthConfig.authorizerName = mqtt5IoTCoreNoSigningAuthorizerName;
        customAuthConfig.username = mqtt5IoTCoreNoSigningAuthorizerUsername;
        customAuthConfig.password = mqtt5IoTCoreNoSigningAuthorizerPassword.getBytes();

        AwsIotMqtt5ClientBuilder builder = AwsIotMqtt5ClientBuilder.newWebsocketMqttBuilderWithCustomAuth(
            mqtt5IoTCoreHost, customAuthConfig);

        LifecycleEvents_Futured lifecycleEvents = new LifecycleEvents_Futured();
        builder.withLifeCycleEvents(lifecycleEvents);

        PublishEvents_Futured publishEvents = new PublishEvents_Futured();
        builder.withPublishEvents(publishEvents);

        Mqtt5Client client = builder.build();
        TestSubPubUnsub(client, lifecycleEvents, publishEvents);
        client.close();
        builder.close();
    }

    /* Custom Auth (with signing) connect - Websockets */
    @Test
    public void ConnIoT_CustomAuth_UC4()
    {
        assumeTrue(mqtt5IoTCoreHost != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerName != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerUsername != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerPassword != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerToken != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerTokenKeyName != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerTokenSignature != null);

        AwsIotMqtt5ClientBuilder.MqttConnectCustomAuthConfig customAuthConfig = new AwsIotMqtt5ClientBuilder.MqttConnectCustomAuthConfig();
        customAuthConfig.authorizerName = mqtt5IoTCoreSigningAuthorizerName;
        customAuthConfig.username = mqtt5IoTCoreSigningAuthorizerUsername;
        customAuthConfig.password = mqtt5IoTCoreSigningAuthorizerPassword.getBytes();
        customAuthConfig.tokenValue = mqtt5IoTCoreSigningAuthorizerToken;
        customAuthConfig.tokenKeyName = mqtt5IoTCoreSigningAuthorizerTokenKeyName;
        customAuthConfig.tokenSignature = mqtt5IoTCoreSigningAuthorizerTokenSignature;

        AwsIotMqtt5ClientBuilder builder = AwsIotMqtt5ClientBuilder.newWebsocketMqttBuilderWithCustomAuth(
            mqtt5IoTCoreHost, customAuthConfig);

        LifecycleEvents_Futured lifecycleEvents = new LifecycleEvents_Futured();
        builder.withLifeCycleEvents(lifecycleEvents);

        PublishEvents_Futured publishEvents = new PublishEvents_Futured();
        builder.withPublishEvents(publishEvents);

        Mqtt5Client client = builder.build();
        TestSubPubUnsub(client, lifecycleEvents, publishEvents);
        client.close();
        builder.close();
    }

    /* Custom Auth (with signing and an unencoded signature) connect - Websockets */
    @Test
    public void ConnIoT_CustomAuth_UC4_unencoded()
    {
        assumeTrue(mqtt5IoTCoreHost != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerName != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerUsername != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerPassword != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerToken != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerTokenKeyName != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerTokenSignatureUnencoded != null);

        AwsIotMqtt5ClientBuilder.MqttConnectCustomAuthConfig customAuthConfig = new AwsIotMqtt5ClientBuilder.MqttConnectCustomAuthConfig();
        customAuthConfig.authorizerName = mqtt5IoTCoreSigningAuthorizerName;
        customAuthConfig.username = mqtt5IoTCoreSigningAuthorizerUsername;
        customAuthConfig.password = mqtt5IoTCoreSigningAuthorizerPassword.getBytes();
        customAuthConfig.tokenValue = mqtt5IoTCoreSigningAuthorizerToken;
        customAuthConfig.tokenKeyName = mqtt5IoTCoreSigningAuthorizerTokenKeyName;
        customAuthConfig.tokenSignature = mqtt5IoTCoreSigningAuthorizerTokenSignatureUnencoded;

        AwsIotMqtt5ClientBuilder builder = AwsIotMqtt5ClientBuilder.newWebsocketMqttBuilderWithCustomAuth(
                mqtt5IoTCoreHost, customAuthConfig);

        LifecycleEvents_Futured lifecycleEvents = new LifecycleEvents_Futured();
        builder.withLifeCycleEvents(lifecycleEvents);

        PublishEvents_Futured publishEvents = new PublishEvents_Futured();
        builder.withPublishEvents(publishEvents);

        Mqtt5Client client = builder.build();
        TestSubPubUnsub(client, lifecycleEvents, publishEvents);
        client.close();
        builder.close();
    }

    /* Custom Auth (with signing) connect - Websockets - Invalid Password */
    @Test
    public void ConnIoT_CustomAuth_InvalidPassword()
    {
        assumeTrue(mqtt5IoTCoreHost != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerName != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerUsername != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerToken != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerTokenKeyName != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerTokenSignature != null);

        AwsIotMqtt5ClientBuilder.MqttConnectCustomAuthConfig customAuthConfig = new AwsIotMqtt5ClientBuilder.MqttConnectCustomAuthConfig();
        customAuthConfig.authorizerName = mqtt5IoTCoreSigningAuthorizerName;
        customAuthConfig.username = mqtt5IoTCoreSigningAuthorizerUsername;
        customAuthConfig.password = "InvalidPassword".getBytes();
        customAuthConfig.tokenValue = mqtt5IoTCoreSigningAuthorizerToken;
        customAuthConfig.tokenKeyName = mqtt5IoTCoreSigningAuthorizerTokenKeyName;
        customAuthConfig.tokenSignature = mqtt5IoTCoreSigningAuthorizerTokenSignature;

        AwsIotMqtt5ClientBuilder builder = AwsIotMqtt5ClientBuilder.newWebsocketMqttBuilderWithCustomAuth(
            mqtt5IoTCoreHost, customAuthConfig);

        LifecycleEvents_Futured lifecycleEvents = new LifecycleEvents_Futured();
        builder.withLifeCycleEvents(lifecycleEvents);

        PublishEvents_Futured publishEvents = new PublishEvents_Futured();
        builder.withPublishEvents(publishEvents);

        Mqtt5Client client = builder.build();

        client.start();
        assertThrows(Exception.class, () -> lifecycleEvents.connectedFuture.get(120, TimeUnit.SECONDS));

        client.close();
        builder.close();
    }

    /* Custom Auth (with signing) connect - Websockets - Invalid Token */
    @Test
    public void ConnIoT_CustomAuth_InvalidToken()
    {
        assumeTrue(mqtt5IoTCoreHost != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerName != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerUsername != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerPassword != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerToken != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerTokenSignature != null);

        AwsIotMqtt5ClientBuilder.MqttConnectCustomAuthConfig customAuthConfig = new AwsIotMqtt5ClientBuilder.MqttConnectCustomAuthConfig();
        customAuthConfig.authorizerName = mqtt5IoTCoreSigningAuthorizerName;
        customAuthConfig.username = mqtt5IoTCoreSigningAuthorizerUsername;
        customAuthConfig.password = mqtt5IoTCoreSigningAuthorizerPassword.getBytes();
        customAuthConfig.tokenValue = mqtt5IoTCoreSigningAuthorizerToken;
        customAuthConfig.tokenKeyName = "Invalid Token";
        customAuthConfig.tokenSignature = mqtt5IoTCoreSigningAuthorizerTokenSignature;

        AwsIotMqtt5ClientBuilder builder = AwsIotMqtt5ClientBuilder.newWebsocketMqttBuilderWithCustomAuth(
            mqtt5IoTCoreHost, customAuthConfig);

        LifecycleEvents_Futured lifecycleEvents = new LifecycleEvents_Futured();
        builder.withLifeCycleEvents(lifecycleEvents);

        PublishEvents_Futured publishEvents = new PublishEvents_Futured();
        builder.withPublishEvents(publishEvents);

        Mqtt5Client client = builder.build();

        client.start();
        assertThrows(Exception.class, () -> lifecycleEvents.connectedFuture.get(120, TimeUnit.SECONDS));

        client.close();
        builder.close();
    }

    /* Custom Auth (with signing) connect - Websockets - Invalid Token Signature */
    @Test
    public void ConnIoT_CustomAuth_InvalidTokenSignature()
    {
        assumeTrue(mqtt5IoTCoreHost != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerName != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerUsername != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerPassword != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerToken != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerTokenKeyName != null);

        AwsIotMqtt5ClientBuilder.MqttConnectCustomAuthConfig customAuthConfig = new AwsIotMqtt5ClientBuilder.MqttConnectCustomAuthConfig();
        customAuthConfig.authorizerName = mqtt5IoTCoreSigningAuthorizerName;
        customAuthConfig.username = mqtt5IoTCoreSigningAuthorizerUsername;
        customAuthConfig.password = mqtt5IoTCoreSigningAuthorizerPassword.getBytes();
        customAuthConfig.tokenValue = mqtt5IoTCoreSigningAuthorizerToken;
        customAuthConfig.tokenKeyName = mqtt5IoTCoreSigningAuthorizerTokenKeyName;
        customAuthConfig.tokenSignature = "InvalidTokenSignature";

        AwsIotMqtt5ClientBuilder builder = AwsIotMqtt5ClientBuilder.newWebsocketMqttBuilderWithCustomAuth(
            mqtt5IoTCoreHost, customAuthConfig);

        LifecycleEvents_Futured lifecycleEvents = new LifecycleEvents_Futured();
        builder.withLifeCycleEvents(lifecycleEvents);

        PublishEvents_Futured publishEvents = new PublishEvents_Futured();
        builder.withPublishEvents(publishEvents);

        Mqtt5Client client = builder.build();

        client.start();
        assertThrows(Exception.class, () -> lifecycleEvents.connectedFuture.get(120, TimeUnit.SECONDS));

        client.close();
        builder.close();
    }

    /* MQTT311 builder to MQTT5 builder - simple direct connection */
    @Test
    public void ConnIoT_DirectConnect_MQTT311_to_MQTT5_UC1()
    {
        assumeTrue(mqtt5IoTCoreHost != null);
        assumeTrue(mqtt5IoTCoreCertificatePath != null);
        assumeTrue(mqtt5IoTCoreKeyPath != null);

        // Make a simple MQTT311 builder
        AwsIotMqttConnectionBuilder mqtt311Builder = AwsIotMqttConnectionBuilder.newMtlsBuilderFromPath(
            mqtt5IoTCoreCertificatePath, mqtt5IoTCoreKeyPath);
        mqtt311Builder.withEndpoint(mqtt5IoTCoreHost);
        AwsIotMqtt5ClientBuilder builder = null;
        // CONVERT
        try {
            builder = mqtt311Builder.toAwsIotMqtt5ClientBuilder();
        } catch (Exception ex) {
            fail("Exception occurred making AwsIotMqtt5ClientBuilder from MQTT311 config! Exception: " + ex.getMessage());
        }
        // Close the MQTT311 builder
        mqtt311Builder.close();

        LifecycleEvents_Futured lifecycleEvents = new LifecycleEvents_Futured();
        builder.withLifeCycleEvents(lifecycleEvents);

        PublishEvents_Futured publishEvents = new PublishEvents_Futured();
        builder.withPublishEvents(publishEvents);

        Mqtt5Client client = builder.build();
        TestSubPubUnsub(client, lifecycleEvents, publishEvents);
        client.close();
        builder.close();
    }
}
