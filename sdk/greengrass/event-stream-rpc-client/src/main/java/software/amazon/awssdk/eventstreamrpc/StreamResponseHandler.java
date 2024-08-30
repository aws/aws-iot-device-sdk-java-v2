/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

/* This file is part of greengrass-ipc project. */

package software.amazon.awssdk.eventstreamrpc;

/**
 * Operation response handler is needed to invoke an operation that has a streaming
 * response element to it.
 *
 * @param <StreamEventType> The stream event type
 */
public interface StreamResponseHandler<StreamEventType> {

    /**
     * Called when there is a stream event to process
     * @param streamEvent The stream event to process
     */
    void onStreamEvent(final StreamEventType streamEvent);

    /**
     * Called when there's an error in the stream. Return value of this function
     * suggests whether or not the client handling will keep the stream open
     * or close it.
     *
     * There are conditions when onStreamError() may be triggered but the client handling will
     * close the connection anyways.
     *
     * @param error The error that occurred
     * @return true if the stream should be closed on this error, false if stream should remain open
     */
    boolean onStreamError(final Throwable error);

    /**
     * Called when stream is closed
     */
    void onStreamClosed();
}
