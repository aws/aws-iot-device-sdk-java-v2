/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.iot.iotjobs.model;

import java.util.HashMap;
import software.amazon.awssdk.iot.iotjobs.model.JobStatus;

public class UpdateJobExecutionRequest {
    public String thingName;
    public Long executionNumber;
    public HashMap<String, String> statusDetails;
    public Boolean includeJobExecutionState;
    public String jobId;
    public Integer expectedVersion;
    public Boolean includeJobDocument;
    public JobStatus status;
    public Long stepTimeoutInMinutes;
    public String clientToken;
}
