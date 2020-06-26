/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.iot.iotshadow.model;

import software.amazon.awssdk.iot.Timestamp;
import software.amazon.awssdk.iot.iotshadow.model.ShadowUpdatedSnapshot;

public class ShadowUpdatedEvent {
    public ShadowUpdatedSnapshot previous;
    public ShadowUpdatedSnapshot current;
    public Timestamp timestamp;
}
