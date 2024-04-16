/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

/* This file is part of greengrass-ipc project. */

package software.amazon.awssdk.eventstreamrpc;

import software.amazon.awssdk.crt.eventstream.ClientConnectionContinuation;
import software.amazon.awssdk.crt.eventstream.Header;
import software.amazon.awssdk.crt.eventstream.MessageFlags;
import software.amazon.awssdk.crt.eventstream.MessageType;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

/**
 * Underlying type for operation response handling. Enables publishing on stream operations from
 * client, closing of any open stream, and retrieval of response. Specific generated operation response
 * handlers are usually simple wrappers with the generic types specified
 *
 * @param <ResponseType> The response type
 * @param <StreamRequestType> The stream response type
 */
public class OperationResponse<ResponseType extends EventStreamJsonMessage,
                        StreamRequestType extends EventStreamJsonMessage>
        implements StreamResponse<ResponseType, StreamRequestType>, AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(OperationResponse.class.getName());
    private final OperationModelContext operationModelContext;
    private final ClientConnectionContinuation continuation;
    private final CompletableFuture<ResponseType> responseFuture;
    private final CompletableFuture<Void> requestFlushFuture;
    private final AtomicBoolean isClosed;

    /**
     * Creates a new OperationResponse from the given data
     * @param operationModelContext The operation model context to use
     * @param continuation The continuation to use
     * @param responseFuture The response future to use
     * @param requestFlushFuture The request flush future to use
     */
    public OperationResponse(OperationModelContext<ResponseType, ?, StreamRequestType, ?> operationModelContext,
                             ClientConnectionContinuation continuation,
                             CompletableFuture<ResponseType> responseFuture,
                             CompletableFuture<Void> requestFlushFuture) {
        this.operationModelContext = operationModelContext;
        this.continuation = continuation;
        this.responseFuture = responseFuture;
        this.requestFlushFuture = requestFlushFuture;
        this.isClosed = new AtomicBoolean(continuation != null && !continuation.isNull());
    }

    /**
     * Returns the request flush future to use
     * @return The request flush future to use
     */
    final public CompletableFuture<Void> getRequestFlushFuture() {
        return requestFlushFuture;
    }

    /**
     * Get the response completable future to wait on the initial response
     * if there is one.
     *
     * May throw exception if requestFlushFuture throws an exception and will
     * block if requestFlush has not completed.
     *
     * @return the response completable future to wait on the initial response
     * if there is one.
     */
    public CompletableFuture<ResponseType> getResponse() {
        //semantics here are: if the request was never successfully sent
        //then the request flush future holds the exception thrown so that
        //must be made visible of the caller waits for the response directly.
        //It is impossible to have a successful response future completed
        //with a request flush never having completed or having thrown an
        //exception.
        return requestFlushFuture.thenCompose((v) -> responseFuture);
    }

    /**
     * Publish stream events on an open operation's event stream.
     * @param streamEvent event to publish
     */
    @Override
    public CompletableFuture<Void> sendStreamEvent(final StreamRequestType streamEvent) {
        try {
            final List<Header> headers = new LinkedList<>();
            headers.add(Header.createHeader(EventStreamRPCServiceModel.SERVICE_MODEL_TYPE_HEADER,
                    (String) operationModelContext.getStreamingRequestApplicationModelType().get()));
            headers.add(Header.createHeader(EventStreamRPCServiceModel.CONTENT_TYPE_HEADER,
                    EventStreamRPCServiceModel.CONTENT_TYPE_APPLICATION_JSON));
            final byte[] payload = operationModelContext.getServiceModel()
                    .toJson(streamEvent);
            return continuation.sendMessage(headers, payload,
                    MessageType.ApplicationMessage, 0)
                    .whenComplete((res, ex) -> {
                        if (ex != null) {
                            LOGGER.warning(String.format("%s caught %s while sending message the event stream: %s",
                                    operationModelContext.getOperationName(), ex.getClass().getName(),
                                    ex.getMessage()));
                            closeStream();
                        }
                    });
        } catch (Exception e) {
            final CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    /**
     * Initiate a close on the event stream from the client side.
     *
     * @return A future that completes when the event stream is closed
     */
    @Override
    public CompletableFuture<Void> closeStream() {
        if (continuation != null && !continuation.isNull()) {
            return continuation.sendMessage(null, null,
                    MessageType.ApplicationMessage, MessageFlags.TerminateStream.getByteValue())
                    .whenComplete((res, ex) -> {
                        LOGGER.info(operationModelContext.getOperationName() + " operation stream closed");
                        continuation.close();
                        if (ex != null) {
                            LOGGER.warning(String.format("%s threw %s while closing the event stream: %s",
                                    operationModelContext.getOperationName(), ex.getClass().getName(),
                                    ex.getMessage()));
                        }
                    });
        }
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Checks if the stream is closed
     * @return True if the stream is closed
     */
    public boolean isClosed() {
        return isClosed.get();
    }

    @Override
    public void close() throws Exception {
        if (isClosed.compareAndSet(false, true)) {
            closeStream();
        }
    }
}
