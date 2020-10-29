package software.amazon.awssdk.aws.greengrass;

import java.lang.Override;
import software.amazon.awssdk.aws.greengrass.model.UpdateStateRequest;
import software.amazon.awssdk.aws.greengrass.model.UpdateStateResponse;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandler;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandlerContext;
import software.amazon.awssdk.eventstreamrpc.OperationModelContext;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public abstract class GeneratedAbstractUpdateStateOperationHandler extends OperationContinuationHandler<UpdateStateRequest, UpdateStateResponse, EventStreamJsonMessage, EventStreamJsonMessage> {
  protected GeneratedAbstractUpdateStateOperationHandler(
      OperationContinuationHandlerContext context) {
    super(context);
  }

  @Override
  public OperationModelContext<UpdateStateRequest, UpdateStateResponse, EventStreamJsonMessage, EventStreamJsonMessage> getOperationModelContext(
      ) {
    return GreengrassCoreIPCServiceModel.getUpdateStateModelContext();
  }
}
