/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.iot.iotcommands.model;


/**
 * Sent whenever a command execution is added for a thing or a client.
 *
 */
public class CommandExecutionEvent {

    /**
     * Opaque data containing instructions sent from the IoT commands service.
     *
     */
    public byte[] payload;


    /**
     * Unique ID for the specific execution of a command. A command can have multiple executions, each with a unique ID.
     *
     */
    public String executionId;


    /**
     * Data format of the payload. It is supposed to be a MIME type (IANA media type), but can be an arbitrary string.
     *
     */
    public String contentType;


    /**
     * Number of seconds before the IoT commands service decides that this command execution is timed out.
     *
     */
    public Integer timeout;


}
