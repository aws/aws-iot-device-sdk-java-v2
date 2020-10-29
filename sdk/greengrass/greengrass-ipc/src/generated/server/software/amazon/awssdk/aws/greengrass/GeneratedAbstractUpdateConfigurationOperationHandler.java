package software.amazon.awssdk.aws.greengrass;

import java.lang.Override;
import software.amazon.awssdk.aws.greengrass.model.UpdateConfigurationRequest;
import software.amazon.awssdk.aws.greengrass.model.UpdateConfigurationResponse;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandler;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandlerContext;
import software.amazon.awssdk.eventstreamrpc.OperationModelContext;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public abstract class GeneratedAbstractUpdateConfigurationOperationHandler extends OperationContinuationHandler<UpdateConfigurationRequest, UpdateConfigurationResponse, EventStreamJsonMessage, EventStreamJsonMessage> {
  protected GeneratedAbstractUpdateConfigurationOperationHandler(
      OperationContinuationHandlerContext context) {
    super(context);
  }

  @Override
  public OperationModelContext<UpdateConfigurationRequest, UpdateConfigurationResponse, EventStreamJsonMessage, EventStreamJsonMessage> getOperationModelContext(
      ) {
    return GreengrassCoreIPCServiceModel.getUpdateConfigurationModelContext();
  }
}
