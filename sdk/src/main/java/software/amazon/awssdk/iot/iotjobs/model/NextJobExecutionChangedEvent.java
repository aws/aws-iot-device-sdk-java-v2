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
 * Sent whenever there is a change to which job execution is next on the list of pending job executions for a thing, as defined for DescribeJobExecution with jobId $next. This message is not sent when the next job's execution details change, only when the next job that would be returned by DescribeJobExecution with jobId $next has changed.
 *
 */
public class NextJobExecutionChangedEvent {

    /**
     * Contains data about a job execution.
     *
     */
    public JobExecutionData execution;


    /**
     * The time when the message was sent.
     *
     */
    public Timestamp timestamp;


}
