package software.amazon.awssdk.awstest;

import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.util.Optional;
import software.amazon.awssdk.awstest.model.CauseServiceErrorRequest;
import software.amazon.awssdk.awstest.model.CauseServiceErrorResponse;
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCServiceModel;
import software.amazon.awssdk.eventstreamrpc.OperationModelContext;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class CauseServiceErrorOperationContext implements OperationModelContext<CauseServiceErrorRequest, CauseServiceErrorResponse, EventStreamJsonMessage, EventStreamJsonMessage> {
  @Override
  public EventStreamRPCServiceModel getServiceModel() {
    return EchoTestRPCServiceModel.getInstance();
  }

  @Override
  public String getOperationName() {
    return EchoTestRPCServiceModel.CAUSE_SERVICE_ERROR;
  }

  @Override
  public Class<CauseServiceErrorRequest> getRequestTypeClass() {
    return CauseServiceErrorRequest.class;
  }

  @Override
  public Class<CauseServiceErrorResponse> getResponseTypeClass() {
    return CauseServiceErrorResponse.class;
  }

  @Override
  public String getRequestApplicationModelType() {
    return CauseServiceErrorRequest.APPLICATION_MODEL_TYPE;
  }

  @Override
  public String getResponseApplicationModelType() {
    return CauseServiceErrorResponse.APPLICATION_MODEL_TYPE;
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
