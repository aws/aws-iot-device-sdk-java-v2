package software.amazon.awssdk.awstest;

import software.amazon.awssdk.awstest.model.EchoStreamingMessage;
import software.amazon.awssdk.awstest.model.EchoStreamingRequest;
import software.amazon.awssdk.awstest.model.EchoStreamingResponse;
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCServiceModel;
import software.amazon.awssdk.eventstreamrpc.OperationModelContext;

import java.util.Optional;

public class CauseStreamServiceToErrorOperationContext implements OperationModelContext<EchoStreamingRequest, EchoStreamingResponse, EchoStreamingMessage, EchoStreamingMessage> {
  @Override
  public EventStreamRPCServiceModel getServiceModel() {
    return EchoTestRPCServiceModel.getInstance();
  }

  @Override
  public String getOperationName() {
    return EchoTestRPCServiceModel.CAUSE_STREAM_SERVICE_TO_ERROR;
  }

  @Override
  public Class<EchoStreamingRequest> getRequestTypeClass() {
    return EchoStreamingRequest.class;
  }

  @Override
  public Class<EchoStreamingResponse> getResponseTypeClass() {
    return EchoStreamingResponse.class;
  }

  @Override
  public String getRequestApplicationModelType() {
    return EchoStreamingRequest.APPLICATION_MODEL_TYPE;
  }

  @Override
  public String getResponseApplicationModelType() {
    return EchoStreamingResponse.APPLICATION_MODEL_TYPE;
  }

  @Override
  public Optional<Class<EchoStreamingMessage>> getStreamingRequestTypeClass() {
    return Optional.of(EchoStreamingMessage.class);
  }

  @Override
  public Optional<Class<EchoStreamingMessage>> getStreamingResponseTypeClass() {
    return Optional.of(EchoStreamingMessage.class);
  }

  public Optional<String> getStreamingRequestApplicationModelType() {
    return Optional.of(EchoStreamingMessage.APPLICATION_MODEL_TYPE);
  }

  @Override
  public Optional<String> getStreamingResponseApplicationModelType() {
    return Optional.of(EchoStreamingMessage.APPLICATION_MODEL_TYPE);
  }
}
