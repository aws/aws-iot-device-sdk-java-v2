package software.amazon.awssdk.awstest;

import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.util.Optional;
import software.amazon.awssdk.awstest.model.EchoStreamingMessage;
import software.amazon.awssdk.awstest.model.EchoStreamingRequest;
import software.amazon.awssdk.awstest.model.EchoStreamingResponse;
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCServiceModel;
import software.amazon.awssdk.eventstreamrpc.OperationModelContext;

public class EchoStreamMessagesOperationContext implements OperationModelContext<EchoStreamingRequest, EchoStreamingResponse, EchoStreamingMessage, EchoStreamingMessage> {
  @Override
  public EventStreamRPCServiceModel getServiceModel() {
    return EchoTestRPCServiceModel.getInstance();
  }

  @Override
  public String getOperationName() {
    return EchoTestRPCServiceModel.ECHO_STREAM_MESSAGES;
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
