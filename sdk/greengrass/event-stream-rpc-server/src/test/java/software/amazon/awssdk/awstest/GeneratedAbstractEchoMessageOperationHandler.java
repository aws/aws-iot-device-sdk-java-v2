package software.amazon.awssdk.awstest;

import software.amazon.awssdk.awstest.EchoTestRPCServiceModel;
import software.amazon.awssdk.awstest.model.EchoMessageRequest;
import software.amazon.awssdk.awstest.model.EchoMessageResponse;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandler;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandlerContext;
import software.amazon.awssdk.eventstreamrpc.OperationModelContext;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public abstract class GeneratedAbstractEchoMessageOperationHandler extends
        OperationContinuationHandler<EchoMessageRequest, EchoMessageResponse, EventStreamJsonMessage, EventStreamJsonMessage> {
  protected GeneratedAbstractEchoMessageOperationHandler(
      OperationContinuationHandlerContext context) {
    super(context);
  }

  @Override
  public OperationModelContext<EchoMessageRequest, EchoMessageResponse, EventStreamJsonMessage, EventStreamJsonMessage> getOperationModelContext(
      ) {
    return EchoTestRPCServiceModel.getEchoMessageModelContext();
  }
}
