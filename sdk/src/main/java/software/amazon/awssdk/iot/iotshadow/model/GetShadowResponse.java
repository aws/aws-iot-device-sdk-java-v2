/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.iot.iotshadow.model;

import software.amazon.awssdk.iot.Timestamp;
import software.amazon.awssdk.iot.iotshadow.model.ShadowMetadata;
import software.amazon.awssdk.iot.iotshadow.model.ShadowStateWithDelta;

public class GetShadowResponse {
    public Integer version;
    public String clientToken;
    public ShadowStateWithDelta state;
    public ShadowMetadata metadata;
    public Timestamp timestamp;
}
