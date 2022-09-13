/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.aws.greengrass.model;

import com.google.gson.annotations.Expose;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class SubscribeToTopicRequest implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#SubscribeToTopicRequest";

  public static final SubscribeToTopicRequest VOID;

  static {
    VOID = new SubscribeToTopicRequest() {
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
  private Optional<String> topic;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<String> receiveMode;

  public SubscribeToTopicRequest() {
    this.topic = Optional.empty();
    this.receiveMode = Optional.empty();
  }

  public String getTopic() {
    if (topic.isPresent()) {
      return topic.get();
    }
    return null;
  }

  public void setTopic(final String topic) {
    this.topic = Optional.ofNullable(topic);
  }

  public SubscribeToTopicRequest withTopic(final String topic) {
    setTopic(topic);
    return this;
  }

  public ReceiveMode getReceiveMode() {
    if (receiveMode.isPresent()) {
      return ReceiveMode.get(receiveMode.get());
    }
    return null;
  }

  public String getReceiveModeAsString() {
    if (receiveMode.isPresent()) {
      return receiveMode.get();
    }
    return null;
  }

  public void setReceiveMode(final String receiveMode) {
    this.receiveMode = Optional.ofNullable(receiveMode);
  }

  public SubscribeToTopicRequest withReceiveMode(final String receiveMode) {
    setReceiveMode(receiveMode);
    return this;
  }

  public void setReceiveMode(final ReceiveMode receiveMode) {
    this.receiveMode = Optional.ofNullable(receiveMode.getValue());
  }

  public SubscribeToTopicRequest withReceiveMode(final ReceiveMode receiveMode) {
    setReceiveMode(receiveMode);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof SubscribeToTopicRequest)) return false;
    if (this == rhs) return true;
    final SubscribeToTopicRequest other = (SubscribeToTopicRequest)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.topic.equals(other.topic);
    isEquals = isEquals && this.receiveMode.equals(other.receiveMode);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(topic, receiveMode);
  }
}
