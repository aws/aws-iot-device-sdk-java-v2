package software.amazon.awssdk.awstest;

import software.amazon.awssdk.awstest.EchoTestRPCServiceModel;
import software.amazon.awssdk.awstest.model.EchoStreamingMessage;
import software.amazon.awssdk.awstest.model.EchoStreamingRequest;
import software.amazon.awssdk.awstest.model.EchoStreamingResponse;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandler;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandlerContext;
import software.amazon.awssdk.eventstreamrpc.OperationModelContext;

public abstract class GeneratedAbstractEchoStreamMessagesOperationHandler extends
        OperationContinuationHandler<EchoStreamingRequest, EchoStreamingResponse, EchoStreamingMessage, EchoStreamingMessage> {
  protected GeneratedAbstractEchoStreamMessagesOperationHandler(
      OperationContinuationHandlerContext context) {
    super(context);
  }

  @Override
  public OperationModelContext<EchoStreamingRequest, EchoStreamingResponse, EchoStreamingMessage, EchoStreamingMessage> getOperationModelContext(
      ) {
    return EchoTestRPCServiceModel.getEchoStreamMessagesModelContext();
  }
}
