/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

/* This file is part of greengrass-ipc project. */

package software.amazon.awssdk.eventstreamrpc.model;

/**
 * Thrown when an access denied exception occurs
 */
public class AccessDeniedException extends EventStreamOperationError {
    /**
    * The error code associated with a access denied exception
    */
    public static final String ERROR_CODE = "aws#AccessDenied";

    /**
     * Message constructor may reveal what operation or resource was denied access
     * or the principal/authN that was rejected
     *
     * Do not overexpose reason or logic for AccessDenied. Prefer internal logging
     *
     * @param serviceName The name of the service that caused the exception
     * @param message The message to associate with the exception
     */
    public AccessDeniedException(String serviceName, String message) {
        super(serviceName, ERROR_CODE, message);
    }

    /**
     * Message constructor may reveal what operation or resource was denied access
     * or the principal/authN that was rejected
     *
     * Do not overexpose reason or logic for AccessDenied. Prefer internal logging
     *
     * @param serviceName The name of the service that caused the exception
     */
    public AccessDeniedException(String serviceName) {
        super(serviceName, ERROR_CODE, "AccessDenied");
    }

    /**
     * Returns the named model type. May be used for a header.
     *
     * @return The named model type
     */
    @Override
    public String getApplicationModelType() {
        return ERROR_CODE;
    }
}
