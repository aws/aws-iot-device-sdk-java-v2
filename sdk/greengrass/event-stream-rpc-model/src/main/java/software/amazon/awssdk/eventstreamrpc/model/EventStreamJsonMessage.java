/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.eventstreamrpc.model;

import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;

/**
 * All generated model types implement this interface, including errors.
 */
public interface EventStreamJsonMessage {
    /**
     * Serialize this object into a JSON payload. Does not validate object being serialized
     *
     * WARNING: implementers should not override this method. This could be an abstract class
     * with final implementations for serialization/deserialization. Or better yet, rework
     * how it works
     *
     * @param gson The GSON to convert to a JSON payload
     * @return The GSON converted to a JSON payload
     */
    default byte[] toPayload(final Gson gson) {
        final String payloadString = gson.toJson(this);
        if (payloadString == null || payloadString.isEmpty() || payloadString.equals("null")) {
            return "{}".getBytes(StandardCharsets.UTF_8);
        }
        return payloadString.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Converts the given GSON and payload into a EventStreamJsonMessage
     * @param gson The GSON to convert
     * @param payload The payload to convert
     * @return A EventStreamJsonMessage
     */
    default EventStreamJsonMessage fromJson(final Gson gson, byte[] payload) {
        final String payloadString = new String(payload, StandardCharsets.UTF_8);
        if (payloadString.equals("null")) {
            return gson.fromJson("{}", this.getClass());
        }
        return gson.fromJson(payloadString, this.getClass());
    }

    /**
     * If anything needs to be done in memory after parsing from JSON, override and perform it here
     */
    default void postFromJson() { }

    /**
     * Returns the named model type. May be used for a header.
     * @return the named model type
     */
    public String getApplicationModelType();

    /**
     * Returns whether the EventStreamJsonMessage is void
     * @return True if the EventStreamJsonMessage is void
     */
    default boolean isVoid() { return false; }
}
