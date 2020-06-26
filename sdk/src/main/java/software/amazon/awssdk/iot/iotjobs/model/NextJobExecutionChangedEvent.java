/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.iot.iotjobs.model;

import software.amazon.awssdk.iot.Timestamp;
import software.amazon.awssdk.iot.iotjobs.model.JobExecutionData;

public class NextJobExecutionChangedEvent {
    public JobExecutionData execution;
    public Timestamp timestamp;
}
