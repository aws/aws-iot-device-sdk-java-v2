/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.eventstreamrpc;

import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

/**
 * Unmapped data exception is generated either on server or client side when recieving data
 * over the wire and is unable to map properly to an expected type to be received
 * for the operation. Or an exception (don't have to be called out).
 */
public class UnmappedDataException extends RuntimeException {

    /**
     * Creates a new Unmapped data exception.
     * @param applicationModelType The application model type that caused the exception
     */
    public UnmappedDataException(String applicationModelType) {
        super(String.format("Cannot find Java class type for application model type: %s", applicationModelType));
    }

    /**
     * Creates a new Unmapped data exception.
     * @param expectedClass The application class that caused the exception
     */
    public UnmappedDataException(Class<? extends EventStreamJsonMessage> expectedClass) {
        super(String.format("Data does not map into Java class: %s", expectedClass.getCanonicalName()));
    }

    /**
     * Creates a new Unmapped data exception.
     * @param applicationModelType The application model type that caused the exception
     * @param expectedClass The application class that caused the exception
     */
    public UnmappedDataException(String applicationModelType, Class<? extends EventStreamJsonMessage> expectedClass) {
        super(String.format("Found model-type {%s} which does not map into Java class: %s", applicationModelType, expectedClass.getCanonicalName()));
    }
}
