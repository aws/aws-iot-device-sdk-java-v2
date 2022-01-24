package software.amazon.awssdk.eventstreamrpc.echotest;

import software.amazon.awssdk.awstest.GeneratedAbstractGetAllProductsOperationHandler;
import software.amazon.awssdk.awstest.model.GetAllProductsRequest;
import software.amazon.awssdk.awstest.model.GetAllProductsResponse;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandlerContext;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class GetAllProductsHandler extends GeneratedAbstractGetAllProductsOperationHandler {

    protected GetAllProductsHandler(OperationContinuationHandlerContext context) {
        super(context);
    }

    @Override
    protected void onStreamClosed() {
        // do nothing
    }

    @Override
    public GetAllProductsResponse handleRequest(GetAllProductsRequest request) {
        final GetAllProductsResponse response = new GetAllProductsResponse();
        return response;
    }

    @Override
    public void handleStreamEvent(EventStreamJsonMessage streamRequestEvent) {
        // maybe unsupported operation?
        throw new RuntimeException("No stream event should be occurring on this operation");
    }
}
