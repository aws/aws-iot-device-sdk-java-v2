
/*
 * Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package software.amazon.awssdk.iot;

import software.amazon.awssdk.crt.utils.PackageInfo;

import java.io.UnsupportedEncodingException;
import java.util.function.Consumer;

import software.amazon.awssdk.crt.CrtResource;
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
    /* connection */
    private String endpoint;
    private int port;
    private SocketOptions socketOptions;

    /* mqtt general*/
    private MqttClient client; // Lazy create, cached
    private String clientId;
    private String username;
    private String password;
    private MqttClientConnectionEvents callbacks;
    private int keepAliveMs = 0;
    private int pingTimeoutMs = 0;
    private boolean cleanSession = true;

    /* mqtt will */
    private MqttMessage willMessage = null;
    private QualityOfService willQos;
    private boolean willRetain;

    /* mqtt websockets */
    private boolean useWebsockets = false;
    private HttpProxyOptions websocketProxyOptions;
    private Consumer<WebsocketHandshakeTransformArgs> websocketHandshakeTransform;
    private String websocketSigningRegion;

    /* Internal config and state */
    private ClientTlsContext tlsContext;  // Lazy create, cached
    private TlsContextOptions tlsOptions;
    private ClientBootstrap bootstrap;

    private boolean resetCachedResources = true;

    private void resetDefaultPort() {
        if (TlsContextOptions.isAlpnSupported()) {
            this.tlsOptions.withAlpnList("x-amzn-mqtt-ca");
            this.port = 443;
        } else {
            this.port = 8883;
        }
    }

    private AwsIotMqttConnectionBuilder(TlsContextOptions tlsOptions) {
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
     * This class just tracks native resources: mqtt client, tls context, tls context options, client bootstrap
     */
    protected boolean isNativeResource() { return false; }

    /**
     * Create a new builder with mTLS file paths
     * 
     * @param certPath       - Path to certificate, in PEM format
     * @param privateKeyPath - Path to private key, in PEM format
     */
    public static AwsIotMqttConnectionBuilder newMtlsBuilderFromPath(String certPath, String privateKeyPath) {
        AwsIotMqttConnectionBuilder builder = null;
        try (TlsContextOptions tlsContextOptions = TlsContextOptions.createWithMtlsFromPath(certPath, privateKeyPath)) {
            builder = new AwsIotMqttConnectionBuilder(tlsContextOptions);
        }

        return builder;
    }

    /**
     * Create a new builder with mTLS cert pair in memory
     * 
     * @param certificate - Certificate, in PEM format
     * @param privateKey  - Private key, in PEM format
     */
    public static AwsIotMqttConnectionBuilder newMtlsBuilder(String certificate, String privateKey) {
        AwsIotMqttConnectionBuilder builder = null;
        try (TlsContextOptions tlsContextOptions = TlsContextOptions.createWithMtls(certificate, privateKey)) {
            builder = new AwsIotMqttConnectionBuilder(tlsContextOptions);
        }

        return builder;
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
     * Create a new builder from a Pkcs12 blob
     *
     * @param pkcs12Path - The path to a PKCS12 file
     * @param pkcs12Password  - The password to the PKCS12 file
     */
    public static AwsIotMqttConnectionBuilder newMtlsPkcs12Builder(String pkcs12Path, String pkcs12Password) {
        AwsIotMqttConnectionBuilder builder = null;
        try (TlsContextOptions tlsContextOptions = TlsContextOptions.createWithMtlsPkcs12(pkcs12Path, pkcs12Password)) {
            builder = new AwsIotMqttConnectionBuilder(tlsContextOptions);
        }

        return builder;
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
        resetCachedResources = true;
        return this;
    }

    /**
     * Overrides the default system trust store.
     * 
     * @param caRoot - Buffer containing all trust CAs, in PEM format
     */
    public AwsIotMqttConnectionBuilder withCertificateAuthority(String caRoot) {
        this.tlsOptions.overrideDefaultTrustStore(caRoot);
        resetCachedResources = true;
        return this;
    }

    /**
     * Configures the IoT endpoint for connections from this builder.  Can be varied between calls to build().
     * 
     * @param endpoint The IoT endpoint to connect to
     */
    public AwsIotMqttConnectionBuilder withEndpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    /**
     * Configures the port to connect to for connections from this builder.  Can be varied between calls to build().
     * 
     * @param port The port to connect to on the IoT endpoint. Usually 8883 for
     *             MQTT, or 443 for websockets
     */
    public AwsIotMqttConnectionBuilder withPort(short port) {
        this.port = port;
        return this;
    }

    /**
     * Configures the client id to use to connect to the IoT Core service.  Can be varied betweens calls to build().
     * 
     * @param clientId The client id for connections from this builder. Needs to be unique across
     *                  all devices/clients.
     */
    public AwsIotMqttConnectionBuilder withClientId(String clientId) {
        this.clientId = clientId;
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
        this.cleanSession = cleanSession;
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
        this.keepAliveMs = keepAliveMs;
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
        this.pingTimeoutMs = pingTimeoutMs;
        return this;
    }

    /**
     * Configures the TCP socket connect timeout (in milliseconds)
     * 
     * @param timeoutMs TCP socket timeout
     */
    public AwsIotMqttConnectionBuilder withTimeoutMs(int timeoutMs) {
        this.socketOptions.connectTimeoutMs = timeoutMs;
        return this;
    }

    /**
     * Configures the common settings for the socket to use for connections created by this builder
     * 
     * @param socketOptions The socket settings
     */
    public AwsIotMqttConnectionBuilder withSocketOptions(SocketOptions socketOptions) {
        swapReferenceTo(this.socketOptions, socketOptions);
        this.socketOptions = socketOptions;
        return this;
    }

    /**
     * Configures the username to include in the initial CONNECT mqtt packet for connections created by this builder.
     *
     * @param username username to use in CONNECT
     */
    public AwsIotMqttConnectionBuilder withUsername(String username) {
        this.username = String.format("%s?SDK=JavaV2&Version=%s", username, new PackageInfo().toString());
        return this;
    }

    /**
     * Configures the password to include in the initial CONNECT mqtt packet for connections created by this builder.
     *
     * @param password password to use in CONNECT
     */
    public AwsIotMqttConnectionBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    /**
     * Configures the connection-related callbacks to use for connections created by this builder
     *
     * @param callbacks connection event callbacks to use
     */
    public AwsIotMqttConnectionBuilder withConnectionEventCallbacks(MqttClientConnectionEvents callbacks) {
        this.callbacks = callbacks;
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
        this.willMessage = message;
        this.willQos = qos;
        this.willRetain = retain;

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
        resetCachedResources = true;

        return this;
    }

    /**
     * Configures whether or not to the connection uses websockets
     *
     * @param useWebsockets whether or not to use websockets
     */
    public AwsIotMqttConnectionBuilder withWebsockets(boolean useWebsockets) {
        this.useWebsockets = useWebsockets;

        if (useWebsockets) {
            this.tlsOptions.alpnList.clear();
            this.port = 443;
        } else {
            resetDefaultPort();
        }

        resetCachedResources = true;

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
        this.websocketHandshakeTransform = handshakeTransform;

        return this;
    }

    /**
     * Configures any http proxy options to use if the connection uses websockets
     *
     * @param proxyOptions http proxy options to use when establishing a websockets-based connection
     */
    public AwsIotMqttConnectionBuilder withWebsocketProxyOptions(HttpProxyOptions proxyOptions) {
        this.websocketProxyOptions = proxyOptions;

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
            if (tlsOptions != null && (tlsContext == null || resetCachedResources)) {
                try (ClientTlsContext clientTlsContext = new ClientTlsContext(tlsOptions)) {
                    swapReferenceTo(tlsContext, clientTlsContext);
                    tlsContext = clientTlsContext;
                }
            }

            if (client == null || resetCachedResources) {
                try (MqttClient mqttClient = (tlsContext == null) ? new MqttClient(bootstrap) : new MqttClient(bootstrap, tlsContext)) {
                    swapReferenceTo(client, mqttClient);
                    client = mqttClient;
                }
            }
        }

        resetCachedResources = false;

        // Connection create
        try (MqttConnectionConfig config = new MqttConnectionConfig()) {
            config.setMqttClient(client);
            config.setClientId(clientId);
            config.setEndpoint(endpoint);
            config.setPort(port);
            config.setConnectionCallbacks(callbacks);
            config.setSocketOptions(socketOptions);
            config.setCleanSession(cleanSession);
            config.setKeepAliveMs(keepAliveMs);
            config.setPingTimeoutMs(pingTimeoutMs);

            if (willMessage != null) {
                config.setWill(willMessage, willQos, willRetain);
            }

            if (username != null && password != null) {
                config.setLogin(username, password);
            }

            if (useWebsockets) {
                if (websocketHandshakeTransform != null) {
                    config.setWebsocketHandshakeTransform(websocketHandshakeTransform);
                } else {
                    config.setWebsocketHandshakeTransform(new AwsSigv4HandshakeTransformer());
                }

                config.setWebsocketProxyOptions(websocketProxyOptions);
            }

            return new MqttClientConnection(config);
        }
    }
}


