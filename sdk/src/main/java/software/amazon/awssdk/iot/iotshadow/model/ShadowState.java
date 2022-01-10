/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.iot.iotshadow.model;

import java.util.HashMap;

/**
 * (Potentially partial) state of an AWS IoT thing's shadow.
 *
 */
public class ShadowState {

    /**
     * The desired shadow state (from external services and devices).
     *
     */
    public HashMap<String, Object> desired;

    /**
     * The (last) reported shadow state from the device.
     *
     */
    public HashMap<String, Object> reported;

}
