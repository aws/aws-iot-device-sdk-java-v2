package software.amazon.awssdk.aws.greengrass;

import java.lang.Override;
import software.amazon.awssdk.aws.greengrass.model.GetSecretValueRequest;
import software.amazon.awssdk.aws.greengrass.model.GetSecretValueResponse;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandler;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandlerContext;
import software.amazon.awssdk.eventstreamrpc.OperationModelContext;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public abstract class GeneratedAbstractGetSecretValueOperationHandler extends OperationContinuationHandler<GetSecretValueRequest, GetSecretValueResponse, EventStreamJsonMessage, EventStreamJsonMessage> {
  protected GeneratedAbstractGetSecretValueOperationHandler(
      OperationContinuationHandlerContext context) {
    super(context);
  }

  @Override
  public OperationModelContext<GetSecretValueRequest, GetSecretValueResponse, EventStreamJsonMessage, EventStreamJsonMessage> getOperationModelContext(
      ) {
    return GreengrassCoreIPCServiceModel.getGetSecretValueModelContext();
  }
}
