package software.amazon.awssdk.awstest;

import software.amazon.awssdk.awstest.model.GetAllProductsResponse;
import software.amazon.awssdk.eventstreamrpc.OperationResponse;
import software.amazon.awssdk.eventstreamrpc.StreamResponse;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

import java.util.concurrent.CompletableFuture;

public final class GetAllProductsResponseHandler implements StreamResponse<GetAllProductsResponse, EventStreamJsonMessage> {
  private final OperationResponse<GetAllProductsResponse, EventStreamJsonMessage> operationResponse;

  public GetAllProductsResponseHandler(
      final OperationResponse<GetAllProductsResponse, EventStreamJsonMessage> operationResponse) {
    this.operationResponse = operationResponse;
  }

  @Override
  public CompletableFuture<Void> getRequestFlushFuture() {
    return operationResponse.getRequestFlushFuture();
  }

  @Override
  public CompletableFuture<GetAllProductsResponse> getResponse() {
    return operationResponse.getResponse();
  }

  @Override
  public CompletableFuture<Void> sendStreamEvent(final EventStreamJsonMessage event) {
    return operationResponse.sendStreamEvent(event);
  }

  @Override
  public CompletableFuture<Void> closeStream() {
    return operationResponse.closeStream();
  }

  @Override
  public boolean isClosed() {
    return operationResponse.isClosed();
  }
}
