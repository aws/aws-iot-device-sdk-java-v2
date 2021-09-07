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
 * @param <RequestType> - Request Type
 * @param <ResponseType> - Response Type
 * @param <StreamingRequestType> - Streaming Request Type
 * @param <StreamingResponseType> - Streaming Response Type
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
     * @return {@link EventStreamRPCServiceModel}
     */
    EventStreamRPCServiceModel getServiceModel();

    /**
     * Returns the canonical operation name associated with this context across any client language.
     * Namespace included
     *
     * Example: aws.greengrass#SubscribeToTopic
     * @return - Operation Name
     */
    String getOperationName();

    /**
     * Returns the initial-request java class type
     * @return - Type
     */
    Class<RequestType> getRequestTypeClass();

    /**
     * Returns the application model type string for the initial-request object
     * @return - Application Model Type
     */
    String getRequestApplicationModelType();

    /**
     * Returns the initial-response java class type
     * @return - Response Type
     */
    Class<ResponseType> getResponseTypeClass();

    /**
     * Returns the application model type string for the initial response object
     * @return - Response Application Model Type
     */
    String getResponseApplicationModelType();

    /**
     * Returns the streaming-request java class type
     * @return - Streaming Request Type Class
     */
    Optional<Class<StreamingRequestType>> getStreamingRequestTypeClass();

    /**
     * Returns the application model type of
     * @return - Streaming Request Application Model Type
     */
    Optional<String> getStreamingRequestApplicationModelType();

    /**
     * Returns the streaming-response java class type
     *
     * @return - Streaming Response Type Class
     */
    Optional<Class<StreamingResponseType>> getStreamingResponseTypeClass();

    /**
     * Returns the streaming response application model string
     *
     * @return - Streaming Response Application Model Type
     */
    Optional<String> getStreamingResponseApplicationModelType();

    /**
     * Returns true if there is a streaming request or response associated with the operation
     * or both
     * @return boolean
     */
    default boolean isStreamingOperation() {
        return getStreamingRequestTypeClass().isPresent() || getStreamingResponseTypeClass().isPresent();
    }

}
