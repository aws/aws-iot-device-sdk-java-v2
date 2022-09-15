/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.aws.greengrass;

import java.lang.Override;
import java.lang.Void;
import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.aws.greengrass.model.SubscribeToTopicResponse;
import software.amazon.awssdk.eventstreamrpc.OperationResponse;
import software.amazon.awssdk.eventstreamrpc.StreamResponse;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public final class SubscribeToTopicResponseHandler implements StreamResponse<SubscribeToTopicResponse, EventStreamJsonMessage> {
  private final OperationResponse<SubscribeToTopicResponse, EventStreamJsonMessage> operationResponse;

  public SubscribeToTopicResponseHandler(
      final OperationResponse<SubscribeToTopicResponse, EventStreamJsonMessage> operationResponse) {
    this.operationResponse = operationResponse;
  }

  @Override
  public CompletableFuture<Void> getRequestFlushFuture() {
    return operationResponse.getRequestFlushFuture();
  }

  @Override
  public CompletableFuture<SubscribeToTopicResponse> getResponse() {
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
