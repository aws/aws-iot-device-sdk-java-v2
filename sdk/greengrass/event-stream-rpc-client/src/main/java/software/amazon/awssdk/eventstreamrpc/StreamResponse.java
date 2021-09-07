package software.amazon.awssdk.eventstreamrpc;

import software.amazon.awssdk.eventstreamrpc.StreamEventPublisher;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

import java.util.concurrent.CompletableFuture;

public interface StreamResponse<ResponseType extends EventStreamJsonMessage, StreamRequestType extends EventStreamJsonMessage>
                        extends StreamEventPublisher<StreamRequestType> {
    /**
     * Completable future indicating flush of the request that initiated the stream operation
     *
     * @return {@link CompletableFuture}
     */
    CompletableFuture<Void> getRequestFlushFuture();

    /**
     * Completable future for retrieving the initial-response of the stream operation
     *
     * @return {@link CompletableFuture}
     */
    CompletableFuture<ResponseType> getResponse();

    /**
     * Tests if the stream is closed
     * @return - boolean
     */
    boolean isClosed();
}
