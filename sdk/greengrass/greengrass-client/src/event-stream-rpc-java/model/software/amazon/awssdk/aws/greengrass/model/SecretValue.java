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
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCServiceModel;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class SecretValue implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#SecretValue";

  private transient UnionMember setUnionMember;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<String> secretString;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<byte[]> secretBinary;

  public SecretValue() {
    this.secretString = Optional.empty();
    this.secretBinary = Optional.empty();
  }

  public String getSecretString() {
    if (secretString.isPresent() && (setUnionMember == UnionMember.SECRET_STRING)) {
      return secretString.get();
    }
    return null;
  }

  public void setSecretString(final String secretString) {
    if (setUnionMember != null) {
      setUnionMember.nullify(this);
    }
    this.secretString = Optional.of(secretString);
    this.setUnionMember = UnionMember.SECRET_STRING;
  }

  public SecretValue withSecretString(final String secretString) {
    setSecretString(secretString);
    return this;
  }

  public byte[] getSecretBinary() {
    if (secretBinary.isPresent() && (setUnionMember == UnionMember.SECRET_BINARY)) {
      return secretBinary.get();
    }
    return null;
  }

  public void setSecretBinary(final byte[] secretBinary) {
    if (setUnionMember != null) {
      setUnionMember.nullify(this);
    }
    this.secretBinary = Optional.of(secretBinary);
    this.setUnionMember = UnionMember.SECRET_BINARY;
  }

  public SecretValue withSecretBinary(final byte[] secretBinary) {
    setSecretBinary(secretBinary);
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
    if (!(rhs instanceof SecretValue)) return false;
    if (this == rhs) return true;
    final SecretValue other = (SecretValue)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.secretString.equals(other.secretString);
    isEquals = isEquals && EventStreamRPCServiceModel.blobTypeEquals(this.secretBinary, other.secretBinary);
    isEquals = isEquals && this.setUnionMember.equals(other.setUnionMember);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(secretString, secretBinary, setUnionMember);
  }

  public enum UnionMember {
    SECRET_STRING("SECRET_STRING", (software.amazon.awssdk.aws.greengrass.model.SecretValue obj) -> obj.secretString = Optional.empty(), (software.amazon.awssdk.aws.greengrass.model.SecretValue obj) -> obj.secretString != null && obj.secretString.isPresent()),

    SECRET_BINARY("SECRET_BINARY", (software.amazon.awssdk.aws.greengrass.model.SecretValue obj) -> obj.secretBinary = Optional.empty(), (software.amazon.awssdk.aws.greengrass.model.SecretValue obj) -> obj.secretBinary != null && obj.secretBinary.isPresent());

    private String fieldName;

    private Consumer<SecretValue> nullifier;

    private Predicate<SecretValue> isPresent;

    UnionMember(String fieldName, Consumer<SecretValue> nullifier,
        Predicate<SecretValue> isPresent) {
      this.fieldName = fieldName;
      this.nullifier = nullifier;
      this.isPresent = isPresent;
    }

    void nullify(SecretValue obj) {
      nullifier.accept(obj);
    }

    boolean isPresent(SecretValue obj) {
      return isPresent.test(obj);
    }
  }
}
