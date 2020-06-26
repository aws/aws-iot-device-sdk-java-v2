/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.iot.iotshadow.model;

import java.util.HashMap;
import software.amazon.awssdk.iot.Timestamp;

public class ShadowDeltaUpdatedEvent {
    public Integer version;
    public Timestamp timestamp;
    public HashMap<String, Object> metadata;
    public HashMap<String, Object> state;
}
