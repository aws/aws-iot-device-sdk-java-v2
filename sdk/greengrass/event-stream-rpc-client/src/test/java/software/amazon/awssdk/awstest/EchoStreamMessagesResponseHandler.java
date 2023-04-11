package software.amazon.awssdk.awstest;

import java.lang.Override;
import java.lang.Void;
import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.awstest.model.EchoStreamingMessage;
import software.amazon.awssdk.awstest.model.EchoStreamingResponse;
import software.amazon.awssdk.eventstreamrpc.OperationResponse;
import software.amazon.awssdk.eventstreamrpc.StreamResponse;

public final class EchoStreamMessagesResponseHandler implements StreamResponse<EchoStreamingResponse, EchoStreamingMessage> {
  private final OperationResponse<EchoStreamingResponse, EchoStreamingMessage> operationResponse;

  public EchoStreamMessagesResponseHandler(
      final OperationResponse<EchoStreamingResponse, EchoStreamingMessage> operationResponse) {
    this.operationResponse = operationResponse;
  }

  @Override
  public CompletableFuture<Void> getRequestFlushFuture() {
    return operationResponse.getRequestFlushFuture();
  }

  @Override
  public CompletableFuture<EchoStreamingResponse> getResponse() {
    return operationResponse.getResponse();
  }

  @Override
  public CompletableFuture<Void> sendStreamEvent(final EchoStreamingMessage event) {
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
