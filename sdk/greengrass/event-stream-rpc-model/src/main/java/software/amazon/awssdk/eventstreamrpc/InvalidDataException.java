/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.eventstreamrpc;

import software.amazon.awssdk.crt.eventstream.MessageType;

/**
 * An exception for invalid/unexpected data
 */
public class InvalidDataException extends RuntimeException {
    /**
     * Constructs a new InvalidDataException with the given MessageType, whose name will
     * be added to the exception.
     * @param unexpectedType The MessageType that caused the exception
     */
    public InvalidDataException(MessageType unexpectedType) {
        super(String.format("Unexpected message type received: %s", unexpectedType.name()));
    }
}
