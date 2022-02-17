/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.iot.iotshadow.model;

import java.util.HashMap;

/**
 * Contains the last-updated timestamps for each attribute in the desired and reported sections of the shadow state.
 *
 */
public class ShadowMetadata {

    /**
     * Contains the timestamps for each attribute in the desired section of a shadow's state.
     *
     */
    public HashMap<String, Object> desired;


    /**
     * Contains the timestamps for each attribute in the reported section of a shadow's state.
     *
     */
    public HashMap<String, Object> reported;


}
