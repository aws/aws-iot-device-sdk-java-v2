/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.iot.iotshadow.model;

import software.amazon.awssdk.iot.iotshadow.model.ShadowState;

public class UpdateNamedShadowRequest {
    public String shadowName;
    public String clientToken;
    public String thingName;
    public ShadowState state;
    public Integer version;
}
