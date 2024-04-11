/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.iot.iotshadow.model;

import software.amazon.awssdk.iot.iotshadow.model.ShadowState;

/**
 * Data needed to make an UpdateNamedShadow request.
 *
 */
public class UpdateNamedShadowRequest {

    /**
     * Aws IoT thing to update a named shadow of.
     *
     */
    public String thingName;


    /**
     * Name of the shadow to update.
     *
     */
    public String shadowName;


    /**
     * Optional. A client token used to correlate requests and responses. Enter an arbitrary value here and it is reflected in the response.
     *
     */
    public String clientToken;


    /**
     * Requested changes to shadow state.  Updates affect only the fields specified.
     *
     */
    public ShadowState state;


    /**
     * (Optional) The Device Shadow service applies the update only if the specified version matches the latest version.
     *
     */
    public Integer version;


}
