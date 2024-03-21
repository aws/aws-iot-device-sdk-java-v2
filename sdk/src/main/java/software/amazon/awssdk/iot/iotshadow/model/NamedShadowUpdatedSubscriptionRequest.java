/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.iot.iotshadow.model;


/**
 * Data needed to subscribe to a device's NamedShadowUpdated events.
 *
 */
public class NamedShadowUpdatedSubscriptionRequest {

    /**
     * Name of the AWS IoT thing to get NamedShadowUpdated events for.
     *
     */
    public String thingName;


    /**
     * Name of the shadow to get NamedShadowUpdated events for.
     *
     */
    public String shadowName;


}
