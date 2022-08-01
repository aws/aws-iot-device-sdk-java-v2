import com.google.gson.Gson;
import greengrass.GeneratedAbstractCreateLocalDeploymentOperationHandler;
import greengrass.GeneratedAbstractSubscribeToTopicOperationHandler;
import greengrass.GreengrassCoreIPCService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.aws.greengrass.GreengrassCoreIPCClientV2;
import software.amazon.awssdk.aws.greengrass.SubscribeToTopicResponseHandler;
import software.amazon.awssdk.aws.greengrass.model.BinaryMessage;
import software.amazon.awssdk.aws.greengrass.model.CreateLocalDeploymentRequest;
import software.amazon.awssdk.aws.greengrass.model.CreateLocalDeploymentResponse;
import software.amazon.awssdk.aws.greengrass.model.SubscribeToTopicRequest;
import software.amazon.awssdk.aws.greengrass.model.SubscribeToTopicResponse;
import software.amazon.awssdk.aws.greengrass.model.SubscriptionResponseMessage;
import software.amazon.awssdk.crt.CrtResource;
import software.amazon.awssdk.crt.io.EventLoopGroup;
import software.amazon.awssdk.crt.io.SocketOptions;
import software.amazon.awssdk.eventstreamrpc.Authorization;
import software.amazon.awssdk.eventstreamrpc.GreengrassEventStreamConnectMessage;
import software.amazon.awssdk.eventstreamrpc.RpcServer;
import software.amazon.awssdk.eventstreamrpc.StreamResponseHandler;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GreengrassV2ClientTest {
    private static final Random RANDOM = new Random(); //default instantiation uses time
    private int port;
    private RpcServer ipcServer;
    private GreengrassEventStreamConnectMessage authenticationRequest;
    private GreengrassCoreIPCClientV2 client;
    private CompletableFuture<Void> subscriptionClosed = new CompletableFuture<>();

    public static int randomPort() {
        return RANDOM.nextInt(65535 - 1024) + 1024;
    }

    @BeforeEach
    public void before() throws IOException {
        port = randomPort();

        try (final EventLoopGroup elGroup = new EventLoopGroup(1);
             SocketOptions socketOptions = new SocketOptions()) {
            socketOptions.connectTimeoutMs = 3000;
            socketOptions.domain = SocketOptions.SocketDomain.IPv4;
            socketOptions.type = SocketOptions.SocketType.STREAM;

            GreengrassCoreIPCService service = new GreengrassCoreIPCService();
            service.setCreateLocalDeploymentHandler((c) -> new GeneratedAbstractCreateLocalDeploymentOperationHandler(c) {
                @Override
                protected void onStreamClosed() {
                }

                @Override
                public CreateLocalDeploymentResponse handleRequest(CreateLocalDeploymentRequest request) {
                    return new CreateLocalDeploymentResponse().withDeploymentId("deployment");
                }

                @Override
                public void handleStreamEvent(EventStreamJsonMessage streamRequestEvent) {
                }
            });
            service.setSubscribeToTopicHandler((c) -> new GeneratedAbstractSubscribeToTopicOperationHandler(c) {
                @Override
                protected void onStreamClosed() {
                    subscriptionClosed.complete(null);
                }

                @Override
                public SubscribeToTopicResponse handleRequest(SubscribeToTopicRequest request) {
                    new Thread(() -> {
                        sendStreamEvent(new SubscriptionResponseMessage().withBinaryMessage(
                                new BinaryMessage().withMessage("message".getBytes(StandardCharsets.UTF_8))));
                    }).start();
                    return new SubscribeToTopicResponse().withTopicName(request.getTopic());
                }

                @Override
                public void handleStreamEvent(EventStreamJsonMessage streamRequestEvent) {
                }
            });
            service.setAuthenticationHandler((headers, bytes) -> {
                authenticationRequest = new Gson().fromJson(new String(bytes), GreengrassEventStreamConnectMessage.class);
                return () -> "connected";
            });
            service.setAuthorizationHandler(authenticationData -> Authorization.ACCEPT);

            ipcServer = new RpcServer(elGroup, socketOptions, null, "127.0.0.1", port, service);
            ipcServer.runServer();

            client = GreengrassCoreIPCClientV2.builder().withPort(port).withSocketPath("127.0.0.1")
                    .withSocketDomain(SocketOptions.SocketDomain.IPv4).withAuthToken("myAuthToken").build();
        }
    }

    @AfterEach
    public void after() throws Exception {
        ipcServer.close();
        if (client != null) {
            client.close();
        }

        CrtResource.waitForNoResources();
    }

    @Test
    public void testV2Client() throws InterruptedException, ExecutionException, TimeoutException {
        assertEquals(authenticationRequest.getAuthToken(), "myAuthToken");
        CreateLocalDeploymentResponse depResp = client.createLocalDeployment(new CreateLocalDeploymentRequest());
        assertEquals("deployment", depResp.getDeploymentId());

        CompletableFuture<CreateLocalDeploymentResponse> asyncDepResp =
                client.createLocalDeploymentAsync(new CreateLocalDeploymentRequest());
        assertEquals("deployment", asyncDepResp.get().getDeploymentId());

        CompletableFuture<String> receivedMessage = new CompletableFuture<>();
        CompletableFuture<String> finalReceivedMessage = receivedMessage;
        GreengrassCoreIPCClientV2.StreamingResponse<SubscribeToTopicResponse, SubscribeToTopicResponseHandler> subResp =
                client.subscribeToTopic(new SubscribeToTopicRequest().withTopic("abc"), (x) -> {
                    if (!Thread.currentThread().getName().contains("pool")) {
                        System.out.println(Thread.currentThread().getName());
                        finalReceivedMessage.completeExceptionally(
                                new RuntimeException("Ran on event loop instead of executor"));
                    }
                    finalReceivedMessage.complete(new String(x.getBinaryMessage().getMessage()));
                }, Optional.empty(), Optional.empty());

        assertEquals("message", receivedMessage.get());
        subResp.getHandler().closeStream().get();
        subscriptionClosed.get(5, TimeUnit.SECONDS);

        subscriptionClosed = new CompletableFuture<>();
        receivedMessage = new CompletableFuture<>();
        CompletableFuture<String> finalReceivedMessage1 = receivedMessage;
        subResp = client.subscribeToTopic(new SubscribeToTopicRequest().withTopic("abc"),
                new StreamResponseHandler<SubscriptionResponseMessage>() {
            @Override
            public void onStreamEvent(SubscriptionResponseMessage streamEvent) {
                if (!Thread.currentThread().getName().contains("pool")) {
                    finalReceivedMessage1.completeExceptionally(
                            new RuntimeException("Ran on event loop instead of executor"));
                }
                finalReceivedMessage1.complete(new String(streamEvent.getBinaryMessage().getMessage()));
            }

            @Override
            public boolean onStreamError(Throwable error) {
                return false;
            }

            @Override
            public void onStreamClosed() {
            }
        });

        assertEquals("message", receivedMessage.get());
        subResp.getHandler().closeStream().get();
        subscriptionClosed.get(5, TimeUnit.SECONDS);

        subscriptionClosed = new CompletableFuture<>();
        receivedMessage = new CompletableFuture<>();
        CompletableFuture<String> finalReceivedMessage2 = receivedMessage;
        GreengrassCoreIPCClientV2.StreamingResponse<CompletableFuture<SubscribeToTopicResponse>, SubscribeToTopicResponseHandler>
                subRespAsync = client.subscribeToTopicAsync(new SubscribeToTopicRequest().withTopic("abc"),
                new StreamResponseHandler<SubscriptionResponseMessage>() {
                    @Override
                    public void onStreamEvent(SubscriptionResponseMessage streamEvent) {
                        if (!Thread.currentThread().getName().contains("pool")) {
                            finalReceivedMessage2.completeExceptionally(
                                    new RuntimeException("Ran on event loop instead of executor"));
                        }
                        finalReceivedMessage2.complete(new String(streamEvent.getBinaryMessage().getMessage()));
                    }

                    @Override
                    public boolean onStreamError(Throwable error) {
                        return false;
                    }

                    @Override
                    public void onStreamClosed() {
                    }
                });

        assertEquals("message", receivedMessage.get());
        subRespAsync.getHandler().closeStream().get();
        subscriptionClosed.get(5, TimeUnit.SECONDS);

        subscriptionClosed = new CompletableFuture<>();
        receivedMessage = new CompletableFuture<>();
        CompletableFuture<String> finalReceivedMessage3 = receivedMessage;
        subRespAsync = client.subscribeToTopicAsync(new SubscribeToTopicRequest().withTopic("abc"), (x) -> {
            if (!Thread.currentThread().getName().contains("pool")) {
                finalReceivedMessage3.completeExceptionally(
                        new RuntimeException("Ran on event loop instead of executor"));
            }
            finalReceivedMessage3.complete(new String(x.getBinaryMessage().getMessage()));
        }, Optional.empty(), Optional.empty());

        assertEquals("message", receivedMessage.get());
        subRespAsync.getHandler().closeStream().get();
        subscriptionClosed.get(5, TimeUnit.SECONDS);
    }
}
