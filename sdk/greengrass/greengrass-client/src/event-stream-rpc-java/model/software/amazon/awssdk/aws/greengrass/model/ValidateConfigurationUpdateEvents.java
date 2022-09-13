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

public class ValidateConfigurationUpdateEvents implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#ValidateConfigurationUpdateEvents";

  private transient UnionMember setUnionMember;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<ValidateConfigurationUpdateEvent> validateConfigurationUpdateEvent;

  public ValidateConfigurationUpdateEvents() {
    this.validateConfigurationUpdateEvent = Optional.empty();
  }

  public ValidateConfigurationUpdateEvent getValidateConfigurationUpdateEvent() {
    if (validateConfigurationUpdateEvent.isPresent() && (setUnionMember == UnionMember.VALIDATE_CONFIGURATION_UPDATE_EVENT)) {
      return validateConfigurationUpdateEvent.get();
    }
    return null;
  }

  public void setValidateConfigurationUpdateEvent(
      final ValidateConfigurationUpdateEvent validateConfigurationUpdateEvent) {
    if (setUnionMember != null) {
      setUnionMember.nullify(this);
    }
    this.validateConfigurationUpdateEvent = Optional.of(validateConfigurationUpdateEvent);
    this.setUnionMember = UnionMember.VALIDATE_CONFIGURATION_UPDATE_EVENT;
  }

  public ValidateConfigurationUpdateEvents withValidateConfigurationUpdateEvent(
      final ValidateConfigurationUpdateEvent validateConfigurationUpdateEvent) {
    setValidateConfigurationUpdateEvent(validateConfigurationUpdateEvent);
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
    if (!(rhs instanceof ValidateConfigurationUpdateEvents)) return false;
    if (this == rhs) return true;
    final ValidateConfigurationUpdateEvents other = (ValidateConfigurationUpdateEvents)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.validateConfigurationUpdateEvent.equals(other.validateConfigurationUpdateEvent);
    isEquals = isEquals && this.setUnionMember.equals(other.setUnionMember);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(validateConfigurationUpdateEvent, setUnionMember);
  }

  public enum UnionMember {
    VALIDATE_CONFIGURATION_UPDATE_EVENT("VALIDATE_CONFIGURATION_UPDATE_EVENT", (software.amazon.awssdk.aws.greengrass.model.ValidateConfigurationUpdateEvents obj) -> obj.validateConfigurationUpdateEvent = Optional.empty(), (software.amazon.awssdk.aws.greengrass.model.ValidateConfigurationUpdateEvents obj) -> obj.validateConfigurationUpdateEvent != null && obj.validateConfigurationUpdateEvent.isPresent());

    private String fieldName;

    private Consumer<ValidateConfigurationUpdateEvents> nullifier;

    private Predicate<ValidateConfigurationUpdateEvents> isPresent;

    UnionMember(String fieldName, Consumer<ValidateConfigurationUpdateEvents> nullifier,
        Predicate<ValidateConfigurationUpdateEvents> isPresent) {
      this.fieldName = fieldName;
      this.nullifier = nullifier;
      this.isPresent = isPresent;
    }

    void nullify(ValidateConfigurationUpdateEvents obj) {
      nullifier.accept(obj);
    }

    boolean isPresent(ValidateConfigurationUpdateEvents obj) {
      return isPresent.test(obj);
    }
  }
}
