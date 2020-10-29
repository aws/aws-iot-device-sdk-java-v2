package software.amazon.awssdk.aws.greengrass;

import java.lang.Override;
import software.amazon.awssdk.aws.greengrass.model.ValidateAuthorizationTokenRequest;
import software.amazon.awssdk.aws.greengrass.model.ValidateAuthorizationTokenResponse;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandler;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandlerContext;
import software.amazon.awssdk.eventstreamrpc.OperationModelContext;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public abstract class GeneratedAbstractValidateAuthorizationTokenOperationHandler extends OperationContinuationHandler<ValidateAuthorizationTokenRequest, ValidateAuthorizationTokenResponse, EventStreamJsonMessage, EventStreamJsonMessage> {
  protected GeneratedAbstractValidateAuthorizationTokenOperationHandler(
      OperationContinuationHandlerContext context) {
    super(context);
  }

  @Override
  public OperationModelContext<ValidateAuthorizationTokenRequest, ValidateAuthorizationTokenResponse, EventStreamJsonMessage, EventStreamJsonMessage> getOperationModelContext(
      ) {
    return GreengrassCoreIPCServiceModel.getValidateAuthorizationTokenModelContext();
  }
}
