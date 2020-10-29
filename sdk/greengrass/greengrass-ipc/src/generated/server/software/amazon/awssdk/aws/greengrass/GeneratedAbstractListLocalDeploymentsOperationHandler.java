package software.amazon.awssdk.aws.greengrass;

import java.lang.Override;
import software.amazon.awssdk.aws.greengrass.model.ListLocalDeploymentsRequest;
import software.amazon.awssdk.aws.greengrass.model.ListLocalDeploymentsResponse;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandler;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandlerContext;
import software.amazon.awssdk.eventstreamrpc.OperationModelContext;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public abstract class GeneratedAbstractListLocalDeploymentsOperationHandler extends OperationContinuationHandler<ListLocalDeploymentsRequest, ListLocalDeploymentsResponse, EventStreamJsonMessage, EventStreamJsonMessage> {
  protected GeneratedAbstractListLocalDeploymentsOperationHandler(
      OperationContinuationHandlerContext context) {
    super(context);
  }

  @Override
  public OperationModelContext<ListLocalDeploymentsRequest, ListLocalDeploymentsResponse, EventStreamJsonMessage, EventStreamJsonMessage> getOperationModelContext(
      ) {
    return GreengrassCoreIPCServiceModel.getListLocalDeploymentsModelContext();
  }
}
