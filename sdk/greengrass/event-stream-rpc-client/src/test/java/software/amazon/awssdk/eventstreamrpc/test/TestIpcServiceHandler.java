package software.amazon.awssdk.eventstreamrpc.test;

import software.amazon.awssdk.crt.eventstream.ServerConnectionContinuationHandler;
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCServiceHandler;
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCServiceModel;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandler;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandlerContext;
import software.amazon.awssdk.eventstreamrpc.OperationModelContext;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;

/**
 * Test server handler operates on all operations and simply stores what it recieves in a way that's publicly
 * retrievable for verifiable IO behavior
 */
public class TestIpcServiceHandler extends EventStreamRPCServiceHandler {
    private final Queue<Map.Entry<String, TestOperationContinuationHandler>> continuationHandlerQueue;
    private boolean streamClosed;

    private final boolean isStreaming;
    private Function<EventStreamJsonMessage, EventStreamJsonMessage> requestHandler;

    private Class requestClass;
    private Class responseClass;
    private Class streamingRequestClass;
    private Class streamingResponseClass;

    private EventStreamRPCServiceModel serviceModel;

    public TestIpcServiceHandler(boolean isStreaming, Function<EventStreamJsonMessage, EventStreamJsonMessage> requestHandler,
                                 Class requestClass, Class responseClass, Class streamingRequestClass, Class streamingResponseClass) {
        this.isStreaming = isStreaming;
        this.requestHandler = requestHandler;
        this.requestClass = requestClass;
        this.responseClass = responseClass;
        this.streamingRequestClass = streamingRequestClass;
        this.streamingResponseClass = streamingResponseClass;
        this.continuationHandlerQueue = new LinkedList<>();

        this.serviceModel = new EventStreamRPCServiceModel() {
            @Override
            public String getServiceName() {
                return "TestIpcService";
            }

            @Override
            public Collection<String> getAllOperations() {
                return new HashSet<>();
            }

            @Override
            protected Optional<Class<? extends EventStreamJsonMessage>> getServiceClassType(String applicationModelType) {
                return Optional.empty();
            }

            @Override
            public OperationModelContext getOperationModelContext(String operationName) {
                return new OperationModelContext() {
                    @Override
                    public EventStreamRPCServiceModel getServiceModel() {
                        return serviceModel;
                    }

                    @Override
                    public String getOperationName() {
                        return operationName;
                    }

                    @Override
                    public Class getRequestTypeClass() {
                        return requestClass;
                    }

                    @Override
                    public String getRequestApplicationModelType() {
                        return "aws.greengrass#" + requestClass.getSimpleName();
                    }

                    @Override
                    public Class getResponseTypeClass() {
                        return responseClass;
                    }

                    @Override
                    public String getResponseApplicationModelType() {
                        return "aws.greengrass#" + responseClass.getSimpleName();
                    }

                    @Override
                    public Optional<Class> getStreamingRequestTypeClass() {
                        return Optional.of(streamingRequestClass);
                    }

                    @Override
                    public Optional<String> getStreamingRequestApplicationModelType() {
                        return Optional.of("aws.greengrass#" + streamingRequestClass.getSimpleName());
                    }

                    @Override
                    public Optional<Class> getStreamingResponseTypeClass() {
                        return Optional.of(streamingResponseClass);
                    }

                    @Override
                    public Optional<String> getStreamingResponseApplicationModelType() {
                        return Optional.of("aws.greengrass#" + streamingResponseClass.getSimpleName());
                    }

                    @Override
                    public boolean isStreamingOperation() {
                        return isStreaming;
                    }
                };
            }
        };
    }

    @Override
    protected EventStreamRPCServiceModel getServiceModel() {
        return serviceModel;
    }

    /**
     * Probably only useful for logging
     *
     * @return Returns the service name for the set of RPC operations
     */
    @Override
    public String getServiceName() {
        return "TestIpcServiceHandler";
    }

    /**
     * Exposes some things so tests can verify
     */
    public static abstract class TestOperationContinuationHandler extends OperationContinuationHandler {
        public TestOperationContinuationHandler(OperationContinuationHandlerContext context) {
            super(context);
        }

        public OperationContinuationHandlerContext getContextForTest() {
            return getContext();
        }
    }

    @Override
    public Function<OperationContinuationHandlerContext, ? extends ServerConnectionContinuationHandler> getOperationHandler(final String operationName) {
        final EventStreamRPCServiceModel svcModel = this.serviceModel;
        System.out.println("Operation handler retrieved for operation name: " + operationName);
        return (context) -> {
                final TestOperationContinuationHandler handler = new TestOperationContinuationHandler(context) {
                    @Override
                    public OperationModelContext getOperationModelContext() {
                        return svcModel.getOperationModelContext(operationName);
                    }

                    @Override
                    protected void onStreamClosed() {
                        streamClosed = true;
                    }

                    @Override
                    public EventStreamJsonMessage handleRequest(EventStreamJsonMessage request) {
                        System.out.println("Handling request...");
                        return requestHandler.apply(request);
                    }

                    @Override
                    public void handleStreamEvent(EventStreamJsonMessage streamRequestEvent) {
                        System.out.println("Handling request stream event...");
                    }
                };
                continuationHandlerQueue.offer(new AbstractMap.SimpleImmutableEntry<>(operationName, handler));
                return handler;
            };
    }

    @Override
    public Set<String> getAllOperations() {
        //return no operations, IpcServer doesn't actually care about that
        //only that validating all operations are set before start up
        return new HashSet<>();
    }

    @Override
    public boolean hasHandlerForOperation(String operation) {
        return true;
    }

    public Map.Entry<String, TestOperationContinuationHandler> popHandler() {
        return continuationHandlerQueue.poll();
    }
}
