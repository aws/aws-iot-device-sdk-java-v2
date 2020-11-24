package software.amazon.awssdk.eventstreamrpc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.eventstream.*;
import software.amazon.awssdk.eventstreamrpc.model.AccessDeniedException;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamError;

public class EventStreamRPCConnection implements AutoCloseable {
    protected static class ConnectionState {
        enum Phase {
            DISCONNECTED,
            CONNECTING_SOCKET,
            WAITING_CONNACK,
            CONNECTED,
            CLOSING
        };
        
        Phase connectionPhase;
        ClientConnection connection;
        Throwable closeReason;
        
        protected ConnectionState(Phase phase, ClientConnection connection, Throwable closeReason) {
            this.connectionPhase = phase;
            this.connection = connection;
            this.closeReason = closeReason;
        }
    };

    private static final Logger LOGGER = Logger.getLogger(EventStreamRPCConnection.class.getName());

    private final EventStreamRPCConnectionConfig config;
    private ConnectionState connectionState;

    public EventStreamRPCConnection(final EventStreamRPCConnectionConfig config) {
        this.config = config;
        this.connectionState = new ConnectionState(ConnectionState.Phase.DISCONNECTED, null, null);
    }

    /**
     * Separate method to allow override for testing mismatch. May have external use
     * @return returns the event-stream-rpc version string to check against server compatibility
     */
    protected String getVersionString() {
        return Version.getInstance().getVersionString();
    }

    /**
     * Connects to the event stream RPC server asynchronously
     *
     * @return
     */
    public CompletableFuture<Void> connect(final LifecycleHandler lifecycleHandler) {
        synchronized (connectionState) {
            if (connectionState.connectionPhase == ConnectionState.Phase.DISCONNECTED) {
                connectionState.connectionPhase = ConnectionState.Phase.CONNECTING_SOCKET;
            } else {
                throw new IllegalStateException("Connection is already established");
            }
        }
        final CompletableFuture<Void> initialConnectFuture = new CompletableFuture<>();

        ClientConnection.connect(config.getHost(), (short) config.getPort(), config.getSocketOptions(),
                config.getTlsContext(), config.getClientBootstrap(), new ClientConnectionHandler() {
                    @Override
                    protected void onConnectionSetup(ClientConnection clientConnection, int errorCode) {
                        LOGGER.info(String.format("Socket connection %s:%d to server result [%s]",
                                config.getHost(), config.getPort(), CRT.awsErrorName(errorCode)));
                        synchronized (connectionState) {
                            if (connectionState.connectionPhase == ConnectionState.Phase.CLOSING ||
                                    CRT.AWS_CRT_SUCCESS != errorCode) {
                                doOnDisconnect(lifecycleHandler, errorCode);
                                clientConnection.closeConnection(errorCode);
                            } else {
                                connectionState.connection = clientConnection;
                                connectionState.connectionPhase = ConnectionState.Phase.WAITING_CONNACK;
                                LOGGER.fine("Waiting for connect ack message back from event stream RPC server");

                                final MessageAmendInfo messageAmendInfo = config.getConnectMessageAmender().get();
                                final List<Header> headers = new ArrayList<>(messageAmendInfo.getHeaders().size() + 1);
                                headers.add(Header.createHeader(EventStreamRPCServiceModel.VERSION_HEADER,
                                        getVersionString()));
                                headers.addAll(messageAmendInfo.getHeaders().stream()
                                        .filter(header -> !header.getName().equals(EventStreamRPCServiceModel.VERSION_HEADER))
                                        .collect(Collectors.toList()));
                                clientConnection.sendProtocolMessage(headers,
                                        messageAmendInfo.getPayload(), MessageType.Connect, 0);
                            }
                        }
                    }

                    @Override
                    protected void onProtocolMessage(List<Header> headers, byte[] payload, MessageType messageType, int messageFlags) {
                        if (MessageType.ConnectAck.equals(messageType)) {
                            synchronized (connectionState) {
                                if ((messageFlags & MessageFlags.ConnectionAccepted.getByteValue()) != 0) {
                                    connectionState.connectionPhase = ConnectionState.Phase.CONNECTED;
                                    //now the client is open for business to invoke operations
                                    LOGGER.info("Connection established with event stream RPC server");
                                    if (!initialConnectFuture.isDone()) {
                                        initialConnectFuture.complete(null);
                                    }
                                    doOnConnect(lifecycleHandler);
                                } else {
                                    //This is access denied, implied due to not having ConnectionAccepted msg flag
                                    LOGGER.warning("AccessDenied to event stream RPC server");
                                    connectionState.connectionPhase = ConnectionState.Phase.DISCONNECTED;
                                    connectionState.connection.closeConnection(0);
                                    connectionState.connection = null;
                                    
                                    final AccessDeniedException ade = new AccessDeniedException("Connection access denied to event stream RPC server");
                                    if (!initialConnectFuture.isDone()) {
                                        initialConnectFuture.completeExceptionally(ade);
                                    }
                                    doOnError(lifecycleHandler, ade);
                                }
                            }
                        } else if (MessageType.PingResponse.equals(messageType)) {
                            LOGGER.finer("Ping response received");
                        } else if (MessageType.Ping.equals(messageType)) {
                            sendPingResponse(Optional.of(new MessageAmendInfo(
                                    headers.stream().filter(header -> !header.getName().startsWith(":"))
                                    .collect(Collectors.toList()), payload)))
                                .whenComplete((res, ex) -> {
                                    LOGGER.finer("Ping response sent");
                                });
                        } else if (MessageType.Connect.equals(messageType)) {
                            LOGGER.severe("Erroneous connect message type received by client. Closing");
                            //TODO: client sends protocol error here?
                            disconnect();
                        } else if (MessageType.ProtocolError.equals(messageType) || MessageType.ServerError.equals(messageType)) {
                            LOGGER.severe("Received " + messageType.name() + ": " + CRT.awsErrorName(CRT.awsLastError()));
                            final EventStreamError ese = EventStreamError.create(headers, payload, messageType);
                            if (!initialConnectFuture.isDone()) {
                                initialConnectFuture.completeExceptionally(ese);
                            }
                            doOnError(lifecycleHandler, ese);
                            disconnect();
                        } else {
                            LOGGER.severe("Unprocessed message type: " + messageType.name());
                            doOnError(lifecycleHandler, new EventStreamError("Unprocessed message type: " + messageType.name()));
                        }
                    }

                    @Override
                    protected void onConnectionClosed(int errorCode) {
                        synchronized (connectionState) {
                            connectionState.connection = null;
                            connectionState.connectionPhase = ConnectionState.Phase.DISCONNECTED;
                        }
                        LOGGER.finer("Socket connection closed: " + CRT.awsErrorName(errorCode));
                        doOnDisconnect(lifecycleHandler, errorCode);
                    }
                });
        return initialConnectFuture;
    }

    public ClientConnectionContinuation newStream(ClientConnectionContinuationHandler continuationHandler) {
        synchronized (connectionState) {
            if (connectionState.connectionPhase == ConnectionState.Phase.CONNECTED) {
                return connectionState.connection.newStream(continuationHandler);
            } else {
                throw new software.amazon.awssdk.eventstreamrpc.EventStreamClosedException("EventStream connection is not open!");
            }
        }
    }

    public void disconnect() {
        synchronized (connectionState) {
            if (connectionState.connectionPhase != ConnectionState.Phase.CLOSING &&
                    connectionState.connectionPhase != ConnectionState.Phase.DISCONNECTED) {
                connectionState.connectionPhase = ConnectionState.Phase.CLOSING;
                if (connectionState.connection != null) {
                    connectionState.connection.closeConnection(0);
                }
            }
        }
    }

    private void doOnConnect(LifecycleHandler lifecycleHandler) {
        try {
            lifecycleHandler.onConnect();
        }
        catch (Exception ex) {
            LOGGER.warning(String.format("LifecycleHandler::onConnect() threw %s : %s",
                    ex.getClass().getCanonicalName(), ex.getMessage()));
            doOnError(lifecycleHandler, ex);
        }
    }

    private void doOnError(LifecycleHandler lifecycleHandler, Throwable t) {
        try {
            if (lifecycleHandler.onError(t)) {
                LOGGER.fine("Closing connection due to LifecycleHandler::onError() returning true");
                disconnect();
            }
        }
        catch (Exception ex) {
            LOGGER.warning(String.format("Closing connection due to LifecycleHandler::onError() throwing %s : %s",
                    ex.getClass().getCanonicalName(), ex.getMessage()));
            disconnect();
        }
    }

    private void doOnDisconnect(LifecycleHandler lifecycleHandler, int errorCode) {
        try {
            lifecycleHandler.onDisconnect(errorCode);
        }
        catch (Exception ex) {
            LOGGER.warning(String.format("LifecycleHandler::onDisconnect(" + CRT.awsErrorName(errorCode) + ") threw %s : %s",
                    ex.getClass().getCanonicalName(), ex.getMessage()));
        }
    }

    /**
     * Interface to send ping. Optional MessageAmendInfo will use the headers and payload
     * for the ping message verbatim. Should trigger a pong response and server copies back
     * @param pingData
     * @return
     */
    public CompletableFuture<Void> sendPing(Optional<MessageAmendInfo> pingData) {
        ClientConnection connection;
        synchronized (connectionState) {
            if (connectionState.connectionPhase != ConnectionState.Phase.CONNECTED) {
                throw new software.amazon.awssdk.eventstreamrpc.EventStreamClosedException("EventStream connection not established");
            }
            connection = connectionState.connection;
        }
        if (connection != null && !connection.isOpen()) {
            if (pingData.isPresent()) {
                return connection.sendProtocolMessage(pingData.get().getHeaders(), pingData.get().getPayload(),
                        MessageType.Ping, 0);
            } else {
                return connection.sendProtocolMessage(null, null, MessageType.Ping, 0);
            }
        }
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Interface to send pingResponse. Optional MessageAmendInfo will use the headers and payload
     * for the ping message verbatim. Should trigger a pong response and server copies back
     * @param pingResponseData
     * @return
     */
    public CompletableFuture<Void> sendPingResponse(Optional<MessageAmendInfo> pingResponseData) {
        ClientConnection connection;
        synchronized (connectionState) {
            if (connectionState.connectionPhase != ConnectionState.Phase.CONNECTED) {
                throw new software.amazon.awssdk.eventstreamrpc.EventStreamClosedException("EventStream connection not established");
            }
            connection = connectionState.connection;
        }
        if (connection != null && !connection.isOpen()) {
            if (pingResponseData.isPresent()) {
                return connection.sendProtocolMessage(pingResponseData.get().getHeaders(), pingResponseData.get().getPayload(),
                        MessageType.Ping, 0);
            } else {
                return connection.sendProtocolMessage(null, null, MessageType.PingResponse, 0);
            }
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void close() throws Exception {
        disconnect();
    }

    /**
     * Lifecycle handler is how a client can react and respond to connectivity interruptions. Connectivity
     * interruptions are isolated from operation availability issues.
     */
    public interface LifecycleHandler {
        /**
         * Invoked only if there is a successful connection. Leaves out the error code since it will have
         * already been compared to AWS_OP_SUCCESS
         */
        void onConnect();

        /**
         * Invoked for both connect failures and disconnects from a healthy state
         *
         * @param errorCode
         */
        void onDisconnect(int errorCode);

        /**
         * Used to communicate errors that occur on the connection during any phase of its lifecycle that may
         * not appropriately or easily attach to onConnect() or onDisconnect(). Return value of this indicates
         * whether or not the client should stay connected or terminate the connection. Returning true indicates
         * the connection should terminate as a result of the error, and false indicates that the connection
         * should not. If the handler throws, is the same as returning true.
         *
         * Note: Some conditions when onError() is called will not care about the return value and always
         * result in closing the connection. AccessDeniedException is such an example
         *
         * @param t Exception
         * @returns true if the connection should be terminated as a result of handling the error
         */
        boolean onError(Throwable t);

        /**
         * Do nothing on ping by default. Inform handler of ping data
         *
         * TODO: Could use boolean return here as a hint on whether a pong reply should be sent?
         */
        default void onPing(List<Header> headers, byte[] payload) { };
    }
}
