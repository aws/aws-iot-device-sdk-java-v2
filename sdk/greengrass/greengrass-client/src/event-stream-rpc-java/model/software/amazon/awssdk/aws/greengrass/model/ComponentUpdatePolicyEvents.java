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

public class ComponentUpdatePolicyEvents implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#ComponentUpdatePolicyEvents";

  private transient UnionMember setUnionMember;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<PreComponentUpdateEvent> preUpdateEvent;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<PostComponentUpdateEvent> postUpdateEvent;

  public ComponentUpdatePolicyEvents() {
    this.preUpdateEvent = Optional.empty();
    this.postUpdateEvent = Optional.empty();
  }

  public PreComponentUpdateEvent getPreUpdateEvent() {
    if (preUpdateEvent.isPresent() && (setUnionMember == UnionMember.PRE_UPDATE_EVENT)) {
      return preUpdateEvent.get();
    }
    return null;
  }

  public void setPreUpdateEvent(final PreComponentUpdateEvent preUpdateEvent) {
    if (setUnionMember != null) {
      setUnionMember.nullify(this);
    }
    this.preUpdateEvent = Optional.of(preUpdateEvent);
    this.setUnionMember = UnionMember.PRE_UPDATE_EVENT;
  }

  public ComponentUpdatePolicyEvents withPreUpdateEvent(
      final PreComponentUpdateEvent preUpdateEvent) {
    setPreUpdateEvent(preUpdateEvent);
    return this;
  }

  public PostComponentUpdateEvent getPostUpdateEvent() {
    if (postUpdateEvent.isPresent() && (setUnionMember == UnionMember.POST_UPDATE_EVENT)) {
      return postUpdateEvent.get();
    }
    return null;
  }

  public void setPostUpdateEvent(final PostComponentUpdateEvent postUpdateEvent) {
    if (setUnionMember != null) {
      setUnionMember.nullify(this);
    }
    this.postUpdateEvent = Optional.of(postUpdateEvent);
    this.setUnionMember = UnionMember.POST_UPDATE_EVENT;
  }

  public ComponentUpdatePolicyEvents withPostUpdateEvent(
      final PostComponentUpdateEvent postUpdateEvent) {
    setPostUpdateEvent(postUpdateEvent);
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
    if (!(rhs instanceof ComponentUpdatePolicyEvents)) return false;
    if (this == rhs) return true;
    final ComponentUpdatePolicyEvents other = (ComponentUpdatePolicyEvents)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.preUpdateEvent.equals(other.preUpdateEvent);
    isEquals = isEquals && this.postUpdateEvent.equals(other.postUpdateEvent);
    isEquals = isEquals && this.setUnionMember.equals(other.setUnionMember);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(preUpdateEvent, postUpdateEvent, setUnionMember);
  }

  public enum UnionMember {
    PRE_UPDATE_EVENT("PRE_UPDATE_EVENT", (software.amazon.awssdk.aws.greengrass.model.ComponentUpdatePolicyEvents obj) -> obj.preUpdateEvent = Optional.empty(), (software.amazon.awssdk.aws.greengrass.model.ComponentUpdatePolicyEvents obj) -> obj.preUpdateEvent != null && obj.preUpdateEvent.isPresent()),

    POST_UPDATE_EVENT("POST_UPDATE_EVENT", (software.amazon.awssdk.aws.greengrass.model.ComponentUpdatePolicyEvents obj) -> obj.postUpdateEvent = Optional.empty(), (software.amazon.awssdk.aws.greengrass.model.ComponentUpdatePolicyEvents obj) -> obj.postUpdateEvent != null && obj.postUpdateEvent.isPresent());

    private String fieldName;

    private Consumer<ComponentUpdatePolicyEvents> nullifier;

    private Predicate<ComponentUpdatePolicyEvents> isPresent;

    UnionMember(String fieldName, Consumer<ComponentUpdatePolicyEvents> nullifier,
        Predicate<ComponentUpdatePolicyEvents> isPresent) {
      this.fieldName = fieldName;
      this.nullifier = nullifier;
      this.isPresent = isPresent;
    }

    void nullify(ComponentUpdatePolicyEvents obj) {
      nullifier.accept(obj);
    }

    boolean isPresent(ComponentUpdatePolicyEvents obj) {
      return isPresent.test(obj);
    }
  }
}
