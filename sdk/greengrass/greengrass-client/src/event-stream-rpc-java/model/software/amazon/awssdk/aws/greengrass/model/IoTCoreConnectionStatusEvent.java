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

public class IoTCoreConnectionStatusEvent implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#IoTCoreConnectionStatusEvent";

  private transient UnionMember setUnionMember;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<ConnectionStatusEvent> connectionStatusEvent;

  public IoTCoreConnectionStatusEvent() {
    this.connectionStatusEvent = Optional.empty();
  }

  public ConnectionStatusEvent getConnectionStatusEvent() {
    if (connectionStatusEvent.isPresent() && (setUnionMember == UnionMember.CONNECTION_STATUS_EVENT)) {
      return connectionStatusEvent.get();
    }
    return null;
  }

  public void setConnectionStatusEvent(final ConnectionStatusEvent connectionStatusEvent) {
    if (setUnionMember != null) {
      setUnionMember.nullify(this);
    }
    this.connectionStatusEvent = Optional.of(connectionStatusEvent);
    this.setUnionMember = UnionMember.CONNECTION_STATUS_EVENT;
  }

  public IoTCoreConnectionStatusEvent withConnectionStatusEvent(
      final ConnectionStatusEvent connectionStatusEvent) {
    setConnectionStatusEvent(connectionStatusEvent);
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
    if (!(rhs instanceof IoTCoreConnectionStatusEvent)) return false;
    if (this == rhs) return true;
    final IoTCoreConnectionStatusEvent other = (IoTCoreConnectionStatusEvent)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.connectionStatusEvent.equals(other.connectionStatusEvent);
    isEquals = isEquals && this.setUnionMember.equals(other.setUnionMember);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(connectionStatusEvent, setUnionMember);
  }

  public enum UnionMember {
    CONNECTION_STATUS_EVENT("CONNECTION_STATUS_EVENT", (software.amazon.awssdk.aws.greengrass.model.IoTCoreConnectionStatusEvent obj) -> obj.connectionStatusEvent = Optional.empty(), (software.amazon.awssdk.aws.greengrass.model.IoTCoreConnectionStatusEvent obj) -> obj.connectionStatusEvent != null && obj.connectionStatusEvent.isPresent());

    private String fieldName;

    private Consumer<IoTCoreConnectionStatusEvent> nullifier;

    private Predicate<IoTCoreConnectionStatusEvent> isPresent;

    UnionMember(String fieldName, Consumer<IoTCoreConnectionStatusEvent> nullifier,
        Predicate<IoTCoreConnectionStatusEvent> isPresent) {
      this.fieldName = fieldName;
      this.nullifier = nullifier;
      this.isPresent = isPresent;
    }

    void nullify(IoTCoreConnectionStatusEvent obj) {
      nullifier.accept(obj);
    }

    boolean isPresent(IoTCoreConnectionStatusEvent obj) {
      return isPresent.test(obj);
    }
  }
}
