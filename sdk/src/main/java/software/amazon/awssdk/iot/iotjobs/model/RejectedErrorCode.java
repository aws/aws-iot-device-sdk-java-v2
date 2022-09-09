/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.iot.iotjobs.model;

/**
 * A value indicating the kind of error encountered while processing an AWS IoT Jobs request
 *
 */
public enum RejectedErrorCode {

    /**
     * Enum value is an unknown value
     */
    UNKNOWN_ENUM_VALUE("UNKNOWN_ENUM_VALUE"),

    /**
     * The request was sent to a topic in the AWS IoT Jobs namespace that does not map to any API.
     */
    INVALID_TOPIC("InvalidTopic"),

    /**
     * An update attempted to change the job execution to a state that is invalid because of the job execution's current state. In this case, the body of the error message also contains the executionState field.
     */
    INVALID_STATE_TRANSITION("InvalidStateTransition"),

    /**
     * The JobExecution specified by the request topic does not exist.
     */
    RESOURCE_NOT_FOUND("ResourceNotFound"),

    /**
     * The contents of the request were invalid. The message contains details about the error.
     */
    INVALID_REQUEST("InvalidRequest"),

    /**
     * The request was throttled.
     */
    REQUEST_THROTTLED("RequestThrottled"),

    /**
     * There was an internal error during the processing of the request.
     */
    INTERNAL_ERROR("InternalError"),

    /**
     * Occurs when a command to describe a job is performed on a job that is in a terminal state.
     */
    TERMINAL_STATE_REACHED("TerminalStateReached"),

    /**
     * The contents of the request could not be interpreted as valid UTF-8-encoded JSON.
     */
    INVALID_JSON("InvalidJson"),

    /**
     * The expected version specified in the request does not match the version of the job execution in the AWS IoT Jobs service. In this case, the body of the error message also contains the executionState field.
     */
    VERSION_MISMATCH("VersionMismatch");

    private String value;

    private RejectedErrorCode(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    /**
     * Returns The enum associated with the given string or UNKNOWN_ENUM_VALUE
     * if no enum is found.
     * @param val The string to use
     * @return The enum associated with the string or UNKNOWN_ENUM_VALUE
     */
    static RejectedErrorCode fromString(String val) {
        for (RejectedErrorCode e : RejectedErrorCode.class.getEnumConstants()) {
            if (e.toString().compareTo(val) == 0) {
                return e;
            }
        }
        return UNKNOWN_ENUM_VALUE;
    }
}
