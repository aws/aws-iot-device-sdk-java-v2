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
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class CreateDebugPasswordResponse implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#CreateDebugPasswordResponse";

  public static final CreateDebugPasswordResponse VOID;

  static {
    VOID = new CreateDebugPasswordResponse() {
      @Override
      public boolean isVoid() {
        return true;
      }
    };
  }

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<String> password;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<String> username;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<Instant> passwordExpiration;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<String> certificateSHA256Hash;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<String> certificateSHA1Hash;

  public CreateDebugPasswordResponse() {
    this.password = Optional.empty();
    this.username = Optional.empty();
    this.passwordExpiration = Optional.empty();
    this.certificateSHA256Hash = Optional.empty();
    this.certificateSHA1Hash = Optional.empty();
  }

  public String getPassword() {
    if (password.isPresent()) {
      return password.get();
    }
    return null;
  }

  public void setPassword(final String password) {
    this.password = Optional.ofNullable(password);
  }

  public CreateDebugPasswordResponse withPassword(final String password) {
    setPassword(password);
    return this;
  }

  public String getUsername() {
    if (username.isPresent()) {
      return username.get();
    }
    return null;
  }

  public void setUsername(final String username) {
    this.username = Optional.ofNullable(username);
  }

  public CreateDebugPasswordResponse withUsername(final String username) {
    setUsername(username);
    return this;
  }

  public Instant getPasswordExpiration() {
    if (passwordExpiration.isPresent()) {
      return passwordExpiration.get();
    }
    return null;
  }

  public void setPasswordExpiration(final Instant passwordExpiration) {
    this.passwordExpiration = Optional.ofNullable(passwordExpiration);
  }

  public CreateDebugPasswordResponse withPasswordExpiration(final Instant passwordExpiration) {
    setPasswordExpiration(passwordExpiration);
    return this;
  }

  public String getCertificateSHA256Hash() {
    if (certificateSHA256Hash.isPresent()) {
      return certificateSHA256Hash.get();
    }
    return null;
  }

  public void setCertificateSHA256Hash(final String certificateSHA256Hash) {
    this.certificateSHA256Hash = Optional.ofNullable(certificateSHA256Hash);
  }

  public CreateDebugPasswordResponse withCertificateSHA256Hash(final String certificateSHA256Hash) {
    setCertificateSHA256Hash(certificateSHA256Hash);
    return this;
  }

  public String getCertificateSHA1Hash() {
    if (certificateSHA1Hash.isPresent()) {
      return certificateSHA1Hash.get();
    }
    return null;
  }

  public void setCertificateSHA1Hash(final String certificateSHA1Hash) {
    this.certificateSHA1Hash = Optional.ofNullable(certificateSHA1Hash);
  }

  public CreateDebugPasswordResponse withCertificateSHA1Hash(final String certificateSHA1Hash) {
    setCertificateSHA1Hash(certificateSHA1Hash);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof CreateDebugPasswordResponse)) return false;
    if (this == rhs) return true;
    final CreateDebugPasswordResponse other = (CreateDebugPasswordResponse)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.password.equals(other.password);
    isEquals = isEquals && this.username.equals(other.username);
    isEquals = isEquals && this.passwordExpiration.equals(other.passwordExpiration);
    isEquals = isEquals && this.certificateSHA256Hash.equals(other.certificateSHA256Hash);
    isEquals = isEquals && this.certificateSHA1Hash.equals(other.certificateSHA1Hash);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(password, username, passwordExpiration, certificateSHA256Hash, certificateSHA1Hash);
  }
}
