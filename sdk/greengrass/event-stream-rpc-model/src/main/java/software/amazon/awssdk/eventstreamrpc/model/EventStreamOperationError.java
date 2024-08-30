/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

/* This file is part of greengrass-ipc project. */

package software.amazon.awssdk.eventstreamrpc.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Root error type returned by any continuation error message
 *
 * TODO: To mimic public AWS SDK clients, any exception thrown by
 *       a given service should inherit from it's generated model
 *       service exception.
 */
public abstract class EventStreamOperationError
        extends RuntimeException
        implements EventStreamJsonMessage {
    @SerializedName("_service")
    @Expose(serialize = true, deserialize = true)
    private final String _service;

    @SerializedName("_message")
    @Expose(serialize = true, deserialize = true)
    private final String _message;

    @SerializedName("_errorCode")
    @Expose(serialize = true, deserialize = true)
    private final String _errorCode;

    /**
     * Creates a new EventStreamOperationError from the given service name, error code, and message
     * @param serviceName The service that caused the error
     * @param errorCode The error code associated with the error
     * @param message The message to show alongside the error
     */
    public EventStreamOperationError(final String serviceName, final String errorCode, final String message) {
        super(String.format("%s[%s]: %s", errorCode, serviceName, message));
        this._service = serviceName;
        this._errorCode = errorCode;
        this._message = message;
    }

    /**
     * Returns the name of the service that caused the error
     * @return the name of the service that caused the error
     */
    public String getService() {
        return _service;
    }

    /**
     * Likely overridden by a specific field defined in service-operation model
     * @return The message associated with the error
     */
    public String getMessage() {
        return _message;
    }

    /**
     * Likely subclasses will have a more limited set of valid error codes
     * @return The error code associated with the error
     */
    public String getErrorCode() { return _errorCode; }
}
