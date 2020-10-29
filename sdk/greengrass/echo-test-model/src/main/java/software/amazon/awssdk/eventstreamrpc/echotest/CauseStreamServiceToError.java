package software.amazon.awssdk.eventstreamrpc.echotest;

import software.amazon.awssdk.awstest.GeneratedAbstractCauseStreamServiceToErrorOperationHandler;
import software.amazon.awssdk.awstest.model.EchoStreamingMessage;
import software.amazon.awssdk.awstest.model.EchoStreamingRequest;
import software.amazon.awssdk.awstest.model.EchoStreamingResponse;
import software.amazon.awssdk.awstest.model.ServiceError;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandlerContext;

public class CauseStreamServiceToError extends GeneratedAbstractCauseStreamServiceToErrorOperationHandler {
    protected CauseStreamServiceToError(OperationContinuationHandlerContext context) {
        super(context);
    }

    @Override
    protected void onStreamClosed() {
        //do nothing
    }

    @Override
    public EchoStreamingResponse handleRequest(EchoStreamingRequest request) {
        return EchoStreamingResponse.VOID;
    }

    /**
     * Handle an incoming stream event from the connected client on the operation.
     * <p>
     * If the implementation throws an exception, the framework will respond with the modeled
     * exception to the client, if it is modeled. If it is not modeled, it will respond with
     * an internal error and log appropriately. Either case, throwing an exception will result
     * in closing the stream. To keep the stream open, do not throw
     *
     * @param streamRequestEvent
     */
    @Override
    public void handleStreamEvent(EchoStreamingMessage streamRequestEvent) {
        throw new ServiceError("Intentionally caused ServiceError on stream");
    }
}
