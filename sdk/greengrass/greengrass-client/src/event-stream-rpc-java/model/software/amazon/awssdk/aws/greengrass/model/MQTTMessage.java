/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.aws.greengrass.model;

import com.google.gson.annotations.Expose;
import java.lang.Boolean;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCServiceModel;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class MQTTMessage implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#MQTTMessage";

  public static final MQTTMessage VOID;

  static {
    VOID = new MQTTMessage() {
      @Override
      public boolean isVoid() {
        return true;
      }
    };
  }

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<String> topicName;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<byte[]> payload;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<Boolean> retain;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<List<UserProperty>> userProperties;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<Long> messageExpiryIntervalSeconds;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<byte[]> correlationData;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<String> responseTopic;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<String> payloadFormat;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<String> contentType;

  public MQTTMessage() {
    this.topicName = Optional.empty();
    this.payload = Optional.empty();
    this.retain = Optional.empty();
    this.userProperties = Optional.empty();
    this.messageExpiryIntervalSeconds = Optional.empty();
    this.correlationData = Optional.empty();
    this.responseTopic = Optional.empty();
    this.payloadFormat = Optional.empty();
    this.contentType = Optional.empty();
  }

  public String getTopicName() {
    if (topicName.isPresent()) {
      return topicName.get();
    }
    return null;
  }

  public void setTopicName(final String topicName) {
    this.topicName = Optional.ofNullable(topicName);
  }

  public MQTTMessage withTopicName(final String topicName) {
    setTopicName(topicName);
    return this;
  }

  public byte[] getPayload() {
    if (payload.isPresent()) {
      return payload.get();
    }
    return null;
  }

  public void setPayload(final byte[] payload) {
    this.payload = Optional.ofNullable(payload);
  }

  public MQTTMessage withPayload(final byte[] payload) {
    setPayload(payload);
    return this;
  }

  public Boolean isRetain() {
    if (retain.isPresent()) {
      return retain.get();
    }
    return null;
  }

  public void setRetain(final Boolean retain) {
    this.retain = Optional.ofNullable(retain);
  }

  public MQTTMessage withRetain(final Boolean retain) {
    setRetain(retain);
    return this;
  }

  public List<UserProperty> getUserProperties() {
    if (userProperties.isPresent()) {
      return userProperties.get();
    }
    return null;
  }

  public void setUserProperties(final List<UserProperty> userProperties) {
    this.userProperties = Optional.ofNullable(userProperties);
  }

  public MQTTMessage withUserProperties(final List<UserProperty> userProperties) {
    setUserProperties(userProperties);
    return this;
  }

  public Long getMessageExpiryIntervalSeconds() {
    if (messageExpiryIntervalSeconds.isPresent()) {
      return messageExpiryIntervalSeconds.get();
    }
    return null;
  }

  public void setMessageExpiryIntervalSeconds(final Long messageExpiryIntervalSeconds) {
    this.messageExpiryIntervalSeconds = Optional.ofNullable(messageExpiryIntervalSeconds);
  }

  public MQTTMessage withMessageExpiryIntervalSeconds(final Long messageExpiryIntervalSeconds) {
    setMessageExpiryIntervalSeconds(messageExpiryIntervalSeconds);
    return this;
  }

  public byte[] getCorrelationData() {
    if (correlationData.isPresent()) {
      return correlationData.get();
    }
    return null;
  }

  public void setCorrelationData(final byte[] correlationData) {
    this.correlationData = Optional.ofNullable(correlationData);
  }

  public MQTTMessage withCorrelationData(final byte[] correlationData) {
    setCorrelationData(correlationData);
    return this;
  }

  public String getResponseTopic() {
    if (responseTopic.isPresent()) {
      return responseTopic.get();
    }
    return null;
  }

  public void setResponseTopic(final String responseTopic) {
    this.responseTopic = Optional.ofNullable(responseTopic);
  }

  public MQTTMessage withResponseTopic(final String responseTopic) {
    setResponseTopic(responseTopic);
    return this;
  }

  public PayloadFormat getPayloadFormat() {
    if (payloadFormat.isPresent()) {
      return PayloadFormat.get(payloadFormat.get());
    }
    return null;
  }

  public String getPayloadFormatAsString() {
    if (payloadFormat.isPresent()) {
      return payloadFormat.get();
    }
    return null;
  }

  public void setPayloadFormat(final String payloadFormat) {
    this.payloadFormat = Optional.ofNullable(payloadFormat);
  }

  public MQTTMessage withPayloadFormat(final String payloadFormat) {
    setPayloadFormat(payloadFormat);
    return this;
  }

  public void setPayloadFormat(final PayloadFormat payloadFormat) {
    this.payloadFormat = Optional.ofNullable(payloadFormat.getValue());
  }

  public MQTTMessage withPayloadFormat(final PayloadFormat payloadFormat) {
    setPayloadFormat(payloadFormat);
    return this;
  }

  public String getContentType() {
    if (contentType.isPresent()) {
      return contentType.get();
    }
    return null;
  }

  public void setContentType(final String contentType) {
    this.contentType = Optional.ofNullable(contentType);
  }

  public MQTTMessage withContentType(final String contentType) {
    setContentType(contentType);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof MQTTMessage)) return false;
    if (this == rhs) return true;
    final MQTTMessage other = (MQTTMessage)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.topicName.equals(other.topicName);
    isEquals = isEquals && EventStreamRPCServiceModel.blobTypeEquals(this.payload, other.payload);
    isEquals = isEquals && this.retain.equals(other.retain);
    isEquals = isEquals && this.userProperties.equals(other.userProperties);
    isEquals = isEquals && this.messageExpiryIntervalSeconds.equals(other.messageExpiryIntervalSeconds);
    isEquals = isEquals && EventStreamRPCServiceModel.blobTypeEquals(this.correlationData, other.correlationData);
    isEquals = isEquals && this.responseTopic.equals(other.responseTopic);
    isEquals = isEquals && this.payloadFormat.equals(other.payloadFormat);
    isEquals = isEquals && this.contentType.equals(other.contentType);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(topicName, payload, retain, userProperties, messageExpiryIntervalSeconds, correlationData, responseTopic, payloadFormat, contentType);
  }
}
