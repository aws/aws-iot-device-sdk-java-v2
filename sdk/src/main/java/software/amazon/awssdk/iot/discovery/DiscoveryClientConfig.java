package software.amazon.awssdk.iot.discovery;

import software.amazon.awssdk.crt.http.HttpProxyOptions;
import software.amazon.awssdk.crt.io.ClientBootstrap;
import software.amazon.awssdk.crt.io.SocketOptions;
import software.amazon.awssdk.crt.io.TlsContext;

public class DiscoveryClientConfig {
    final private ClientBootstrap bootstrap;
    final private TlsContext tlsContext;
    final private SocketOptions socketOptions;
    final private String region;
    final private int maxConnections;
    final private HttpProxyOptions proxyOptions;

    public DiscoveryClientConfig(
            final ClientBootstrap bootstrap,
            final TlsContext tlsContext,
            final SocketOptions socketOptions,
            final String region,
            final int maxConnections,
            final HttpProxyOptions proxyOptions) {
        this.bootstrap = bootstrap;
        this.tlsContext = tlsContext;
        this.socketOptions = socketOptions;
        this.region = region;
        this.maxConnections = maxConnections;
        this.proxyOptions = proxyOptions;
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
}
