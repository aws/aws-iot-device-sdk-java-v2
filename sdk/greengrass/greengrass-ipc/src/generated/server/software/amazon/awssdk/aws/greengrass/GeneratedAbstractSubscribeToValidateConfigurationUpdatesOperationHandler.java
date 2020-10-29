package software.amazon.awssdk.aws.greengrass;

import java.lang.Override;
import software.amazon.awssdk.aws.greengrass.model.SubscribeToValidateConfigurationUpdatesRequest;
import software.amazon.awssdk.aws.greengrass.model.SubscribeToValidateConfigurationUpdatesResponse;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandler;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandlerContext;
import software.amazon.awssdk.eventstreamrpc.OperationModelContext;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public abstract class GeneratedAbstractSubscribeToValidateConfigurationUpdatesOperationHandler extends OperationContinuationHandler<SubscribeToValidateConfigurationUpdatesRequest, SubscribeToValidateConfigurationUpdatesResponse, EventStreamJsonMessage, EventStreamJsonMessage> {
  protected GeneratedAbstractSubscribeToValidateConfigurationUpdatesOperationHandler(
      OperationContinuationHandlerContext context) {
    super(context);
  }

  @Override
  public OperationModelContext<SubscribeToValidateConfigurationUpdatesRequest, SubscribeToValidateConfigurationUpdatesResponse, EventStreamJsonMessage, EventStreamJsonMessage> getOperationModelContext(
      ) {
    return GreengrassCoreIPCServiceModel.getSubscribeToValidateConfigurationUpdatesModelContext();
  }
}
