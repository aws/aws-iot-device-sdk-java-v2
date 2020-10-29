package software.amazon.awssdk.aws.greengrass;

import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.util.Optional;
import software.amazon.awssdk.aws.greengrass.model.UpdateRecipesAndArtifactsRequest;
import software.amazon.awssdk.aws.greengrass.model.UpdateRecipesAndArtifactsResponse;
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCServiceModel;
import software.amazon.awssdk.eventstreamrpc.OperationModelContext;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class UpdateRecipesAndArtifactsOperationContext implements OperationModelContext<UpdateRecipesAndArtifactsRequest, UpdateRecipesAndArtifactsResponse, EventStreamJsonMessage, EventStreamJsonMessage> {
  @Override
  public EventStreamRPCServiceModel getServiceModel() {
    return GreengrassCoreIPCServiceModel.getInstance();
  }

  @Override
  public String getOperationName() {
    return GreengrassCoreIPCServiceModel.UPDATE_RECIPES_AND_ARTIFACTS;
  }

  @Override
  public Class<UpdateRecipesAndArtifactsRequest> getRequestTypeClass() {
    return UpdateRecipesAndArtifactsRequest.class;
  }

  @Override
  public Class<UpdateRecipesAndArtifactsResponse> getResponseTypeClass() {
    return UpdateRecipesAndArtifactsResponse.class;
  }

  @Override
  public String getRequestApplicationModelType() {
    return UpdateRecipesAndArtifactsRequest.APPLICATION_MODEL_TYPE;
  }

  @Override
  public String getResponseApplicationModelType() {
    return UpdateRecipesAndArtifactsResponse.APPLICATION_MODEL_TYPE;
  }

  @Override
  public Optional<Class<EventStreamJsonMessage>> getStreamingRequestTypeClass() {
    return Optional.empty();
  }

  @Override
  public Optional<Class<EventStreamJsonMessage>> getStreamingResponseTypeClass() {
    return Optional.empty();
  }

  public Optional<String> getStreamingRequestApplicationModelType() {
    return Optional.empty();
  }

  @Override
  public Optional<String> getStreamingResponseApplicationModelType() {
    return Optional.empty();
  }
}
