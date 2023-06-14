package software.amazon.awssdk.iot.discovery;

import java.util.concurrent.ExecutorService;

import software.amazon.awssdk.crt.http.HttpProxyOptions;
import software.amazon.awssdk.crt.io.ClientBootstrap;
import software.amazon.awssdk.crt.io.SocketOptions;
import software.amazon.awssdk.crt.io.TlsContext;
import software.amazon.awssdk.crt.io.TlsContextOptions;

/**
 * Configuration object for the Greengrass discovery client
 */
public class DiscoveryClientConfig implements AutoCloseable {
    final private ClientBootstrap bootstrap;
    final private TlsContext tlsContext;
    final private SocketOptions socketOptions;
    final private String region;
    final private int maxConnections;
    final private HttpProxyOptions proxyOptions;
    final private String ggServerName;
    private ExecutorService discoveryExecutor;

    /**
     * Constructor for DiscoveryClientConfig creates the correct endpoint if not in a special region
     *
     * @param bootstrap client bootstrap to use to establish network connections
     * @param tlsContextOptions tls configuration for client network connections.  For greengrass discovery, the
     *                          tls context must be initialized with the certificate and private key of the
     *                          device/thing that is querying greengrass core availability.
     * @param socketOptions socket configuration for client network connections
     * @param region AWS region to query for greengrass information
     * @param maxConnections maximum concurrent http connections within the client
     * @param proxyOptions proxy configuration for client network connections
     */
    public DiscoveryClientConfig(
            final ClientBootstrap bootstrap,
            final TlsContextOptions tlsContextOptions,
            final SocketOptions socketOptions,
            final String region,
            final int maxConnections,
            final HttpProxyOptions proxyOptions) {
        this.bootstrap = bootstrap;
        this.tlsContext = new TlsContext(tlsContextOptions);
        this.socketOptions = socketOptions;
        this.region = region;
        this.maxConnections = maxConnections;
        this.proxyOptions = proxyOptions;
        this.ggServerName = "";
        this.discoveryExecutor = null;
    }

    /**
     * Constructor for DiscoveryClientConfig creates the correct endpoint if not in a special region using
     * the default static bootstrap.
     *
     * @param tlsContextOptions tls configuration for client network connections.  For greengrass discovery, the
     *                          tls context must be initialized with the certificate and private key of the
     *                          device/thing that is querying greengrass core availability.
     * @param socketOptions socket configuration for client network connections
     * @param region AWS region to query for greengrass information
     * @param maxConnections maximum concurrent http connections within the client
     * @param proxyOptions proxy configuration for client network connections
     */
    public DiscoveryClientConfig(
            final TlsContextOptions tlsContextOptions,
            final SocketOptions socketOptions,
            final String region,
            final int maxConnections,
            final HttpProxyOptions proxyOptions) {
        this(ClientBootstrap.getOrCreateStaticDefault(), tlsContextOptions,
                socketOptions, region, maxConnections, proxyOptions);
    }

    /**
     * Default Constructor for DiscoveryClientConfig that allows the specification of a specific ggServerName to use in special regions
     *
     * @param bootstrap client bootstrap to use to establish network connections
     * @param tlsContextOptions tls configuration for client network connections.  For greengrass discovery, the
     *                          tls context must be initialized with the certificate and private key of the
     *                          device/thing that is querying greengrass core availability.
     * @param socketOptions socket configuration for client network connections
     * @param maxConnections maximum concurrent http connections within the client
     * @param proxyOptions proxy configuration for client network connections
     * @param ggServerName full endpoint to use when connecting in special regions
     */
    public DiscoveryClientConfig(
            final ClientBootstrap bootstrap,
            final TlsContextOptions tlsContextOptions,
            final SocketOptions socketOptions,
            final int maxConnections,
            final HttpProxyOptions proxyOptions,
            final String ggServerName) {
        this.bootstrap = bootstrap;
        this.tlsContext = new TlsContext(tlsContextOptions);
        this.socketOptions = socketOptions;
        this.region = "";
        this.maxConnections = maxConnections;
        this.proxyOptions = proxyOptions;
        this.ggServerName = ggServerName;
        this.discoveryExecutor = null;
    }

    /**
     * Default Constructor for DiscoveryClientConfig that allows the specification of a specific ggServerName to use in special regions
     * using the static default client bootstrap.
     *
     * @param tlsContextOptions tls configuration for client network connections.  For greengrass discovery, the
     *                          tls context must be initialized with the certificate and private key of the
     *                          device/thing that is querying greengrass core availability.
     * @param socketOptions socket configuration for client network connections
     * @param maxConnections maximum concurrent http connections within the client
     * @param proxyOptions proxy configuration for client network connections
     * @param ggServerName full endpoint to use when connecting in special regions
     */
    public DiscoveryClientConfig(
            final TlsContextOptions tlsContextOptions,
            final SocketOptions socketOptions,
            final int maxConnections,
            final HttpProxyOptions proxyOptions,
            final String ggServerName) {
        this(ClientBootstrap.getOrCreateStaticDefault(), tlsContextOptions,
                socketOptions, maxConnections, proxyOptions, ggServerName);
    }

    /**
     * @return client bootstrap to use to establish network connections
     */
    public ClientBootstrap getBootstrap() {
        return bootstrap;
    }

    /**
     * @return tls configuration for client network connections
     */
    public TlsContext getTlsContext() {
        return tlsContext;
    }

    /**
     * @return socket configuration for client network connections
     */
    public SocketOptions getSocketOptions() {
        return socketOptions;
    }

    /**
     * @return AWS region to query for greengrass information
     */
    public String getRegion() {
        return region;
    }

    /**
     * @return maximum concurrent http connections within the client
     */
    public int getMaxConnections() {
        return maxConnections;
    }

    /**
     * @return proxy configuration for client network connections
     */
    public HttpProxyOptions getProxyOptions() {
        return proxyOptions;
    }

    public String getGGServerName() {
        return ggServerName;
    }

    /**
     * @return the ExecutorService set for this discover client config. Returns null if one hasn't been set.
     */
    public ExecutorService getDiscoveryExecutor() {
        return this.discoveryExecutor;
    }

    /**
     * Sets the executor that is used when calling discover().
     * If set using this function, you are expected to close/shutdown the executor when finished with it.
     *
     * If it is not set, the discovery client will internally manage its own executor.
     *
     * @param override The executor to use
     * @return The client config
     */
    public DiscoveryClientConfig setDiscoveryExecutor(ExecutorService override) {
        this.discoveryExecutor = override;
        return this;
    }

    @Override
    public void close() {
        if(tlsContext != null) {
            tlsContext.close();
        }
    }
}
