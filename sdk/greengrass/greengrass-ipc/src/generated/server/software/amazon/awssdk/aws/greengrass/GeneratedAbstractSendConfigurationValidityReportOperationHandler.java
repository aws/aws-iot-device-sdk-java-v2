package software.amazon.awssdk.aws.greengrass;

import java.lang.Override;
import software.amazon.awssdk.aws.greengrass.model.SendConfigurationValidityReportRequest;
import software.amazon.awssdk.aws.greengrass.model.SendConfigurationValidityReportResponse;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandler;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandlerContext;
import software.amazon.awssdk.eventstreamrpc.OperationModelContext;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public abstract class GeneratedAbstractSendConfigurationValidityReportOperationHandler extends OperationContinuationHandler<SendConfigurationValidityReportRequest, SendConfigurationValidityReportResponse, EventStreamJsonMessage, EventStreamJsonMessage> {
  protected GeneratedAbstractSendConfigurationValidityReportOperationHandler(
      OperationContinuationHandlerContext context) {
    super(context);
  }

  @Override
  public OperationModelContext<SendConfigurationValidityReportRequest, SendConfigurationValidityReportResponse, EventStreamJsonMessage, EventStreamJsonMessage> getOperationModelContext(
      ) {
    return GreengrassCoreIPCServiceModel.getSendConfigurationValidityReportModelContext();
  }
}
