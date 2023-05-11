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

public class PublishToIoTCoreRequest implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#PublishToIoTCoreRequest";

  public static final PublishToIoTCoreRequest VOID;

  static {
    VOID = new PublishToIoTCoreRequest() {
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
  private Optional<String> qos;

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

  public PublishToIoTCoreRequest() {
    this.topicName = Optional.empty();
    this.qos = Optional.empty();
    this.payload = Optional.empty();
    this.retain = Optional.empty();
    this.userProperties = Optional.empty();
    this.messageExpiryIntervalSeconds = Optional.empty();
    this.correlationData = Optional.empty();
    this.responseTopic = Optional.empty();
    this.payloadFormat = Optional.empty();
    this.contentType = Optional.empty();
  }

  /**
   * The topic to which to publish the message.
   */
  public String getTopicName() {
    if (topicName.isPresent()) {
      return topicName.get();
    }
    return null;
  }

  /**
   * The topic to which to publish the message.
   */
  public void setTopicName(final String topicName) {
    this.topicName = Optional.ofNullable(topicName);
  }

  /**
   * The topic to which to publish the message.
   */
  public PublishToIoTCoreRequest withTopicName(final String topicName) {
    setTopicName(topicName);
    return this;
  }

  public QOS getQos() {
    if (qos.isPresent()) {
      return QOS.get(qos.get());
    }
    return null;
  }

  /**
   * The MQTT QoS to use.
   */
  public String getQosAsString() {
    if (qos.isPresent()) {
      return qos.get();
    }
    return null;
  }

  /**
   * The MQTT QoS to use.
   */
  public void setQos(final String qos) {
    this.qos = Optional.ofNullable(qos);
  }

  /**
   * The MQTT QoS to use.
   */
  public PublishToIoTCoreRequest withQos(final String qos) {
    setQos(qos);
    return this;
  }

  /**
   * The MQTT QoS to use.
   */
  public void setQos(final QOS qos) {
    this.qos = Optional.ofNullable(qos.getValue());
  }

  /**
   * The MQTT QoS to use.
   */
  public PublishToIoTCoreRequest withQos(final QOS qos) {
    setQos(qos);
    return this;
  }

  /**
   * (Optional) The message payload as a blob.
   */
  public byte[] getPayload() {
    if (payload.isPresent()) {
      return payload.get();
    }
    return null;
  }

  /**
   * (Optional) The message payload as a blob.
   */
  public void setPayload(final byte[] payload) {
    this.payload = Optional.ofNullable(payload);
  }

  /**
   * (Optional) The message payload as a blob.
   */
  public PublishToIoTCoreRequest withPayload(final byte[] payload) {
    setPayload(payload);
    return this;
  }

  /**
   * (Optional) Whether to set MQTT retain option to true when publishing.
   */
  public Boolean isRetain() {
    if (retain.isPresent()) {
      return retain.get();
    }
    return null;
  }

  /**
   * (Optional) Whether to set MQTT retain option to true when publishing.
   */
  public void setRetain(final Boolean retain) {
    this.retain = Optional.ofNullable(retain);
  }

  /**
   * (Optional) Whether to set MQTT retain option to true when publishing.
   */
  public PublishToIoTCoreRequest withRetain(final Boolean retain) {
    setRetain(retain);
    return this;
  }

  /**
   * (Optional) MQTT user properties associated with the message.
   */
  public List<UserProperty> getUserProperties() {
    if (userProperties.isPresent()) {
      return userProperties.get();
    }
    return null;
  }

  /**
   * (Optional) MQTT user properties associated with the message.
   */
  public void setUserProperties(final List<UserProperty> userProperties) {
    this.userProperties = Optional.ofNullable(userProperties);
  }

  /**
   * (Optional) MQTT user properties associated with the message.
   */
  public PublishToIoTCoreRequest withUserProperties(final List<UserProperty> userProperties) {
    setUserProperties(userProperties);
    return this;
  }

  /**
   * (Optional) Message expiry interval in seconds.
   */
  public Long getMessageExpiryIntervalSeconds() {
    if (messageExpiryIntervalSeconds.isPresent()) {
      return messageExpiryIntervalSeconds.get();
    }
    return null;
  }

  /**
   * (Optional) Message expiry interval in seconds.
   */
  public void setMessageExpiryIntervalSeconds(final Long messageExpiryIntervalSeconds) {
    this.messageExpiryIntervalSeconds = Optional.ofNullable(messageExpiryIntervalSeconds);
  }

  /**
   * (Optional) Message expiry interval in seconds.
   */
  public PublishToIoTCoreRequest withMessageExpiryIntervalSeconds(
      final Long messageExpiryIntervalSeconds) {
    setMessageExpiryIntervalSeconds(messageExpiryIntervalSeconds);
    return this;
  }

  /**
   * (Optional) Correlation data blob for request/response.
   */
  public byte[] getCorrelationData() {
    if (correlationData.isPresent()) {
      return correlationData.get();
    }
    return null;
  }

  /**
   * (Optional) Correlation data blob for request/response.
   */
  public void setCorrelationData(final byte[] correlationData) {
    this.correlationData = Optional.ofNullable(correlationData);
  }

  /**
   * (Optional) Correlation data blob for request/response.
   */
  public PublishToIoTCoreRequest withCorrelationData(final byte[] correlationData) {
    setCorrelationData(correlationData);
    return this;
  }

  /**
   * (Optional) Response topic for request/response.
   */
  public String getResponseTopic() {
    if (responseTopic.isPresent()) {
      return responseTopic.get();
    }
    return null;
  }

  /**
   * (Optional) Response topic for request/response.
   */
  public void setResponseTopic(final String responseTopic) {
    this.responseTopic = Optional.ofNullable(responseTopic);
  }

  /**
   * (Optional) Response topic for request/response.
   */
  public PublishToIoTCoreRequest withResponseTopic(final String responseTopic) {
    setResponseTopic(responseTopic);
    return this;
  }

  public PayloadFormat getPayloadFormat() {
    if (payloadFormat.isPresent()) {
      return PayloadFormat.get(payloadFormat.get());
    }
    return null;
  }

  /**
   * (Optional) Message payload format.
   */
  public String getPayloadFormatAsString() {
    if (payloadFormat.isPresent()) {
      return payloadFormat.get();
    }
    return null;
  }

  /**
   * (Optional) Message payload format.
   */
  public void setPayloadFormat(final String payloadFormat) {
    this.payloadFormat = Optional.ofNullable(payloadFormat);
  }

  /**
   * (Optional) Message payload format.
   */
  public PublishToIoTCoreRequest withPayloadFormat(final String payloadFormat) {
    setPayloadFormat(payloadFormat);
    return this;
  }

  /**
   * (Optional) Message payload format.
   */
  public void setPayloadFormat(final PayloadFormat payloadFormat) {
    this.payloadFormat = Optional.ofNullable(payloadFormat.getValue());
  }

  /**
   * (Optional) Message payload format.
   */
  public PublishToIoTCoreRequest withPayloadFormat(final PayloadFormat payloadFormat) {
    setPayloadFormat(payloadFormat);
    return this;
  }

  /**
   * (Optional) Message content type.
   */
  public String getContentType() {
    if (contentType.isPresent()) {
      return contentType.get();
    }
    return null;
  }

  /**
   * (Optional) Message content type.
   */
  public void setContentType(final String contentType) {
    this.contentType = Optional.ofNullable(contentType);
  }

  /**
   * (Optional) Message content type.
   */
  public PublishToIoTCoreRequest withContentType(final String contentType) {
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
    if (!(rhs instanceof PublishToIoTCoreRequest)) return false;
    if (this == rhs) return true;
    final PublishToIoTCoreRequest other = (PublishToIoTCoreRequest)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.topicName.equals(other.topicName);
    isEquals = isEquals && this.qos.equals(other.qos);
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
    return Objects.hash(topicName, qos, payload, retain, userProperties, messageExpiryIntervalSeconds, correlationData, responseTopic, payloadFormat, contentType);
  }
}
