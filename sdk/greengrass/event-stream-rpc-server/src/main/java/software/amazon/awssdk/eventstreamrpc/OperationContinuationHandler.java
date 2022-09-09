/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.awssdk.eventstreamrpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.crt.eventstream.Header;
import software.amazon.awssdk.crt.eventstream.MessageFlags;
import software.amazon.awssdk.crt.eventstream.MessageType;
import software.amazon.awssdk.crt.eventstream.ServerConnectionContinuationHandler;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamOperationError;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * Class to process continuations
 */
public abstract class OperationContinuationHandler<RequestType extends EventStreamJsonMessage,
        ResponseType extends EventStreamJsonMessage,
        StreamingRequestType extends EventStreamJsonMessage,
        StreamingResponseType extends EventStreamJsonMessage>
        extends ServerConnectionContinuationHandler implements StreamEventPublisher<StreamingResponseType> {
    private static final Logger LOGGER = LoggerFactory.getLogger(OperationContinuationHandler.class);

    private final OperationContinuationHandlerContext context;
    private List<Header> initialRequestHeaders;
    private RequestType initialRequest;

    /**
     * Returns the operation model context
     * @return the operation model context
     */
    abstract public OperationModelContext<RequestType, ResponseType,
            StreamingRequestType, StreamingResponseType> getOperationModelContext();

    /**
     * Constructs a new OperationContinuationHandler from the given context
     * @param context The operation OperationContinuationHandlerContext to use
     */
    public OperationContinuationHandler(final OperationContinuationHandlerContext context) {
        super(context.getContinuation());
        this.context = context;
    }

    @Override
    final protected void onContinuationClosed() {
        LOGGER.debug("{} stream continuation closed.", getOperationName());
        continuation.close();
        try {
            onStreamClosed();
        } catch (Exception e) {
            LOGGER.error("{} threw {}: {}", getOperationName(), e.getClass().getCanonicalName(), e.getMessage());
        }
    }

    /**
     * Returns the operation model context request type class
     * @return The operation model context request type class
     */
    final protected Class<RequestType> getRequestClass() {
        return getOperationModelContext().getRequestTypeClass();
    }

    /**
     * Returns the operation model context response type class
     * @return The operation model context response type class
     */
    final protected Class<ResponseType> getResponseClass() {
        return getOperationModelContext().getResponseTypeClass();
    }

    /**
     * Returns the operation model context streaming request type class
     * @return the operation model context streaming request type class
     */
    final protected Class<StreamingRequestType> getStreamingRequestClass() {
        return getOperationModelContext().getStreamingRequestTypeClass().get();
    }

    /**
     * Returns the operation model context streamining response type class
     * @return the operation model context streamining response type class
     */
    final protected Class<StreamingResponseType> getStreamingResponseClass() {
        return getOperationModelContext().getStreamingResponseTypeClass().get();
    }

    /**
     * Returns the operation name implemented by the handler. Generated code should populate this
     *
     * @return the operation name implemented by the handler.
     */
    private String getOperationName() {
        return getOperationModelContext().getOperationName();
    }

    /**
     * Called when the underlying continuation is closed. Gives operations a chance to cleanup whatever resources may be
     * on the other end of an open stream. Also invoked when an underlying ServerConnection is closed associated with
     * the stream/continuation
     */
    protected abstract void onStreamClosed();

    /**
     * Should return true if operation has either streaming input or output. If neither, return false and only allows
     * an initial-request -> initial->response before closing the continuation.
     *
     * @return true if operation has either streaming input or output
     */
    final protected boolean isStreamingOperation() {
        return getOperationModelContext().isStreamingOperation();
    }

    /**
     * Main request handler for any operation to do work on an initial request. Streaming response operations still must
     * send an initial-response which is empty.
     * <p>
     * Implementers should not call sendStreamEvent() during handleRequest() to send a streaming response after an
     * initial-response. This would violate the sequence of messages expected to occur for the specific operation.
     * Override "afterHandleRequest()" as a way of being informed of the quickest possible time to sent a stream
     * response after handleRequest returns.
     *
     * @param request The request to handle
     * @return The ResponseType after handling the request
     */
    public abstract ResponseType handleRequest(final RequestType request);

    /**
     * Same as handleRequest, but returns a future rather than running immediately on the SDK's thread.
     * If this method returns null, then handleRequest will be called.
     *
     * @param request The request to handle
     * @return A future containing the ResponseType after handling the request
     */
    public CompletableFuture<ResponseType> handleRequestAsync(final RequestType request) {
        return null;
    }

    /**
     * Override to appropriately enforce stream responses are sent after the initial response. This only gets called if
     * handleRequest returns normally and starts to send a response.
     */
    public void afterHandleRequest() {
    }

    /**
     * Handle an incoming stream event from the connected client on the operation.
     * <p>
     * If the implementation throws an exception, the framework will respond with the modeled exception to the client,
     * if it is modeled. If it is not modeled, it will respond with an internal error and log appropriately. Either
     * case, throwing an exception will result in closing the stream. To keep the stream open, do not throw
     *
     * @param streamRequestEvent The stream request event to handle
     */
    public abstract void handleStreamEvent(final StreamingRequestType streamRequestEvent);

    /**
     * Retrieves the underlying EventStream request headers for inspection. Pulling these headers out shouldn't be
     * necessary as it means operations are aware of the underlying protocol. Any headers needed to be pulled are
     * candidates for what should be in the service model directly
     *
     * @return The underlying EventStream request headers
     */
    final protected List<Header> getInitialRequestHeaders() {
        return initialRequestHeaders;   //not a defensive copy
    }

    /**
     * Retrieves the initial request object that initiated the stream
     * <p>
     * For use in handler implementations if initial request is wanted to handle further in-out events May be unecessary
     * memory, but also initial request may be used by framework to log errors with 'request-id' like semantics
     *
     * @return The initial request object that initiated the stream
     */
    final protected RequestType getInitialRequest() {
        return initialRequest;
    }

    /**
     * Retrieves the operation handler context. Use for inspecting state outside of the limited scope of this operation
     * handler.
     *
     * @return The operation handler context
     */
    final protected OperationContinuationHandlerContext getContext() {
        return context;
    }

    /**
     * TODO: close stream should be sent with the final message, or separately? Either should be fine
     *
     * @return A future that completes when the stream is closed
     */
    @Override
    final public CompletableFuture<Void> closeStream() {
        LOGGER.debug("[{}] closing stream", getOperationName());
        return continuation.sendMessage(null, null, MessageType.ApplicationMessage,
                MessageFlags.TerminateStream.getByteValue()).whenComplete((res, ex) -> {
            continuation.close();
            if (ex == null) {
                LOGGER.debug("[{}] closed stream", getOperationName());
            } else {
                LOGGER.error("[{}] {} error closing stream: {}", getOperationName(), ex.getClass().getName(),
                        ex.getMessage());
            }
        });
    }

    /**
     * Used so other processes/events going on in the server can push events back into this operation's opened
     * continuation
     *
     * @param streamingResponse A future that completes when the stream event message is sent
     */
    final public CompletableFuture<Void> sendStreamEvent(final StreamingResponseType streamingResponse) {
        return sendMessage(streamingResponse, false);
    }

    /**
     * Sends a message through the given continuation. If close is true, then the continuation is closed once finished
     * @param message The message to send
     * @param close If true, the continuation is closed after the message is sent
     * @return A future that completes when the message is sent
     */
    final protected CompletableFuture<Void> sendMessage(final EventStreamJsonMessage message, final boolean close) {
        if (continuation.isClosed()) { //is this check necessary?
            return CompletableFuture.supplyAsync(() -> {
                throw new EventStreamClosedException(continuation.getNativeHandle());
            });
        }
        final List<Header> responseHeaders = new ArrayList<>();
        byte[] outputPayload = getOperationModelContext().getServiceModel().toJson(message);
        responseHeaders.add(Header.createHeader(EventStreamRPCServiceModel.CONTENT_TYPE_HEADER,
                EventStreamRPCServiceModel.CONTENT_TYPE_APPLICATION_JSON));
        responseHeaders.add(Header.createHeader(EventStreamRPCServiceModel.SERVICE_MODEL_TYPE_HEADER,
                message.getApplicationModelType()));

        return continuation.sendMessage(responseHeaders, outputPayload, MessageType.ApplicationMessage,
                close ? MessageFlags.TerminateStream.getByteValue() : 0).whenComplete((res, ex) -> {
            if (close) {
                continuation.close();
            }
        });
    }

    /**
     * Sends an error over the stream. Same method is used for errors from the initial response or any errors that occur
     * while the stream is open. It will always close the stream/continuation on the same message using the terminate
     * flag on the same message
     *
     * @param message The message to send
     * @return A future that completes when the error is sent
     */
    final protected CompletableFuture<Void> sendModeledError(final EventStreamJsonMessage message) {
        if (continuation.isClosed()) {  //is this check necessary?
            return CompletableFuture.supplyAsync(() -> {
                throw new EventStreamClosedException(continuation.getNativeHandle());
            });
        }
        final List<Header> responseHeaders = new ArrayList<>();
        byte[] outputPayload = getOperationModelContext().getServiceModel().toJson(message);
        responseHeaders.add(Header.createHeader(EventStreamRPCServiceModel.CONTENT_TYPE_HEADER,
                EventStreamRPCServiceModel.CONTENT_TYPE_APPLICATION_JSON));
        responseHeaders.add(Header.createHeader(EventStreamRPCServiceModel.SERVICE_MODEL_TYPE_HEADER,
                message.getApplicationModelType()));

        return continuation.sendMessage(responseHeaders, outputPayload, MessageType.ApplicationError,
                MessageFlags.TerminateStream.getByteValue()).whenComplete((res, ex) -> {
            //complete silence on any error closing here
            continuation.close();
        });
    }

    private void invokeAfterHandleRequest() {
        try {
            afterHandleRequest();
        } catch (Exception e) {
            LOGGER.warn("{}.{} afterHandleRequest() threw {}: {}",
                    getOperationModelContext().getServiceModel().getServiceName(), getOperationName(),
                    e.getClass().getCanonicalName(), e.getMessage());
        }
    }

    @Override
    final protected void onContinuationMessage(List<Header> list, byte[] bytes, MessageType messageType,
                                               int messageFlags) {
        LOGGER.debug("Continuation native id: " + continuation.getNativeHandle());

        //We can prevent a client from sending a request, and hanging up before receiving a response
        //but doing so will prevent any work from being done
        if (initialRequest == null && (messageFlags & MessageFlags.TerminateStream.getByteValue()) != 0) {
            LOGGER.debug("Not invoking " + getOperationName() + " operation for client request received with a "
                    + "terminate flag set to 1");
            return;
        }
        final EventStreamRPCServiceModel serviceModel = getOperationModelContext().getServiceModel();
        try {
            if (initialRequest != null) {
                // Empty close stream messages from the client are valid. Do not need any processing here.
                if ((messageFlags & MessageFlags.TerminateStream.getByteValue()) != 0 && (bytes == null
                        || bytes.length == 0)) {
                    return;
                } else {
                    final StreamingRequestType streamEvent = serviceModel.fromJson(getStreamingRequestClass(), bytes);
                    //exceptions occurring during this processing will result in closure of stream
                    handleStreamEvent(streamEvent);
                }
            } else {
                //this is the initial request
                initialRequestHeaders = new ArrayList<>(list);
                initialRequest = serviceModel.fromJson(getRequestClass(), bytes);
                //call into business logic
                CompletableFuture<ResponseType> resultFuture = handleRequestAsync(initialRequest);
                if (resultFuture == null) {
                    resultFuture = CompletableFuture.completedFuture(handleRequest(initialRequest));
                }
                resultFuture.handle((result, throwable) -> {
                    if (throwable != null) {
                        handleAndSendError(throwable);
                        return null;
                    }
                    if (result != null) {
                        if (!getResponseClass().isInstance(result)) {
                            throw new RuntimeException("Handler for operation [" + getOperationName()
                                    + "] did not return expected type. Found: " + result.getClass().getName());
                        }
                        sendMessage(result, !isStreamingOperation()).whenComplete((res, ex) -> {
                            if (ex != null) {
                                LOGGER.error(ex.getClass().getName() + " sending response message: " + ex.getMessage());
                            } else {
                                LOGGER.trace("Response successfully sent");
                            }
                        });
                        invokeAfterHandleRequest();
                    } else {
                        //not streaming, but null response? we have a problem
                        throw new RuntimeException("Operation handler returned null response!");
                    }
                    return null;
                }).exceptionally((throwable) -> {
                    if (throwable != null) {
                        handleAndSendError(throwable);
                    }
                    return null;
                });
            }
        } catch (Exception e) {
            handleAndSendError(e);
        }
    }

    private void handleAndSendError(Throwable throwable) {
        // Pull out the underlying error from the "handle" method of a CompletableFuture
        if (throwable instanceof CompletionException) {
            throwable = throwable.getCause();
        }
        if (throwable instanceof EventStreamOperationError) {
            //We do not check if the specific exception thrown is a part of the core service?
            sendModeledError((EventStreamOperationError) throwable);
            invokeAfterHandleRequest();
        } else {
            final List<Header> responseHeaders = new ArrayList<>(1);
            byte[] outputPayload = "InternalServerError".getBytes(StandardCharsets.UTF_8);
            responseHeaders.add(Header.createHeader(EventStreamRPCServiceModel.CONTENT_TYPE_HEADER,
                    EventStreamRPCServiceModel.CONTENT_TYPE_APPLICATION_TEXT));
            //are there any exceptions we wouldn't want to return a generic server fault?
            //this is the kind of exception that should be logged with a request ID especially in a server-client context
            LOGGER.error("[{}] operation threw unexpected {}: {}", getOperationName(),
                    throwable.getClass().getCanonicalName(), throwable.getMessage());

            continuation.sendMessage(responseHeaders, outputPayload, MessageType.ApplicationError,
                    MessageFlags.TerminateStream.getByteValue()).whenComplete((res, ex) -> {
                if (ex != null) {
                    LOGGER.error(ex.getClass().getName() + " sending error response message: " + ex.getMessage());
                } else {
                    LOGGER.trace("Error response successfully sent");
                }
                continuation.close();
            });
        }
    }
}
