package software.amazon.awssdk.awstest;

import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.util.Optional;
import software.amazon.awssdk.awstest.model.GetAllProductsRequest;
import software.amazon.awssdk.awstest.model.GetAllProductsResponse;
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCServiceModel;
import software.amazon.awssdk.eventstreamrpc.OperationModelContext;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class GetAllProductsOperationContext implements OperationModelContext<GetAllProductsRequest, GetAllProductsResponse, EventStreamJsonMessage, EventStreamJsonMessage> {
  @Override
  public EventStreamRPCServiceModel getServiceModel() {
    return EchoTestRPCServiceModel.getInstance();
  }

  @Override
  public String getOperationName() {
    return EchoTestRPCServiceModel.GET_ALL_PRODUCTS;
  }

  @Override
  public Class<GetAllProductsRequest> getRequestTypeClass() {
    return GetAllProductsRequest.class;
  }

  @Override
  public Class<GetAllProductsResponse> getResponseTypeClass() {
    return GetAllProductsResponse.class;
  }

  @Override
  public String getRequestApplicationModelType() {
    return GetAllProductsRequest.APPLICATION_MODEL_TYPE;
  }

  @Override
  public String getResponseApplicationModelType() {
    return GetAllProductsResponse.APPLICATION_MODEL_TYPE;
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
