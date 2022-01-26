package software.amazon.awssdk.awstest;

import software.amazon.awssdk.awstest.EchoTestRPCServiceModel;
import software.amazon.awssdk.awstest.model.CauseServiceErrorRequest;
import software.amazon.awssdk.awstest.model.CauseServiceErrorResponse;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandler;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandlerContext;
import software.amazon.awssdk.eventstreamrpc.OperationModelContext;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public abstract class GeneratedAbstractCauseServiceErrorOperationHandler extends
        OperationContinuationHandler<CauseServiceErrorRequest, CauseServiceErrorResponse, EventStreamJsonMessage, EventStreamJsonMessage> {
  protected GeneratedAbstractCauseServiceErrorOperationHandler(
      OperationContinuationHandlerContext context) {
    super(context);
  }

  @Override
  public OperationModelContext<CauseServiceErrorRequest, CauseServiceErrorResponse, EventStreamJsonMessage, EventStreamJsonMessage> getOperationModelContext(
      ) {
    return EchoTestRPCServiceModel.getCauseServiceErrorModelContext();
  }
}
