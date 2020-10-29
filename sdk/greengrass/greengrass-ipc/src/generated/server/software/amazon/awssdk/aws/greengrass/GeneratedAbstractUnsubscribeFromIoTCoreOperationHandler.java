package software.amazon.awssdk.aws.greengrass;

import java.lang.Override;
import software.amazon.awssdk.aws.greengrass.model.UnsubscribeFromIoTCoreRequest;
import software.amazon.awssdk.aws.greengrass.model.UnsubscribeFromIoTCoreResponse;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandler;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandlerContext;
import software.amazon.awssdk.eventstreamrpc.OperationModelContext;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public abstract class GeneratedAbstractUnsubscribeFromIoTCoreOperationHandler extends OperationContinuationHandler<UnsubscribeFromIoTCoreRequest, UnsubscribeFromIoTCoreResponse, EventStreamJsonMessage, EventStreamJsonMessage> {
  protected GeneratedAbstractUnsubscribeFromIoTCoreOperationHandler(
      OperationContinuationHandlerContext context) {
    super(context);
  }

  @Override
  public OperationModelContext<UnsubscribeFromIoTCoreRequest, UnsubscribeFromIoTCoreResponse, EventStreamJsonMessage, EventStreamJsonMessage> getOperationModelContext(
      ) {
    return GreengrassCoreIPCServiceModel.getUnsubscribeFromIoTCoreModelContext();
  }
}
