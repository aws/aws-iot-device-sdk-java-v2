/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.iot.iotshadow.model;


/**
 * Data needed to subscribe to a device's NamedShadowDelta events.
 *
 */
public class NamedShadowDeltaUpdatedSubscriptionRequest {

    /**
     * Name of the AWS IoT thing to get NamedShadowDelta events for.
     *
     */
    public String thingName;


    /**
     * Name of the shadow to get ShadowDelta events for.
     *
     */
    public String shadowName;


}
