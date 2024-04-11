
/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */
package software.amazon.awssdk.iot;

import software.amazon.awssdk.crt.utils.PackageInfo;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.CrtResource;
import software.amazon.awssdk.crt.CrtRuntimeException;
import software.amazon.awssdk.crt.Log;
import software.amazon.awssdk.crt.Log.LogLevel;
import software.amazon.awssdk.crt.Log.LogSubject;
import software.amazon.awssdk.crt.auth.credentials.CredentialsProvider;
import software.amazon.awssdk.crt.auth.credentials.DefaultChainCredentialsProvider;
import software.amazon.awssdk.crt.auth.signing.AwsSigningConfig;
import software.amazon.awssdk.crt.http.HttpProxyOptions;
import software.amazon.awssdk.crt.io.ClientBootstrap;
import software.amazon.awssdk.crt.io.ClientTlsContext;
import software.amazon.awssdk.crt.io.SocketOptions;
import software.amazon.awssdk.crt.io.TlsContextCustomKeyOperationOptions;
import software.amazon.awssdk.crt.io.TlsContextOptions;
import software.amazon.awssdk.crt.io.TlsContextPkcs11Options;
import software.amazon.awssdk.crt.mqtt.MqttClient;
import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt.MqttClientConnectionEvents;
import software.amazon.awssdk.crt.mqtt.MqttConnectionConfig;
import software.amazon.awssdk.crt.mqtt.MqttException;
import software.amazon.awssdk.crt.mqtt.MqttMessage;
import software.amazon.awssdk.crt.mqtt.QualityOfService;
import software.amazon.awssdk.crt.mqtt.WebsocketHandshakeTransformArgs;

/**
 * A central class for building Mqtt connections without manually managing a large variety of native objects (some
 * still need to be created though).
 */
public final class AwsIotMqttConnectionBuilder extends CrtResource {

    private static String IOT_SIGNING_SERVICE = "iotdevicegateway";
    private static String AMZ_DATE_HEADER = "x-amz-date";
    private static String AMZ_SECURITY_TOKEN_HEADER = "x-amz-security-token";

    MqttConnectionConfig config;

    /* Internal config and state */
    private MqttClient client; // Lazy create, cached
    private CredentialsProvider websocketCredentialsProvider;
    private String websocketSigningRegion;
    private ClientTlsContext tlsContext;  // Lazy create, cached
    private TlsContextOptions tlsOptions;
    private ClientBootstrap bootstrap;

    private boolean resetLazilyCreatedResources = true;
    // Used to detect if we need to set the ALPN list for custom authorizer
    private boolean isUsingCustomAuthorizer = false;

    private void resetDefaultPort() {
        if (TlsContextOptions.isAlpnSupported()) {
            this.tlsOptions.withAlpnList("x-amzn-mqtt-ca");
            this.config.setPort(443);
        } else {
            this.config.setPort(8883);
        }
    }

    private AwsIotMqttConnectionBuilder(TlsContextOptions tlsOptions) {
        try (MqttConnectionConfig connectionConfig = new MqttConnectionConfig()) {
            addReferenceTo(connectionConfig);
            config = connectionConfig;
        }

        this.tlsOptions = tlsOptions;
        addReferenceTo(tlsOptions);

        resetDefaultPort();
    }

    /**
     * Required override method that must begin the release process of the acquired native handle
     */
    @Override
    protected void releaseNativeHandle() {}

    /**
     * Override that determines whether a resource releases its dependencies at the same time the native handle is released or if it waits.
     * Resources with asynchronous shutdown processes should override this with false, and establish a callback from native code that
     * invokes releaseReferences() when the asynchronous shutdown process has completed.  See HttpClientConnectionManager for an example.
     */
    @Override
    protected boolean canReleaseReferencesImmediately() { return true; }


    /**
     * Create a new builder with mTLS file paths
     *
     * @param certPath       - Path to certificate, in PEM format
     * @param privateKeyPath - Path to private key, in PEM format
     * @return {@link AwsIotMqttConnectionBuilder}
     */
    public static AwsIotMqttConnectionBuilder newMtlsBuilderFromPath(String certPath, String privateKeyPath) {
        try (TlsContextOptions tlsContextOptions = TlsContextOptions.createWithMtlsFromPath(certPath, privateKeyPath)) {
            return new AwsIotMqttConnectionBuilder(tlsContextOptions);
        }
    }

    /**
     * Create a new builder with mTLS cert pair in memory
     *
     * @param certificate - Certificate, in PEM format
     * @param privateKey  - Private key, in PEM format
     * @return {@link AwsIotMqttConnectionBuilder}
     */
    public static AwsIotMqttConnectionBuilder newMtlsBuilder(String certificate, String privateKey) {
        try (TlsContextOptions tlsContextOptions = TlsContextOptions.createWithMtls(certificate, privateKey)) {
            return new AwsIotMqttConnectionBuilder(tlsContextOptions);
        }
    }

    /**
     * Create a new builder with mTLS cert pair in memory
     *
     * @param certificate - Certificate, in PEM format
     * @param privateKey  - Private key, in PEM format
     * @return {@link AwsIotMqttConnectionBuilder}
     * @throws UnsupportedEncodingException if encoding is unsupported
     */
    public static AwsIotMqttConnectionBuilder newMtlsBuilder(byte[] certificate, byte[] privateKey)
            throws UnsupportedEncodingException {
        return newMtlsBuilder(new String(certificate, "UTF8"), new String(privateKey, "UTF8"));
    }

    /**
     * Create a new builder with mTLS, using a PKCS#11 library for private key operations.
     *
     * NOTE: Unix only
     *
     * @param pkcs11Options PKCS#11 options
     * @return {@link AwsIotMqttConnectionBuilder}
     */
    public static AwsIotMqttConnectionBuilder newMtlsPkcs11Builder(TlsContextPkcs11Options pkcs11Options) {
        try (TlsContextOptions tlsContextOptions = TlsContextOptions.createWithMtlsPkcs11(pkcs11Options)) {
            return new AwsIotMqttConnectionBuilder(tlsContextOptions);
        }
    }

    /**
     * Create a new builder with mTLS, using a custom handler for private key operations.
     *
     * @param operationOptions options for using a custom handler
     * @return {@link AwsIotMqttConnectionBuilder}
     */
    public static AwsIotMqttConnectionBuilder newMtlsCustomKeyOperationsBuilder(TlsContextCustomKeyOperationOptions operationOptions) {
        try (TlsContextOptions tlsContextOptions = TlsContextOptions.createWithMtlsCustomKeyOperations(operationOptions)) {
            return new AwsIotMqttConnectionBuilder(tlsContextOptions);
        }
    }

    /**
     * Create a new builder with mTLS, using a certificate in a Windows certificate store.
     *
     * NOTE: Windows only
     *
     * @param certificatePath Path to certificate in a Windows certificate store.
     *                        The path must use backslashes and end with the
     *                        certificate's thumbprint. Example:
     *                        {@code CurrentUser\MY\A11F8A9B5DF5B98BA3508FBCA575D09570E0D2C6}
     * @return {@link AwsIotMqttConnectionBuilder}
     */
    public static AwsIotMqttConnectionBuilder newMtlsWindowsCertStorePathBuilder(String certificatePath) {
        try (TlsContextOptions tlsContextOptions = TlsContextOptions
                .createWithMtlsWindowsCertStorePath(certificatePath)) {
            return new AwsIotMqttConnectionBuilder(tlsContextOptions);
        }
    }

    /**
     * Create a new builder with mTLS, using a certificate and key stored in the passed-in Java keystore.
     *
     * Note: function assumes the passed keystore has already been loaded from a file by calling "keystore.load(file, password)"
     *
     * @param keyStore The Java keystore to use. Assumed to be loaded with certificates and keys
     * @param certificateAlias The alias of the certificate and key to use with the builder.
     * @param certificatePassword The password of the certificate and key to use with the builder.
     * @throws CrtRuntimeException if an error occurs, like the keystore cannot be opened or the certificate is not found.
     * @return {@link AwsIotMqttConnectionBuilder}
     */
    public static AwsIotMqttConnectionBuilder newJavaKeystoreBuilder(
        java.security.KeyStore keyStore, String certificateAlias, String certificatePassword) throws CrtRuntimeException {
        try (TlsContextOptions tlsContextOptions = TlsContextOptions
                .createWithMtlsJavaKeystore(keyStore, certificateAlias, certificatePassword)) {
            return new AwsIotMqttConnectionBuilder(tlsContextOptions);
        }
    }

    /**
     * Create a new builder with mTLS, using a PKCS12 library for private key operations.
     *
     * NOTE: MacOS only
     *
     * @param pkcs12Path Path to the PKCS12 file to use with the builder.
     * @param pkcs12Password The password of the PKCS12 file to use with the builder.
     * @return {@link AwsIotMqttConnectionBuilder}
     */
    public static AwsIotMqttConnectionBuilder newMtlsPkcs12Builder(
            String pkcs12Path, String pkcs12Password) {
        try (TlsContextOptions tlsContextOptions = TlsContextOptions.createWithMtlsPkcs12(pkcs12Path, pkcs12Password)) {
            return new AwsIotMqttConnectionBuilder(tlsContextOptions);
        }
    }

    /**
     * Create a new builder with no default Tls options
     *
     * @return a new builder with default Tls options
     * @throws UnsupportedEncodingException if encoding is unsupported
     */
    public static AwsIotMqttConnectionBuilder newDefaultBuilder()
            throws UnsupportedEncodingException {
        try (TlsContextOptions tlsContextOptions = TlsContextOptions.createDefaultClient()) {
            return new AwsIotMqttConnectionBuilder(tlsContextOptions);
        }
    }

    /**
     * Overrides the default system trust store.
     *
     * @param caDirPath  - Only used on Unix-style systems where all trust anchors
     *                    are stored in a directory (e.g. /etc/ssl/certs).
     * @param caFilePath - Single file containing all trust CAs, in PEM format
     * @return {@link AwsIotMqttConnectionBuilder}
     */
    public AwsIotMqttConnectionBuilder withCertificateAuthorityFromPath(String caDirPath, String caFilePath) {
        this.tlsOptions.overrideDefaultTrustStoreFromPath(caDirPath, caFilePath);
        resetLazilyCreatedResources = true;
        return this;
    }

    /**
     * Overrides the default system trust store.
     *
     * @param caRoot - Buffer containing all trust CAs, in PEM format
     * @return {@link AwsIotMqttConnectionBuilder}
     */
    public AwsIotMqttConnectionBuilder withCertificateAuthority(String caRoot) {
        this.tlsOptions.overrideDefaultTrustStore(caRoot);
        resetLazilyCreatedResources = true;
        return this;
    }

    /**
     * Configures the IoT endpoint for connections from this builder.
     *
     * @param endpoint The IoT endpoint to connect to
     * @return {@link AwsIotMqttConnectionBuilder}
     */
    public AwsIotMqttConnectionBuilder withEndpoint(String endpoint) {
        this.config.setEndpoint(endpoint);
        return this;
    }

    /**
     * Configures the port to connect to for connections from this builder.  If not set, 443 will be used for
     * a websocket connection or where ALPN support is available.  Otherwise the default is 8883.
     *
     * @param port The port to connect to on the IoT endpoint. Usually 8883 for
     *             MQTT, or 443 for websockets
     * @return {@link AwsIotMqttConnectionBuilder}
     */
    public AwsIotMqttConnectionBuilder withPort(int port) {
        this.config.setPort(port);
        return this;
    }

    /**
     * Configures the client id to use to connect to the IoT Core service.
     *
     * @param clientId The client id for connections from this builder. Needs to be unique across
     *                  all devices/clients.
     * @return {@link AwsIotMqttConnectionBuilder}
     */
    public AwsIotMqttConnectionBuilder withClientId(String clientId) {
        this.config.setClientId(clientId);
        return this;
    }

    /**
     * Configures whether or not the service should try to resume prior
     * subscriptions, if it has any
     *
     * @param cleanSession true if the session should drop prior subscriptions when
     *                     a connection from this builder is established, false to resume the session
     * @return {@link AwsIotMqttConnectionBuilder}
     */
    public AwsIotMqttConnectionBuilder withCleanSession(boolean cleanSession) {
        this.config.setCleanSession(cleanSession);
        return this;
    }

    /**
     * @deprecated Configures MQTT keep-alive via PING messages. Note that this is not TCP
     * keepalive.
     *
     * @param keepAliveMs How often in milliseconds to send an MQTT PING message to the
     *                   service to keep connections alive
     * @return {@link AwsIotMqttConnectionBuilder}
     */
    @Deprecated
    public AwsIotMqttConnectionBuilder withKeepAliveMs(int keepAliveMs) {
        this.config.setKeepAliveSecs(keepAliveMs/1000);
        return this;
    }

    /**
     * Configures MQTT keep-alive via PING messages. Note that this is not TCP
     * keepalive.
     *
     * @param keepAliveSecs How often in seconds to send an MQTT PING message to the
     *                   service to keep connections alive
     * @return {@link AwsIotMqttConnectionBuilder}
     */
    public AwsIotMqttConnectionBuilder withKeepAliveSecs(int keepAliveSecs) {
        this.config.setKeepAliveSecs(keepAliveSecs);
        return this;
    }

    /**
     * Controls ping timeout value.  If a response is not received within this
     * interval, the connection will be reestablished.
     *
     * @param pingTimeoutMs How long to wait for a ping response before resetting a connection built from this
     *                        builder.
     * @return {@link AwsIotMqttConnectionBuilder}
     */
    public AwsIotMqttConnectionBuilder withPingTimeoutMs(int pingTimeoutMs) {
        this.config.setPingTimeoutMs(pingTimeoutMs);
        return this;
    }

    /**
     * Controls timeout value for requests that response is required on healthy connection.
     * If a response is not received within this interval, the request will fail as server not receiving it.
     * Applied to publish (QoS greater than 0) and unsubscribe
     *
     * @param protocolOperationTimeoutMs How long to wait for a request response (in milliseconds) before failing
     * @return {@link AwsIotMqttConnectionBuilder}
     */
    public AwsIotMqttConnectionBuilder withProtocolOperationTimeoutMs(int protocolOperationTimeoutMs) {
        this.config.setProtocolOperationTimeoutMs(protocolOperationTimeoutMs);
        return this;
    }

    /**
     * Configures the TCP socket connect timeout (in milliseconds)
     *
     * @param timeoutMs TCP socket timeout
     * @return {@link AwsIotMqttConnectionBuilder}
     */
    public AwsIotMqttConnectionBuilder withTimeoutMs(int timeoutMs) {
        if (this.config.getSocketOptions() == null) {
            this.withSocketOptions(new SocketOptions());
        }
        this.config.getSocketOptions().connectTimeoutMs = timeoutMs;
        return this;
    }

    /**
     * Configures the minimum and maximum reconnect timeouts.
     *
     * The time between reconnect attempts will start at min and multiply by 2 until max is reached.
     *
     * @param minTimeoutSecs The timeout to start with
     * @param maxTimeoutSecs The highest allowable wait time between reconnect attempts
     * @return {@link AwsIotMqttConnectionBuilder}
     */
    public AwsIotMqttConnectionBuilder withReconnectTimeoutSecs(long minTimeoutSecs, long maxTimeoutSecs) {
        this.config.setReconnectTimeoutSecs(minTimeoutSecs, maxTimeoutSecs);
        return this;
    }

    /**
     * Configures the common settings for the socket to use for connections created by this builder
     *
     * @param socketOptions The socket settings
     * @return {@link AwsIotMqttConnectionBuilder}
     */
    public AwsIotMqttConnectionBuilder withSocketOptions(SocketOptions socketOptions) {
        this.config.setSocketOptions(socketOptions);
        return this;
    }

    /**
     * Configures the username to include in the initial CONNECT mqtt packet for connections created by this builder.
     *
     * @param username username to use in CONNECT
     * @return {@link AwsIotMqttConnectionBuilder}
     */
    public AwsIotMqttConnectionBuilder withUsername(String username) {
        this.config.setUsername(username);
        return this;
    }

    /**
     * Configures the password to include in the initial CONNECT mqtt packet for connections created by this builder.
     *
     * @param password password to use in CONNECT
     * @return {@link AwsIotMqttConnectionBuilder}
     */
    public AwsIotMqttConnectionBuilder withPassword(String password) {
        this.config.setPassword(password);
        return this;
    }

    /**
     * Configures the connection-related callbacks to use for connections created by this builder
     *
     * @param callbacks connection event callbacks to use
     * @return {@link AwsIotMqttConnectionBuilder}
     */
    public AwsIotMqttConnectionBuilder withConnectionEventCallbacks(MqttClientConnectionEvents callbacks) {
        this.config.setConnectionCallbacks(callbacks);
        return this;
    }

    /**
     * Sets the last will and testament message to be delivered to a topic when a connection created by this builder
     * disconnects
     *
     * @param message The message to publish as the will. The message contains the
     *                topic that the message will be published to on disconnect,
     *                along with the {@link QualityOfService} that it will be
     *                published with and whether it will be retained when it is published.
     * @return {@link AwsIotMqttConnectionBuilder}
     */
    public AwsIotMqttConnectionBuilder withWill(MqttMessage message) throws MqttException {
        this.config.setWillMessage(message);

        return this;
    }

    /**
     * @deprecated Use alternate withWill(). QoS and retain are now set directly on the {@link MqttMessage}
     * @param message deprecated
     * @param qos deprecated
     * @param retain depricated
     * @return {@link AwsIotMqttConnectionBuilder}
     */
    @Deprecated
    public AwsIotMqttConnectionBuilder withWill(MqttMessage message, QualityOfService qos, boolean retain) throws MqttException {
        return withWill(new MqttMessage(message.getTopic(), message.getPayload(), qos, retain));
    }

    /**
     * Configures the client bootstrap to use for connections created by this builder
     *
     * @param bootstrap client bootstrap to use for created connections
     * @return {@link AwsIotMqttConnectionBuilder}
     */
    public AwsIotMqttConnectionBuilder withBootstrap(ClientBootstrap bootstrap) {
        swapReferenceTo(this.bootstrap, bootstrap);
        this.bootstrap = bootstrap;
        resetLazilyCreatedResources = true;

        return this;
    }

    /**
     * Configures whether or not to the connection uses websockets
     *
     * @param useWebsockets whether or not to use websockets
     * @return {@link AwsIotMqttConnectionBuilder}
     */
    public AwsIotMqttConnectionBuilder withWebsockets(boolean useWebsockets) {
        this.config.setUseWebsockets(useWebsockets);

        if (useWebsockets) {
            this.tlsOptions.alpnList.clear();
            this.config.setPort(443);
        } else {
            resetDefaultPort();
        }

        resetLazilyCreatedResources = true;

        return this;
    }

    /**
     * Configures handshake transform used when establishing a connection via websockets.  If no transform has been
     * set then a default transform is used that adds AWS IoT authentication parameters and signs the request via
     * Sigv4.
     *
     * When done mutating the request, complete MUST be called on the future contained within the
     * transform args parameter.
     *
     * @param handshakeTransform handshake request transformation function
     * @return {@link AwsIotMqttConnectionBuilder}
     */
    public AwsIotMqttConnectionBuilder withWebsocketHandshakeTransform(Consumer<WebsocketHandshakeTransformArgs> handshakeTransform) {
        this.config.setWebsocketHandshakeTransform(handshakeTransform);

        return this;
    }

    /**
     * @deprecated use withHttpProxyOptions instead
     * Configures any http proxy options to use if the connection uses websockets
     *
     * @param proxyOptions http proxy options to use when establishing a websockets-based connection
     * @return {@link AwsIotMqttConnectionBuilder}
     */
    public AwsIotMqttConnectionBuilder withWebsocketProxyOptions(HttpProxyOptions proxyOptions) {
        this.config.setHttpProxyOptions(proxyOptions);

        return this;
    }

    /**
     * Configures any http proxy options to use
     *
     * @param proxyOptions http proxy options to use when establishing a connection
     * @return {@link AwsIotMqttConnectionBuilder}
     */
    public AwsIotMqttConnectionBuilder withHttpProxyOptions(HttpProxyOptions proxyOptions) {
        this.config.setHttpProxyOptions(proxyOptions);

        return this;
    }

    /**
     * Configures the region to use when signing (via Sigv4) the websocket upgrade request.  Only applicable
     * if the handshake transform is null (enabling the default sigv4 transform injection).
     *
     * @param region region to use when signing the websocket upgrade request
     * @return {@link AwsIotMqttConnectionBuilder}
     */
    public AwsIotMqttConnectionBuilder withWebsocketSigningRegion(String region) {
        this.websocketSigningRegion = region;

        return this;
    }

    /**
     * Configures the credentials provider to use for websocket handshake signing.  Only applicable to sigv4
     * based authentication.  If provider is null, the default provider chain will be used.
     *
     * @param provider  credentials provider to pull Aws credentials from.
     * @return {@link AwsIotMqttConnectionBuilder}
     */
    public AwsIotMqttConnectionBuilder withWebsocketCredentialsProvider(CredentialsProvider provider) {
        swapReferenceTo(this.websocketCredentialsProvider, provider);
        this.websocketCredentialsProvider = provider;

        return this;
    }

    /**
     * A helper function to add parameters to the username in the withCustomAuthorizer function
     */
    private String addUsernameParameter(String inputString, String parameterValue, String parameterPreText, Boolean addedStringToUsername) {
        String return_string = inputString;
        if (addedStringToUsername == false) {
            return_string += "?";
        } else {
            return_string += "&";
        }

        if (parameterValue.contains(parameterPreText)) {
            return return_string + parameterValue;
        } else {
            return return_string + parameterPreText + parameterValue;
        }
    }

    /**
     * Configures the MQTT connection so it can use a custom authorizer.
     * This function will modify the username, port, and TLS options.
     *
     * @deprecated Please use the full withCustomAuthorizer function that includes `tokenKeyName` and
     *             `tokenValue`. This version is left for backwards compatibility purposes.
     *
     * Note: All arguments are optional and can have "null" as valid input.
     * See the description for each argument for information on what happens if null is passed.
     * @param username The username to use with the custom authorizer. If null is passed, it will check to
     *                 see if a username has already been set (via withUsername function). If no username is set then
     *                 no username will be passed with the MQTT connection.
     * @param authorizerName The name of the custom authorizer. If null is passed, then 'x-amz-customauthorizer-name'
     *                       will not be added with the MQTT connection.
     * @param authorizerSignature The signature of the custom authorizer.
     *                            NOTE: This will NOT work without the token key name and token value, which requires
     *                            using the non-depreciated API.
     * @param password The password to use with the custom authorizer. If null is passed, then no password will be set.
     * @return {@link AwsIotMqttConnectionBuilder}
     */
    public AwsIotMqttConnectionBuilder withCustomAuthorizer(String username, String authorizerName, String authorizerSignature, String password) {
        return this.withCustomAuthorizer(username, authorizerName, authorizerSignature, password, null, null);
    }

    /**
     * Configures the MQTT connection so it can use a custom authorizer.
     * This function will modify the username, port, and TLS options.
     *
     * Note: All arguments are optional and can have "null" as valid input.
     * See the description for each argument for information on what happens if null is passed.
     * @param username The username to use with the custom authorizer. If null is passed, it will check to
     *                 see if a username has already been set (via withUsername function). If no username is set then
     *                 no username will be passed with the MQTT connection.
     * @param authorizerName The name of the custom authorizer. If null is passed, then 'x-amz-customauthorizer-name'
     *                       will not be added with the MQTT connection.
     * @param authorizerSignature The signature of the custom authorizer. If null is passed, then
     *                            'x-amz-customauthorizer-signature' will not be added with the MQTT connection.
     *                            The signature must be based on the private key associated with the custom authorizer.
     *                            The signature must be base64 encoded. Required if the custom authorizer has signing
     *                            enabled. It is strongly suggested to URL-encode this value; the SDK will not do
     *                            so for you.
     * @param password The password to use with the custom authorizer. If null is passed, then no password will be set.
     * @param tokenKeyName Key used to extract the custom authorizer token from MQTT username query-string properties.
     *                     Required if the custom authorizer has signing enabled.  It is strongly suggested to URL-encode
     *                     this value; the SDK will not do so for you.
     * @param tokenValue An opaque token value.
     *                   Required if the custom authorizer has signing enabled. This value must be signed by the private
     *                   key associated with the custom authorizer and the result placed in the authorizerSignature argument.
     * @return {@link AwsIotMqttConnectionBuilder}
     */
    public AwsIotMqttConnectionBuilder withCustomAuthorizer(String username, String authorizerName, String authorizerSignature, String password, String tokenKeyName, String tokenValue) {
        if (authorizerSignature != null || tokenKeyName != null || tokenValue != null) {
            if (tokenKeyName == null || tokenValue == null || authorizerSignature == null) {
                throw new RuntimeException("Token-based custom authentication requires all token-related properties to be set");
            }
        }

        isUsingCustomAuthorizer = true;
        String usernameString = "";
        Boolean addedStringToUsername = false;

        if (username == null) {
            if (config.getUsername() != null) {
                usernameString += config.getUsername();
            }
        } else {
            usernameString += username;
        }

        if (authorizerName != null) {
            usernameString = addUsernameParameter(usernameString, authorizerName, "x-amz-customauthorizer-name=", addedStringToUsername);
            addedStringToUsername = true;
        }

        if (authorizerSignature != null)
        {
            if (!authorizerSignature.contains("%")) {
                try {
                    authorizerSignature = URLEncoder.encode(authorizerSignature, StandardCharsets.UTF_8.toString());
                } catch (UnsupportedEncodingException uee) {
                    throw new CrtRuntimeException(uee.toString());
                }
            }

            usernameString = addUsernameParameter(usernameString, authorizerSignature, "x-amz-customauthorizer-signature=", addedStringToUsername);
        }

        if (tokenKeyName != null && tokenValue != null) {
            usernameString = addUsernameParameter(usernameString, tokenValue, tokenKeyName + "=", addedStringToUsername);
        }

        config.setUsername(usernameString);

        if (password != null) {
            config.setPassword(password);
        }

        if (config.getUseWebsockets() == false) {
            tlsOptions.alpnList.clear();
            tlsOptions.alpnList.add("mqtt");
        }
        config.setPort(443);

        return this;
    }

    /**
     * Converts a AwsIotMqttConnectionBuilder to a AwsIotMqtt5ClientBuilder, converting as much as possible.
     * See AwsIotMqtt5ClientBuilder.newMqttBuilderFromMqtt311ConnectionConfig for more information on what can
     * be converted, the quirks, etc.
     *
     * @return A AwsIotMqtt5ClientBuilder using AwsIotMqttConnectionBuilder settings
     * @throws Exception If an unsupported option is present
     */
    public AwsIotMqtt5ClientBuilder toAwsIotMqtt5ClientBuilder() throws Exception {
        return AwsIotMqtt5ClientBuilder.newMqttBuilderFromMqtt311ConnectionConfig(config, tlsOptions);
    }

    /**
     * Builds a new mqtt connection from the configuration stored in the builder.  Because some objects are created
     * lazily, certain properties should not be modified after this is first invoked (tls options, bootstrap).
     *
     * @return a new mqtt connection
     */
    public MqttClientConnection build() {
        // Validate
        if (bootstrap == null) {
            bootstrap = ClientBootstrap.getOrCreateStaticDefault();
        }

        // Lazy create
        // This does mean that once you call build() once, modifying the tls context options or client bootstrap
        // has no affect on subsequently-created connections.
        synchronized(this) {

            // Check to see if a custom authorizer is being used but not through the builder.
            if (isUsingCustomAuthorizer == false) {
                if (config.getUsername() != null) {
                    if (config.getUsername().contains("x-amz-customauthorizer-name=") ||
                        config.getUsername().contains("x-amz-customauthorizer-signature="))
                    {
                        isUsingCustomAuthorizer = true;
                    }
                }
            }
            // Is the user trying to connect using a custom authorizer?
            if (isUsingCustomAuthorizer == true) {
                if (config.getPort() != 443) {
                    Log.log(LogLevel.Warn, LogSubject.MqttClient,"Attempting to connect to authorizer with unsupported port. Port is not 443...");
                }
                if (config.getUseWebsockets() == false) {
                    if (tlsOptions.alpnList.size() == 1) {
                        if (tlsOptions.alpnList.get(0) != "mqtt") {
                            tlsOptions.alpnList.clear();
                            tlsOptions.alpnList.add("mqtt");
                        }
                    } else {
                        tlsOptions.alpnList.clear();
                        tlsOptions.alpnList.add("mqtt");
                    }
                }
            }

            if (tlsOptions != null && (tlsContext == null || resetLazilyCreatedResources)) {
                try (ClientTlsContext clientTlsContext = new ClientTlsContext(tlsOptions)) {
                    swapReferenceTo(tlsContext, clientTlsContext);
                    tlsContext = clientTlsContext;
                }
            }

            if (client == null || resetLazilyCreatedResources) {
                try (MqttClient mqttClient = (tlsContext == null) ? new MqttClient(bootstrap) : new MqttClient(bootstrap, tlsContext)) {
                    swapReferenceTo(client, mqttClient);
                    client = mqttClient;
                    config.setMqttClient(client);
                }
            }
        }

        resetLazilyCreatedResources = false;

        // Connection create
        try (MqttConnectionConfig connectionConfig = config.clone()) {

            // Whether or not a username has been added, append our metrics tokens
            String usernameOrEmpty = "";
            if (connectionConfig.getUsername() != null) {
                usernameOrEmpty = connectionConfig.getUsername();
            }
            String queryStringConcatenation = "?";
            if (usernameOrEmpty.contains("?")) {
                queryStringConcatenation = "&";
            }

            if(CRT.getOSIdentifier() == "android"){
                connectionConfig.setUsername(String.format("%s%sSDK=AndroidV2&Version=%s",
                usernameOrEmpty,
                queryStringConcatenation,
                new PackageInfo().version.toString()));
            } else {
                connectionConfig.setUsername(String.format("%s%sSDK=JavaV2&Version=%s",
                usernameOrEmpty,
                queryStringConcatenation,
                new PackageInfo().version.toString()));
            }

            if (connectionConfig.getUseWebsockets() && connectionConfig.getWebsocketHandshakeTransform() == null) {
                if (websocketCredentialsProvider == null) {
                    DefaultChainCredentialsProvider.DefaultChainCredentialsProviderBuilder providerBuilder = new DefaultChainCredentialsProvider.DefaultChainCredentialsProviderBuilder();
                    providerBuilder.withClientBootstrap(bootstrap);

                    try (CredentialsProvider defaultProvider = providerBuilder.build()) {
                        withWebsocketCredentialsProvider(defaultProvider);
                    }
                }

                try (AwsSigningConfig signingConfig = new AwsSigningConfig()) {
                    signingConfig.setAlgorithm(AwsSigningConfig.AwsSigningAlgorithm.SIGV4);
                    signingConfig.setSignatureType(AwsSigningConfig.AwsSignatureType.HTTP_REQUEST_VIA_QUERY_PARAMS);
                    signingConfig.setRegion(websocketSigningRegion);
                    signingConfig.setService(IOT_SIGNING_SERVICE);
                    signingConfig.setCredentialsProvider(websocketCredentialsProvider);
                    signingConfig.setOmitSessionToken(true);

                    try (AwsSigv4HandshakeTransformer transformer = new AwsSigv4HandshakeTransformer(signingConfig)) {
                        connectionConfig.setWebsocketHandshakeTransform(transformer);

                        /*
                         * transformer is actually a CrtResource since we track a SigningConfig (which tracks a Credentials Provider
                         * But the MqttConnectionConfig only knows of the transformer as a Consumer function, so it's not
                         * able to properly add a forward reference to the transformer.  So we do it manually here after setting.
                         */
                        connectionConfig.addReferenceTo(transformer);
                    }
                }
            }

            return new MqttClientConnection(connectionConfig);
        }
    }
}
