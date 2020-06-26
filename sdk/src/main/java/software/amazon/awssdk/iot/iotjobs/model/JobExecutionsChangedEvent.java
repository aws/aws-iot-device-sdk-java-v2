/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.iot.iotjobs.model;

import java.util.HashMap;
import software.amazon.awssdk.iot.Timestamp;

public class JobExecutionsChangedEvent {
    public HashMap<software.amazon.awssdk.iot.iotjobs.model.JobStatus, java.util.List<software.amazon.awssdk.iot.iotjobs.model.JobExecutionSummary>> jobs;
    public Timestamp timestamp;
}
