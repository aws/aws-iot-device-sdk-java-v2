/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.iot.iotjobs.model;


public class DescribeJobExecutionRequest {
    public Long executionNumber;
    public String thingName;
    public Boolean includeJobDocument;
    public String jobId;
    public String clientToken;
}
