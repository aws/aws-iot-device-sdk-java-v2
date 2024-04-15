/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

/* This file is part of greengrass-ipc project. */

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
 * @param <RequestType> The initial-request
 * @param <ResponseType> The initial-response
 * @param <StreamingRequestType> The streaming initial-request
 * @param <StreamingResponseType> The streaming initial-response
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
     * @return the service model which can look up all/any Java error class types if an
     * operation throws it so the handling has a chance
     */
    EventStreamRPCServiceModel getServiceModel();

    /**
     * Returns the canonical operation name associated with this context across any client language.
     * Namespace included
     *
     * Example: aws.greengrass#SubscribeToTopic
     * @return the canonical operation name associated with this context across any client language.
     */
    String getOperationName();

    /**
     * Returns the initial-request java class type
     * @return the initial-request java class type
     */
    Class<RequestType> getRequestTypeClass();

    /**
     * Returns the application model type string for the initial-request object
     * @return the application model type string for the initial-request object
     */
    String getRequestApplicationModelType();

    /**
     * Returns the initial-response java class type
     * @return the initial-response java class type
     */
    Class<ResponseType> getResponseTypeClass();

    /**
     * Returns the application model type string for the initial response object
     * @return the application model type string for the initial response object
     */
    String getResponseApplicationModelType();

    /**
     * Returns the streaming-request java class type
     * @return the streaming-request java class type
     */
    Optional<Class<StreamingRequestType>> getStreamingRequestTypeClass();

    /**
     * Returns the application model type of
     * @return the application model type of
     */
    Optional<String> getStreamingRequestApplicationModelType();

    /**
     * Returns the streaming-response java class type
     *
     * @return the streaming-response java class type
     */
    Optional<Class<StreamingResponseType>> getStreamingResponseTypeClass();

    /**
     * Returns the streaming response application model string
     *
     * @return the streaming response application model string
     */
    Optional<String> getStreamingResponseApplicationModelType();

    /**
     * Returns true if there is a streaming request or response associated with the operation
     * or both
     * @return true if there is a streaming request or response associated with the operation
     * or both
     */
    default boolean isStreamingOperation() {
        return getStreamingRequestTypeClass().isPresent() || getStreamingResponseTypeClass().isPresent();
    }

}
