/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.iot.iotjobs.model;

public enum RejectedErrorCode {
    UNKNOWN_ENUM_VALUE("UNKNOWN_ENUM_VALUE"),
    INVALID_TOPIC("InvalidTopic"),
    INVALID_STATE_TRANSITION("InvalidStateTransition"),
    RESOURCE_NOT_FOUND("ResourceNotFound"),
    INVALID_REQUEST("InvalidRequest"),
    REQUEST_THROTTLED("RequestThrottled"),
    INTERNAL_ERROR("InternalError"),
    TERMINAL_STATE_REACHED("TerminalStateReached"),
    INVALID_JSON("InvalidJson"),
    VERSION_MISMATCH("VersionMismatch");

    private String value;

    private RejectedErrorCode(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    static RejectedErrorCode fromString(String val) {
        for (RejectedErrorCode e : RejectedErrorCode.class.getEnumConstants()) {
            if (e.toString().compareTo(val) == 0) {
                return e;
            }
        }
        return UNKNOWN_ENUM_VALUE;
    }
}
