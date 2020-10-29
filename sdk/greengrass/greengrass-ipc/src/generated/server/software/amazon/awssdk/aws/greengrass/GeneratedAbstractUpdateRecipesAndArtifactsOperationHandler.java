package software.amazon.awssdk.aws.greengrass;

import java.lang.Override;
import software.amazon.awssdk.aws.greengrass.model.UpdateRecipesAndArtifactsRequest;
import software.amazon.awssdk.aws.greengrass.model.UpdateRecipesAndArtifactsResponse;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandler;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandlerContext;
import software.amazon.awssdk.eventstreamrpc.OperationModelContext;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public abstract class GeneratedAbstractUpdateRecipesAndArtifactsOperationHandler extends OperationContinuationHandler<UpdateRecipesAndArtifactsRequest, UpdateRecipesAndArtifactsResponse, EventStreamJsonMessage, EventStreamJsonMessage> {
  protected GeneratedAbstractUpdateRecipesAndArtifactsOperationHandler(
      OperationContinuationHandlerContext context) {
    super(context);
  }

  @Override
  public OperationModelContext<UpdateRecipesAndArtifactsRequest, UpdateRecipesAndArtifactsResponse, EventStreamJsonMessage, EventStreamJsonMessage> getOperationModelContext(
      ) {
    return GreengrassCoreIPCServiceModel.getUpdateRecipesAndArtifactsModelContext();
  }
}
