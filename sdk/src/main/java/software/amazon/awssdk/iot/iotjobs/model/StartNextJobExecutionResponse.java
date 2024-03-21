/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.iot.iotjobs.model;

import software.amazon.awssdk.iot.Timestamp;
import software.amazon.awssdk.iot.iotjobs.model.JobExecutionData;

/**
 * Response payload to a StartNextJobExecution request.
 *
 */
public class StartNextJobExecutionResponse {

    /**
     * A client token used to correlate requests and responses.
     *
     */
    public String clientToken;


    /**
     * Contains data about a job execution.
     *
     */
    public JobExecutionData execution;


    /**
     * The time when the message was sent to the device. 
     *
     */
    public Timestamp timestamp;


}
