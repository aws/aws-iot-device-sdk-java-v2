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

public class CredentialDocument implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#CredentialDocument";

  private transient UnionMember setUnionMember;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<MQTTCredential> mqttCredential;

  public CredentialDocument() {
    this.mqttCredential = Optional.empty();
  }

  public MQTTCredential getMqttCredential() {
    if (mqttCredential.isPresent() && (setUnionMember == UnionMember.MQTT_CREDENTIAL)) {
      return mqttCredential.get();
    }
    return null;
  }

  public void setMqttCredential(final MQTTCredential mqttCredential) {
    if (setUnionMember != null) {
      setUnionMember.nullify(this);
    }
    this.mqttCredential = Optional.of(mqttCredential);
    this.setUnionMember = UnionMember.MQTT_CREDENTIAL;
  }

  public CredentialDocument withMqttCredential(final MQTTCredential mqttCredential) {
    setMqttCredential(mqttCredential);
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
    if (!(rhs instanceof CredentialDocument)) return false;
    if (this == rhs) return true;
    final CredentialDocument other = (CredentialDocument)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.mqttCredential.equals(other.mqttCredential);
    isEquals = isEquals && this.setUnionMember.equals(other.setUnionMember);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(mqttCredential, setUnionMember);
  }

  public enum UnionMember {
    MQTT_CREDENTIAL("MQTT_CREDENTIAL", (software.amazon.awssdk.aws.greengrass.model.CredentialDocument obj) -> obj.mqttCredential = Optional.empty(), (software.amazon.awssdk.aws.greengrass.model.CredentialDocument obj) -> obj.mqttCredential != null && obj.mqttCredential.isPresent());

    private String fieldName;

    private Consumer<CredentialDocument> nullifier;

    private Predicate<CredentialDocument> isPresent;

    UnionMember(String fieldName, Consumer<CredentialDocument> nullifier,
        Predicate<CredentialDocument> isPresent) {
      this.fieldName = fieldName;
      this.nullifier = nullifier;
      this.isPresent = isPresent;
    }

    void nullify(CredentialDocument obj) {
      nullifier.accept(obj);
    }

    boolean isPresent(CredentialDocument obj) {
      return isPresent.test(obj);
    }
  }
}
