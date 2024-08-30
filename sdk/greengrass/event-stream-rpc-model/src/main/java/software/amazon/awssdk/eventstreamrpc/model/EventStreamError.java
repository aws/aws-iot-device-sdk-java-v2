/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

/* This file is part of greengrass-ipc project. */

package software.amazon.awssdk.eventstreamrpc.model;

import com.google.gson.JsonSyntaxException;
import software.amazon.awssdk.crt.eventstream.Header;
import software.amazon.awssdk.crt.eventstream.MessageType;
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCServiceModel;

import java.util.HashMap;
import java.util.List;

/**
 * Used to hold event stream RPC error messages that are not tied to any service.
 * Message info comes back with a payload of JSON like:
 *
 * { "message": "..." }
 *
 * And we map that to this exception type to convey the information
 */
public class EventStreamError
        extends RuntimeException {

    private final List<Header> headers;
    private final MessageType messageType;

    /**
     * Creates a new EventStreamError
     * @param headers currently unusued, but likely a useful element for output
     * @param payload The payload to associated with the EventStreamError
     * @param messageType The message type to associate with the EventStreamError
     * @return A new EventStreamError
     */
    public static EventStreamError create(final List<Header> headers, final byte[] payload, final MessageType messageType) {
        try {
            final HashMap<String, Object> map = EventStreamRPCServiceModel.getStaticGson().fromJson(new String(payload), HashMap.class);
            final String message = map.getOrDefault("message", "no message").toString();
            return new EventStreamError(String.format("%s: %s", messageType.name(), message), headers, messageType);
        } catch (JsonSyntaxException jse) {
            return new EventStreamError(String.format("%s: Failed to deserialize error message as JSON(%s)", messageType.name(), jse.toString()), headers, messageType);
        }
    }

    /**
     * Creates a new EventStream error with only a message
     * @param message The message to associate with the EventStreamError
     */
    public EventStreamError(String message) {
        super(message);
        this.messageType = null;
        this.headers = null;
    }

    /**
     * Creates a new EventStreamError with a message, headers, and message type
     * @param message The message to associate with the EventStreamError
     * @param headers Headers to associate with the EventStreamError
     * @param messageType The message type to associate with the EventStreamError
     */
    public EventStreamError(String message, List<Header> headers, MessageType messageType) {
        super(message);
        this.messageType = messageType;
        this.headers = headers;
    }

    /**
     * Returns the headers associated with the EventStreamError
     * @return the headers associated with the EventStreamError
     */
    public List<Header> getMessageHeaders() {
        return headers;
    }

    /**
     * Returns the message type associated with the EventStreamError
     * @return the message type associated with the EventStreamError
     */
    public MessageType getMessageType() {
        return messageType;
    }
}
