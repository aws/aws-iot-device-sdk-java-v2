/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

/* This file is part of greengrass-ipc project. */

package software.amazon.awssdk.eventstreamrpc;

import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

import java.util.concurrent.CompletableFuture;

/**
 * Interface to enable sending events over an open stream operation.
 *
 * @param <StreamEventType> Data to push over the open stream
 */
public interface StreamEventPublisher<StreamEventType extends EventStreamJsonMessage> {
    /**
     * Publish an event over an open stream operation.
     *
     * @param streamEvent event to publish
     * @return Completable future indicating flush of the event over the stream
     */
    public CompletableFuture<Void> sendStreamEvent(final StreamEventType streamEvent);

    /**
     * Closes the stream by sending an empty message
     *
     * @return Completable future indicating flush of the stream termination message
     */
    public CompletableFuture<Void> closeStream();
}
