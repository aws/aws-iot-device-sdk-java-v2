package software.amazon.awssdk.awstest;

import java.util.Optional;
import software.amazon.awssdk.awstest.model.CauseServiceErrorRequest;
import software.amazon.awssdk.awstest.model.EchoMessageRequest;
import software.amazon.awssdk.awstest.model.EchoStreamingMessage;
import software.amazon.awssdk.awstest.model.EchoStreamingRequest;
import software.amazon.awssdk.awstest.model.GetAllCustomersRequest;
import software.amazon.awssdk.awstest.model.GetAllProductsRequest;
import software.amazon.awssdk.eventstreamrpc.StreamResponseHandler;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public interface EchoTestRPC {
  CauseServiceErrorResponseHandler causeServiceError(final CauseServiceErrorRequest request,
      final Optional<StreamResponseHandler<EventStreamJsonMessage>> streamResponseHandler);

  CauseStreamServiceToErrorResponseHandler causeStreamServiceToError(
      final EchoStreamingRequest request,
      final Optional<StreamResponseHandler<EchoStreamingMessage>> streamResponseHandler);

  EchoMessageResponseHandler echoMessage(final EchoMessageRequest request,
      final Optional<StreamResponseHandler<EventStreamJsonMessage>> streamResponseHandler);

  EchoStreamMessagesResponseHandler echoStreamMessages(final EchoStreamingRequest request,
      final Optional<StreamResponseHandler<EchoStreamingMessage>> streamResponseHandler);

  GetAllCustomersResponseHandler getAllCustomers(final GetAllCustomersRequest request,
      final Optional<StreamResponseHandler<EventStreamJsonMessage>> streamResponseHandler);

  GetAllProductsResponseHandler getAllProducts(final GetAllProductsRequest request,
      final Optional<StreamResponseHandler<EventStreamJsonMessage>> streamResponseHandler);
}
