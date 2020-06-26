/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.iot.iotjobs.model;

import java.util.HashMap;
import software.amazon.awssdk.iot.iotjobs.model.JobStatus;

public class JobExecutionState {
    public HashMap<String, String> statusDetails;
    public Integer versionNumber;
    public JobStatus status;
}
