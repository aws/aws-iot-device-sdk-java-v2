/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.aws.greengrass.model;

import com.google.gson.annotations.Expose;
import java.lang.Deprecated;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class SubscribeToTopicResponse implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#SubscribeToTopicResponse";

  public static final SubscribeToTopicResponse VOID;

  static {
    VOID = new SubscribeToTopicResponse() {
      @Override
      public boolean isVoid() {
        return true;
      }
    };
  }

  /**
   * @deprecated No longer used
   */
  @Expose(
      serialize = true,
      deserialize = true
  )
  @Deprecated
  private Optional<String> topicName;

  public SubscribeToTopicResponse() {
    this.topicName = Optional.empty();
  }

  @Deprecated
  public String getTopicName() {
    if (topicName.isPresent()) {
      return topicName.get();
    }
    return null;
  }

  @Deprecated
  public void setTopicName(final String topicName) {
    this.topicName = Optional.ofNullable(topicName);
  }

  @Deprecated
  public SubscribeToTopicResponse withTopicName(final String topicName) {
    setTopicName(topicName);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof SubscribeToTopicResponse)) return false;
    if (this == rhs) return true;
    final SubscribeToTopicResponse other = (SubscribeToTopicResponse)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.topicName.equals(other.topicName);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(topicName);
  }
}
