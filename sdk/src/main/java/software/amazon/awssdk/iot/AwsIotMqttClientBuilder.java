/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */
package software.amazon.awssdk.iot;

import java.security.KeyStore;
import java.util.Objects;
import java.util.UUID;

import software.amazon.awssdk.crt.auth.credentials.CredentialsProvider;
import software.amazon.awssdk.crt.http.HttpProxyOptions;
import software.amazon.awssdk.crt.io.ExponentialBackoffRetryOptions.JitterMode;
import software.amazon.awssdk.crt.io.SocketOptions;
import software.amazon.awssdk.crt.io.TlsContext;
import software.amazon.awssdk.crt.io.TlsContextCustomKeyOperationOptions;
import software.amazon.awssdk.crt.io.TlsContextOptions;
import software.amazon.awssdk.crt.io.TlsContextPkcs11Options;
import software.amazon.awssdk.crt.mqtt5.Mqtt5Client;
import software.amazon.awssdk.crt.mqtt5.Mqtt5ClientOptions;
import software.amazon.awssdk.crt.mqtt5.TopicAliasingOptions;
import software.amazon.awssdk.crt.mqtt5.packets.ConnectPacket;

/**
 * <p><b>Deprecated.</b> Use {@link software.amazon.awssdk.crt.mqtt5.Mqtt5Client} instead.</p>
 * 
 * <p>The MQTT 3.1.1 client remains fully supported, but migrating to
 * MQTT 5 gives you a richer feature set, clearer error handling, and
 * improved lifetime management.</p>
 * 
 * Builder for making MQTT5 Clients with different connection methods for AWS IoT Core.
 */
public final class AwsIotMqttClientBuilder {

    private static final int PORT_MQTT = 8883;
    private static final int PORT_WS   = 443;

    /**
     * Creates a new MQTT5 Client Builder that uses mTLS to connect.
     * @param endpoint - AWS IoT endpoint to connect to
     * @return - a new AwsIotMqttClientBuilder
     */
    public static Builder newMtls(String endpoint) {
        return new Builder(endpoint, PORT_MQTT, false);
    }

    /**
     * Creates a new MQTT5 Client Builder that will use websockets and AWS Sigv4 signing to establish
     * mutually-authenticated (mTLS) connections.
     * @param endpoint - AWS IoT endpoint to connect to
     * @return - a new AwsIotMqttClientBuilder
     */
    public static Builder newWebsocketSigV4Auth(String endpoint) {
        return new Builder(endpoint, PORT_WS, true);
    }

    public static final class Builder {
        private final String endpoint;
        private final int port;
        private final boolean websocket;

        private WebsocketSigV4Options sigv4;
        private TlsBuilderOptions tlsOptionsBuilder;
        private ConnectOptions connect = ConnectOptions.defaults();

        private ReconnectStrategy reconnect = ReconnectStrategy.fullJitter();
        private HttpProxyOptions proxy;
        private SocketOptions socketOptions;
        private TopicAliasingOptions topicAliasing;
        private Mqtt5ClientOptions.ExtendedValidationAndFlowControlOptions extValidation =
                Mqtt5ClientOptions.ExtendedValidationAndFlowControlOptions.AWS_IOT_CORE_DEFAULTS;

        private Mqtt5ClientOptions.LifecycleEvents lifecycleEvents;
        private Mqtt5ClientOptions.PublishEvents   publishEvents;

        private Builder(String endpoint, int port, boolean websocket) {
            this.endpoint  = Objects.requireNonNull(endpoint, "endpoint");
            this.port      = port;
            this.websocket = websocket;
        }

        public Builder tlsOptions(TlsBuilderOptions tlsBuilderOptions) {
            this.tlsOptionsBuilder = tlsBuilderOptions;
            return this;
        }

        public ConnectBuilder connect() { 
            return new ConnectBuilder(this); 
        }

        public Builder reconnect(ReconnectStrategy s) { 
            this.reconnect = s; 
            return this;
        }

        public Builder proxy(HttpProxyOptions p) { 
            this.proxy = p; 
            return this; 
        }

        public Builder socketOptions(SocketOptions s) { 
            this.socketOptions = s; 
            return this; 
        }
        public Builder topicAliasing(TopicAliasingOptions o) { 
            this.topicAliasing = o; 
            return this; 
        }

        public Builder extendedValidation(Mqtt5ClientOptions.ExtendedValidationAndFlowControlOptions o) { 
            this.extValidation = o; 
            return this; 
        }

        public Builder websocketSigning(WebsocketSigV4Options o) { 
            this.sigv4 = o; 
            return this; 
        }

        public Builder lifecycleEvents(Mqtt5ClientOptions.LifecycleEvents e) { 
            this.lifecycleEvents = e; 
            return this; 
        }

        public Builder publishEvents(Mqtt5ClientOptions.PublishEvents p) {
            this.publishEvents = p; 
            return this; 
        }

        public Mqtt5Client build() {
            if (websocket) {
                Objects.requireNonNull(sigv4, "WebSocket transport requires SigV4 signing options");
            } else {
                Objects.requireNonNull(tlsOptionsBuilder, "Mutual‑TLS transport requires TLS material");
            }

            Mqtt5ClientOptions.Mqtt5ClientOptionsBuilder crt =
                    new Mqtt5ClientOptions.Mqtt5ClientOptionsBuilder(endpoint, (long) port);

            if (tlsOptionsBuilder != null) {
                crt.withTlsContext(tlsOptionsBuilder.toContext());
            }
            connect.apply(crt);
            reconnect.apply(crt);

            if (proxy != null)         crt.withHttpProxyOptions(proxy);
            if (socketOptions != null) crt.withSocketOptions(socketOptions);
            if (topicAliasing != null) crt.withTopicAliasingOptions(topicAliasing);
            crt.withExtendedValidationAndFlowControlOptions(extValidation);
            if (lifecycleEvents != null) crt.withLifecycleEvents(lifecycleEvents);
            if (publishEvents   != null) crt.withPublishEvents(publishEvents);

            if (websocket) {
                sigv4.apply(crt, endpoint);
            }

            return new Mqtt5Client(crt.build());
        }
    }

    /* ----- CHILD BUILDERS ------------------------------------------------ */

    public static final class ConnectBuilder {
        private final Builder parent; 
        private String clientId = UUID.randomUUID().toString(); 
        private long keepAlive = 1200;
        
        private ConnectBuilder(Builder parent) {
            this.parent = parent;
        }
        
        public ConnectBuilder clientId(String id) {
            this.clientId = id;
            return this;
        }

        public ConnectBuilder keepAliveSeconds(long s) {
            this.keepAlive=s;
            return this;
        }

        public Builder done() { 
            parent.connect = new ConnectOptions(clientId, keepAlive); 
            return parent;
        }
    }

    /* ----- VALUE OBJECTS ------------------------------------------------- */

    public interface TlsBuilderOptions { 
        /** create the raw TlsContextOptions for this TLS type (no ALPN yet) */
        TlsContextOptions newOptions();

        /** common implementation shared by every TlsContext Type */
        default TlsContext toContext() {
            TlsContextOptions tlsCtxOptions = newOptions();

            if (TlsContextOptions.isAlpnSupported()){
                tlsCtxOptions.withAlpnList("x-amzn-mqtt-ca");
            }

            try {
                return new TlsContext(tlsCtxOptions);
            } finally {
                // we don’t need the options object after the context is built
                tlsCtxOptions.close();
            }
        }
    }

    public static final class MtlsFromPath implements TlsBuilderOptions {
        private final String certPath;
        private final String keyPath;

        public MtlsFromPath(String certificatePath, String privateKeyPath) {
            certPath = certificatePath;
            keyPath = privateKeyPath;
        }

        public TlsContextOptions newOptions() {
            return TlsContextOptions.createWithMtlsFromPath(certPath, keyPath);
        }
    }

    public static final class MtlsFromMemory implements TlsBuilderOptions {
        private final String certPem;
        private final String keyPem;

        public MtlsFromMemory(String certificate,String privateKey) {
            certPem = certificate;
            keyPem = privateKey;
        }

        public TlsContextOptions newOptions() {
            return TlsContextOptions.createWithMtls(certPem, keyPem);
        }
    }

    public static final class Pkcs11 implements TlsBuilderOptions {
        private final TlsContextPkcs11Options pkcs11Options;

        public Pkcs11(TlsContextPkcs11Options pkcs11Options) {
            this.pkcs11Options = pkcs11Options;
        }

        public TlsContextOptions newOptions() {
            return TlsContextOptions.createWithMtlsPkcs11(pkcs11Options);
        }
    }

    public static final class CustomKeyOps implements TlsBuilderOptions {
        private final TlsContextCustomKeyOperationOptions operationOptions;
        
        public CustomKeyOps(TlsContextCustomKeyOperationOptions operationOptions) {
            this.operationOptions = operationOptions;
        }

        public TlsContextOptions newOptions() {
            return TlsContextOptions.createWithMtlsCustomKeyOperations(operationOptions);
        }
    }

    public static final class WindowsCertStore implements TlsBuilderOptions {
        private final String path;
        public WindowsCertStore(String certificatePath) {
            path = certificatePath;
        }

        public TlsContextOptions newOptions() {
            return TlsContextOptions.createWithMtlsWindowsCertStorePath(path);
        }
    }

    public static final class JavaKeystore implements TlsBuilderOptions {
        private final KeyStore ks;
        private final String alias;
        private final String pwd; 
        public JavaKeystore(KeyStore k,String a, String p) {
            ks = k;
            alias = a;
            pwd = p;
        }

        public TlsContextOptions newOptions() {
            return TlsContextOptions.createWithMtlsJavaKeystore(ks,alias,pwd);
        }
    }

    public static final class Pkcs12 implements TlsBuilderOptions {
        private final String path;
        private final String pwd;
        public Pkcs12(String p, String w) {
            path = p;
            pwd = w;
        }

        public TlsContextOptions newOptions() {
            return TlsContextOptions.createWithMtlsPkcs12(path,pwd);
        }
    }


    public static final class ConnectOptions {
        private final String clientId; 
        private final long keepAliveSeconds;

        ConnectOptions(String clientId,long keepAliveSeconds) {
            this.clientId = clientId;
            this.keepAliveSeconds = keepAliveSeconds;
        }

        static ConnectOptions defaults() {
            return new ConnectOptions(null,1200);
        }

        void apply(Mqtt5ClientOptions.Mqtt5ClientOptionsBuilder clientOptionsBuilder) {
            ConnectPacket.ConnectPacketBuilder connectPacket = new ConnectPacket.ConnectPacketBuilder();
            if(clientId != null) {
                connectPacket.withClientId(clientId); 
            }
            connectPacket.withKeepAliveIntervalSeconds(keepAliveSeconds);
            clientOptionsBuilder.withConnectOptions(connectPacket.build());
        }
    }

    public static final class ReconnectStrategy {
        private final long minDelay; 
        private final long maxDelay; 
        private final JitterMode jitter;
        ReconnectStrategy(long min,long max,JitterMode j) {
            minDelay=min;maxDelay=max;jitter=j;
        }
        static ReconnectStrategy fullJitter() {
            return new ReconnectStrategy(100,60_000,JitterMode.Full);
        }
        void apply(Mqtt5ClientOptions.Mqtt5ClientOptionsBuilder b) {
            b.withMinReconnectDelayMs(minDelay);
            b.withMaxReconnectDelayMs(maxDelay);
            b.withRetryJitterMode(jitter);
        }
    }

    public static final class WebsocketSigV4Options {
        private final CredentialsProvider provider; private final String region;
        public WebsocketSigV4Options(CredentialsProvider p,String r){provider=p;region=r;}
        void apply(Mqtt5ClientOptions.Mqtt5ClientOptionsBuilder crt,String endpoint){/* TODO */}
    }
}
