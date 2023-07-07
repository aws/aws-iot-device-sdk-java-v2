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
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class CancelLocalDeploymentRequest implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#CancelLocalDeploymentRequest";

  public static final CancelLocalDeploymentRequest VOID;

  static {
    VOID = new CancelLocalDeploymentRequest() {
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
  private Optional<String> deploymentId;

  public CancelLocalDeploymentRequest() {
    this.deploymentId = Optional.empty();
  }

  /**
   * (Optional) The ID of the local deployment to cancel.
   */
  public String getDeploymentId() {
    if (deploymentId.isPresent()) {
      return deploymentId.get();
    }
    return null;
  }

  /**
   * (Optional) The ID of the local deployment to cancel.
   */
  public void setDeploymentId(final String deploymentId) {
    this.deploymentId = Optional.ofNullable(deploymentId);
  }

  /**
   * (Optional) The ID of the local deployment to cancel.
   */
  public CancelLocalDeploymentRequest withDeploymentId(final String deploymentId) {
    setDeploymentId(deploymentId);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof CancelLocalDeploymentRequest)) return false;
    if (this == rhs) return true;
    final CancelLocalDeploymentRequest other = (CancelLocalDeploymentRequest)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.deploymentId.equals(other.deploymentId);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(deploymentId);
  }
}
