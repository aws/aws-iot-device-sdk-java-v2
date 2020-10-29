package software.amazon.awssdk.aws.greengrass;

import java.lang.Override;
import software.amazon.awssdk.aws.greengrass.model.StopComponentRequest;
import software.amazon.awssdk.aws.greengrass.model.StopComponentResponse;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandler;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandlerContext;
import software.amazon.awssdk.eventstreamrpc.OperationModelContext;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public abstract class GeneratedAbstractStopComponentOperationHandler extends OperationContinuationHandler<StopComponentRequest, StopComponentResponse, EventStreamJsonMessage, EventStreamJsonMessage> {
  protected GeneratedAbstractStopComponentOperationHandler(
      OperationContinuationHandlerContext context) {
    super(context);
  }

  @Override
  public OperationModelContext<StopComponentRequest, StopComponentResponse, EventStreamJsonMessage, EventStreamJsonMessage> getOperationModelContext(
      ) {
    return GreengrassCoreIPCServiceModel.getStopComponentModelContext();
  }
}
