/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.iot.iotcommands.model;

import software.amazon.awssdk.iot.iotcommands.model.DeviceType;

/**
 * Data needed to subscribe to CommandExecution events.
 *
 */
public class CommandExecutionsSubscriptionRequest {

    /**
     * The type of a target device. Determine if the device should subscribe for commands addressed to an IoT Thing or MQTT client.
     *
     */
    public DeviceType deviceType;


    /**
     * Depending on device type value, this field is either an IoT Thing name or a client ID.
     *
     */
    public String deviceId;


}
