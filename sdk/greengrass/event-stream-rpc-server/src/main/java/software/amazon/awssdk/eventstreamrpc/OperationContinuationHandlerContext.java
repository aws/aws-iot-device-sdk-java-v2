package software.amazon.awssdk.eventstreamrpc;

import software.amazon.awssdk.crt.eventstream.ServerConnection;
import software.amazon.awssdk.crt.eventstream.ServerConnectionContinuation;

/**
 * When the server picks up a new incoming stream for an operation, and it has context that must
 * be exposed to an operation handler, that access should be granted here.
 *
 * Any intentional exposure to the server connection state or the client that connected, anything
 * that is beyond the operation's knowledge and information from request or stream inputs  should
 * be populated here
 */
public class OperationContinuationHandlerContext {
    private final ServerConnection serverConnection;
    private final ServerConnectionContinuation continuation;
    private final AuthenticationData authenticationData;

    /**
     * Creates a new OperationContinuationHandlerContext
     * @param connection The connection to associate with the OperationContinuationHandlerContext
     * @param continuation The continuation to associate with the OperationContinuationHandlerContext
     * @param authenticationData The authentication data to associate with the OperationContinuationHandlerContext
     */
    public OperationContinuationHandlerContext(final ServerConnection connection,
           final ServerConnectionContinuation continuation,
           final AuthenticationData authenticationData) {
        this.serverConnection = connection;
        this.continuation = continuation;
        this.authenticationData = authenticationData;
    }

    /**
     * Returns the connection associated with the OperationContinuationHandlerContext
     * @return the connection associated with the OperationContinuationHandlerContext
     */
    public ServerConnection getServerConnection() {
        return serverConnection;
    }

    /**
     * Returns the continuation associated with the OperationContinuationHandlerContext
     * @return the continuation associated with the OperationContinuationHandlerContext
     */
    public ServerConnectionContinuation getContinuation() {
        return continuation;
    }

    /**
     * Returns the authentication data associated with the OperationContinuationHandlerContext
     * @return the authentication data associated with the OperationContinuationHandlerContext
     */
    public AuthenticationData getAuthenticationData() {
        return authenticationData;
    }
}
