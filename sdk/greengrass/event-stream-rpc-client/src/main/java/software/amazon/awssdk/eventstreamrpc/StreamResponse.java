/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.eventstreamrpc;

import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

import java.util.concurrent.CompletableFuture;

/**
 * Interface for stream responses
 */
public interface StreamResponse<ResponseType extends EventStreamJsonMessage, StreamRequestType extends EventStreamJsonMessage>
                        extends StreamEventPublisher<StreamRequestType> {
    /**
     * Completable future indicating flush of the request that initiated the stream operation
     *
     * @return Completable future indicating flush of the request that initiated the stream operation
     */
    CompletableFuture<Void> getRequestFlushFuture();

    /**
     * Completable future for retrieving the initial-response of the stream operation
     *
     * @return Completable future for retrieving the initial-response of the stream operation
     */
    CompletableFuture<ResponseType> getResponse();

    /**
     * Tests if the stream is closed
     * @return True if the stream is closed
     */
    boolean isClosed();
}
