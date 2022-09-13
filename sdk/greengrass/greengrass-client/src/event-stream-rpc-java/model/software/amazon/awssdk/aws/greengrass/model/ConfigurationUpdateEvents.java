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

public class ConfigurationUpdateEvents implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#ConfigurationUpdateEvents";

  private transient UnionMember setUnionMember;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<ConfigurationUpdateEvent> configurationUpdateEvent;

  public ConfigurationUpdateEvents() {
    this.configurationUpdateEvent = Optional.empty();
  }

  public ConfigurationUpdateEvent getConfigurationUpdateEvent() {
    if (configurationUpdateEvent.isPresent() && (setUnionMember == UnionMember.CONFIGURATION_UPDATE_EVENT)) {
      return configurationUpdateEvent.get();
    }
    return null;
  }

  public void setConfigurationUpdateEvent(final ConfigurationUpdateEvent configurationUpdateEvent) {
    if (setUnionMember != null) {
      setUnionMember.nullify(this);
    }
    this.configurationUpdateEvent = Optional.of(configurationUpdateEvent);
    this.setUnionMember = UnionMember.CONFIGURATION_UPDATE_EVENT;
  }

  public ConfigurationUpdateEvents withConfigurationUpdateEvent(
      final ConfigurationUpdateEvent configurationUpdateEvent) {
    setConfigurationUpdateEvent(configurationUpdateEvent);
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
    if (!(rhs instanceof ConfigurationUpdateEvents)) return false;
    if (this == rhs) return true;
    final ConfigurationUpdateEvents other = (ConfigurationUpdateEvents)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.configurationUpdateEvent.equals(other.configurationUpdateEvent);
    isEquals = isEquals && this.setUnionMember.equals(other.setUnionMember);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(configurationUpdateEvent, setUnionMember);
  }

  public enum UnionMember {
    CONFIGURATION_UPDATE_EVENT("CONFIGURATION_UPDATE_EVENT", (software.amazon.awssdk.aws.greengrass.model.ConfigurationUpdateEvents obj) -> obj.configurationUpdateEvent = Optional.empty(), (software.amazon.awssdk.aws.greengrass.model.ConfigurationUpdateEvents obj) -> obj.configurationUpdateEvent != null && obj.configurationUpdateEvent.isPresent());

    private String fieldName;

    private Consumer<ConfigurationUpdateEvents> nullifier;

    private Predicate<ConfigurationUpdateEvents> isPresent;

    UnionMember(String fieldName, Consumer<ConfigurationUpdateEvents> nullifier,
        Predicate<ConfigurationUpdateEvents> isPresent) {
      this.fieldName = fieldName;
      this.nullifier = nullifier;
      this.isPresent = isPresent;
    }

    void nullify(ConfigurationUpdateEvents obj) {
      nullifier.accept(obj);
    }

    boolean isPresent(ConfigurationUpdateEvents obj) {
      return isPresent.test(obj);
    }
  }
}
