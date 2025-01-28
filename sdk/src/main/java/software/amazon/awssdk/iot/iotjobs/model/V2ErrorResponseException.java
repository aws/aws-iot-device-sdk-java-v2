/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.iot.iotjobs.model;

import software.amazon.awssdk.crt.CrtRuntimeException;

/**
 * An exception that can wrap a specific modeled service error (V2ErrorResponse) as optional,
 * auxiliary data.
 */
public class V2ErrorResponseException extends CrtRuntimeException {
    private final V2ErrorResponse modeledError;

    /**
     * Constructor
     */
    public V2ErrorResponseException(String msg, V2ErrorResponse modeledError) {
        super(msg);
        this.modeledError = modeledError;
    }

    /**
     * Gets the modeled error, if any, associated with this exception.
     */
    public V2ErrorResponse getModeledError() {
        return this.modeledError;
    }
}
