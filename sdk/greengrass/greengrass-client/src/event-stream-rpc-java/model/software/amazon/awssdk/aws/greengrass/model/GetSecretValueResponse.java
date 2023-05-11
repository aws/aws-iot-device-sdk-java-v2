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
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class GetSecretValueResponse implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#GetSecretValueResponse";

  public static final GetSecretValueResponse VOID;

  static {
    VOID = new GetSecretValueResponse() {
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
  private Optional<String> secretId;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<String> versionId;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<List<String>> versionStage;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<SecretValue> secretValue;

  public GetSecretValueResponse() {
    this.secretId = Optional.empty();
    this.versionId = Optional.empty();
    this.versionStage = Optional.empty();
    this.secretValue = Optional.empty();
  }

  /**
   * The ID of the secret.
   */
  public String getSecretId() {
    if (secretId.isPresent()) {
      return secretId.get();
    }
    return null;
  }

  /**
   * The ID of the secret.
   */
  public void setSecretId(final String secretId) {
    this.secretId = Optional.ofNullable(secretId);
  }

  /**
   * The ID of the secret.
   */
  public GetSecretValueResponse withSecretId(final String secretId) {
    setSecretId(secretId);
    return this;
  }

  /**
   * The ID of this version of the secret.
   */
  public String getVersionId() {
    if (versionId.isPresent()) {
      return versionId.get();
    }
    return null;
  }

  /**
   * The ID of this version of the secret.
   */
  public void setVersionId(final String versionId) {
    this.versionId = Optional.ofNullable(versionId);
  }

  /**
   * The ID of this version of the secret.
   */
  public GetSecretValueResponse withVersionId(final String versionId) {
    setVersionId(versionId);
    return this;
  }

  /**
   * The list of staging labels attached to this version of the secret.
   */
  public List<String> getVersionStage() {
    if (versionStage.isPresent()) {
      return versionStage.get();
    }
    return null;
  }

  /**
   * The list of staging labels attached to this version of the secret.
   */
  public void setVersionStage(final List<String> versionStage) {
    this.versionStage = Optional.ofNullable(versionStage);
  }

  /**
   * The list of staging labels attached to this version of the secret.
   */
  public GetSecretValueResponse withVersionStage(final List<String> versionStage) {
    setVersionStage(versionStage);
    return this;
  }

  /**
   * The value of this version of the secret.
   */
  public SecretValue getSecretValue() {
    if (secretValue.isPresent()) {
      return secretValue.get();
    }
    return null;
  }

  /**
   * The value of this version of the secret.
   */
  public void setSecretValue(final SecretValue secretValue) {
    this.secretValue = Optional.ofNullable(secretValue);
  }

  /**
   * The value of this version of the secret.
   */
  public GetSecretValueResponse withSecretValue(final SecretValue secretValue) {
    setSecretValue(secretValue);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof GetSecretValueResponse)) return false;
    if (this == rhs) return true;
    final GetSecretValueResponse other = (GetSecretValueResponse)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.secretId.equals(other.secretId);
    isEquals = isEquals && this.versionId.equals(other.versionId);
    isEquals = isEquals && this.versionStage.equals(other.versionStage);
    isEquals = isEquals && this.secretValue.equals(other.secretValue);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(secretId, versionId, versionStage, secretValue);
  }
}
