package software.amazon.awssdk.eventstreamrpc;

import junit.framework.AssertionFailedError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.Log;
import software.amazon.awssdk.crt.eventstream.Header;
import software.amazon.awssdk.crt.eventstream.MessageType;
import software.amazon.awssdk.crt.io.ClientBootstrap;
import software.amazon.awssdk.crt.io.EventLoopGroup;
import software.amazon.awssdk.crt.io.SocketOptions;
import software.amazon.awssdk.eventstreamrpc.model.AccessDeniedException;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;
import software.amazon.awssdk.eventstreamrpc.test.TestAuthNZHandlers;
import software.amazon.awssdk.eventstreamrpc.test.TestIpcServiceHandler;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class EventStreamRPCClientTests {
    private static final Random RANDOM = new Random(); //default instantiation uses time
    public static int randomPort() {
        return RANDOM.nextInt(65535-1024) + 1024;
    }

    static {
        Log.initLoggingToFile(Log.LogLevel.Trace, "crt-EventStreamRPCClientTests.log");
    }

    @Test
    public void testConnectionEstablished() {
        final int port = randomPort();
        SocketOptions socketOptions = new SocketOptions();
        socketOptions.connectTimeoutMs = 3000;
        socketOptions.domain = SocketOptions.SocketDomain.IPv4;
        socketOptions.type = SocketOptions.SocketType.STREAM;

        //below class is generated and just gets instantiated for what it is
        final TestIpcServiceHandler service = new TestIpcServiceHandler(false,
                request -> request,
                EventStreamJsonMessage.class, EventStreamJsonMessage.class,
                EventStreamJsonMessage.class, EventStreamJsonMessage.class);

        //handlers aren't relevant since no request will be made
        service.setAuthenticationHandler(TestAuthNZHandlers.getAuthNHandler());
        service.setAuthorizationHandler(TestAuthNZHandlers.getAuthZHandler());

        final CompletableFuture<Void> disconnectFuture = new CompletableFuture<>();
        final CompletableFuture<Void> connectedFuture = new CompletableFuture<>();

        try(final EventLoopGroup elGroup = new EventLoopGroup(1);
            final ClientBootstrap clientBootstrap = new ClientBootstrap(elGroup, null)) {
            final RpcServer ipcServer = new RpcServer(elGroup, socketOptions, null, "127.0.0.1", port, service);
            ipcServer.runServer();

            final EventStreamRPCConnectionConfig config = new EventStreamRPCConnectionConfig(
                    clientBootstrap, elGroup, socketOptions, null, "127.0.0.1", port, () ->
                    {
                        final List<Header> headers = new LinkedList<>();
                        headers.add(Header.createHeader("client-name", "accepted.foo"));
                        return new MessageAmendInfo(headers, null);
                    }
            );
            final EventStreamRPCConnection connection = new EventStreamRPCConnection(config);
            final EventStreamRPCConnection.LifecycleHandler lifecycleHandler = new EventStreamRPCConnection.LifecycleHandler() {
                @Override
                public void onConnect() {
                    connectedFuture.complete(null);
                }

                @Override
                public void onDisconnect(int errorCode) {
                    disconnectFuture.complete(null);
                }

                @Override
                public boolean onError(Throwable t) {
                    if (!connectedFuture.isDone()) {
                        connectedFuture.completeExceptionally(t);
                    }
                    if (!disconnectFuture.isDone()) {
                        disconnectFuture.completeExceptionally(t);
                    }
                    return true;
                }
            };
            final CompletableFuture<Void> initialConnect = connection.connect(lifecycleHandler);
            //highly likely above line is establishing connection
            Assertions.assertThrows(IllegalStateException.class, () -> connection.connect(lifecycleHandler));
            connectedFuture.get(2, TimeUnit.SECONDS);
            Assertions.assertTrue(initialConnect.isDone() && !initialConnect.isCompletedExceptionally());
            //connection is fully established
            Assertions.assertThrows(IllegalStateException.class, () -> connection.connect(lifecycleHandler));
            connection.disconnect();
        } catch (ExecutionException | TimeoutException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                disconnectFuture.get(5, TimeUnit.SECONDS);
            } catch (ExecutionException | TimeoutException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    public void testConnectionVersionMismatch() {
        final int port = randomPort();
        SocketOptions socketOptions = new SocketOptions();
        socketOptions.connectTimeoutMs = 3000;
        socketOptions.domain = SocketOptions.SocketDomain.IPv4;
        socketOptions.type = SocketOptions.SocketType.STREAM;

        //below class is generated and just gets instantiated for what it is
        final TestIpcServiceHandler service = new TestIpcServiceHandler(false,
                request -> request,
                EventStreamJsonMessage.class, EventStreamJsonMessage.class,
                EventStreamJsonMessage.class, EventStreamJsonMessage.class);

        //handlers aren't relevant since no request will be made
        service.setAuthenticationHandler(TestAuthNZHandlers.getAuthNHandler());
        service.setAuthorizationHandler(TestAuthNZHandlers.getAuthZHandler());

        try(final EventLoopGroup elGroup = new EventLoopGroup(1);
            final ClientBootstrap clientBootstrap = new ClientBootstrap(elGroup, null)) {
            final RpcServer ipcServer = new RpcServer(elGroup, socketOptions, null, "127.0.0.1", port, service);
            ipcServer.runServer();

            final EventStreamRPCConnectionConfig config = new EventStreamRPCConnectionConfig(
                    clientBootstrap, elGroup, socketOptions, null, "127.0.0.1", port, () ->
            {
                final List<Header> headers = new LinkedList<>();
                headers.add(Header.createHeader("client-name", "accepted.foo"));
                return new MessageAmendInfo(headers, null);
            }
            );
            final EventStreamRPCConnection connection = new EventStreamRPCConnection(config) {
                @Override
                protected String getVersionString() {
                    return "19.19.19";
                }
            };
            final CompletableFuture<Throwable> futureAccessDenied = new CompletableFuture<>();
            final CompletableFuture<Integer> shutdownFuture = new CompletableFuture<>();
            final CompletableFuture<Void> initialConnect = connection.connect(new EventStreamRPCConnection.LifecycleHandler() {
                @Override
                public void onConnect() {
                    futureAccessDenied.completeExceptionally(new AssertionFailedError("onConnect lifecycle handler method should not be called!"));
                }
                @Override
                public void onDisconnect(int errorCode) {
                    shutdownFuture.complete(errorCode);
                }
                @Override
                public boolean onError(Throwable t) {
                    futureAccessDenied.complete(t);
                    return true;
                }
            });
            futureAccessDenied.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void testConnectionAccessDenied() {
        final int port = randomPort();
        SocketOptions socketOptions = new SocketOptions();
        socketOptions.connectTimeoutMs = 3000;
        socketOptions.domain = SocketOptions.SocketDomain.IPv4;
        socketOptions.type = SocketOptions.SocketType.STREAM;

        //below class is generated and just gets instantiated for what it is
        final TestIpcServiceHandler service = new TestIpcServiceHandler(false,
                request -> request,
                EventStreamJsonMessage.class, EventStreamJsonMessage.class,
                EventStreamJsonMessage.class, EventStreamJsonMessage.class);

        //handlers aren't relevant since no request will be made
        service.setAuthenticationHandler(TestAuthNZHandlers.getAuthNHandler());
        service.setAuthorizationHandler(TestAuthNZHandlers.getAuthZHandler());
        final Semaphore semaphore = new Semaphore(1);

        try(final EventLoopGroup elGroup = new EventLoopGroup(1);
            final ClientBootstrap clientBootstrap = new ClientBootstrap(elGroup, null)) {
            final RpcServer ipcServer = new RpcServer(elGroup, socketOptions, null, "127.0.0.1", port, service);
            ipcServer.runServer();

            semaphore.acquire();
            final EventStreamRPCConnectionConfig config = new EventStreamRPCConnectionConfig(
                    clientBootstrap, elGroup, socketOptions, null, "127.0.0.1", port, () ->
                {
                    final List<Header> headers = new LinkedList<>();
                    headers.add(Header.createHeader("client-name", "rejected.foo"));
                    return new MessageAmendInfo(headers, null);
                }
            );
            final EventStreamRPCConnection connection = new EventStreamRPCConnection(config);
            final CompletableFuture<Void> initialConnect = connection.connect(new EventStreamRPCConnection.LifecycleHandler() {
                @Override
                public void onConnect() {
                    Assertions.fail("Full connection expected to be rejected");
                    semaphore.release();
                }
                @Override
                public void onDisconnect(int errorCode) {
                    System.out.println("Client disconnected...");
                    semaphore.release();
                }
                @Override
                public boolean onError(Throwable t) {
                    Assertions.assertEquals(t.getClass(), AccessDeniedException.class, "Expected access denied exception type!");
                    semaphore.release();
                    return false;
                }
            });
            semaphore.acquire();
            Assertions.assertTrue(initialConnect.isDone());
            try {
                initialConnect.get();
            } catch (ExecutionException e) {
                Assertions.assertTrue(e.getCause() instanceof AccessDeniedException);
            }
            connection.disconnect();
            semaphore.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Runs a dummy service on a random port and creates a connection for it so a test can do whatever on the connection.
     * Assumes the particular operation invocations don't matter.
     * @param lifecycleHandler
     * @param testCode
     */
    public static void runDummyService(EventStreamRPCConnection.LifecycleHandler lifecycleHandler, Consumer<EventStreamRPCConnection> testCode) {
        final int port = randomPort();
        SocketOptions socketOptions = new SocketOptions();
        socketOptions.connectTimeoutMs = 3000;
        socketOptions.domain = SocketOptions.SocketDomain.IPv4;
        socketOptions.type = SocketOptions.SocketType.STREAM;
        final TestIpcServiceHandler service = new TestIpcServiceHandler(false,
                request -> request,
                EventStreamJsonMessage.class, EventStreamJsonMessage.class,
                EventStreamJsonMessage.class, EventStreamJsonMessage.class);

        service.setAuthenticationHandler(TestAuthNZHandlers.getAuthNHandler());
        service.setAuthorizationHandler(TestAuthNZHandlers.getAuthZHandler());

        try(final EventLoopGroup elGroup = new EventLoopGroup(1);
            final ClientBootstrap clientBootstrap = new ClientBootstrap(elGroup, null)) {
            final RpcServer ipcServer = new RpcServer(elGroup, socketOptions, null, "127.0.0.1", port, service);
            ipcServer.runServer();

            final EventStreamRPCConnectionConfig config = new EventStreamRPCConnectionConfig(
                    clientBootstrap, elGroup, socketOptions, null, "127.0.0.1", port, () ->
                {
                    final List<Header> headers = new LinkedList<>();
                    headers.add(Header.createHeader("client-name", "accepted.foo"));
                    return new MessageAmendInfo(headers, null);
                }
            );
            final EventStreamRPCConnection connection = new EventStreamRPCConnection(config);
            final CompletableFuture<Void> initialConnect = connection.connect(lifecycleHandler);
            initialConnect.get(2, TimeUnit.SECONDS);
            testCode.accept(connection);
        } catch (ExecutionException | TimeoutException | InterruptedException e) {
            Assertions.fail(e);
        }
    }
}
