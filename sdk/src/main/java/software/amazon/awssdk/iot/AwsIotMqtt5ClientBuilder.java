/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */
package software.amazon.awssdk.iot;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import software.amazon.awssdk.crt.auth.credentials.CredentialsProvider;
import software.amazon.awssdk.crt.auth.credentials.DefaultChainCredentialsProvider;
import software.amazon.awssdk.crt.auth.signing.AwsSigningConfig;
import software.amazon.awssdk.crt.auth.signing.AwsSigningConfig.AwsSignatureType;
import software.amazon.awssdk.crt.auth.signing.AwsSigningConfig.AwsSigningAlgorithm;
import software.amazon.awssdk.crt.http.HttpProxyOptions;
import software.amazon.awssdk.crt.io.ClientBootstrap;
import software.amazon.awssdk.crt.io.SocketOptions;
import software.amazon.awssdk.crt.io.TlsContext;
import software.amazon.awssdk.crt.io.TlsContextCustomKeyOperationOptions;
import software.amazon.awssdk.crt.io.TlsContextOptions;
import software.amazon.awssdk.crt.io.TlsContextPkcs11Options;
import software.amazon.awssdk.crt.io.ExponentialBackoffRetryOptions.JitterMode;
import software.amazon.awssdk.crt.mqtt5.Mqtt5Client;
import software.amazon.awssdk.crt.mqtt5.Mqtt5ClientOptions;
import software.amazon.awssdk.crt.mqtt5.Mqtt5ClientOptions.Mqtt5ClientOptionsBuilder;
import software.amazon.awssdk.crt.mqtt5.packets.ConnectPacket.ConnectPacketBuilder;
import software.amazon.awssdk.crt.utils.PackageInfo;

/**
 * Builders for making MQTT5 clients with different connection methods for AWS IoT Core.
 *
 * !! Developer Preview !! - This class is currently in developer preview.
 * The interface is not guaranteed to be stable yet.
 * Please report any issues or make suggestions in https://github.com/aws/aws-iot-device-sdk-java-v2/issues
 */
public class AwsIotMqtt5ClientBuilder extends software.amazon.awssdk.crt.CrtResource {
    private static Long DEFAULT_WEBSOCKET_MQTT_PORT = 443L;
    private static Long DEFAULT_DIRECT_MQTT_PORT = 8883L;
    private static Long DEFAULT_KEEP_ALIVE = 1200L;

    private Mqtt5ClientOptionsBuilder config;
    private ConnectPacketBuilder configConnect;
    private TlsContextOptions configTls;
    private MqttConnectCustomAuthConfig configCustomAuth;

    private AwsIotMqtt5ClientBuilder(String hostName, Long port, TlsContextOptions tlsContext) {
        config = new Mqtt5ClientOptionsBuilder(hostName, port);
        configTls = tlsContext;
        configConnect = new ConnectPacketBuilder();
        configConnect.withKeepAliveIntervalSeconds(DEFAULT_KEEP_ALIVE);
        config.withExtendedValidationAndFlowControlOptions(Mqtt5ClientOptions.ExtendedValidationAndFlowControlOptions.AWS_IOT_CORE_DEFAULTS);
        addReferenceTo(configTls);
    }

    protected boolean canReleaseReferencesImmediately() {
        return true;
    }
    protected void releaseNativeHandle() {}

    /**
     * Creates a new MQTT5 client builder with mTLS file paths.
     *
     * @param hostName - AWS IoT endpoint to connect to
     * @param certificatePath - Path to certificate, in PEM format
     * @param privateKeyPath - Path to private key, in PEM format
     * @return - A new AwsIotMqtt5ClientBuilder
     */
    public static AwsIotMqtt5ClientBuilder newDirectMqttBuilderWithMtlsFromPath(String hostName, String certificatePath, String privateKeyPath) {
        TlsContextOptions options = TlsContextOptions.createWithMtlsFromPath(certificatePath, privateKeyPath);
        AwsIotMqtt5ClientBuilder builder = new AwsIotMqtt5ClientBuilder(hostName, DEFAULT_DIRECT_MQTT_PORT, options);
        options.close();
        if (TlsContextOptions.isAlpnSupported()) {
            builder.configTls.withAlpnList("x-amzn-mqtt-ca");
        }
        return builder;
    }

    /**
     * Creates a new MQTT5 client builder with mTLS cert pair in memory
     *
     * @param hostName - AWS IoT endpoint to connect to
     * @param certificate - Certificate, in PEM format
     * @param privateKey - Private key, in PEM format
     * @return - A new AwsIotMqtt5ClientBuilder
     */
    public static AwsIotMqtt5ClientBuilder newDirectMqttBuilderWithMtlsFromMemory(String hostName, String certificate, String privateKey) {
        TlsContextOptions options = TlsContextOptions.createWithMtls(certificate, privateKey);
        AwsIotMqtt5ClientBuilder builder = new AwsIotMqtt5ClientBuilder(hostName, DEFAULT_DIRECT_MQTT_PORT, options);
        options.close();
        if (TlsContextOptions.isAlpnSupported()) {
            builder.configTls.withAlpnList("x-amzn-mqtt-ca");
        }
        return builder;
    }

    /**
     * Creates a new MQTT5 client builder with mTLS using a PKCS#11 library for private key operations
     *
     * NOTE: This configuration only works on Unix devices.
     *
     * @param hostName - AWS IoT endpoint to connect to
     * @param pkcs11Options - PKCS#11 options
     * @return - A new AwsIotMqtt5ClientBuilder
     */
    public static AwsIotMqtt5ClientBuilder newDirectMqttBuilderWithMtlsFromPkcs11(String hostName, TlsContextPkcs11Options pkcs11Options) {
        TlsContextOptions options = TlsContextOptions.createWithMtlsPkcs11(pkcs11Options);
        AwsIotMqtt5ClientBuilder builder = new AwsIotMqtt5ClientBuilder(hostName, DEFAULT_DIRECT_MQTT_PORT, options);
        options.close();
        if (TlsContextOptions.isAlpnSupported()) {
            builder.configTls.withAlpnList("x-amzn-mqtt-ca");
        }
        return builder;
    }

     /**
      * Creates a new MQTT5 client builder with mTLS using a custom handler for private key operations
      *
      * NOTE: This configuration only works on Unix devices.
      *
      * @param hostName - AWS IoT endpoint to connect to
      * @param operationOptions - Options for using a custom handler
      * @return - A new AwsIotMqtt5ClientBuilder
      */
    public static AwsIotMqtt5ClientBuilder newDirectMtlsCustomKeyOperationsBuilder(String hostName, TlsContextCustomKeyOperationOptions operationOptions) {
        TlsContextOptions options = TlsContextOptions.createWithMtlsCustomKeyOperations(operationOptions);
        AwsIotMqtt5ClientBuilder builder = new AwsIotMqtt5ClientBuilder(hostName, DEFAULT_DIRECT_MQTT_PORT, options);
        options.close();
        if (TlsContextOptions.isAlpnSupported()) {
            builder.configTls.withAlpnList("x-amzn-mqtt-ca");
        }
        return builder;
    }

    /**
     * Creates a new MQTT5 client builder with mTLS using a certificate in a Windows certificate store.
     *
     * NOTE: This configuration only works on Windows devices.
     *
     * @param hostName - AWS IoT endpoint to connect to
     * @param certificatePath - Path to certificate in a Windows certificate store.
     *      The path must use backslashes and end with the certificate's thumbprint.
     *      Example: `CurrentUser\MY\A11F8A9B5DF5B98BA3508FBCA575D09570E0D2C6`
     * @return - A new AwsIotMqtt5ClientBuilder
     */
    public static AwsIotMqtt5ClientBuilder newDirectMqttBuilderWithMtlsFromWindowsCertStorePath(String hostName, String certificatePath) {
        TlsContextOptions options = TlsContextOptions.createWithMtlsWindowsCertStorePath(certificatePath);
        AwsIotMqtt5ClientBuilder builder = new AwsIotMqtt5ClientBuilder(hostName, DEFAULT_DIRECT_MQTT_PORT, options);
        options.close();
        if (TlsContextOptions.isAlpnSupported()) {
            builder.configTls.withAlpnList("x-amzn-mqtt-ca");
        }
        return builder;
    }

    /**
     * Creates a new MQTT5 client builder that will use direct MQTT and a custom authenticator controlled by the
     * username and password values.
     *
     * @param hostName - AWS IoT endpoint to connect to
     * @param customAuthConfig - AWS IoT custom auth configuration
     * @return
     */
    public static AwsIotMqtt5ClientBuilder newDirectMqttBuilderWithCustomAuth(String hostName, MqttConnectCustomAuthConfig customAuthConfig) {
        TlsContextOptions options = TlsContextOptions.createDefaultClient();
        options.alpnList.clear();
        options.alpnList.add("mqtt");

        AwsIotMqtt5ClientBuilder builder = new AwsIotMqtt5ClientBuilder(hostName, DEFAULT_WEBSOCKET_MQTT_PORT, options);
        builder.configCustomAuth = customAuthConfig;
        options.close();

        return builder;
    }

    /**
     * Create a new MQTT5 client builder that will use websockets and AWS Sigv4 signing to establish
     * mutually-authenticated (mTLS) connections.
     *
     * @param hostName - AWS IoT endpoint to connect to
     * @param config - Additional Sigv4-oriented options to use
     * @return - A new AwsIotMqtt5ClientBuilder
     */
    public static AwsIotMqtt5ClientBuilder newWebsocketMqttBuilderWithSigv4Auth(String hostName, WebsocketSigv4Config config) {
        TlsContextOptions options = TlsContextOptions.createDefaultClient();
        options.alpnList.clear();

        AwsIotMqtt5ClientBuilder builder = new AwsIotMqtt5ClientBuilder(hostName, DEFAULT_WEBSOCKET_MQTT_PORT, options);
        options.close();

        CredentialsProvider provider = null;
        if (config != null) {
            provider = config.credentialsProvider;
        }

        try (AwsSigningConfig signingConfig = new AwsSigningConfig()) {
            signingConfig.setAlgorithm(AwsSigningAlgorithm.SIGV4);
            signingConfig.setSignatureType(AwsSignatureType.HTTP_REQUEST_VIA_QUERY_PARAMS);

            if (provider != null) {
                signingConfig.setCredentialsProvider(provider);
            } else {
                DefaultChainCredentialsProvider.DefaultChainCredentialsProviderBuilder providerBuilder = new DefaultChainCredentialsProvider.DefaultChainCredentialsProviderBuilder();
                providerBuilder.withClientBootstrap(ClientBootstrap.getOrCreateStaticDefault());
                try (CredentialsProvider defaultProvider = providerBuilder.build()) {
                    signingConfig.setCredentialsProvider(defaultProvider);
                }
            }

            if (config != null) {
                if (config.region != null) {
                    signingConfig.setRegion(config.region);
                } else {
                    signingConfig.setRegion(extractRegionFromEndpoint(hostName));
                }
            } else {
                signingConfig.setRegion(extractRegionFromEndpoint(hostName));
            }
            signingConfig.setService("iotdevicegateway");
            signingConfig.setOmitSessionToken(true);
            // Needs to stay alive as long as the MQTT5 client, which we can allow by pinning
            // the resource to the signingConfig
            options.addReferenceTo(signingConfig);

            try (AwsMqtt5Sigv4HandshakeTransformer transformer = new AwsMqtt5Sigv4HandshakeTransformer(signingConfig)) {
                builder.config.withWebsocketHandshakeTransform(transformer);
                // Needs to stay alive as long as the MQTT5 client, which we can allow by pinning
                // the resource to the signingConfig
                options.addReferenceTo(transformer);
            }

        } catch (Exception ex) {
            System.out.println("Error - exception occurred while making Websocket Sigv4 builder: " + ex.toString());
            ex.printStackTrace();
            return null;
        }

        return builder;
    }

    /**
     * Creates a new MQTT5 client builder with default TLS options. This requires setting all connection details manually.
     * Default port to direct MQTT.
     *
     * @param hostName - AWS IoT endpoint to connect to
     * @return - A new AwsIotMqtt5ClientBuilder
     */
    public static AwsIotMqtt5ClientBuilder newMqttBuilder(String hostName) {
        TlsContextOptions options = TlsContextOptions.createDefaultClient();
        AwsIotMqtt5ClientBuilder builder = new AwsIotMqtt5ClientBuilder(hostName, DEFAULT_DIRECT_MQTT_PORT, options);
        options.close();
        if (TlsContextOptions.isAlpnSupported()) {
            builder.configTls.withAlpnList("x-amzn-mqtt-ca");
        }
        return builder;
    }

    /* Instance methods for various config overrides */

    /**
     * Overrides the default system trust store.
     *
     * @param caDirPath - Only used on Unix-style systems where all trust anchors are
     * stored in a directory (e.g. /etc/ssl/certs).
     * @param caFilePath - Single file containing all trust CAs, in PEM format.
     * @return - The AwsIotMqtt5ClientBuilder
     */
    public AwsIotMqtt5ClientBuilder withCertificateAuthorityFromPath(String caDirPath, String caFilePath) {
        this.configTls.overrideDefaultTrustStoreFromPath(caDirPath, caFilePath);
        return this;
    }

    /**
     * Overrides the default trust store.
     *
     * @param caRoot - Buffer containing all trust CAs, in PEM format.
     * @return - The AwsIotMqtt5ClientBuilder
     */
    public AwsIotMqtt5ClientBuilder withCertificateAuthority(String caRoot) {
        this.configTls.overrideDefaultTrustStore(caRoot);
        return this;
    }

    /**
     * Overrides the port to connect to on the IoT endpoint
     *
     * @param port - The port to connect to on the IoT endpoint. Usually 8883 for MQTT, or 443 for websockets
     * @return - The AwsIotMqtt5ClientBuilder
     */
    public AwsIotMqtt5ClientBuilder withPort(Long port) {
        this.config.withPort(port);
        return this;
    }

    /**
     * Overrides all configurable options with respect to the CONNECT packet sent by the client, including the will.
     * These connect properties will be used for every connection attempt made by the client. Custom authentication
     * configuration will override the username and password values in this configuration.
     *
     * @param connectPacket - All configurable options with respect to the CONNECT packet sent by the client
     * @return - The AwsIotMqtt5ClientBuilder
     */
    public AwsIotMqtt5ClientBuilder withConnectProperties(ConnectPacketBuilder connectPacket) {
        this.configConnect = connectPacket;
        return this;
    }

    /**
     * Overrides how the MQTT5 client should behave with respect to MQTT sessions.
     *
     * @param sessionBehavior - How the MQTT5 client should behave with respect to MQTT sessions.
     * @return - The AwsIotMqtt5ClientBuilder
     */
    public AwsIotMqtt5ClientBuilder withSessionBehavior(Mqtt5ClientOptions.ClientSessionBehavior sessionBehavior) {
        this.config.withSessionBehavior(sessionBehavior);
        return this;
    }

    /**
     * Overrides how the reconnect delay is modified in order to smooth out the distribution of reconnect attempt
     * time points for a large set of reconnecting clients.
     *
     * @param jitterMode - Controls how the reconnect delay is modified in order to smooth out the distribution
     * of reconnect attempt time points for a large set of reconnecting clients.
     * @return - The AwsIotMqtt5ClientBuilder
     */
    public AwsIotMqtt5ClientBuilder withRetryJitterMode(JitterMode jitterMode) {
        this.config.withRetryJitterMode(jitterMode);
        return this;
    }

    /**
     * Overrides the minimum amount of time to wait to reconnect after a disconnect. Exponential back-off is
     * performed with controllable jitter after each connection failure.
     *
     * @param minReconnectDelayMs - Minimum amount of time to wait to reconnect after a disconnect.
     * @return - The AwsIotMqtt5ClientBuilder
     */
    public AwsIotMqtt5ClientBuilder withMinReconnectDelayMs(Long minReconnectDelayMs) {
        this.config.withMinReconnectDelayMs(minReconnectDelayMs);
        return this;
    }

    /**
     * Overrides the maximum amount of time to wait to reconnect after a disconnect. Exponential back-off is
     * performed with controllable jitter after each connection failure.
     *
     * @param maxReconnectDelayMs - Maximum amount of time to wait to reconnect after a disconnect.
     * @return - The AwsIotMqtt5ClientBuilder
     */
    public AwsIotMqtt5ClientBuilder withMaxReconnectDelayMs(Long maxReconnectDelayMs) {
        this.config.withMinReconnectDelayMs(maxReconnectDelayMs);
        return this;
    }

    /**
     * Overrides the amount of time that must elapse with an established connection before the reconnect delay is
     * reset to the minimum.  This helps alleviate bandwidth-waste in fast reconnect cycles due to permission
     * failures on operations.
     *
     * @param minConnectedTimeToResetReconnectDelayMs - The amount of time that must elapse with an established
     * connection before the reconnect delay is reset to the minimum.
     * @return - The AwsIotMqtt5ClientBuilder
     */
    public AwsIotMqtt5ClientBuilder withMinConnectedTimeToResetReconnectDelayMs(Long minConnectedTimeToResetReconnectDelayMs) {
        this.config.withMinConnectedTimeToResetReconnectDelayMs(minConnectedTimeToResetReconnectDelayMs);
        return this;
    }

    /**
     * Overrides the time interval to wait after sending a CONNECT request for a CONNACK to arrive.  If one does not
     * arrive, the connection will be shut down.
     *
     * @param connackTimeoutMs - The time interval to wait after sending a CONNECT request for a CONNACK to arrive.
     * @return - The AwsIotMqtt5ClientBuilder
     */
    public AwsIotMqtt5ClientBuilder withConnackTimeoutMs(Long connackTimeoutMs) {
        this.config.withConnackTimeoutMs(connackTimeoutMs);
        return this;
    }

    /**
     * Overrides how disconnects affect the queued and in-progress operations tracked by the client.  Also controls
     * how new operations are handled while the client is not connected.  In particular, if the client is not connected,
     * then any operation that would be failed on disconnect (according to these rules) will also be rejected.
     *
     * @param offlineQueueBehavior - How disconnects affect the queued and in-progress operations tracked by the client.
     * @return - The AwsIotMqtt5ClientBuilder
     */
    public AwsIotMqtt5ClientBuilder withOfflineQueueBehavior(Mqtt5ClientOptions.ClientOfflineQueueBehavior offlineQueueBehavior) {
        this.config.withOfflineQueueBehavior(offlineQueueBehavior);
        return this;
    }

    /**
     * Overrides the time interval to wait after sending a PINGREQ for a PINGRESP to arrive.  If one does not arrive,
     * the client will close the current connection.
     *
     * @param pingTimeoutMs - The time interval to wait after sending a PINGREQ for a PINGRESP to arrive.
     * @return - The AwsIotMqtt5ClientBuilder
     */
    public AwsIotMqtt5ClientBuilder withPingTimeoutMs(Long pingTimeoutMs) {
        this.config.withPingTimeoutMs(pingTimeoutMs);
        return this;
    }

    /**
     * Overrides the time interval to wait for an ack after sending a QoS 1+ PUBLISH, SUBSCRIBE, or UNSUBSCRIBE before
     * failing the operation.  Defaults to no timeout.
     *
     * @param operationTimeoutSeconds - the time interval to wait for an ack after sending a QoS 1+ PUBLISH, SUBSCRIBE,
     * or UNSUBSCRIBE before failing the operation
     * @return - The AwsIotMqtt5ClientBuilder
     */
    public AwsIotMqtt5ClientBuilder withOperationTimeoutSeconds(Long operationTimeoutSeconds) {
        this.config.withOperationTimeoutSeconds(operationTimeoutSeconds);
        return this;
    }

    /**
     * Overrides the socket properties of the underlying MQTT connections made by the client.  Leave undefined to use
     * defaults (no TCP keep alive, 10 second socket timeout).
     *
     * @param socketOptions - The socket properties of the underlying MQTT connections made by the client
     * @return - The AwsIotMqtt5ClientBuilder
     */
    public AwsIotMqtt5ClientBuilder withSocketOptions(SocketOptions socketOptions) {
        this.config.withSocketOptions(socketOptions);
        return this;
    }

    /**
     * Overrides (tunneling) HTTP proxy usage when establishing MQTT connections.
     *
     * @param httpProxyOptions - HTTP proxy options to use when establishing MQTT connections.
     * @return - The AwsIotMqtt5ClientBuilder
     */
    public AwsIotMqtt5ClientBuilder withHttpProxyOptions(HttpProxyOptions httpProxyOptions) {
        this.config.withHttpProxyOptions(httpProxyOptions);
        return this;
    }

    /**
     * Overrides additional controls for client behavior with respect to operation validation and flow control; these
     * checks go beyond the base MQTT5 spec to respect limits of specific MQTT brokers.
     *
     * @param extendedValidationAndFlowControlOptions - additional controls for client behavior with respect to operation
     * validation and flow control
     * @return - The AwsIotMqtt5ClientBuilder
     */
    public AwsIotMqtt5ClientBuilder withExtendedValidationAndFlowControlOptions(Mqtt5ClientOptions.ExtendedValidationAndFlowControlOptions extendedValidationAndFlowControlOptions) {
        this.config.withExtendedValidationAndFlowControlOptions(extendedValidationAndFlowControlOptions);
        return this;
    }

    /**
     * Sets the LifeCycleEvents that will be called by the client when receives a life cycle events. Examples of
     * life cycle events are: Connection success, connection failure, disconnection, etc.
     *
     * @param lifecycleEvents - The LifeCycleEvents to be called
     * @return - The AwsIotMqtt5ClientBuilder
     */
    public AwsIotMqtt5ClientBuilder withLifeCycleEvents(Mqtt5ClientOptions.LifecycleEvents lifecycleEvents) {
        this.config.withLifecycleEvents(lifecycleEvents);
        return this;
    }

    /**
     * Sets the PublishEvents that will be called by the client when it receives a publish packet.
     *
     * @param publishEvents The PublishEvents to be called
     * @return - The AwsIotMqtt5ClientBuilder
     */
    public AwsIotMqtt5ClientBuilder withPublishEvents(Mqtt5ClientOptions.PublishEvents publishEvents) {
        this.config.withPublishEvents(publishEvents);
        return this;
    }

    /**
     * Constructs an MQTT5 client object configured with the options set.
     * @return A MQTT5ClientOptions
     */
    public Mqtt5Client build() {
        if (this.configTls == null) {
            this.configTls = TlsContextOptions.createDefaultClient();
            addReferenceTo(this.configTls);
            this.configTls.close();
        }
        TlsContext tlsContext = new TlsContext(this.configTls);
        this.config.withTlsContext(tlsContext);
        addReferenceTo(tlsContext);
        tlsContext.close();

        try {
            this.configConnect.withUsername(buildMqtt5FinalUsername(this.configCustomAuth));
            if (this.configCustomAuth != null) {
                if (this.configCustomAuth.password != null) {
                    this.configConnect.withPassword(this.configCustomAuth.password);
                }
            }
        } catch (Exception ex) {
            System.out.println("Error - exception occurred while building MQTT5 client options builder: " + ex.toString());
            ex.printStackTrace();
            return null;
        }

        this.config.withConnectOptions(this.configConnect.build());

        Mqtt5Client returnClient = new Mqtt5Client(this.config.build());

        // Keep a reference to the TLS configuration so any possible Websockets-related CrtResources are kept alive
        returnClient.addReferenceTo(this.configTls);
        return returnClient;
    }

    /* Helper functions and structs */

    /**
     * Websocket-specific MQTT5 connection AWS IoT configuration options
     */
    public static final class WebsocketSigv4Config {
        /**
         * Sources the AWS Credentials used to sign the websocket connection handshake. If not provided,
         * the default credentials provider chain is used.
         */
        public CredentialsProvider credentialsProvider;

        /**
         * The AWS region the websocket connection is being established in. Must match the region embedded in the
         * endpoint. If not provided, pattern-matching logic is used to extract the region from the endpoint.
         * Use this option if the pattern-matching logic has not yet been updated to handle new endpoint formats.
         */
        public String region;
    }

    /**
     * Attempts to determine the AWS region associated with an endpoint.
     * Will throw an exception if it cannot find the region.
     *
     * @param endpoint - The endpoint to compute the region for.
     * @return The region associated with the endpoint.
     */
    public static String extractRegionFromEndpoint(String endpoint) throws Exception {
        Pattern regexPattern = Pattern.compile("^[\\w\\-]+\\.[\\w\\-]+\\.([\\w+\\-]+)\\.");
        Matcher regexMatcher = regexPattern.matcher(endpoint);
        try {
            if (regexMatcher.find()) {
                String result = regexMatcher.group(1);
                if (result != null) {
                    return result;
                }
            }
        } catch (Exception ex) {
            throw new Exception("AWS region could not be extracted from endpoint. Use 'region' property on WebsocketConfig to set manually.");
        }
        throw new Exception("AWS region could not be extracted from endpoint. Use 'region' property on WebsocketConfig to set manually.");
    }

    /**
     * Configuration options specific to
     * <a href="https://docs.aws.amazon.com/iot/latest/developerguide/custom-authentication.html">AWS IoT Core custom authentication</a>
     * features.  For clients constructed by an AwsIotMqtt5ClientBuilder, all parameters associated
     * with AWS IoT custom authentication are passed via the username and password properties in the CONNECT packet.
     */
    public static final class MqttConnectCustomAuthConfig {

        /**
         * Name of the custom authorizer to use.
         *
         * Required if the endpoint does not have a default custom authorizer associated with it.
         * It is strongly suggested to URL-encode this value; the SDK will not do so for you.
         */
        public String authorizerName;

        /**
         * The username to use with the custom authorizer. Query-string elements of this property value will be unioned
         * with the query-string elements implied by other properties in this object.
         *
         * For example, if you set this to:
         *
         * 'MyUsername?someKey=someValue'
         *
         * and use authorizerName to specify the authorizer, the final username would look like:
         *
         * `MyUsername?someKey=someValue&x-amz-customauthorizer-name=<your authorizer's name>&...`
         */
        public String username;

        /**
         * The password to use with the custom authorizer. Becomes the MQTT5 CONNECT packet's password property.
         * AWS IoT Core will base64 encode this binary data before passing it to the authorizer's lambda function.
         */
        public byte[] password;

        /**
         * Key used to extract the custom authorizer token from MQTT username query-string properties.
         *
         * Required if the custom authorizer has signing enabled. It is strongly suggested to URL-encode this value; the
         * SDK will not do so for you.
         */
        public String tokenKeyName;

        /**
         * An opaque token value. This value must be signed by the private key associated with the custom authorizer and
         * the result placed in the tokenSignature property.
         *
         * Required if the custom authorizer has signing enabled.
         */
        public String tokenValue;

        /**
         * The digital signature of the token value in the tokenValue property. The signature must be based on
         * the private key associated with the custom authorizer.  The signature must be base64 encoded.
         *
         * Required if the custom authorizer has signing enabled.  It is strongly suggested to URL-encode this value; the
         * SDK will not do so for you.
         */
        public String tokenSignature;
    }

    /**
     * Adds a username parameter to the given list. Will only add to the list if the paramValue is not null.
     * Always adds both values in pair. Set the key to null if you need to only add a single value.
     *
     * @param paramList The parameter list to use
     * @param paramName The new parameter name
     * @param paramValue The new parameter value
     */
    private void addToUsernameParam(List<String> paramList, String paramName, String paramValue) {
        if (paramValue != null) {
            paramList.add(paramName);
            paramList.add(paramValue);
        }
    }

    /**
     * Takes a list of strings and returns a formatted username. Will correctly handle adding
     * `?` or `&` to append the strings together.
     *
     * Note: The paramList is expected to have either zero elements or an even amount. Will throw if uneven.
     * Will correctly handle if the parameter name is null but the parameter value is not null.
     *
     * @param paramList The parameter list to use for creating the username.
     * @return A string formatted from the parameter list.
     */
    private String formUsernameFromParam(List<String> paramList) throws Exception {
        boolean firstAddition = true;
        boolean useAmp = false;
        String result = "";

        // If there are no params, end early
        if (paramList.size() == 0) {
            return result;
        }

        // We only allow pairs, so make sure it is even
        if (paramList.size() % 2 != 0) {
            throw new Exception("Username parameters are not an even number!");
        }

        for (int i = 0; i < paramList.size(); i++) {
            String key = paramList.get(i);
            String value = paramList.get(i+1);

            if (firstAddition == true) {
                firstAddition = false;
            } else {
                if (useAmp == false) {
                    result += "?";
                    useAmp = true;
                } else {
                    result += "&";
                }
            }

            if (key != null && value != null) {
                result += key + "=" + value;
            } else if (value != null) {
                // Needed for the initial username and other value-only items
                result += value;
            }

            i = i+1;
        }
        return result;
    }

    /**
     * Builds the final value for the CONNECT packet's username property based on AWS IoT custom auth configuration
     * and SDK metrics properties.
     *
     * @param config - The intended AWS IoT custom auth client configuration (optional - leave null if not used)
     * @return The final username string
     */
    private String buildMqtt5FinalUsername(MqttConnectCustomAuthConfig config) throws Exception {
        ArrayList<String> paramList = new ArrayList<String>();

        if (config != null) {
            boolean usingSigning = false;
            if (config.tokenValue != null || config.tokenKeyName != null || config.tokenSignature != null) {
                usingSigning = true;
                if (config.tokenValue == null || config.tokenKeyName == null || config.tokenSignature == null) {
                    throw new Exception("Token-based custom authentication requires all token-related properties to be set");
                }
            }

            String username = config.username;
            if (username != null) {
                if (username.contains("?")) {
                    // split and process
                    String[] questionSplit = username.split("?");
                    if (questionSplit.length > 1) {
                        throw new Exception("Custom auth username property value is invalid");
                    }
                    else {
                        // Add the username:
                        addToUsernameParam(paramList, null, questionSplit[0]);

                        // Is there multiple key-value pairs or just one? If multiple, split on the &
                        if (questionSplit[1].contains("&")) {
                            String[] ampSplit = questionSplit[1].split("&");
                            for (int i = 0; i < ampSplit.length; i++) {
                                // We only want pairs
                                String[] keyValueSplit = ampSplit[i].split("=");
                                if (keyValueSplit.length == 1) {
                                    addToUsernameParam(paramList, keyValueSplit[0], keyValueSplit[1]);
                                }
                            }
                        } else {
                            // We only want pairs
                            String[] keyValueSplit = questionSplit[1].split("=");
                            if (keyValueSplit.length == 1) {
                                addToUsernameParam(paramList, keyValueSplit[0], keyValueSplit[1]);
                            }
                        }
                    }

                } else {
                    addToUsernameParam(paramList, null, username);
                }
            }

            addToUsernameParam(paramList, "x-amz-customauthorizer-name", config.authorizerName);
            if (usingSigning == true) {
                addToUsernameParam(paramList, config.tokenKeyName, config.tokenValue);
                addToUsernameParam(paramList, "x-amz-customauthorizer-signature", config.tokenSignature);
            }
        }

        addToUsernameParam(paramList, "SDK", "JavaV2");
        addToUsernameParam(paramList, "Version", new PackageInfo().version.toString());

        return formUsernameFromParam(paramList);
    }
}
