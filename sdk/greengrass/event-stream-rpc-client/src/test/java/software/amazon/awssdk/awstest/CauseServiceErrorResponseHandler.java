package software.amazon.awssdk.awstest;

import java.lang.Override;
import java.lang.Void;
import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.awstest.model.CauseServiceErrorResponse;
import software.amazon.awssdk.eventstreamrpc.OperationResponse;
import software.amazon.awssdk.eventstreamrpc.StreamResponse;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public final class CauseServiceErrorResponseHandler implements StreamResponse<CauseServiceErrorResponse, EventStreamJsonMessage> {
  private final OperationResponse<CauseServiceErrorResponse, EventStreamJsonMessage> operationResponse;

  public CauseServiceErrorResponseHandler(
      final OperationResponse<CauseServiceErrorResponse, EventStreamJsonMessage> operationResponse) {
    this.operationResponse = operationResponse;
  }

  @Override
  public CompletableFuture<Void> getRequestFlushFuture() {
    return operationResponse.getRequestFlushFuture();
  }

  @Override
  public CompletableFuture<CauseServiceErrorResponse> getResponse() {
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
