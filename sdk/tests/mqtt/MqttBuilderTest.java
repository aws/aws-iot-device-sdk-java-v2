/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.UUID;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt.WebsocketHandshakeTransformArgs;
import software.amazon.awssdk.iot.AwsIotMqttConnectionBuilder;

/**
 * NOTE: Will later add more testing and split MQTT5 testing setup from MQTT311 testing setup.
 * In the short term, the MQTT311 tests will reuse the majority of MQTT5 material.
 */

public class MqttBuilderTest {

    private String mqtt5IoTCoreHost;
    private String mqtt5IoTCoreRegion;

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
        mqtt5IoTCoreRegion = System.getenv("AWS_TEST_MQTT5_IOT_CORE_REGION");

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

    private Consumer<WebsocketHandshakeTransformArgs> websocketTransform = new Consumer<WebsocketHandshakeTransformArgs>() {
        @Override
        public void accept(WebsocketHandshakeTransformArgs t) {
            t.complete(t.getHttpRequest());
        }
    };

    MqttBuilderTest() {
        populateTestingEnvironmentVariables();
    }

    /**
     * ============================================================
     * IOT BUILDER TEST CASES
     * ============================================================
     */

    /* MQTT311 Custom Auth (no signing) connect */
    @Test
    public void ConnIoT_CustomAuth_UC1()
    {
        assumeTrue(mqtt5IoTCoreHost != null);
        assumeTrue(mqtt5IoTCoreNoSigningAuthorizerName != null);
        assumeTrue(mqtt5IoTCoreNoSigningAuthorizerUsername != null);
        assumeTrue(mqtt5IoTCoreNoSigningAuthorizerPassword != null);

        try {
            AwsIotMqttConnectionBuilder builder = AwsIotMqttConnectionBuilder.newDefaultBuilder();
            builder.withEndpoint(mqtt5IoTCoreHost);
            String clientId = "test-" + UUID.randomUUID().toString();
            builder.withClientId(clientId);
            builder.withCustomAuthorizer(
                mqtt5IoTCoreNoSigningAuthorizerUsername,
                mqtt5IoTCoreNoSigningAuthorizerName,
                null,
                mqtt5IoTCoreNoSigningAuthorizerPassword,
                null,
                null);
            MqttClientConnection connection = builder.build();
            builder.close();

            connection.connect().get();
            connection.disconnect().get();
            connection.close();

        } catch (Exception ex) {
            fail(ex);
        }
    }

    /* MQTT311 Custom Auth (with signing) connect */
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

        try {
            AwsIotMqttConnectionBuilder builder = AwsIotMqttConnectionBuilder.newDefaultBuilder();
            builder.withEndpoint(mqtt5IoTCoreHost);
            String clientId = "test-" + UUID.randomUUID().toString();
            builder.withClientId(clientId);
            builder.withCustomAuthorizer(
                mqtt5IoTCoreSigningAuthorizerUsername,
                mqtt5IoTCoreSigningAuthorizerName,
                mqtt5IoTCoreSigningAuthorizerTokenSignature,
                mqtt5IoTCoreSigningAuthorizerPassword,
                mqtt5IoTCoreSigningAuthorizerTokenKeyName,
                mqtt5IoTCoreSigningAuthorizerToken);
            MqttClientConnection connection = builder.build();
            builder.close();

            connection.connect().get();
            connection.disconnect().get();
            connection.close();

        } catch (Exception ex) {
            fail(ex);
        }
    }

    /* MQTT311 Custom Auth (with signing and unencoded signature) connect */
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

        try {
            AwsIotMqttConnectionBuilder builder = AwsIotMqttConnectionBuilder.newDefaultBuilder();
            builder.withEndpoint(mqtt5IoTCoreHost);
            String clientId = "test-" + UUID.randomUUID().toString();
            builder.withClientId(clientId);
            builder.withCustomAuthorizer(
                    mqtt5IoTCoreSigningAuthorizerUsername,
                    mqtt5IoTCoreSigningAuthorizerName,
                    mqtt5IoTCoreSigningAuthorizerTokenSignatureUnencoded,
                    mqtt5IoTCoreSigningAuthorizerPassword,
                    mqtt5IoTCoreSigningAuthorizerTokenKeyName,
                    mqtt5IoTCoreSigningAuthorizerToken);
            MqttClientConnection connection = builder.build();
            builder.close();

            connection.connect().get();
            connection.disconnect().get();
            connection.close();

        } catch (Exception ex) {
            fail(ex);
        }
    }

    /* Custom Auth (no signing) connect - Websockets */
    @Test
    public void ConnIoT_CustomAuth_UC3()
    {
        assumeTrue(mqtt5IoTCoreHost != null);
        assumeTrue(mqtt5IoTCoreRegion != null);
        assumeTrue(mqtt5IoTCoreNoSigningAuthorizerName != null);
        assumeTrue(mqtt5IoTCoreNoSigningAuthorizerUsername != null);
        assumeTrue(mqtt5IoTCoreNoSigningAuthorizerPassword != null);

        try {
            AwsIotMqttConnectionBuilder builder = AwsIotMqttConnectionBuilder.newDefaultBuilder();
            builder.withEndpoint(mqtt5IoTCoreHost);
            String clientId = "test-" + UUID.randomUUID().toString();
            builder.withClientId(clientId);

            builder.withWebsockets(true);
            builder.withWebsocketSigningRegion(mqtt5IoTCoreRegion);
            builder.withWebsocketHandshakeTransform(websocketTransform);

            builder.withCustomAuthorizer(
                mqtt5IoTCoreNoSigningAuthorizerUsername,
                mqtt5IoTCoreNoSigningAuthorizerName,
                null,
                mqtt5IoTCoreNoSigningAuthorizerPassword,
                null,
                null);

            MqttClientConnection connection = builder.build();
            builder.close();

            connection.connect().get();
            connection.disconnect().get();
            connection.close();

        } catch (Exception ex) {
            fail(ex);
        }
    }

    /* Custom Auth (with signing) connect - Websockets */
    @Test
    public void ConnIoT_CustomAuth_UC4()
    {
        assumeTrue(mqtt5IoTCoreHost != null);
        assumeTrue(mqtt5IoTCoreRegion != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerName != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerUsername != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerPassword != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerToken != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerTokenKeyName != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerTokenSignature != null);

        try {
            AwsIotMqttConnectionBuilder builder = AwsIotMqttConnectionBuilder.newDefaultBuilder();
            builder.withEndpoint(mqtt5IoTCoreHost);
            String clientId = "test-" + UUID.randomUUID().toString();
            builder.withClientId(clientId);

            builder.withWebsockets(true);
            builder.withWebsocketSigningRegion(mqtt5IoTCoreRegion);
            builder.withWebsocketHandshakeTransform(websocketTransform);

            builder.withCustomAuthorizer(
                mqtt5IoTCoreSigningAuthorizerUsername,
                mqtt5IoTCoreSigningAuthorizerName,
                mqtt5IoTCoreSigningAuthorizerTokenSignature,
                mqtt5IoTCoreSigningAuthorizerPassword,
                mqtt5IoTCoreSigningAuthorizerTokenKeyName,
                mqtt5IoTCoreSigningAuthorizerToken);

            MqttClientConnection connection = builder.build();
            builder.close();

            connection.connect().get();
            connection.disconnect().get();
            connection.close();

        } catch (Exception ex) {
            fail(ex);
        }
    }

    /* Custom Auth (with signing and an unencoded signature) connect - Websockets */
    @Test
    public void ConnIoT_CustomAuth_UC4_unencoded()
    {
        assumeTrue(mqtt5IoTCoreHost != null);
        assumeTrue(mqtt5IoTCoreRegion != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerName != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerUsername != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerPassword != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerToken != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerTokenKeyName != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerTokenSignatureUnencoded != null);

        try {
            AwsIotMqttConnectionBuilder builder = AwsIotMqttConnectionBuilder.newDefaultBuilder();
            builder.withEndpoint(mqtt5IoTCoreHost);
            String clientId = "test-" + UUID.randomUUID().toString();
            builder.withClientId(clientId);

            builder.withWebsockets(true);
            builder.withWebsocketSigningRegion(mqtt5IoTCoreRegion);
            builder.withWebsocketHandshakeTransform(websocketTransform);

            builder.withCustomAuthorizer(
                    mqtt5IoTCoreSigningAuthorizerUsername,
                    mqtt5IoTCoreSigningAuthorizerName,
                    mqtt5IoTCoreSigningAuthorizerTokenSignatureUnencoded,
                    mqtt5IoTCoreSigningAuthorizerPassword,
                    mqtt5IoTCoreSigningAuthorizerTokenKeyName,
                    mqtt5IoTCoreSigningAuthorizerToken);

            MqttClientConnection connection = builder.build();
            builder.close();

            connection.connect().get();
            connection.disconnect().get();
            connection.close();

        } catch (Exception ex) {
            fail(ex);
        }
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

        try {
            AwsIotMqttConnectionBuilder builder = AwsIotMqttConnectionBuilder.newDefaultBuilder();
            builder.withEndpoint(mqtt5IoTCoreHost);
            String clientId = "test-" + UUID.randomUUID().toString();
            builder.withClientId(clientId);

            builder.withWebsockets(true);
            builder.withWebsocketSigningRegion(mqtt5IoTCoreRegion);
            builder.withWebsocketHandshakeTransform(websocketTransform);

            builder.withCustomAuthorizer(
                mqtt5IoTCoreSigningAuthorizerUsername,
                mqtt5IoTCoreSigningAuthorizerName,
                mqtt5IoTCoreSigningAuthorizerTokenSignature,
                "InvalidPassword",
                mqtt5IoTCoreSigningAuthorizerTokenKeyName,
                mqtt5IoTCoreSigningAuthorizerToken);
            MqttClientConnection connection = builder.build();
            builder.close();

            assertThrows(Exception.class, () -> connection.connect().get());

            connection.close();

        } catch (Exception ex) {
            fail(ex);
        }
    }

    /* Custom Auth (with signing) connect - Websockets - Invalid Token */
    @Test
    public void ConnIoT_CustomAuth_InvalidToken()
    {
        assumeTrue(mqtt5IoTCoreHost != null);
        assumeTrue(mqtt5IoTCoreRegion != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerName != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerUsername != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerPassword != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerToken != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerTokenSignature != null);

        try {
            AwsIotMqttConnectionBuilder builder = AwsIotMqttConnectionBuilder.newDefaultBuilder();
            builder.withEndpoint(mqtt5IoTCoreHost);
            String clientId = "test-" + UUID.randomUUID().toString();
            builder.withClientId(clientId);

            builder.withWebsockets(true);
            builder.withWebsocketSigningRegion(mqtt5IoTCoreRegion);
            builder.withWebsocketHandshakeTransform(websocketTransform);

            builder.withCustomAuthorizer(
                mqtt5IoTCoreSigningAuthorizerUsername,
                mqtt5IoTCoreSigningAuthorizerName,
                mqtt5IoTCoreSigningAuthorizerTokenSignature,
                mqtt5IoTCoreSigningAuthorizerPassword,
                "Invalid Token",
                mqtt5IoTCoreSigningAuthorizerToken);

            MqttClientConnection connection = builder.build();
            builder.close();

            assertThrows(Exception.class, () -> connection.connect().get());
            connection.close();

        } catch (Exception ex) {
            fail(ex);
        }
    }

    /* Custom Auth (with signing) connect - Websockets - Invalid Token Signature */
    @Test
    public void ConnIoT_CustomAuth_InvalidTokenSignature()
    {
        assumeTrue(mqtt5IoTCoreHost != null);
        assumeTrue(mqtt5IoTCoreRegion != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerName != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerUsername != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerPassword != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerToken != null);
        assumeTrue(mqtt5IoTCoreSigningAuthorizerTokenKeyName != null);

        try {
            AwsIotMqttConnectionBuilder builder = AwsIotMqttConnectionBuilder.newDefaultBuilder();
            builder.withEndpoint(mqtt5IoTCoreHost);
            String clientId = "test-" + UUID.randomUUID().toString();
            builder.withClientId(clientId);

            builder.withWebsockets(true);
            builder.withWebsocketSigningRegion(mqtt5IoTCoreRegion);
            builder.withWebsocketHandshakeTransform(websocketTransform);

            builder.withCustomAuthorizer(
                mqtt5IoTCoreSigningAuthorizerUsername,
                mqtt5IoTCoreSigningAuthorizerName,
                "InvalidTokenSignature",
                mqtt5IoTCoreSigningAuthorizerPassword,
                mqtt5IoTCoreSigningAuthorizerTokenKeyName,
                mqtt5IoTCoreSigningAuthorizerToken);

            MqttClientConnection connection = builder.build();
            builder.close();

            assertThrows(Exception.class, () -> connection.connect().get());
            connection.close();

        } catch (Exception ex) {
            fail(ex);
        }
    }
}
