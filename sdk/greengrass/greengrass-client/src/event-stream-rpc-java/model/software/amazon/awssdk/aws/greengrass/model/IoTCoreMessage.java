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

public class IoTCoreMessage implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#IoTCoreMessage";

  private transient UnionMember setUnionMember;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<MQTTMessage> message;

  public IoTCoreMessage() {
    this.message = Optional.empty();
  }

  public MQTTMessage getMessage() {
    if (message.isPresent() && (setUnionMember == UnionMember.MESSAGE)) {
      return message.get();
    }
    return null;
  }

  public void setMessage(final MQTTMessage message) {
    if (setUnionMember != null) {
      setUnionMember.nullify(this);
    }
    this.message = Optional.of(message);
    this.setUnionMember = UnionMember.MESSAGE;
  }

  public IoTCoreMessage withMessage(final MQTTMessage message) {
    setMessage(message);
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
    if (!(rhs instanceof IoTCoreMessage)) return false;
    if (this == rhs) return true;
    final IoTCoreMessage other = (IoTCoreMessage)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.message.equals(other.message);
    isEquals = isEquals && this.setUnionMember.equals(other.setUnionMember);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(message, setUnionMember);
  }

  public enum UnionMember {
    MESSAGE("MESSAGE", (software.amazon.awssdk.aws.greengrass.model.IoTCoreMessage obj) -> obj.message = Optional.empty(), (software.amazon.awssdk.aws.greengrass.model.IoTCoreMessage obj) -> obj.message != null && obj.message.isPresent());

    private String fieldName;

    private Consumer<IoTCoreMessage> nullifier;

    private Predicate<IoTCoreMessage> isPresent;

    UnionMember(String fieldName, Consumer<IoTCoreMessage> nullifier,
        Predicate<IoTCoreMessage> isPresent) {
      this.fieldName = fieldName;
      this.nullifier = nullifier;
      this.isPresent = isPresent;
    }

    void nullify(IoTCoreMessage obj) {
      nullifier.accept(obj);
    }

    boolean isPresent(IoTCoreMessage obj) {
      return isPresent.test(obj);
    }
  }
}
