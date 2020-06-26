/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.iot.iotjobs.model;

import java.util.HashMap;
import software.amazon.awssdk.iot.Timestamp;
import software.amazon.awssdk.iot.iotjobs.model.JobExecutionState;

public class UpdateJobExecutionResponse {
    public String clientToken;
    public Timestamp timestamp;
    public HashMap<String, Object> jobDocument;
    public JobExecutionState executionState;
}
