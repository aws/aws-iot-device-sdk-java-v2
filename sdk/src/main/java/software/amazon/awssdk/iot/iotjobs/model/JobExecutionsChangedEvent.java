/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.iot.iotjobs.model;

import java.util.HashMap;
import software.amazon.awssdk.iot.Timestamp;

/**
 * Sent whenever a job execution is added to or removed from the list of pending job executions for a thing.
 *
 */
public class JobExecutionsChangedEvent {

    /**
     * Map from JobStatus to a list of Jobs transitioning to that status.
     *
     */
    public HashMap<software.amazon.awssdk.iot.iotjobs.model.JobStatus, java.util.List<software.amazon.awssdk.iot.iotjobs.model.JobExecutionSummary>> jobs;


    /**
     * The time when the message was sent.
     *
     */
    public Timestamp timestamp;


}
