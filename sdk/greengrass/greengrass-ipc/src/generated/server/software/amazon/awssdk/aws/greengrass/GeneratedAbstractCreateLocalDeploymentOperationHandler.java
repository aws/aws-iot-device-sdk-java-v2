package software.amazon.awssdk.aws.greengrass;

import java.lang.Override;
import software.amazon.awssdk.aws.greengrass.model.CreateLocalDeploymentRequest;
import software.amazon.awssdk.aws.greengrass.model.CreateLocalDeploymentResponse;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandler;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandlerContext;
import software.amazon.awssdk.eventstreamrpc.OperationModelContext;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public abstract class GeneratedAbstractCreateLocalDeploymentOperationHandler extends OperationContinuationHandler<CreateLocalDeploymentRequest, CreateLocalDeploymentResponse, EventStreamJsonMessage, EventStreamJsonMessage> {
  protected GeneratedAbstractCreateLocalDeploymentOperationHandler(
      OperationContinuationHandlerContext context) {
    super(context);
  }

  @Override
  public OperationModelContext<CreateLocalDeploymentRequest, CreateLocalDeploymentResponse, EventStreamJsonMessage, EventStreamJsonMessage> getOperationModelContext(
      ) {
    return GreengrassCoreIPCServiceModel.getCreateLocalDeploymentModelContext();
  }
}
