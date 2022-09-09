/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.iot.iotjobs.model;

/**
 * The status of the job execution.
 *
 */
public enum JobStatus {

    /**
     * Enum value is an unknown value
     */
    UNKNOWN_ENUM_VALUE("UNKNOWN_ENUM_VALUE"),

    /**
     * Enum value for IN_PROGRESS
     */
    IN_PROGRESS("IN_PROGRESS"),

    /**
     * Enum value for FAILED
     */
    FAILED("FAILED"),

    /**
     * Enum value for QUEUED
     */
    QUEUED("QUEUED"),

    /**
     * Enum value for TIMED_OUT
     */
    TIMED_OUT("TIMED_OUT"),

    /**
     * Enum value for SUCCEEDED
     */
    SUCCEEDED("SUCCEEDED"),

    /**
     * Enum value for CANCELED
     */
    CANCELED("CANCELED"),

    /**
     * Enum value for REJECTED
     */
    REJECTED("REJECTED"),

    /**
     * Enum value for REMOVED
     */
    REMOVED("REMOVED");

    private String value;

    private JobStatus(String value) {
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
    static JobStatus fromString(String val) {
        for (JobStatus e : JobStatus.class.getEnumConstants()) {
            if (e.toString().compareTo(val) == 0) {
                return e;
            }
        }
        return UNKNOWN_ENUM_VALUE;
    }
}
