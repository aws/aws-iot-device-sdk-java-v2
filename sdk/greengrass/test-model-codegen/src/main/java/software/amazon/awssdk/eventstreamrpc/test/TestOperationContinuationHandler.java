package software.amazon.awssdk.eventstreamrpc.test;


import org.junit.jupiter.api.Assertions;
import software.amazon.awssdk.crt.eventstream.Header;
import software.amazon.awssdk.crt.eventstream.MessageType;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandler;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandlerContext;
import software.amazon.awssdk.eventstreamrpc.OperationModelContext;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public class TestOperationContinuationHandler
        <ReqType extends EventStreamJsonMessage,
         RespType extends EventStreamJsonMessage,
         StrReqType extends EventStreamJsonMessage,
         StrRespType  extends EventStreamJsonMessage>
        extends OperationContinuationHandler<ReqType, RespType, StrReqType, StrRespType> {
    private final OperationModelContext<ReqType, RespType, StrReqType, StrRespType> modelContext;

    private Function<ReqType, RespType> handlerFn;
    private Consumer<StrReqType> incomingStreamEventFn;
    private boolean closed = false;
    private List<StrRespType> streamingResponses = new LinkedList<>();

    public TestOperationContinuationHandler(OperationContinuationHandlerContext context,
                                            OperationModelContext<ReqType, RespType, StrReqType, StrRespType> modelContext,
                                            Function<ReqType, RespType> handlerFn,
                                            Consumer<StrReqType> incomingStreamEventFn) {
        super(context);
        this.modelContext = modelContext;
        this.handlerFn = handlerFn;
        this.incomingStreamEventFn = incomingStreamEventFn;
    }

    @Override
    public OperationModelContext<ReqType, RespType, StrReqType, StrRespType> getOperationModelContext() {
        return modelContext;
    }


    @Override
    protected void onStreamClosed() {
        closed = true;
    }

    @Override
    public RespType handleRequest(ReqType request) {
        if (getInitialRequest() != null) {
            Assertions.fail("Handle request invoked more than once!");
        }
        return handlerFn.apply(request);
    }

    @Override
    public void handleStreamEvent(StrReqType streamRequestEvent) {
        incomingStreamEventFn.accept(streamRequestEvent);
    }

    /**
     * Exposed function for testing to simulate the handler sending a stream event
     * @param streamResponseEvent
     */
    public CompletableFuture<Void> sendTestEvent(StrRespType streamResponseEvent) {
        return sendStreamEvent(streamResponseEvent);
    }

    /**
     * Exposed function to test via invoking the internal protected method
     */
    public void invokeOnContinuationClosed() {
        onContinuationClosed();
    }

    /**
     * Exposed function to test via invoking the internal protected method
     */
    public void invokeOnContinuationMessage(List<Header> headers, byte[] payload, MessageType messageType, int messageFlags) {
        onContinuationMessage(headers, payload, messageType, messageFlags);
    }
}
