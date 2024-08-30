/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

/* This file is part of greengrass-ipc project. */

package software.amazon.awssdk.eventstreamrpc.model;

/**
 * Thrown when a internal server exception occurs
 */
public class InternalServerException extends EventStreamOperationError {
    /**
     * The error code associated with a internal server exception
     */
    public static final String ERROR_CODE = "aws#InternalServerException";

    /**
     * Creates a new internal server exception from the given service name
     * @param serviceName The name of the service that caused the exception
     */
    public InternalServerException(String serviceName) {
        super(serviceName, ERROR_CODE, "An internal server exception has occurred.");
    }

    /**
     * Returns the named model type. May be used for a header.
     *
     * @return - Application Model Type
     */
    @Override
    public String getApplicationModelType() {
        return ERROR_CODE;
    }
}
