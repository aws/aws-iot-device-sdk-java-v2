/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.iot.AwsIotMqttConnectionBuilder;

/**
 * NOTE: Will later add more testing and split MQTT5 testing setup from MQTT311 testing setup.
 * In the short term, the MQTT311 tests will reuse the majority of MQTT5 material.
 */

public class MqttBuilderTest {

    private String mqtt5IoTCoreHost;

    private String mqtt5IoTCoreNoSigningAuthorizerName;
    private String mqtt5IoTCoreNoSigningAuthorizerUsername;
    private String mqtt5IoTCoreNoSigningAuthorizerPassword;

    private String mqtt5IoTCoreSigningAuthorizerName;
    private String mqtt5IoTCoreSigningAuthorizerUsername;
    private String mqtt5IoTCoreSigningAuthorizerPassword;
    private String mqtt5IoTCoreSigningAuthorizerToken;
    private String mqtt5IoTCoreSigningAuthorizerTokenKeyName;
    private String mqtt5IoTCoreSigningAuthorizerTokenSignature;

    private void populateTestingEnvironmentVariables() {
        mqtt5IoTCoreHost = System.getenv("AWS_TEST_MQTT5_IOT_CORE_HOST");

        mqtt5IoTCoreNoSigningAuthorizerName = System.getenv("AWS_TEST_MQTT5_IOT_CORE_NO_SIGNING_AUTHORIZER_NAME");
        mqtt5IoTCoreNoSigningAuthorizerUsername = System.getenv("AWS_TEST_MQTT5_IOT_CORE_NO_SIGNING_AUTHORIZER_USERNAME");
        mqtt5IoTCoreNoSigningAuthorizerPassword = System.getenv("AWS_TEST_MQTT5_IOT_CORE_NO_SIGNING_AUTHORIZER_PASSWORD");

        mqtt5IoTCoreSigningAuthorizerName = System.getenv("AWS_TEST_MQTT5_IOT_CORE_SIGNING_AUTHORIZER_NAME");
        mqtt5IoTCoreSigningAuthorizerUsername = System.getenv("AWS_TEST_MQTT5_IOT_CORE_SIGNING_AUTHORIZER_USERNAME");
        mqtt5IoTCoreSigningAuthorizerPassword = System.getenv("AWS_TEST_MQTT5_IOT_CORE_SIGNING_AUTHORIZER_PASSWORD");
        mqtt5IoTCoreSigningAuthorizerToken = System.getenv("AWS_TEST_MQTT5_IOT_CORE_SIGNING_AUTHORIZER_TOKEN");
        mqtt5IoTCoreSigningAuthorizerTokenKeyName = System.getenv("AWS_TEST_MQTT5_IOT_CORE_SIGNING_AUTHORIZER_TOKEN_KEY_NAME");
        mqtt5IoTCoreSigningAuthorizerTokenSignature = System.getenv("AWS_TEST_MQTT5_IOT_CORE_SIGNING_AUTHORIZER_TOKEN_SIGNATURE");
    }

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

        /* Custom Auth (no signing) connect - Websockets */
    // @Test
    // public void ConnIoT_CustomAuth_UC3()
    // {
    //     assumeTrue(mqtt5IoTCoreHost != null);
    //     assumeTrue(mqtt5IoTCoreNoSigningAuthorizerName != null);
    //     assumeTrue(mqtt5IoTCoreNoSigningAuthorizerUsername != null);
    //     assumeTrue(mqtt5IoTCoreNoSigningAuthorizerPassword != null);

    //     try {
    //         AwsIotMqttConnectionBuilder builder = AwsIotMqttConnectionBuilder.newDefaultBuilder();
    //         builder.withEndpoint(mqtt5IoTCoreHost);
    //         String clientId = "test-" + UUID.randomUUID().toString();
    //         builder.withClientId(clientId);
    //         builder.withCustomAuthorizer(
    //             mqtt5IoTCoreNoSigningAuthorizerUsername,
    //             mqtt5IoTCoreNoSigningAuthorizerName,
    //             null,
    //             mqtt5IoTCoreNoSigningAuthorizerPassword,
    //             null,
    //             null);
    //         builder.withWebsockets(true);
    //         MqttClientConnection connection = builder.build();
    //         builder.close();

    //         connection.connect().get();
    //         connection.disconnect().get();
    //         connection.close();

    //     } catch (Exception ex) {
    //         fail(ex);
    //     }
    // }

    // /* Custom Auth (with signing) connect - Websockets */
    // @Test
    // public void ConnIoT_CustomAuth_UC4()
    // {
    //     assumeTrue(mqtt5IoTCoreHost != null);
    //     assumeTrue(mqtt5IoTCoreSigningAuthorizerName != null);
    //     assumeTrue(mqtt5IoTCoreSigningAuthorizerUsername != null);
    //     assumeTrue(mqtt5IoTCoreSigningAuthorizerPassword != null);
    //     assumeTrue(mqtt5IoTCoreSigningAuthorizerToken != null);
    //     assumeTrue(mqtt5IoTCoreSigningAuthorizerTokenKeyName != null);
    //     assumeTrue(mqtt5IoTCoreSigningAuthorizerTokenSignature != null);

    //     try {
    //         AwsIotMqttConnectionBuilder builder = AwsIotMqttConnectionBuilder.newDefaultBuilder();
    //         builder.withEndpoint(mqtt5IoTCoreHost);
    //         String clientId = "test-" + UUID.randomUUID().toString();
    //         builder.withClientId(clientId);
    //         builder.withCustomAuthorizer(
    //             mqtt5IoTCoreSigningAuthorizerUsername,
    //             mqtt5IoTCoreSigningAuthorizerName,
    //             mqtt5IoTCoreSigningAuthorizerTokenSignature,
    //             mqtt5IoTCoreSigningAuthorizerPassword,
    //             mqtt5IoTCoreSigningAuthorizerTokenKeyName,
    //             mqtt5IoTCoreSigningAuthorizerToken);
    //         builder.withWebsockets(true);
    //         MqttClientConnection connection = builder.build();
    //         builder.close();

    //         connection.connect().get();
    //         connection.disconnect().get();
    //         connection.close();

    //     } catch (Exception ex) {
    //         fail(ex);
    //     }
    // }
}
