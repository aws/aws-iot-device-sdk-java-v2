/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.iot.iotshadow.model;


/**
 * Data needed to make a DeleteShadow request.
 *
 */
public class DeleteShadowRequest {

    /**
     * AWS IoT thing to delete the (classic) shadow of.
     *
     */
    public String thingName;


    /**
     * Optional. A client token used to correlate requests and responses. Enter an arbitrary value here and it is reflected in the response.
     *
     */
    public String clientToken;


}
