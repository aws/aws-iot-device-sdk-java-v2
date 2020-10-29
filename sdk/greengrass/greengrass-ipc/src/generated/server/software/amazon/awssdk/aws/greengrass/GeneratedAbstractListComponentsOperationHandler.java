package software.amazon.awssdk.aws.greengrass;

import java.lang.Override;
import software.amazon.awssdk.aws.greengrass.model.ListComponentsRequest;
import software.amazon.awssdk.aws.greengrass.model.ListComponentsResponse;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandler;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandlerContext;
import software.amazon.awssdk.eventstreamrpc.OperationModelContext;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public abstract class GeneratedAbstractListComponentsOperationHandler extends OperationContinuationHandler<ListComponentsRequest, ListComponentsResponse, EventStreamJsonMessage, EventStreamJsonMessage> {
  protected GeneratedAbstractListComponentsOperationHandler(
      OperationContinuationHandlerContext context) {
    super(context);
  }

  @Override
  public OperationModelContext<ListComponentsRequest, ListComponentsResponse, EventStreamJsonMessage, EventStreamJsonMessage> getOperationModelContext(
      ) {
    return GreengrassCoreIPCServiceModel.getListComponentsModelContext();
  }
}
