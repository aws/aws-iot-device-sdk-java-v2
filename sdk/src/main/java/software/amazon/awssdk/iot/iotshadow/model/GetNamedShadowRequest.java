/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.iot.iotshadow.model;


public class GetNamedShadowRequest {
    public String clientToken;
    public String shadowName;
    public String thingName;
}
