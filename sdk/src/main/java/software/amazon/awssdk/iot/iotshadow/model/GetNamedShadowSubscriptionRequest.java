/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.iot.iotshadow.model;


/**
 * Data needed to subscribe to GetNamedShadow responses.
 *
 */
public class GetNamedShadowSubscriptionRequest {

    /**
     * AWS IoT thing subscribe to GetNamedShadow responses for.
     *
     */
    public String thingName;


    /**
     * Name of the shadow to subscribe to GetNamedShadow responses for.
     *
     */
    public String shadowName;


}
