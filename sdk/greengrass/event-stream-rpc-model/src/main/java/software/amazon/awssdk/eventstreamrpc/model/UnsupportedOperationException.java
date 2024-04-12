/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.eventstreamrpc.model;

/**
 * Thrown when an unsupported operation exception occurs
 */
public class UnsupportedOperationException extends EventStreamOperationError {
    /**
     * The error code associated with a unsupported operation exception
     */
    public static final String ERROR_CODE = "aws#UnsupportedOperation";

    /**
     * Creates a new UnsupportedOperationException from the given service and operation names
     * @param serviceName The name of the service that caused the exception
     * @param operationName The name of the operation that caused the exception
     */
    public UnsupportedOperationException(String serviceName, String operationName) {
        super(serviceName, ERROR_CODE, "UnsupportedOperation: " + operationName);
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
