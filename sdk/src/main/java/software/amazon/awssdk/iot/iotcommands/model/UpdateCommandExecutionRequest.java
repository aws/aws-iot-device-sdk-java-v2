/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.iot.iotcommands.model;

import java.util.HashMap;
import software.amazon.awssdk.iot.iotcommands.model.CommandExecutionResult;
import software.amazon.awssdk.iot.iotcommands.model.CommandExecutionStatus;
import software.amazon.awssdk.iot.iotcommands.model.DeviceType;
import software.amazon.awssdk.iot.iotcommands.model.StatusReason;

/**
 * Data needed to make an UpdateCommandExecution request.
 *
 */
public class UpdateCommandExecutionRequest {

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


    /**
     * ID of the command execution that needs to be updated.
     *
     */
    public String executionId;


    /**
     * The status of the command execution.
     *
     */
    public CommandExecutionStatus status;


    /**
     * A reason for the updated status. Can provide additional information on failures. Should be used when status is one of the following: FAILED, REJECTED, TIMED_OUT.
     *
     */
    public StatusReason statusReason;


    /**
     * The result value for the current state of the command execution. The status provides information about the progress of the command execution. The device can use the result field to share additional details about the execution such as a return value of a remote function call.
     *
     */
    public HashMap<String, software.amazon.awssdk.iot.iotcommands.model.CommandExecutionResult> result;


}
