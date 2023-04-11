package software.amazon.awssdk.awstest;

import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.util.Optional;
import software.amazon.awssdk.awstest.model.GetAllCustomersRequest;
import software.amazon.awssdk.awstest.model.GetAllCustomersResponse;
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCServiceModel;
import software.amazon.awssdk.eventstreamrpc.OperationModelContext;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class GetAllCustomersOperationContext implements OperationModelContext<GetAllCustomersRequest, GetAllCustomersResponse, EventStreamJsonMessage, EventStreamJsonMessage> {
  @Override
  public EventStreamRPCServiceModel getServiceModel() {
    return EchoTestRPCServiceModel.getInstance();
  }

  @Override
  public String getOperationName() {
    return EchoTestRPCServiceModel.GET_ALL_CUSTOMERS;
  }

  @Override
  public Class<GetAllCustomersRequest> getRequestTypeClass() {
    return GetAllCustomersRequest.class;
  }

  @Override
  public Class<GetAllCustomersResponse> getResponseTypeClass() {
    return GetAllCustomersResponse.class;
  }

  @Override
  public String getRequestApplicationModelType() {
    return GetAllCustomersRequest.APPLICATION_MODEL_TYPE;
  }

  @Override
  public String getResponseApplicationModelType() {
    return GetAllCustomersResponse.APPLICATION_MODEL_TYPE;
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
