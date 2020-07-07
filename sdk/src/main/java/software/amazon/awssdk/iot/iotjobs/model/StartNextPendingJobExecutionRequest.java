/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.iot.iotjobs.model;

import java.util.HashMap;

public class StartNextPendingJobExecutionRequest {
    public String thingName;
    public Long stepTimeoutInMinutes;
    public String clientToken;
    public HashMap<String, String> statusDetails;
}
