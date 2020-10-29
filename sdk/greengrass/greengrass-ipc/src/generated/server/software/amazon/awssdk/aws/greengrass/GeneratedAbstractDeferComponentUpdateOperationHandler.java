package software.amazon.awssdk.aws.greengrass;

import java.lang.Override;
import software.amazon.awssdk.aws.greengrass.model.DeferComponentUpdateRequest;
import software.amazon.awssdk.aws.greengrass.model.DeferComponentUpdateResponse;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandler;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandlerContext;
import software.amazon.awssdk.eventstreamrpc.OperationModelContext;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public abstract class GeneratedAbstractDeferComponentUpdateOperationHandler extends OperationContinuationHandler<DeferComponentUpdateRequest, DeferComponentUpdateResponse, EventStreamJsonMessage, EventStreamJsonMessage> {
  protected GeneratedAbstractDeferComponentUpdateOperationHandler(
      OperationContinuationHandlerContext context) {
    super(context);
  }

  @Override
  public OperationModelContext<DeferComponentUpdateRequest, DeferComponentUpdateResponse, EventStreamJsonMessage, EventStreamJsonMessage> getOperationModelContext(
      ) {
    return GreengrassCoreIPCServiceModel.getDeferComponentUpdateModelContext();
  }
}
