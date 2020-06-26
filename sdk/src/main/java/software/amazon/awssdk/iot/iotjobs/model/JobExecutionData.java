/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.iot.iotjobs.model;

import java.util.HashMap;
import software.amazon.awssdk.iot.Timestamp;
import software.amazon.awssdk.iot.iotjobs.model.JobStatus;

public class JobExecutionData {
    public String jobId;
    public HashMap<String, Object> jobDocument;
    public JobStatus status;
    public Integer versionNumber;
    public Timestamp queuedAt;
    public String thingName;
    public Long executionNumber;
    public HashMap<String, String> statusDetails;
    public Timestamp lastUpdatedAt;
    public Timestamp startedAt;
}
