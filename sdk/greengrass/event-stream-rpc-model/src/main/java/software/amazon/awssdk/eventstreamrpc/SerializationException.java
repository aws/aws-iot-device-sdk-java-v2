/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.eventstreamrpc;

/**
 * Thrown when a serialization exception occurs
 */
public class SerializationException extends RuntimeException {
    /**
     * Creates a new serlization exception
     * @param object The object that caused the serlization exception
     */
    public SerializationException(Object object) {
        this(object, null);
    }

    /**
     * Creates a new serlization exception
     * @param object The object that caused the serlization exception
     * @param cause The cause of the serlization exception
     */
    public SerializationException(Object object, Throwable cause) {
        super("Could not serialize object: " + object.toString(), cause);
    }
}
