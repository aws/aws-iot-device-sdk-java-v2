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

public class ClientDeviceCredential implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#ClientDeviceCredential";

  private transient UnionMember setUnionMember;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<String> clientDeviceCertificate;

  public ClientDeviceCredential() {
    this.clientDeviceCertificate = Optional.empty();
  }

  public String getClientDeviceCertificate() {
    if (clientDeviceCertificate.isPresent() && (setUnionMember == UnionMember.CLIENT_DEVICE_CERTIFICATE)) {
      return clientDeviceCertificate.get();
    }
    return null;
  }

  public void setClientDeviceCertificate(final String clientDeviceCertificate) {
    if (setUnionMember != null) {
      setUnionMember.nullify(this);
    }
    this.clientDeviceCertificate = Optional.of(clientDeviceCertificate);
    this.setUnionMember = UnionMember.CLIENT_DEVICE_CERTIFICATE;
  }

  public ClientDeviceCredential withClientDeviceCertificate(final String clientDeviceCertificate) {
    setClientDeviceCertificate(clientDeviceCertificate);
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
    if (!(rhs instanceof ClientDeviceCredential)) return false;
    if (this == rhs) return true;
    final ClientDeviceCredential other = (ClientDeviceCredential)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.clientDeviceCertificate.equals(other.clientDeviceCertificate);
    isEquals = isEquals && this.setUnionMember.equals(other.setUnionMember);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(clientDeviceCertificate, setUnionMember);
  }

  public enum UnionMember {
    CLIENT_DEVICE_CERTIFICATE("CLIENT_DEVICE_CERTIFICATE", (software.amazon.awssdk.aws.greengrass.model.ClientDeviceCredential obj) -> obj.clientDeviceCertificate = Optional.empty(), (software.amazon.awssdk.aws.greengrass.model.ClientDeviceCredential obj) -> obj.clientDeviceCertificate != null && obj.clientDeviceCertificate.isPresent());

    private String fieldName;

    private Consumer<ClientDeviceCredential> nullifier;

    private Predicate<ClientDeviceCredential> isPresent;

    UnionMember(String fieldName, Consumer<ClientDeviceCredential> nullifier,
        Predicate<ClientDeviceCredential> isPresent) {
      this.fieldName = fieldName;
      this.nullifier = nullifier;
      this.isPresent = isPresent;
    }

    void nullify(ClientDeviceCredential obj) {
      nullifier.accept(obj);
    }

    boolean isPresent(ClientDeviceCredential obj) {
      return isPresent.test(obj);
    }
  }
}
