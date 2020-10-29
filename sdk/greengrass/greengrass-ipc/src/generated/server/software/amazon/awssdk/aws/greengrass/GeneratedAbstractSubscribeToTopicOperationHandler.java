package software.amazon.awssdk.aws.greengrass;

import java.lang.Override;
import software.amazon.awssdk.aws.greengrass.model.SubscribeToTopicRequest;
import software.amazon.awssdk.aws.greengrass.model.SubscribeToTopicResponse;
import software.amazon.awssdk.aws.greengrass.model.SubscriptionResponseMessage;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandler;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandlerContext;
import software.amazon.awssdk.eventstreamrpc.OperationModelContext;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public abstract class GeneratedAbstractSubscribeToTopicOperationHandler extends OperationContinuationHandler<SubscribeToTopicRequest, SubscribeToTopicResponse, EventStreamJsonMessage, SubscriptionResponseMessage> {
  protected GeneratedAbstractSubscribeToTopicOperationHandler(
      OperationContinuationHandlerContext context) {
    super(context);
  }

  @Override
  public OperationModelContext<SubscribeToTopicRequest, SubscribeToTopicResponse, EventStreamJsonMessage, SubscriptionResponseMessage> getOperationModelContext(
      ) {
    return GreengrassCoreIPCServiceModel.getSubscribeToTopicModelContext();
  }
}
