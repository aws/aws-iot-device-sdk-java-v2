/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.iot.iotjobs.model;

import java.util.HashMap;
import software.amazon.awssdk.iot.Timestamp;
import software.amazon.awssdk.iot.iotjobs.model.JobExecutionState;

/**
 * Response payload to an UpdateJobExecution request.
 *
 */
public class UpdateJobExecutionResponse {

    /**
     * A client token used to correlate requests and responses.
     *
     */
    public String clientToken;


    /**
     * Contains data about the state of a job execution.
     *
     */
    public JobExecutionState executionState;


    /**
     * A UTF-8 encoded JSON document that contains information that your devices need to perform the job.
     *
     */
    public HashMap<String, Object> jobDocument;


    /**
     * The time when the message was sent.
     *
     */
    public Timestamp timestamp;


}
