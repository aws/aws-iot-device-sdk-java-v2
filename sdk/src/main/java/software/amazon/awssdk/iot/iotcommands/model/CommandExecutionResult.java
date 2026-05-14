/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.iot.iotcommands.model;


/**
 * The result value of the command execution. The device can use the result field to share additional details about the execution such as a return value of a remote function call.
 *
 */
public class CommandExecutionResult {

    /**
     * An attribute of type String.
     *
     */
    public String s;


    /**
     * An attribute of type Boolean.
     *
     */
    public Boolean b;


    /**
     * An attribute of type Binary.
     *
     */
    public byte[] bin;


}
