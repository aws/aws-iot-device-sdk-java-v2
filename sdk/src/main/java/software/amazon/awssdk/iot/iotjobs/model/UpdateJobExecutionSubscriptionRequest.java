/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.iot.iotjobs.model;


/**
 * Data needed to subscribe to UpdateJobExecution responses.
 *
 */
public class UpdateJobExecutionSubscriptionRequest {

    /**
     * Name of the IoT Thing that you want to subscribe to UpdateJobExecution response events for.
     *
     */
    public String thingName;


    /**
     * Job ID that you want to subscribe to UpdateJobExecution response events for.
     *
     */
    public String jobId;


}
