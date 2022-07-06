package software.amazon.awssdk.eventstreamrpc.echotest;

import software.amazon.awssdk.awstest.EchoTestRPC;
import software.amazon.awssdk.awstest.EchoTestRPCClient;
import software.amazon.awssdk.awstest.EchoTestRPCService;
import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.CrtResource;
import software.amazon.awssdk.crt.io.ClientBootstrap;
import software.amazon.awssdk.crt.io.EventLoopGroup;
import software.amazon.awssdk.crt.io.HostResolver;
import software.amazon.awssdk.crt.io.SocketOptions;
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCConnection;
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCConnectionConfig;
import software.amazon.awssdk.eventstreamrpc.RpcServer;
import software.amazon.awssdk.eventstreamrpc.test.TestAuthNZHandlers;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

/**
 * Helper to runs the echo server for unit tests, or any other sandbox testing
 */
public class EchoTestServiceRunner implements AutoCloseable {
    private static final Random RANDOM = new Random(); //default instantiation uses time
    public static int randomPort() {
        return RANDOM.nextInt(65535-1024) + 1024;
    }

    private RpcServer rpcServer;
    private final EventLoopGroup elGroup;
    private final SocketOptions.SocketDomain domain;
    private final String hostname;
    private final int port;

    public EchoTestServiceRunner(EventLoopGroup elGroup, String hostname, int port) {
        this(elGroup, SocketOptions.SocketDomain.IPv4, hostname, port);
    }


    public EchoTestServiceRunner(EventLoopGroup elGroup, SocketOptions.SocketDomain domain, String hostname, int port) {
        this.elGroup = elGroup;
        this.hostname = hostname;
        this.port = port;
        this.domain = domain;
    }


    public void runService() {
        try (SocketOptions socketOptions = new SocketOptions()) {
            socketOptions.connectTimeoutMs = 3000;
            socketOptions.domain = domain;
            socketOptions.type = SocketOptions.SocketType.STREAM;

            final EchoTestRPCService service = new EchoTestRPCService();
            //wiring of operation handlers
            service.setEchoMessageHandler(EchoMessageHandler::new);
            service.setEchoStreamMessagesHandler(EchoStreamMessagesHandler::new);
            service.setCauseServiceErrorHandler(CauseServiceErrorHandler::new);
            service.setCauseStreamServiceToErrorHandler(CauseStreamServiceToError::new);
            service.setGetAllCustomersHandler(GetAllCustomersHandler::new);
            service.setGetAllProductsHandler(GetAllProductsHandler::new);

            service.setAuthenticationHandler(TestAuthNZHandlers.getAuthNHandler());
            service.setAuthorizationHandler(TestAuthNZHandlers.getAuthZHandler());
            rpcServer = new RpcServer(elGroup, socketOptions, null, hostname, port, service);
            rpcServer.runServer();
        }
    }

    @Override
    public void close() throws Exception {
        if (rpcServer != null) {
            rpcServer.close();
        }
    }

    /**
     * Executes testClientLogic in a runnable.
     *  * If the connection encountered an error before the clientTestLogic completes this method will throw that error.
     *  * If the client completes normally first, this will complete normally.
     *  * If the client throws an exception first, this method will throw that exception
     *
     * Note: Use the returned CompletableFuture to check if any errors are occurring AFTER the testClientLogic runs. This
     * may be needed/worth checking
     *
     * @param testClientLogic return
     * @return A CompletableFuture of any connection level error that may have occurred after the testClientLogic completes
     * @throws Exception throws an exception either from the test client logic having thrown, or the connection itself
     *                   encountering an error before test client logic completes
     */
    public static CompletableFuture<Void> runLocalEchoTestServer(final BiConsumer<EventStreamRPCConnection, EchoTestRPC> testClientLogic) throws Exception {
        final int port = randomPort();
        final String hostname = "127.0.0.1";
        try (final EventLoopGroup elGroup = new EventLoopGroup(1);
             final EchoTestServiceRunner runner = new EchoTestServiceRunner(elGroup, hostname, port);
             final HostResolver resolver = new HostResolver(elGroup, 64);
             final ClientBootstrap clientBootstrap = new ClientBootstrap(elGroup, resolver);
             final SocketOptions socketOptions = new SocketOptions()) {
            socketOptions.connectTimeoutMs = 3000;
            socketOptions.domain = SocketOptions.SocketDomain.IPv4;
            socketOptions.type = SocketOptions.SocketType.STREAM;

            runner.runService();
            final EventStreamRPCConnectionConfig config = new EventStreamRPCConnectionConfig(clientBootstrap, elGroup,
                    socketOptions, null, hostname, port, () -> TestAuthNZHandlers.getClientAuth("accepted.foo"));
            try (EventStreamRPCConnection connection = new EventStreamRPCConnection(config)) {
                final CompletableFuture<Void> connectFuture = new CompletableFuture<>();
                final CompletableFuture<Void> clientErrorFuture = new CompletableFuture<>(); //only completes exceptionally if there's an error
                connection.connect(new EventStreamRPCConnection.LifecycleHandler() {
                    @Override
                    public void onConnect() {
                        connectFuture.complete(null);
                    }

                    @Override
                    public void onDisconnect(int errorCode) {
                        if (!connectFuture.isDone()) {
                            connectFuture.completeExceptionally(new RuntimeException("Client initial connection failed due to: " + CRT.awsErrorName(errorCode)));
                        } else if (errorCode != CRT.AWS_CRT_SUCCESS) {
                            clientErrorFuture.completeExceptionally(new RuntimeException("Client disconnected due to: " + CRT.awsErrorName(errorCode)));
                        } else { } //don't care if it normal closure/disconnect
                    }

                    @Override
                    public boolean onError(Throwable t) {
                        if (!connectFuture.isDone()) {
                            connectFuture.completeExceptionally(t);
                        } else {
                            clientErrorFuture.completeExceptionally(t);
                        }
                        return false;
                    }
                });
                connectFuture.get(480, TimeUnit.SECONDS);    //wait for connection to move forward
                final EchoTestRPC client = new EchoTestRPCClient(connection);
                final CompletableFuture<Object> runClientOrError =
                        CompletableFuture.anyOf(clientErrorFuture,
                                CompletableFuture.runAsync(() -> testClientLogic.accept(connection, client),
                                        Executors.newSingleThreadExecutor()));
                runClientOrError.get(240, TimeUnit.SECONDS);
                return clientErrorFuture;
            }
        }
    }

    /**
     * Enables a bit of a performance/load test by reconnecting the client to the same running server multiple times
     * and each time the client connects, it runs the test logic for the number of times.
     * 
     * !!! WARNING SocketOptions.SocketDomain is LOCAL for this server. Test client must match
     */
    public static void runLocalEchoTestServerClientLoopUnixDomain(final String domainSocket,
                                                                  final BiConsumer<EventStreamRPCConnection, EchoTestRPC> testClientLogic, int nCount) throws Exception {
        final int port = randomPort();
        Files.deleteIfExists(Paths.get(domainSocket));
        try (final EventLoopGroup elGroup = new EventLoopGroup(1);
             final EchoTestServiceRunner runner = new EchoTestServiceRunner(elGroup, SocketOptions.SocketDomain.LOCAL, domainSocket, port);
             final SocketOptions socketOptions = new SocketOptions();
             final HostResolver hostResolver = new HostResolver(elGroup, 64);
             final ClientBootstrap clientBootstrap = new ClientBootstrap(elGroup, hostResolver)) {
            socketOptions.connectTimeoutMs = 3000;
            socketOptions.domain = SocketOptions.SocketDomain.LOCAL;
            socketOptions.type = SocketOptions.SocketType.STREAM;

            runner.runService();
            for (int i = 0; i < nCount; ++i) {
                final EventStreamRPCConnectionConfig config = new EventStreamRPCConnectionConfig(clientBootstrap, elGroup,
                        socketOptions, null, domainSocket, port, () -> TestAuthNZHandlers.getClientAuth("accepted.foo"));
                try (EventStreamRPCConnection connection = new EventStreamRPCConnection(config)) {
                    final CompletableFuture<Void> connectFuture = new CompletableFuture<>();
                    final CompletableFuture<Void> clientErrorFuture = new CompletableFuture<>(); //only completes exceptionally if there's an error
                    connection.connect(new EventStreamRPCConnection.LifecycleHandler() {
                        @Override
                        public void onConnect() {
                            connectFuture.complete(null);
                        }

                        @Override
                        public void onDisconnect(int errorCode) {
                            if (!connectFuture.isDone()) {
                                connectFuture.completeExceptionally(new RuntimeException("Client initial connection failed due to: " + CRT.awsErrorName(errorCode)));
                            } else if (errorCode != CRT.AWS_CRT_SUCCESS) {
                                clientErrorFuture.completeExceptionally(new RuntimeException("Client disconnected due to: " + CRT.awsErrorName(errorCode)));
                            } else {
                            } //don't care if it normal closure/disconnect
                        }

                        @Override
                        public boolean onError(Throwable t) {
                            if (!connectFuture.isDone()) {
                                connectFuture.completeExceptionally(t);
                            } else {
                                clientErrorFuture.completeExceptionally(t);
                            }
                            return false;
                        }
                    });
                    connectFuture.get(30, TimeUnit.SECONDS);    //wait for connection to move forward
                    final EchoTestRPC client = new EchoTestRPCClient(connection);
                    final CompletableFuture<Object> runClientOrError =
                            CompletableFuture.anyOf(clientErrorFuture, CompletableFuture.runAsync(
                                    () -> testClientLogic.accept(connection, client), Executors.newSingleThreadExecutor()));
                    runClientOrError.get(240, TimeUnit.SECONDS);
                }
            }
        }
        //given this method is rather explicitly meant to be used in JUnit tests and intends to check per iteration
        //calling waitForNoResources() is necessary to call here, and appropriate
        CrtResource.waitForNoResources();
    }

    /**
     * Runs this dumb service via CLI
     * @param args
     * @throws Exception
     */
    public static void main(String []args) throws Exception {
        try(final EventLoopGroup elGroup = new EventLoopGroup(1);
            EchoTestServiceRunner runner = new EchoTestServiceRunner(elGroup, args[0], Integer.parseInt(args[1]))) {
            runner.runService();
            final Semaphore semaphore = new Semaphore(1);
            semaphore.acquire();
            semaphore.acquire();    //wait until control+C
        }
    }
}
