/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.iot.iotshadow.model;

import java.util.HashMap;

public class ShadowStateWithDelta {
    public HashMap<String, Object> delta;
    public HashMap<String, Object> reported;
    public HashMap<String, Object> desired;
}
