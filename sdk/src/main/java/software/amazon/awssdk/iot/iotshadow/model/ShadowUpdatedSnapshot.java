/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.iot.iotshadow.model;

import software.amazon.awssdk.iot.iotshadow.model.ShadowMetadata;
import software.amazon.awssdk.iot.iotshadow.model.ShadowState;

/**
 * Complete state of the (classic) shadow of an AWS IoT Thing.
 *
 */
public class ShadowUpdatedSnapshot {

    /**
     * Current shadow state.
     *
     */
    public ShadowState state;


    /**
     * Contains the timestamps for each attribute in the desired and reported sections of the state.
     *
     */
    public ShadowMetadata metadata;


    /**
     * The current version of the document for the device's shadow.
     *
     */
    public Integer version;


}
