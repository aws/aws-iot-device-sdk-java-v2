/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.iot.iotcommands.model;

/**
 * A value indicating the kind of error encountered while processing an AWS IoT Commands request
 *
 */
public enum RejectedErrorCode {

    /**
     * Enum value is an unknown value
     */
    UNKNOWN_ENUM_VALUE("UNKNOWN_ENUM_VALUE"),

    /**
     * The request was sent to a topic in the AWS IoT Commands namespace that does not map to any API.
     */
    INVALID_TOPIC("InvalidTopic"),

    /**
     * The contents of the request could not be interpreted as valid UTF-8-encoded JSON.
     */
    INVALID_JSON("InvalidJson"),

    /**
     * The contents of the request were invalid. The message contains details about the error.
     */
    INVALID_REQUEST("InvalidRequest"),

    /**
     * An update attempted to change the command execution to a state that is invalid because of the command execution's current state. In this case, the body of the error message also contains the executionState field.
     */
    INVALID_STATE_TRANSITION("InvalidStateTransition"),

    /**
     * The CommandExecution specified by the request topic does not exist.
     */
    RESOURCE_NOT_FOUND("ResourceNotFound"),

    /**
     * The expected version specified in the request does not match the version of the command execution in the AWS IoT Commands service. In this case, the body of the error message also contains the executionState field.
     */
    VERSION_MISMATCH("VersionMismatch"),

    /**
     * There was an internal error during the processing of the request.
     */
    INTERNAL_ERROR("InternalError"),

    /**
     * The request was throttled.
     */
    REQUEST_THROTTLED("RequestThrottled"),

    /**
     * Occurs when a command to describe a command is performed on a command that is in a terminal state.
     */
    TERMINAL_STATE_REACHED("TerminalStateReached");

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
