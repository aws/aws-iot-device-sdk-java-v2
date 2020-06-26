
/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */
package software.amazon.awssdk.iot;

import software.amazon.awssdk.crt.utils.PackageInfo;

import java.io.UnsupportedEncodingException;
import java.util.function.Consumer;
import java.util.function.Predicate;

import software.amazon.awssdk.crt.CrtResource;
import software.amazon.awssdk.crt.auth.credentials.CredentialsProvider;
import software.amazon.awssdk.crt.auth.credentials.DefaultChainCredentialsProvider;
import software.amazon.awssdk.crt.auth.signing.AwsSigningConfig;
import software.amazon.awssdk.crt.http.HttpProxyOptions;
import software.amazon.awssdk.crt.io.ClientBootstrap;
import software.amazon.awssdk.crt.io.ClientTlsContext;
import software.amazon.awssdk.crt.io.SocketOptions;
import software.amazon.awssdk.crt.io.TlsContextOptions;
import software.amazon.awssdk.crt.mqtt.MqttClient;
import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt.MqttClientConnectionEvents;
import software.amazon.awssdk.crt.mqtt.MqttConnectionConfig;
import software.amazon.awssdk.crt.mqtt.MqttException;
import software.amazon.awssdk.crt.mqtt.MqttMessage;
import software.amazon.awssdk.crt.mqtt.QualityOfService;
import software.amazon.awssdk.crt.mqtt.WebsocketHandshakeTransformArgs;

/*
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
     */
    public static AwsIotMqttConnectionBuilder newMtlsBuilder(byte[] certificate, byte[] privateKey)
            throws UnsupportedEncodingException {
        return newMtlsBuilder(new String(certificate, "UTF8"), new String(privateKey, "UTF8"));
    }

    /**
     * Create a new builder with no default Tls options
     *
     * @return a new builder with default Tls options
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
     */
    public AwsIotMqttConnectionBuilder withPort(short port) {
        this.config.setPort(port);
        return this;
    }

    /**
     * Configures the client id to use to connect to the IoT Core service.
     * 
     * @param clientId The client id for connections from this builder. Needs to be unique across
     *                  all devices/clients.
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
     */
    public AwsIotMqttConnectionBuilder withCleanSession(boolean cleanSession) {
        this.config.setCleanSession(cleanSession);
        return this;
    }

    /**
     * Configures MQTT keep-alive via PING messages. Note that this is not TCP
     * keepalive.
     * 
     * @param keepAliveMs How often in milliseconds to send an MQTT PING message to the
     *                   service to keep connections alive
     */
    public AwsIotMqttConnectionBuilder withKeepAliveMs(int keepAliveMs) {
        this.config.setKeepAliveMs(keepAliveMs);
        return this;
    }

    /**
     * Controls ping timeout value.  If a response is not received within this
     * interval, the connection will be reestablished.
     *
     * @param pingTimeoutMs How long to wait for a ping response before resetting a connection built from this
     *                        builder.
     */
    public AwsIotMqttConnectionBuilder withPingTimeoutMs(int pingTimeoutMs) {
        this.config.setPingTimeoutMs(pingTimeoutMs);
        return this;
    }

    /**
     * Configures the TCP socket connect timeout (in milliseconds)
     * 
     * @param timeoutMs TCP socket timeout
     */
    public AwsIotMqttConnectionBuilder withTimeoutMs(int timeoutMs) {
        this.config.getSocketOptions().connectTimeoutMs = timeoutMs;
        return this;
    }

    /**
     * Configures the common settings for the socket to use for connections created by this builder
     * 
     * @param socketOptions The socket settings
     */
    public AwsIotMqttConnectionBuilder withSocketOptions(SocketOptions socketOptions) {
        this.config.setSocketOptions(socketOptions);
        return this;
    }

    /**
     * Configures the username to include in the initial CONNECT mqtt packet for connections created by this builder.
     *
     * @param username username to use in CONNECT
     */
    public AwsIotMqttConnectionBuilder withUsername(String username) {
        this.config.setUsername(username);
        return this;
    }

    /**
     * Configures the password to include in the initial CONNECT mqtt packet for connections created by this builder.
     *
     * @param password password to use in CONNECT
     */
    public AwsIotMqttConnectionBuilder withPassword(String password) {
        this.config.setPassword(password);
        return this;
    }

    /**
     * Configures the connection-related callbacks to use for connections created by this builder
     *
     * @param callbacks connection event callbacks to use
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
     *                topic that the message will be published to on disconnect.
     * @param qos     The {@link QualityOfService} of the will message
     * @param retain  Whether or not the message should be retained by the mqtt broker to
     *                be delivered to future subscribers
     */
    public AwsIotMqttConnectionBuilder withWill(MqttMessage message, QualityOfService qos, boolean retain) throws MqttException {
        this.config.setWillMessage(message);
        this.config.setWillQos(qos);
        this.config.setWillRetain(retain);

        return this;
    }

    /**
     * Configures the client bootstrap to use for connections created by this builder
     *
     * @param bootstrap client bootstrap to use for created connections
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
     */
    public AwsIotMqttConnectionBuilder withWebsocketHandshakeTransform(Consumer<WebsocketHandshakeTransformArgs> handshakeTransform) {
        this.config.setWebsocketHandshakeTransform(handshakeTransform);

        return this;
    }

    /**
     * Configures any http proxy options to use if the connection uses websockets
     *
     * @param proxyOptions http proxy options to use when establishing a websockets-based connection
     */
    public AwsIotMqttConnectionBuilder withWebsocketProxyOptions(HttpProxyOptions proxyOptions) {
        this.config.setWebsocketProxyOptions(proxyOptions);

        return this;
    }

    /**
     * Configures the region to use when signing (via Sigv4) the websocket upgrade request.  Only applicable
     * if the handshake transform is null (enabling the default sigv4 transform injection).
     *
     * @param region region to use when signing the websocket upgrade request
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
     */
    public AwsIotMqttConnectionBuilder withWebsocketCredentialsProvider(CredentialsProvider provider) {
        swapReferenceTo(this.websocketCredentialsProvider, provider);
        this.websocketCredentialsProvider = provider;

        return this;
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
            throw new MqttException("client bootstrap must be non-null");
        }

        // Lazy create
        // This does mean that once you call build() once, modifying the tls context options or client bootstrap
        // has no affect on subsequently-created connections.
        synchronized(this) {
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
            connectionConfig.setUsername(String.format("%s?SDK=JavaV2&Version=%s", usernameOrEmpty, new PackageInfo().version.toString()));

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



