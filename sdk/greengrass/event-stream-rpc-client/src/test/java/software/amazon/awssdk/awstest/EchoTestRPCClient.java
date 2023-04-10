package software.amazon.awssdk.awstest;

import java.lang.Override;
import java.util.Optional;
import software.amazon.awssdk.awstest.model.CauseServiceErrorRequest;
import software.amazon.awssdk.awstest.model.EchoMessageRequest;
import software.amazon.awssdk.awstest.model.EchoStreamingMessage;
import software.amazon.awssdk.awstest.model.EchoStreamingRequest;
import software.amazon.awssdk.awstest.model.GetAllCustomersRequest;
import software.amazon.awssdk.awstest.model.GetAllProductsRequest;
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCClient;
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCConnection;
import software.amazon.awssdk.eventstreamrpc.StreamResponseHandler;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class EchoTestRPCClient extends EventStreamRPCClient implements EchoTestRPC {
  public EchoTestRPCClient(final EventStreamRPCConnection connection) {
    super(connection);
  }

  @Override
  public CauseServiceErrorResponseHandler causeServiceError(final CauseServiceErrorRequest request,
      final Optional<StreamResponseHandler<EventStreamJsonMessage>> streamResponseHandler) {
    final CauseServiceErrorOperationContext operationContext = EchoTestRPCServiceModel.getCauseServiceErrorModelContext();
    return new CauseServiceErrorResponseHandler(doOperationInvoke(operationContext, request, streamResponseHandler));
  }

  @Override
  public CauseStreamServiceToErrorResponseHandler causeStreamServiceToError(
      final EchoStreamingRequest request,
      final Optional<StreamResponseHandler<EchoStreamingMessage>> streamResponseHandler) {
    final CauseStreamServiceToErrorOperationContext operationContext = EchoTestRPCServiceModel.getCauseStreamServiceToErrorModelContext();
    return new CauseStreamServiceToErrorResponseHandler(doOperationInvoke(operationContext, request, streamResponseHandler));
  }

  @Override
  public EchoMessageResponseHandler echoMessage(final EchoMessageRequest request,
      final Optional<StreamResponseHandler<EventStreamJsonMessage>> streamResponseHandler) {
    final EchoMessageOperationContext operationContext = EchoTestRPCServiceModel.getEchoMessageModelContext();
    return new EchoMessageResponseHandler(doOperationInvoke(operationContext, request, streamResponseHandler));
  }

  @Override
  public EchoStreamMessagesResponseHandler echoStreamMessages(final EchoStreamingRequest request,
      final Optional<StreamResponseHandler<EchoStreamingMessage>> streamResponseHandler) {
    final EchoStreamMessagesOperationContext operationContext = EchoTestRPCServiceModel.getEchoStreamMessagesModelContext();
    return new EchoStreamMessagesResponseHandler(doOperationInvoke(operationContext, request, streamResponseHandler));
  }

  @Override
  public GetAllCustomersResponseHandler getAllCustomers(final GetAllCustomersRequest request,
      final Optional<StreamResponseHandler<EventStreamJsonMessage>> streamResponseHandler) {
    final GetAllCustomersOperationContext operationContext = EchoTestRPCServiceModel.getGetAllCustomersModelContext();
    return new GetAllCustomersResponseHandler(doOperationInvoke(operationContext, request, streamResponseHandler));
  }

  @Override
  public GetAllProductsResponseHandler getAllProducts(final GetAllProductsRequest request,
      final Optional<StreamResponseHandler<EventStreamJsonMessage>> streamResponseHandler) {
    final GetAllProductsOperationContext operationContext = EchoTestRPCServiceModel.getGetAllProductsModelContext();
    return new GetAllProductsResponseHandler(doOperationInvoke(operationContext, request, streamResponseHandler));
  }
}
