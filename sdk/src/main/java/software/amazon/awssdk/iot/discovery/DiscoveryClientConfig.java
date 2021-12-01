package software.amazon.awssdk.iot.discovery;

import software.amazon.awssdk.crt.http.HttpProxyOptions;
import software.amazon.awssdk.crt.io.ClientBootstrap;
import software.amazon.awssdk.crt.io.SocketOptions;
import software.amazon.awssdk.crt.io.TlsContext;
import software.amazon.awssdk.crt.io.TlsContextOptions;

public class DiscoveryClientConfig implements AutoCloseable {
    final private ClientBootstrap bootstrap;
    final private TlsContext tlsContext;
    final private SocketOptions socketOptions;
    final private String region;
    final private int maxConnections;
    final private HttpProxyOptions proxyOptions;
    final private String ggServerName;

    /**
     * Constructor for DiscoveryClientConfig creates the correct endpoint if not in a special region
     * 
     * @param bootstrap ClientBootstrap
     * @param tlsContextOptions TlsContextOptions
     * @param socketOptions SocketOptions
     * @param region AWS region. Not used when ggServerName is specified
     * @param maxConnections integer of max connections
     * @param proxyOptions HttpProxyOptions
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
    }

    /**
     * Default Constructor for DiscoveryClientConfig that allows the specification of a specific ggServerName to use in special regions
     * 
     * @param bootstrap ClientBootstrap
     * @param tlsContextOptions TlsContextOptions
     * @param socketOptions SocketOptions
     * @param maxConnections integer of max connections
     * @param proxyOptions HttpProxyOptions
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
    }

    public ClientBootstrap getBootstrap() {
        return bootstrap;
    }

    public TlsContext getTlsContext() {
        return tlsContext;
    }

    public SocketOptions getSocketOptions() {
        return socketOptions;
    }

    public String getRegion() {
        return region;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public HttpProxyOptions getProxyOptions() {
        return proxyOptions;
    }

    public String getGGServerName() {
        return ggServerName;
    }

    @Override
    public void close() {
        if(tlsContext != null) {
            tlsContext.close();
        }
    }
}
