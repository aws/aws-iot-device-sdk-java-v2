/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.iot.iotjobs.model;

import java.util.HashMap;
import software.amazon.awssdk.iot.Timestamp;
import software.amazon.awssdk.iot.iotjobs.model.JobStatus;

/**
 * Data about a job execution.
 *
 */
public class JobExecutionData {

    /**
     * The unique identifier you assigned to this job when it was created.
     *
     */
    public String jobId;


    /**
     * The name of the thing that is executing the job.
     *
     */
    public String thingName;


    /**
     * The content of the job document.
     *
     */
    public HashMap<String, Object> jobDocument;


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
     * The time when the job execution was enqueued.
     *
     */
    public Timestamp queuedAt;


    /**
     * The time when the job execution started.
     *
     */
    public Timestamp startedAt;


    /**
     * The time when the job execution started. 
     *
     */
    public Timestamp lastUpdatedAt;


    /**
     * The version of the job execution. Job execution versions are incremented each time they are updated by a device.
     *
     */
    public Integer versionNumber;


    /**
     * A number that identifies a job execution on a device. It can be used later in commands that return or update job execution information.
     *
     */
    public Long executionNumber;


}
