package software.amazon.awssdk.awstest;

import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.util.Optional;
import software.amazon.awssdk.awstest.model.EchoMessageRequest;
import software.amazon.awssdk.awstest.model.EchoMessageResponse;
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCServiceModel;
import software.amazon.awssdk.eventstreamrpc.OperationModelContext;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class EchoMessageOperationContext implements OperationModelContext<EchoMessageRequest, EchoMessageResponse, EventStreamJsonMessage, EventStreamJsonMessage> {
  @Override
  public EventStreamRPCServiceModel getServiceModel() {
    return EchoTestRPCServiceModel.getInstance();
  }

  @Override
  public String getOperationName() {
    return EchoTestRPCServiceModel.ECHO_MESSAGE;
  }

  @Override
  public Class<EchoMessageRequest> getRequestTypeClass() {
    return EchoMessageRequest.class;
  }

  @Override
  public Class<EchoMessageResponse> getResponseTypeClass() {
    return EchoMessageResponse.class;
  }

  @Override
  public String getRequestApplicationModelType() {
    return EchoMessageRequest.APPLICATION_MODEL_TYPE;
  }

  @Override
  public String getResponseApplicationModelType() {
    return EchoMessageResponse.APPLICATION_MODEL_TYPE;
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
