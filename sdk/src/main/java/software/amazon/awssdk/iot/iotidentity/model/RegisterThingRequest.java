/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.iot.iotidentity.model;

import java.util.HashMap;

public class RegisterThingRequest {
    public String templateName;
    public HashMap<String, String> parameters;
    public String certificateOwnershipToken;
}
