package software.amazon.awssdk.eventstreamrpc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.eventstream.*;
import software.amazon.awssdk.eventstreamrpc.model.AccessDeniedException;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamError;

public class EventStreamRPCConnection implements AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(EventStreamRPCConnection.class.getName());

    private final EventStreamRPCConnectionConfig config;
    private final AtomicBoolean isConnecting;
    private ClientConnection connection;
    private ClientConnection pendingConnection;

    public EventStreamRPCConnection(final EventStreamRPCConnectionConfig config) {
        this.config = config;
        this.isConnecting = new AtomicBoolean(false);
        this.connection = null;
        this.pendingConnection = null;
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
        if (connection != null) {
            throw new IllegalStateException("Connection already exists");
        }
        if (!isConnecting.compareAndSet(false, true)) {
            throw new IllegalStateException("Connection established is underway");
        }
        final CompletableFuture<Void> initialConnectFuture = new CompletableFuture<>();

        ClientConnection.connect(config.getHost(), (short) config.getPort(), config.getSocketOptions(),
                config.getTlsContext(), config.getClientBootstrap(), new ClientConnectionHandler() {
                    @Override
                    protected void onConnectionSetup(ClientConnection clientConnection, int errorCode) {
                        pendingConnection = clientConnection;
                        LOGGER.info(String.format("Socket connection %s:%d to server result [%s]",
                                config.getHost(), config.getPort(), CRT.awsErrorName(errorCode)));
                        if (CRT.AWS_CRT_SUCCESS == errorCode) {
                            final MessageAmendInfo messageAmendInfo = config.getConnectMessageAmender().get();
                            final List<Header> headers = new ArrayList<>(messageAmendInfo.getHeaders().size() + 1);
                            headers.add(Header.createHeader(EventStreamRPCServiceModel.VERSION_HEADER,
                                    getVersionString()));
                            headers.addAll(messageAmendInfo.getHeaders().stream()
                                    .filter(header -> !header.getName().equals(EventStreamRPCServiceModel.VERSION_HEADER))
                                    .collect(Collectors.toList()));
                            pendingConnection.sendProtocolMessage(headers,
                                    messageAmendInfo.getPayload(), MessageType.Connect, 0);
                        } else {
                            pendingConnection = null;
                            doOnDisconnect(lifecycleHandler, errorCode);
                        }
                    }

                    @Override
                    protected void onProtocolMessage(List<Header> headers, byte[] payload, MessageType messageType, int messageFlags) {
                        if (MessageType.ConnectAck.equals(messageType)) {
                            try {
                                if (messageFlags == MessageFlags.ConnectionAccepted.getByteValue()) {
                                    connection = pendingConnection;
                                    //now the client is open for business to invoke operations
                                    LOGGER.info("Connection established with event stream RPC server");
                                    if (!initialConnectFuture.isDone()) {
                                        initialConnectFuture.complete(null);
                                    }
                                    doOnConnect(lifecycleHandler);
                                } else {
                                    //This is access denied, implied due to not having ConnectionAccepted msg flag
                                    LOGGER.warning("AccessDenied to event stream RPC server");
                                    final AccessDeniedException ade = new AccessDeniedException("Connection access denied to event stream RPC server");
                                    if (!initialConnectFuture.isDone()) {
                                        initialConnectFuture.completeExceptionally(ade);
                                    }
                                    doOnError(lifecycleHandler, ade);
                                    //close pending connection explicitly since it's not fully established
                                    pendingConnection.closeConnection(0);
                                }
                            } finally {
                                //unlock for either failure or success
                                pendingConnection = null;
                                isConnecting.compareAndSet(true, false);
                            }
                        } else if (MessageType.PingResponse.equals(messageType)) {
                            LOGGER.finer("Ping response received");
                        } else if (MessageType.Ping.equals(messageType)) {
                            sendPingResponse(Optional.of(new MessageAmendInfo(headers, payload)))
                                .whenComplete((res, ex) -> {
                                    LOGGER.finer("Ping response sent");
                                });
                        } else if (MessageType.Connect.equals(messageType)) {
                            LOGGER.severe("Erroneous connect message type received by client. Closing");
                            //TODO: client sends protocol error here?
                            connection.closeConnection(0);

                        } else if (MessageType.ProtocolError.equals(messageType) || MessageType.ServerError.equals(messageType)) {
                            LOGGER.severe("Received " + messageType.name() + ": " + CRT.awsErrorName(CRT.awsLastError()));
                            final EventStreamError ese = EventStreamError.create(headers, payload, messageType);
                            if (!initialConnectFuture.isDone()) {
                                initialConnectFuture.completeExceptionally(ese);
                            }
                            doOnError(lifecycleHandler, ese);
                            connection.closeConnection(0);
                        } else {
                            LOGGER.severe("Unprocessed message type: " + messageType.name());
                            doOnError(lifecycleHandler, new EventStreamError("Unprocessed message type: " + messageType.name()));
                        }
                    }

                    @Override
                    protected void onConnectionClosed(int errorCode) {
                        connection = null;  //null this out so a future attempt can be made
                        LOGGER.finer("Socket connection closed: " + CRT.awsErrorName(errorCode));
                        doOnDisconnect(lifecycleHandler, errorCode);
                    }
                });
        return initialConnectFuture;
    }

    public void disconnect() {
        if (!isConnecting.get() && connection != null) {
            connection.closeConnection(0);
            connection = null;
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
        final ClientConnection connection = this.connection;
        if (connection != null && connection.isClosed()) {
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
        final ClientConnection connection = this.connection;
        if (connection != null && connection.isClosed()) {
            if (pingResponseData.isPresent()) {
                return connection.sendProtocolMessage(pingResponseData.get().getHeaders(), pingResponseData.get().getPayload(),
                        MessageType.Ping, 0);
            } else {
                return connection.sendProtocolMessage(null, null, MessageType.PingResponse, 0);
            }
        }
        return CompletableFuture.completedFuture(null);
    }

    public ClientConnection getConnection() {
        return connection;
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
