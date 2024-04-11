/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.iot.iotshadow.model;

import software.amazon.awssdk.iot.Timestamp;

/**
 * Response payload to a DeleteShadow request.
 *
 */
public class DeleteShadowResponse {

    /**
     * A client token used to correlate requests and responses.
     *
     */
    public String clientToken;


    /**
     * The time the response was generated by AWS IoT.
     *
     */
    public Timestamp timestamp;


    /**
     * The current version of the document for the device's shadow.
     *
     */
    public Integer version;


}
