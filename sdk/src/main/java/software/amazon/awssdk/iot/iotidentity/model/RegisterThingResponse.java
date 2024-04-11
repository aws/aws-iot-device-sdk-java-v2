/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.iot.iotidentity.model;

import java.util.HashMap;

/**
 * Response payload to a RegisterThing request.
 *
 */
public class RegisterThingResponse {

    /**
     * The device configuration defined in the template.
     *
     */
    public HashMap<String, String> deviceConfiguration;


    /**
     * The name of the IoT thing created during provisioning.
     *
     */
    public String thingName;


}
