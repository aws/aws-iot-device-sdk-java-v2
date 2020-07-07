/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.iot.iotjobs.model;

public enum JobStatus {
    UNKNOWN_ENUM_VALUE("UNKNOWN_ENUM_VALUE"),
    IN_PROGRESS("IN_PROGRESS"),
    FAILED("FAILED"),
    QUEUED("QUEUED"),
    TIMED_OUT("TIMED_OUT"),
    SUCCEEDED("SUCCEEDED"),
    CANCELED("CANCELED"),
    REJECTED("REJECTED"),
    REMOVED("REMOVED");

    private String value;

    private JobStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    static JobStatus fromString(String val) {
        for (JobStatus e : JobStatus.class.getEnumConstants()) {
            if (e.toString().compareTo(val) == 0) {
                return e;
            }
        }
        return UNKNOWN_ENUM_VALUE;
    }
}
