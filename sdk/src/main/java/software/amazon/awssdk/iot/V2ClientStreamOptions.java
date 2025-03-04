/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.iot;

import software.amazon.awssdk.crt.iot.SubscriptionStatusEvent;

import java.util.function.Consumer;

/**
 * Configuration options for streaming operations created from the V2 service clients
 *
 * @param <T> Type that the stream deserializes MQTT messages into
 */
public class V2ClientStreamOptions<T> {

    private Consumer<T> streamEventHandler;
    private Consumer<SubscriptionStatusEvent> subscriptionEventHandler;
    private Consumer<V2DeserializationFailureEvent> deserializationFailureHandler;

    /**
     * Builder type for V2ClientStreamOptions instances
     *
     * @param <T> Type that the stream deserializes MQTT messages into
     */
    public static class V2ClientStreamOptionsBuilder<T> {
        private V2ClientStreamOptions<T> options = new V2ClientStreamOptions<T>();

        private V2ClientStreamOptionsBuilder() {}

        /**
         * Sets the callback the stream should invoke on a successfully deserialized message
         *
         * @param streamEventHandler the callback the stream should invoke on a successfully deserialized message
         * @return this builder object
         */
        public V2ClientStreamOptionsBuilder<T> withStreamEventHandler(Consumer<T> streamEventHandler) {
            options.streamEventHandler = streamEventHandler;

            return this;
        }

        /**
         * Sets the callback the stream should invoke when a message fails to deserialize
         *
         * @param deserializationFailureHandler the callback the stream should invoke when a message fails to deserialize
         * @return this builder object
         */
        public V2ClientStreamOptionsBuilder<T> withDeserializationFailureHandler(Consumer<V2DeserializationFailureEvent> deserializationFailureHandler) {
            options.deserializationFailureHandler = deserializationFailureHandler;

            return this;
        }

        /**
         * Sets the callback the stream should invoke when something changes about the underlying subscription
         *
         * @param subscriptionEventHandler the callback the stream should invoke when something changes about the underlying subscription
         * @return this builder object
         */
        public V2ClientStreamOptionsBuilder<T> withSubscriptionEventHandler(Consumer<SubscriptionStatusEvent> subscriptionEventHandler) {
            options.subscriptionEventHandler = subscriptionEventHandler;

            return this;
        }

        /**
         * Creates a new V2ClientStreamOptions instance from the existing configuration.
         *
         * @return a new V2ClientStreamOptions instance
         */
        public V2ClientStreamOptions<T> build() {
            return new V2ClientStreamOptions<T>(options);
        }
    }

    private V2ClientStreamOptions() {
    }

    private V2ClientStreamOptions(V2ClientStreamOptions<T> options) {
        if (options.streamEventHandler != null) {
            this.streamEventHandler = options.streamEventHandler;
        } else {
            this.streamEventHandler = (event) -> {};
        }

        if (options.subscriptionEventHandler != null) {
            this.subscriptionEventHandler = options.subscriptionEventHandler;
        } else {
            this.subscriptionEventHandler = (event) -> {};
        }

        if (options.deserializationFailureHandler != null) {
            this.deserializationFailureHandler = options.deserializationFailureHandler;
        } else {
            this.deserializationFailureHandler = (failure) -> {};
        }
    }

    /**
     * Creates a new builder object for V2ClientStreamOptions instances
     *
     * @return a new builder object for V2ClientStreamOptions instances
     * @param <T> Type that the stream deserializes MQTT messages into
     */
    public static <T> V2ClientStreamOptionsBuilder<T> builder() {
        return new V2ClientStreamOptionsBuilder<T>();
    }

    /**
     * @return the callback the stream should invoke on a successfully deserialized message
     */
    public Consumer<T> streamEventHandler() {
        return this.streamEventHandler;
    }

    /**
     * @return the callback the stream should invoke when a message fails to deserialize
     */
    public Consumer<SubscriptionStatusEvent> subscriptionEventHandler() {
        return this.subscriptionEventHandler;
    }

    /**
     * @return the callback the stream should invoke when something changes about the underlying subscription
     */
    public Consumer<V2DeserializationFailureEvent> deserializationFailureHandler() {
        return this.deserializationFailureHandler;
    }
}
