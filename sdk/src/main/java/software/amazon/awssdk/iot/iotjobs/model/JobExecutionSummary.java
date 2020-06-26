/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.iot.iotjobs.model;

import software.amazon.awssdk.iot.Timestamp;

public class JobExecutionSummary {
    public Timestamp lastUpdatedAt;
    public Long executionNumber;
    public Timestamp startedAt;
    public Integer versionNumber;
    public String jobId;
    public Timestamp queuedAt;
}
