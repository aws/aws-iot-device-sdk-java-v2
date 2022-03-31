package software.amazon.awssdk.eventstreamrpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.crt.io.EventLoopGroup;
import software.amazon.awssdk.crt.io.SocketOptions;
import software.amazon.awssdk.crt.io.TlsContextOptions;

/**
 * DEPRECATION WARNING: Stop using this class. Use RpcServer instead
 */
final public class IpcServer extends RpcServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(IpcServer.class);

    public IpcServer(EventLoopGroup eventLoopGroup, SocketOptions socketOptions, TlsContextOptions tlsContextOptions, String hostname, int port, EventStreamRPCServiceHandler serviceHandler) {
        super(eventLoopGroup, socketOptions, tlsContextOptions, hostname, port, serviceHandler);
        LOGGER.warn("IpcServer class is DEPRECATED. Use RpcServer");
    }
}
