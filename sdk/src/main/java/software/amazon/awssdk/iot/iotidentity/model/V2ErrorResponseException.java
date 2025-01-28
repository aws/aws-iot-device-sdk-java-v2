/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.iot.iotidentity.model;

import software.amazon.awssdk.crt.CrtRuntimeException;

public class V2ErrorResponseException extends CrtRuntimeException {
    private final V2ErrorResponse modeledError;

    public V2ErrorResponseException(String msg, V2ErrorResponse modeledError) {
        super(msg);
        this.modeledError = modeledError;
    }

    public V2ErrorResponse getModeledError() {
        return this.modeledError;
    }
}
