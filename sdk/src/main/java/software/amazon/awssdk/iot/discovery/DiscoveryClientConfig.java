package software.amazon.awssdk.iot.discovery;

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

    /**
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

    @Override
    public void close() {
        if(tlsContext != null) {
            tlsContext.close();
        }
    }
}
