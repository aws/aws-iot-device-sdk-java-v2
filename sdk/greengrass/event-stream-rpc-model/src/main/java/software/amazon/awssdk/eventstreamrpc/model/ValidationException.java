/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.eventstreamrpc.model;

/**
 * Thrown when a validation exception occurs
 */
public class ValidationException extends EventStreamOperationError {
    /**
     * The error code associated with a validation exception
     */
    public static final String ERROR_CODE = "aws#ValidationException";

    /**
     * Creates a new ValidationException with the given service name and message
     * @param serviceName The name of the service that caused the exception
     * @param message The reason for the exception
     */
    public ValidationException(String serviceName, String message) {
        super(serviceName, ERROR_CODE, message);
    }

    /**
     * Returns the named model type. May be used for a header.
     *
     * @return the named model type
     */
    @Override
    public String getApplicationModelType() {
        return ERROR_CODE;
    }
}
