package software.amazon.awssdk.aws.greengrass;

import java.lang.Override;
import java.lang.Void;
import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.aws.greengrass.model.CreateLocalDeploymentResponse;
import software.amazon.awssdk.eventstreamrpc.OperationResponse;
import software.amazon.awssdk.eventstreamrpc.StreamResponse;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public final class CreateLocalDeploymentResponseHandler implements StreamResponse<CreateLocalDeploymentResponse, EventStreamJsonMessage> {
  private final OperationResponse<CreateLocalDeploymentResponse, EventStreamJsonMessage> operationResponse;

  public CreateLocalDeploymentResponseHandler(
      final OperationResponse<CreateLocalDeploymentResponse, EventStreamJsonMessage> operationResponse) {
    this.operationResponse = operationResponse;
  }

  @Override
  public CompletableFuture<Void> getRequestFlushFuture() {
    return operationResponse.getRequestFlushFuture();
  }

  @Override
  public CompletableFuture<CreateLocalDeploymentResponse> getResponse() {
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
