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
 * Data needed to make an UpdateJobExecution request.
 *
 */
public class UpdateJobExecutionRequest {

    /**
     * The name of the thing associated with the device. 
     *
     */
    public String thingName;


    /**
     * The unique identifier assigned to this job when it was created.
     *
     */
    public String jobId;


    /**
     * The new status for the job execution (IN_PROGRESS, FAILED, SUCCEEDED, or REJECTED). This must be specified on every update.
     *
     */
    public JobStatus status;


    /**
     * A client token used to correlate requests and responses. Enter an arbitrary value here and it is reflected in the response.
     *
     */
    public String clientToken;


    /**
     * A collection of name-value pairs that describe the status of the job execution. If not specified, the statusDetails are unchanged.
     *
     */
    public HashMap<String, String> statusDetails;


    /**
     * The expected current version of the job execution. Each time you update the job execution, its version is incremented. If the version of the job execution stored in the AWS IoT Jobs service does not match, the update is rejected with a VersionMismatch error, and an ErrorResponse that contains the current job execution status data is returned.
     *
     */
    public Integer expectedVersion;


    /**
     * Optional. A number that identifies a job execution on a device. If not specified, the latest job execution is used.
     *
     */
    public Long executionNumber;


    /**
     * Optional. When included and set to true, the response contains the JobExecutionState field. The default is false.
     *
     */
    public Boolean includeJobExecutionState;


    /**
     * Optional. When included and set to true, the response contains the JobDocument. The default is false.
     *
     */
    public Boolean includeJobDocument;


    /**
     * Specifies the amount of time this device has to finish execution of this job. If the job execution status is not set to a terminal state before this timer expires, or before the timer is reset (by again calling UpdateJobExecution, setting the status to IN_PROGRESS and specifying a new timeout value in this field) the job execution status is set to TIMED_OUT. Setting or resetting this timeout has no effect on the job execution timeout that might have been specified when the job was created (by using CreateJob with the timeoutConfig).
     *
     */
    public Long stepTimeoutInMinutes;


}
