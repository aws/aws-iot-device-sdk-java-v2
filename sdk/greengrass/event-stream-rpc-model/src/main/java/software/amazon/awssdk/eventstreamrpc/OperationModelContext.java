package software.amazon.awssdk.eventstreamrpc;

import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

import java.util.Optional;

/**
 * Interface used for both client and server to dictate how to handle operations modeled by implementing this
 * interface.
 *
 * Smithy code generation should produce one of these per model, but we aren't going to take steps to prevent
 * hand construction.
 *
 * @param <RequestType>
 * @param <ResponseType>
 * @param <StreamingRequestType>
 * @param <StreamingResponseType>
 */
public interface OperationModelContext
        <RequestType extends EventStreamJsonMessage,
         ResponseType extends EventStreamJsonMessage,
         StreamingRequestType extends EventStreamJsonMessage,
         StreamingResponseType extends EventStreamJsonMessage> {

    /**
     * Returns the service model which can look up all/any Java error class types if an
     * operation throws it so the handling has a chance
     *
     * @return
     */
    EventStreamRPCServiceModel getServiceModel();

    /**
     * Returns the canonical operation name associated with this context across any client language.
     * Namespace included
     *
     * Example: aws.greengrass#SubscribeToTopic
     * @return
     */
    String getOperationName();

    /**
     * Returns the initial-request java class type
     * @return
     */
    Class<RequestType> getRequestTypeClass();

    /**
     * Returns the application model type string for the initial-request object
     * @return
     */
    String getRequestApplicationModelType();

    /**
     * Returns the initial-response java class type
     * @return
     */
    Class<ResponseType> getResponseTypeClass();

    /**
     * Returns the application model type string for the initial response object
     * @return
     */
    String getResponseApplicationModelType();

    /**
     * Returns the streaming-request java class type
     * @return
     */
    Optional<Class<StreamingRequestType>> getStreamingRequestTypeClass();

    /**
     * Returns the application model type of
     * @return
     */
    Optional<String> getStreamingRequestApplicationModelType();

    /**
     * Returns the streaming-response java class type
     *
     * @return
     */
    Optional<Class<StreamingResponseType>> getStreamingResponseTypeClass();

    /**
     * Returns the streaming response application model string
     *
     * @return
     */
    Optional<String> getStreamingResponseApplicationModelType();

    /**
     * Returns true if there is a streaming request or response associated with the operation
     * or both
     * @return
     */
    default boolean isStreamingOperation() {
        return getStreamingRequestTypeClass().isPresent() || getStreamingResponseTypeClass().isPresent();
    }

}
