/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.iot.iotshadow.model;


/**
 * Data needed to subscribe to DeleteNamedShadow responses for an AWS IoT thing.
 *
 */
public class DeleteNamedShadowSubscriptionRequest {

    /**
     * AWS IoT thing to subscribe to DeleteNamedShadow operations for.
     *
     */
    public String thingName;


    /**
     * Name of the shadow to subscribe to DeleteNamedShadow operations for.
     *
     */
    public String shadowName;


}
