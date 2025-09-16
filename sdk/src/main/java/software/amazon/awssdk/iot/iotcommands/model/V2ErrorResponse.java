/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.iot.iotcommands.model;

import software.amazon.awssdk.iot.iotcommands.model.RejectedErrorCode;

/**
 * Response document containing details about a failed request.
 *
 */
public class V2ErrorResponse {

    /**
     * Indicates the type of error.
     *
     */
    public RejectedErrorCode error;


    /**
     * A text message that provides additional information.
     *
     */
    public String errorMessage;


    /**
     * Execution ID for which error is set.
     *
     */
    public String executionId;


}
