/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.iot.iotjobs.model;

import java.util.HashMap;

/**
 * Data needed to make a StartNextPendingJobExecution request.
 *
 */
public class StartNextPendingJobExecutionRequest {

    /**
     * IoT Thing the request is relative to.
     *
     */
    public String thingName;


    /**
     * Optional. A client token used to correlate requests and responses. Enter an arbitrary value here and it is reflected in the response.
     *
     */
    public String clientToken;


    /**
     * Specifies the amount of time this device has to finish execution of this job.
     *
     */
    public Long stepTimeoutInMinutes;


    /**
     * A collection of name-value pairs that describe the status of the job execution. If not specified, the statusDetails are unchanged.
     *
     */
    public HashMap<String, String> statusDetails;


}
