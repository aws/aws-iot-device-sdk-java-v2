/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.iot.iotcommands.model;

/**
 * The status of the command execution.
 *
 */
public enum CommandExecutionStatus {

    /**
     * Enum value is an unknown value
     */
    UNKNOWN_ENUM_VALUE("UNKNOWN_ENUM_VALUE"),

    /**
     * The device is currently processing the received command.
     */
    IN_PROGRESS("IN_PROGRESS"),

    /**
     * The device successfully completed the command.
     */
    SUCCEEDED("SUCCEEDED"),

    /**
     * The device failed to complete the command.
     */
    FAILED("FAILED"),

    /**
     * The device received an invalid or incomplete request.
     */
    REJECTED("REJECTED"),

    /**
     * When the command execution timed out, this status can be used to provide additional information in the statusReason field in the UpdateCommandExecutionRequest request.
     */
    TIMED_OUT("TIMED_OUT");

    private String value;

    private CommandExecutionStatus(String value) {
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
    static CommandExecutionStatus fromString(String val) {
        for (CommandExecutionStatus e : CommandExecutionStatus.class.getEnumConstants()) {
            if (e.toString().compareTo(val) == 0) {
                return e;
            }
        }
        return UNKNOWN_ENUM_VALUE;
    }
}
