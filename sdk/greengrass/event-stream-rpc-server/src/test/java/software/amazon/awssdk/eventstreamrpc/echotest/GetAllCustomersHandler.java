package software.amazon.awssdk.eventstreamrpc.echotest;

import software.amazon.awssdk.awstest.GeneratedAbstractGetAllCustomersOperationHandler;
import software.amazon.awssdk.awstest.model.GetAllCustomersRequest;
import software.amazon.awssdk.awstest.model.GetAllCustomersResponse;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandlerContext;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class GetAllCustomersHandler extends GeneratedAbstractGetAllCustomersOperationHandler {

    protected GetAllCustomersHandler(OperationContinuationHandlerContext context) {
        super(context);
    }

    @Override
    protected void onStreamClosed() {
        // do nothing
    }

    @Override
    public GetAllCustomersResponse handleRequest(GetAllCustomersRequest request) {
        final GetAllCustomersResponse response = new GetAllCustomersResponse();
        return response;
    }

    @Override
    public void handleStreamEvent(EventStreamJsonMessage streamRequestEvent) {
        // maybe unsupported operation?
        throw new RuntimeException("No stream event should be occurring on this operation");
    }
}
