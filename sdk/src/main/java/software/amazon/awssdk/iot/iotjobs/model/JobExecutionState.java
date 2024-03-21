/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.iot.iotjobs.model;

import java.util.HashMap;
import software.amazon.awssdk.iot.iotjobs.model.JobStatus;

/**
 * Data about the state of a job execution.
 *
 */
public class JobExecutionState {

    /**
     * The status of the job execution. Can be one of: QUEUED, IN_PROGRESS, FAILED, SUCCEEDED, CANCELED, TIMED_OUT, REJECTED, or REMOVED.
     *
     */
    public JobStatus status;


    /**
     * A collection of name-value pairs that describe the status of the job execution.
     *
     */
    public HashMap<String, String> statusDetails;


    /**
     * The version of the job execution. Job execution versions are incremented each time they are updated by a device.
     *
     */
    public Integer versionNumber;


}
