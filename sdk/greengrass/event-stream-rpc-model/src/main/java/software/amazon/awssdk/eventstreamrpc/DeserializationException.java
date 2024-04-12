/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.eventstreamrpc;

/**
 * Thrown when a deserialization exception occurs
 */
public class DeserializationException extends RuntimeException {
    /**
     * Creates a new DeserializationException from the given data
     * @param lexicalData The data that could not be deserialized
     */
    public DeserializationException(Object lexicalData) {
        this(lexicalData, null);
    }

    /**
     * Creates a new DeserializationException from the given data
     * @param lexicalData The data that could not be deserialized
     * @param cause The reason the data could not be deserialized
     */
    public DeserializationException(Object lexicalData, Throwable cause) {
        super("Could not deserialize data: [" + lexicalData.toString() + "]", cause);
    }
}
