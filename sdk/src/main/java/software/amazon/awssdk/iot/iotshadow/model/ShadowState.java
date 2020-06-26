/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.iot.iotshadow.model;

import java.util.HashMap;

public class ShadowState {
    public HashMap<String, Object> desired;
    public HashMap<String, Object> reported;
}
