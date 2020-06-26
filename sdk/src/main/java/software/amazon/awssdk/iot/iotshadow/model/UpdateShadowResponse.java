/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.iot.iotshadow.model;

import software.amazon.awssdk.iot.Timestamp;
import software.amazon.awssdk.iot.iotshadow.model.ShadowMetadata;
import software.amazon.awssdk.iot.iotshadow.model.ShadowState;

public class UpdateShadowResponse {
    public ShadowState state;
    public String clientToken;
    public Integer version;
    public ShadowMetadata metadata;
    public Timestamp timestamp;
}
