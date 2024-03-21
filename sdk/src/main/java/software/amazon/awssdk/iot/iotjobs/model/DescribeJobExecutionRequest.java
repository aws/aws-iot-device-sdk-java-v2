/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.iot.iotjobs.model;


/**
 * Data needed to make a DescribeJobExecution request.
 *
 */
public class DescribeJobExecutionRequest {

    /**
     * The name of the thing associated with the device.
     *
     */
    public String thingName;


    /**
     * The unique identifier assigned to this job when it was created. Or use $next to return the next pending job execution for a thing (status IN_PROGRESS or QUEUED). In this case, any job executions with status IN_PROGRESS are returned first. Job executions are returned in the order in which they were created.
     *
     */
    public String jobId;


    /**
     * An opaque string used to correlate requests and responses. Enter an arbitrary value here and it is reflected in the response.
     *
     */
    public String clientToken;


    /**
     * Optional. A number that identifies a job execution on a device. If not specified, the latest job execution is returned.
     *
     */
    public Long executionNumber;


    /**
     * Optional. Unless set to false, the response contains the job document. The default is true.
     *
     */
    public Boolean includeJobDocument;


}
