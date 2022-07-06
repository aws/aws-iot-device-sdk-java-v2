package software.amazon.awssdk.eventstreamrpc.echotest;

import software.amazon.awssdk.awstest.GeneratedAbstractCauseServiceErrorOperationHandler;
import software.amazon.awssdk.awstest.model.CauseServiceErrorRequest;
import software.amazon.awssdk.awstest.model.CauseServiceErrorResponse;
import software.amazon.awssdk.awstest.model.ServiceError;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandlerContext;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class CauseServiceErrorHandler extends GeneratedAbstractCauseServiceErrorOperationHandler {
    protected CauseServiceErrorHandler(OperationContinuationHandlerContext context) {
        super(context);
    }

    @Override
    protected void onStreamClosed() {
        //do nothing
    }

    @Override
    public CauseServiceErrorResponse handleRequest(CauseServiceErrorRequest request) {
        throw new ServiceError("Intentionally thrown ServiceError");
    }


    @Override
    public void handleStreamEvent(EventStreamJsonMessage streamRequestEvent) {
        throw new RuntimeException("Should not be handling a stream event when handling stream event.");
    }
}
