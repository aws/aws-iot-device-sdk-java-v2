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
import java.util.function.Consumer;
import java.util.function.Predicate;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class SubscriptionResponseMessage implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#SubscriptionResponseMessage";

  private transient UnionMember setUnionMember;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<JsonMessage> jsonMessage;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<BinaryMessage> binaryMessage;

  public SubscriptionResponseMessage() {
    this.jsonMessage = Optional.empty();
    this.binaryMessage = Optional.empty();
  }

  public JsonMessage getJsonMessage() {
    if (jsonMessage.isPresent() && (setUnionMember == UnionMember.JSON_MESSAGE)) {
      return jsonMessage.get();
    }
    return null;
  }

  public void setJsonMessage(final JsonMessage jsonMessage) {
    if (setUnionMember != null) {
      setUnionMember.nullify(this);
    }
    this.jsonMessage = Optional.of(jsonMessage);
    this.setUnionMember = UnionMember.JSON_MESSAGE;
  }

  public SubscriptionResponseMessage withJsonMessage(final JsonMessage jsonMessage) {
    setJsonMessage(jsonMessage);
    return this;
  }

  public BinaryMessage getBinaryMessage() {
    if (binaryMessage.isPresent() && (setUnionMember == UnionMember.BINARY_MESSAGE)) {
      return binaryMessage.get();
    }
    return null;
  }

  public void setBinaryMessage(final BinaryMessage binaryMessage) {
    if (setUnionMember != null) {
      setUnionMember.nullify(this);
    }
    this.binaryMessage = Optional.of(binaryMessage);
    this.setUnionMember = UnionMember.BINARY_MESSAGE;
  }

  public SubscriptionResponseMessage withBinaryMessage(final BinaryMessage binaryMessage) {
    setBinaryMessage(binaryMessage);
    return this;
  }

  /**
   * Returns an indicator for which enum member is set. Can be used to convert to proper type.
   */
  public UnionMember getSetUnionMember() {
    return setUnionMember;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  public void selfDesignateSetUnionMember() {
    int setCount = 0;
    UnionMember[] members = UnionMember.values();
    for (int memberIdx = 0; memberIdx < UnionMember.values().length; ++memberIdx) {
      if (members[memberIdx].isPresent(this)) {
        ++setCount;
        this.setUnionMember = members[memberIdx];
      }
    }
    // only bad outcome here is if there's more than one member set. It's possible for none to be set
    if (setCount > 1) {
      throw new IllegalArgumentException("More than one union member set for type: " + getApplicationModelType());
    }
  }

  @Override
  public void postFromJson() {
    selfDesignateSetUnionMember();
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof SubscriptionResponseMessage)) return false;
    if (this == rhs) return true;
    final SubscriptionResponseMessage other = (SubscriptionResponseMessage)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.jsonMessage.equals(other.jsonMessage);
    isEquals = isEquals && this.binaryMessage.equals(other.binaryMessage);
    isEquals = isEquals && this.setUnionMember.equals(other.setUnionMember);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(jsonMessage, binaryMessage, setUnionMember);
  }

  public enum UnionMember {
    JSON_MESSAGE("JSON_MESSAGE", (software.amazon.awssdk.aws.greengrass.model.SubscriptionResponseMessage obj) -> obj.jsonMessage = Optional.empty(), (software.amazon.awssdk.aws.greengrass.model.SubscriptionResponseMessage obj) -> obj.jsonMessage != null && obj.jsonMessage.isPresent()),

    BINARY_MESSAGE("BINARY_MESSAGE", (software.amazon.awssdk.aws.greengrass.model.SubscriptionResponseMessage obj) -> obj.binaryMessage = Optional.empty(), (software.amazon.awssdk.aws.greengrass.model.SubscriptionResponseMessage obj) -> obj.binaryMessage != null && obj.binaryMessage.isPresent());

    private String fieldName;

    private Consumer<SubscriptionResponseMessage> nullifier;

    private Predicate<SubscriptionResponseMessage> isPresent;

    UnionMember(String fieldName, Consumer<SubscriptionResponseMessage> nullifier,
        Predicate<SubscriptionResponseMessage> isPresent) {
      this.fieldName = fieldName;
      this.nullifier = nullifier;
      this.isPresent = isPresent;
    }

    void nullify(SubscriptionResponseMessage obj) {
      nullifier.accept(obj);
    }

    boolean isPresent(SubscriptionResponseMessage obj) {
      return isPresent.test(obj);
    }
  }
}
