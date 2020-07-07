/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.iot.iotjobs.model;

import software.amazon.awssdk.iot.Timestamp;
import software.amazon.awssdk.iot.iotjobs.model.JobExecutionState;
import software.amazon.awssdk.iot.iotjobs.model.RejectedErrorCode;

public class RejectedError {
    public Timestamp timestamp;
    public RejectedErrorCode code;
    public String message;
    public String clientToken;
    public JobExecutionState executionState;
}
