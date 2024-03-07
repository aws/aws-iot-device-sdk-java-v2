/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.iot.iotjobs.model;

import java.util.List;
import software.amazon.awssdk.iot.Timestamp;
import software.amazon.awssdk.iot.iotjobs.model.JobExecutionSummary;

/**
 * Response payload to a GetPendingJobExecutions request.
 *
 */
public class GetPendingJobExecutionsResponse {

    /**
     * A list of JobExecutionSummary objects with status IN_PROGRESS.
     *
     */
    public List<software.amazon.awssdk.iot.iotjobs.model.JobExecutionSummary> inProgressJobs;


    /**
     * A list of JobExecutionSummary objects with status QUEUED.
     *
     */
    public List<software.amazon.awssdk.iot.iotjobs.model.JobExecutionSummary> queuedJobs;


    /**
     * The time when the message was sent.
     *
     */
    public Timestamp timestamp;


    /**
     * A client token used to correlate requests and responses.
     *
     */
    public String clientToken;


}
