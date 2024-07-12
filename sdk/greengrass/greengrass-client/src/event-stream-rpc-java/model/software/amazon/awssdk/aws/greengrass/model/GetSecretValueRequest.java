/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.aws.greengrass.model;

import com.google.gson.annotations.Expose;
import java.lang.Boolean;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class GetSecretValueRequest implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#GetSecretValueRequest";

  public static final GetSecretValueRequest VOID;

  static {
    VOID = new GetSecretValueRequest() {
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
  private Optional<String> versionStage;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<Boolean> refresh;

  public GetSecretValueRequest() {
    this.secretId = Optional.empty();
    this.versionId = Optional.empty();
    this.versionStage = Optional.empty();
    this.refresh = Optional.empty();
  }

  /**
   * The name of the secret to get. You can specify either the Amazon Resource Name (ARN) or the friendly name of the secret.
   */
  public String getSecretId() {
    if (secretId.isPresent()) {
      return secretId.get();
    }
    return null;
  }

  /**
   * The name of the secret to get. You can specify either the Amazon Resource Name (ARN) or the friendly name of the secret.
   */
  public void setSecretId(final String secretId) {
    this.secretId = Optional.ofNullable(secretId);
  }

  /**
   * The name of the secret to get. You can specify either the Amazon Resource Name (ARN) or the friendly name of the secret.
   */
  public GetSecretValueRequest withSecretId(final String secretId) {
    setSecretId(secretId);
    return this;
  }

  /**
   * (Optional) The ID of the version to get. If you don't specify versionId or versionStage, this operation defaults to the version with the AWSCURRENT label.
   */
  public String getVersionId() {
    if (versionId.isPresent()) {
      return versionId.get();
    }
    return null;
  }

  /**
   * (Optional) The ID of the version to get. If you don't specify versionId or versionStage, this operation defaults to the version with the AWSCURRENT label.
   */
  public void setVersionId(final String versionId) {
    this.versionId = Optional.ofNullable(versionId);
  }

  /**
   * (Optional) The ID of the version to get. If you don't specify versionId or versionStage, this operation defaults to the version with the AWSCURRENT label.
   */
  public GetSecretValueRequest withVersionId(final String versionId) {
    setVersionId(versionId);
    return this;
  }

  /**
   * (Optional) The staging label of the version to get. If you don't specify versionId or versionStage, this operation defaults to the version with the AWSCURRENT label.
   */
  public String getVersionStage() {
    if (versionStage.isPresent()) {
      return versionStage.get();
    }
    return null;
  }

  /**
   * (Optional) The staging label of the version to get. If you don't specify versionId or versionStage, this operation defaults to the version with the AWSCURRENT label.
   */
  public void setVersionStage(final String versionStage) {
    this.versionStage = Optional.ofNullable(versionStage);
  }

  /**
   * (Optional) The staging label of the version to get. If you don't specify versionId or versionStage, this operation defaults to the version with the AWSCURRENT label.
   */
  public GetSecretValueRequest withVersionStage(final String versionStage) {
    setVersionStage(versionStage);
    return this;
  }

  /**
   * (Optional) Whether to fetch the latest secret from cloud when the request is handled. Defaults to false.
   */
  public Boolean isRefresh() {
    if (refresh.isPresent()) {
      return refresh.get();
    }
    return null;
  }

  /**
   * (Optional) Whether to fetch the latest secret from cloud when the request is handled. Defaults to false.
   */
  public void setRefresh(final Boolean refresh) {
    this.refresh = Optional.ofNullable(refresh);
  }

  /**
   * (Optional) Whether to fetch the latest secret from cloud when the request is handled. Defaults to false.
   */
  public GetSecretValueRequest withRefresh(final Boolean refresh) {
    setRefresh(refresh);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof GetSecretValueRequest)) return false;
    if (this == rhs) return true;
    final GetSecretValueRequest other = (GetSecretValueRequest)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.secretId.equals(other.secretId);
    isEquals = isEquals && this.versionId.equals(other.versionId);
    isEquals = isEquals && this.versionStage.equals(other.versionStage);
    isEquals = isEquals && this.refresh.equals(other.refresh);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(secretId, versionId, versionStage, refresh);
  }
}
