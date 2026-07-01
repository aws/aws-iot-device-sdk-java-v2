/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.iot.iotcommands.model;

/**
 * Possible device types for command executions.
 *
 */
public enum DeviceType {

    /**
     * Enum value is an unknown value
     */
    UNKNOWN_ENUM_VALUE("UNKNOWN_ENUM_VALUE"),

    /**
     * A target for the commands is an IoT Thing.
     */
    THING("things"),

    /**
     * A target for the commands is an MQTT client ID.
     */
    CLIENT("clients");

    private String value;

    private DeviceType(String value) {
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
    static DeviceType fromString(String val) {
        for (DeviceType e : DeviceType.class.getEnumConstants()) {
            if (e.toString().compareTo(val) == 0) {
                return e;
            }
        }
        return UNKNOWN_ENUM_VALUE;
    }
}
