/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.iot;

/**
 * An event emitted by a streaming operation when an incoming messages fails to deserialize
 */
public class V2DeserializationFailureEvent {
    private Throwable cause;
    private byte[] payload;
    private String topic;

    /**
     * Builder class for V2DeserializationFailureEvent instances
     */
    public static class V2DeserializationFailureEventBuilder {
        private final V2DeserializationFailureEvent event = new V2DeserializationFailureEvent();

        private V2DeserializationFailureEventBuilder() {}

        /**
         * Sets the exception that triggered the failure
         *
         * @param cause the exception that triggered the failure
         * @return this builder instance
         */
        public V2DeserializationFailureEventBuilder withCause(Throwable cause) {
            this.event.cause = cause;

            return this;
        }

        /**
         * Sets the payload of the message that triggered the failure
         *
         * @param payload the payload of the message that triggered the failure
         * @return this builder instance
         */
        public V2DeserializationFailureEventBuilder withPayload(byte[] payload) {
            this.event.payload = payload;

            return this;
        }

        /**
         * Sets the topic of the message that triggered the failure
         *
         * @param topic the topic of the message that triggered the failure
         * @return this builder instance
         */
        public V2DeserializationFailureEventBuilder withTopic(String topic) {
            this.event.topic = topic;

            return this;
        }



        /**
         * Creates a new V2DeserializationFailureEvent instance from the existing configuration
         *
         * @return a new V2DeserializationFailureEvent instance
         */
        public V2DeserializationFailureEvent build() {
            return new V2DeserializationFailureEvent(this.event);
        }
    }

    private V2DeserializationFailureEvent() {}

    private V2DeserializationFailureEvent(V2DeserializationFailureEvent event) {
        this.cause = event.cause;
        this.payload = event.payload;
        this.topic = event.topic;
    }

    /**
     * Creates a new builder for V2DeserializationFailureEvent instances
     *
     * @return a new builder for V2DeserializationFailureEvent instances
     */
    public static V2DeserializationFailureEventBuilder builder() {
        return new V2DeserializationFailureEventBuilder();
    }

    /**
     * @return the exception that triggered the failure
     */
    public Throwable getCause() {
        return this.cause;
    }

    /**
     * @return the payload of the message that triggered the failure
     */
    public byte[] getPayload() {
        return this.payload;
    }

    /**
     * @return the topic of the message that triggered the failure
     */
    public String getTopic() {
        return this.topic;
    }
}
