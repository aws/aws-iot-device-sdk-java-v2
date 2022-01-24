package software.amazon.awssdk.eventstreamrpc.echotest;

import software.amazon.awssdk.awstest.GeneratedAbstractEchoMessageOperationHandler;
import software.amazon.awssdk.awstest.model.EchoMessageRequest;
import software.amazon.awssdk.awstest.model.EchoMessageResponse;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandlerContext;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class EchoMessageHandler extends GeneratedAbstractEchoMessageOperationHandler {
    protected EchoMessageHandler(OperationContinuationHandlerContext context) {
        super(context);
    }

    @Override
    protected void onStreamClosed() {
        //do nothing
    }

    @Override
    public EchoMessageResponse handleRequest(EchoMessageRequest request) {
        final EchoMessageResponse response = new EchoMessageResponse();
        response.setMessage(request.getMessage());
        return response;
    }

    @Override
    public void handleStreamEvent(EventStreamJsonMessage streamRequestEvent) {
        //maybe unsupported operation?
        throw new RuntimeException("No stream event should be occurring on this operation");
    }
}
