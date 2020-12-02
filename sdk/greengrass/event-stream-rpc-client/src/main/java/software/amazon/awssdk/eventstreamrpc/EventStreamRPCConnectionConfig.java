package software.amazon.awssdk.eventstreamrpc;

import software.amazon.awssdk.crt.io.*;
import software.amazon.awssdk.eventstreamrpc.MessageAmendInfo;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * The closeable elements inside the EventStreamRPCConnectionConfig are not cleaned up when
 * this config object is done. It is still up to the caller of the constructor to clean up
 * resources that are associated in the config.
 *
 * The connect message transformer is used to supply additional connect message headers
 * and supply the payload of the connect message. This is to be used to supply authentication
 * information on the connect
 */
public class EventStreamRPCConnectionConfig {
    private final ClientBootstrap clientBootstrap;
    private final EventLoopGroup eventLoopGroup;
    private final SocketOptions socketOptions;
    private final ClientTlsContext tlsContext;
    private final String host;
    private final int port;

    /**
     * MessageAmendInfo here is used to add supplied headers to the Connect message, and
     * set the payload of that message as well.
     */
    private final Supplier<CompletableFuture<MessageAmendInfo>> connectMessageAmender;

    public EventStreamRPCConnectionConfig(ClientBootstrap clientBootstrap, EventLoopGroup eventLoopGroup,
                                          SocketOptions socketOptions, ClientTlsContext tlsContext,
                                          String host, int port, Supplier<CompletableFuture<MessageAmendInfo>> connectMessageAmender) {
        this.clientBootstrap = clientBootstrap;
        this.eventLoopGroup = eventLoopGroup;
        this.socketOptions = socketOptions;
        this.tlsContext = tlsContext;
        this.host = host;
        this.port = port;
        this.connectMessageAmender = connectMessageAmender;

        //perform cast to throw exception here if port value is out of short value range
        final short shortPort = (short)port;

        //bit of C++ RAII here, validate what we can
        if (clientBootstrap == null || eventLoopGroup == null || socketOptions == null ||
            host == null || host.isEmpty() || port < 0) {
            throw new IllegalArgumentException("EventStreamRPCConnectionConfig values are invalid!");
        }
    }

    public ClientBootstrap getClientBootstrap() {
        return clientBootstrap;
    }

    public EventLoopGroup getEventLoopGroup() {
        return eventLoopGroup;
    }

    public SocketOptions getSocketOptions() {
        return socketOptions;
    }

    public ClientTlsContext getTlsContext() {
        return tlsContext;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public Supplier<CompletableFuture<MessageAmendInfo>> getConnectMessageAmender() {
        return connectMessageAmender;
    }
}
