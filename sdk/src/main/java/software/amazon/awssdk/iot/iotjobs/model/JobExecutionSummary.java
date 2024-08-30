/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.iot.iotjobs.model;

import software.amazon.awssdk.iot.Timestamp;

/**
 * Contains a subset of information about a job execution.
 *
 */
public class JobExecutionSummary {

    /**
     * The unique identifier you assigned to this job when it was created.
     *
     */
    public String jobId;


    /**
     * A number that identifies a job execution on a device.
     *
     */
    public Long executionNumber;


    /**
     * The version of the job execution. Job execution versions are incremented each time the AWS IoT Jobs service receives an update from a device.
     *
     */
    public Integer versionNumber;


    /**
     * The time when the job execution was last updated.
     *
     */
    public Timestamp lastUpdatedAt;


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


}
