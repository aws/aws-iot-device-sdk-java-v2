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

public class CertificateUpdateEvent implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#CertificateUpdateEvent";

  private transient UnionMember setUnionMember;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<CertificateUpdate> certificateUpdate;

  public CertificateUpdateEvent() {
    this.certificateUpdate = Optional.empty();
  }

  public CertificateUpdate getCertificateUpdate() {
    if (certificateUpdate.isPresent() && (setUnionMember == UnionMember.CERTIFICATE_UPDATE)) {
      return certificateUpdate.get();
    }
    return null;
  }

  public void setCertificateUpdate(final CertificateUpdate certificateUpdate) {
    if (setUnionMember != null) {
      setUnionMember.nullify(this);
    }
    this.certificateUpdate = Optional.of(certificateUpdate);
    this.setUnionMember = UnionMember.CERTIFICATE_UPDATE;
  }

  public CertificateUpdateEvent withCertificateUpdate(final CertificateUpdate certificateUpdate) {
    setCertificateUpdate(certificateUpdate);
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
    if (!(rhs instanceof CertificateUpdateEvent)) return false;
    if (this == rhs) return true;
    final CertificateUpdateEvent other = (CertificateUpdateEvent)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.certificateUpdate.equals(other.certificateUpdate);
    isEquals = isEquals && this.setUnionMember.equals(other.setUnionMember);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(certificateUpdate, setUnionMember);
  }

  public enum UnionMember {
    CERTIFICATE_UPDATE("CERTIFICATE_UPDATE", (software.amazon.awssdk.aws.greengrass.model.CertificateUpdateEvent obj) -> obj.certificateUpdate = Optional.empty(), (software.amazon.awssdk.aws.greengrass.model.CertificateUpdateEvent obj) -> obj.certificateUpdate != null && obj.certificateUpdate.isPresent());

    private String fieldName;

    private Consumer<CertificateUpdateEvent> nullifier;

    private Predicate<CertificateUpdateEvent> isPresent;

    UnionMember(String fieldName, Consumer<CertificateUpdateEvent> nullifier,
        Predicate<CertificateUpdateEvent> isPresent) {
      this.fieldName = fieldName;
      this.nullifier = nullifier;
      this.isPresent = isPresent;
    }

    void nullify(CertificateUpdateEvent obj) {
      nullifier.accept(obj);
    }

    boolean isPresent(CertificateUpdateEvent obj) {
      return isPresent.test(obj);
    }
  }
}
