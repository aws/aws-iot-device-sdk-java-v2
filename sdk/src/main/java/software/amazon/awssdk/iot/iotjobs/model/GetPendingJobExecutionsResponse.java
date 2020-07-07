/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.iot.iotjobs.model;

import java.util.List;
import software.amazon.awssdk.iot.Timestamp;
import software.amazon.awssdk.iot.iotjobs.model.JobExecutionSummary;

public class GetPendingJobExecutionsResponse {
    public List<software.amazon.awssdk.iot.iotjobs.model.JobExecutionSummary> queuedJobs;
    public Timestamp timestamp;
    public String clientToken;
    public List<software.amazon.awssdk.iot.iotjobs.model.JobExecutionSummary> inProgressJobs;
}
