/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.iot.iotshadow.model;


/**
 * Data needed to subscribe to UpdateNamedShadow responses.
 *
 */
public class UpdateNamedShadowSubscriptionRequest {

    /**
     * Name of the AWS IoT thing to listen to UpdateNamedShadow responses for.
     *
     */
    public String thingName;


    /**
     * Name of the shadow to listen to UpdateNamedShadow responses for.
     *
     */
    public String shadowName;


}
