package software.amazon.awssdk.aws.greengrass;

import java.lang.Override;
import software.amazon.awssdk.aws.greengrass.model.RestartComponentRequest;
import software.amazon.awssdk.aws.greengrass.model.RestartComponentResponse;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandler;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandlerContext;
import software.amazon.awssdk.eventstreamrpc.OperationModelContext;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public abstract class GeneratedAbstractRestartComponentOperationHandler extends OperationContinuationHandler<RestartComponentRequest, RestartComponentResponse, EventStreamJsonMessage, EventStreamJsonMessage> {
  protected GeneratedAbstractRestartComponentOperationHandler(
      OperationContinuationHandlerContext context) {
    super(context);
  }

  @Override
  public OperationModelContext<RestartComponentRequest, RestartComponentResponse, EventStreamJsonMessage, EventStreamJsonMessage> getOperationModelContext(
      ) {
    return GreengrassCoreIPCServiceModel.getRestartComponentModelContext();
  }
}
