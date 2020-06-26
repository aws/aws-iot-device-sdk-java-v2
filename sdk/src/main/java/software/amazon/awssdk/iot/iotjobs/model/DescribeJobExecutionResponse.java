/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.iot.iotjobs.model;

import software.amazon.awssdk.iot.Timestamp;
import software.amazon.awssdk.iot.iotjobs.model.JobExecutionData;

public class DescribeJobExecutionResponse {
    public JobExecutionData execution;
    public String clientToken;
    public Timestamp timestamp;
}
