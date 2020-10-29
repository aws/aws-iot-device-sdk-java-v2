package software.amazon.awssdk.aws.greengrass;

import java.lang.Override;
import software.amazon.awssdk.aws.greengrass.model.GetConfigurationRequest;
import software.amazon.awssdk.aws.greengrass.model.GetConfigurationResponse;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandler;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandlerContext;
import software.amazon.awssdk.eventstreamrpc.OperationModelContext;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public abstract class GeneratedAbstractGetConfigurationOperationHandler extends OperationContinuationHandler<GetConfigurationRequest, GetConfigurationResponse, EventStreamJsonMessage, EventStreamJsonMessage> {
  protected GeneratedAbstractGetConfigurationOperationHandler(
      OperationContinuationHandlerContext context) {
    super(context);
  }

  @Override
  public OperationModelContext<GetConfigurationRequest, GetConfigurationResponse, EventStreamJsonMessage, EventStreamJsonMessage> getOperationModelContext(
      ) {
    return GreengrassCoreIPCServiceModel.getGetConfigurationModelContext();
  }
}
