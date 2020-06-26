/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.iot.iotshadow.model;

import software.amazon.awssdk.iot.Timestamp;

public class ErrorResponse {
    public Timestamp timestamp;
    public String message;
    public String clientToken;
    public Integer code;
}
