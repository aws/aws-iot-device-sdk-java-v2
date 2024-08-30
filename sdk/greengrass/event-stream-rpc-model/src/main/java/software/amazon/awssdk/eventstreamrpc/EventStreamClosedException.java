/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

/* This file is part of greengrass-ipc project. */

package software.amazon.awssdk.eventstreamrpc;

/**
 * Thrown when a EventStream closed exception occurs
 */
public class EventStreamClosedException extends RuntimeException {
    /**
     * Creates a new EventStreamClosedException from the given continuation ID
     * @param continauationId The continuation ID that caused the exception
     */
    public EventStreamClosedException(long continauationId) {
        //TODO: Is hex formatting here useful? It is short, but not consistent anywhere else yet
        super(String.format("EventStream continuation [%s] is already closed!", Long.toHexString(continauationId)));
    }

    /**
     * Creates a new EventStreamClosedException with a given messasge
     * @param msg The message to associated with the EventStreamClosedException
     */
    public EventStreamClosedException(String msg) {
        //TODO: Is hex formatting here useful? It is short, but not consistent anywhere else yet
        super(msg);
    }
}
