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

public class LocalDeployment implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#LocalDeployment";

  public static final LocalDeployment VOID;

  static {
    VOID = new LocalDeployment() {
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

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<String> status;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<String> createdOn;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<DeploymentStatusDetails> deploymentStatusDetails;

  public LocalDeployment() {
    this.deploymentId = Optional.empty();
    this.status = Optional.empty();
    this.createdOn = Optional.empty();
    this.deploymentStatusDetails = Optional.empty();
  }

  /**
   * The ID of the local deployment.
   */
  public String getDeploymentId() {
    if (deploymentId.isPresent()) {
      return deploymentId.get();
    }
    return null;
  }

  /**
   * The ID of the local deployment.
   */
  public void setDeploymentId(final String deploymentId) {
    this.deploymentId = Optional.ofNullable(deploymentId);
  }

  /**
   * The ID of the local deployment.
   */
  public LocalDeployment withDeploymentId(final String deploymentId) {
    setDeploymentId(deploymentId);
    return this;
  }

  public DeploymentStatus getStatus() {
    if (status.isPresent()) {
      return DeploymentStatus.get(status.get());
    }
    return null;
  }

  /**
   * The status of the local deployment.
   */
  public String getStatusAsString() {
    if (status.isPresent()) {
      return status.get();
    }
    return null;
  }

  /**
   * The status of the local deployment.
   */
  public void setStatus(final String status) {
    this.status = Optional.ofNullable(status);
  }

  /**
   * The status of the local deployment.
   */
  public LocalDeployment withStatus(final String status) {
    setStatus(status);
    return this;
  }

  /**
   * The status of the local deployment.
   */
  public void setStatus(final DeploymentStatus status) {
    this.status = Optional.ofNullable(status.getValue());
  }

  /**
   * The status of the local deployment.
   */
  public LocalDeployment withStatus(final DeploymentStatus status) {
    setStatus(status);
    return this;
  }

  /**
   * (Optional) The timestamp at which the local deployment was created in MM/dd/yyyy hh:mm:ss format
   */
  public String getCreatedOn() {
    if (createdOn.isPresent()) {
      return createdOn.get();
    }
    return null;
  }

  /**
   * (Optional) The timestamp at which the local deployment was created in MM/dd/yyyy hh:mm:ss format
   */
  public void setCreatedOn(final String createdOn) {
    this.createdOn = Optional.ofNullable(createdOn);
  }

  /**
   * (Optional) The timestamp at which the local deployment was created in MM/dd/yyyy hh:mm:ss format
   */
  public LocalDeployment withCreatedOn(final String createdOn) {
    setCreatedOn(createdOn);
    return this;
  }

  /**
   * (Optional) The status details of the local deployment.
   */
  public DeploymentStatusDetails getDeploymentStatusDetails() {
    if (deploymentStatusDetails.isPresent()) {
      return deploymentStatusDetails.get();
    }
    return null;
  }

  /**
   * (Optional) The status details of the local deployment.
   */
  public void setDeploymentStatusDetails(final DeploymentStatusDetails deploymentStatusDetails) {
    this.deploymentStatusDetails = Optional.ofNullable(deploymentStatusDetails);
  }

  /**
   * (Optional) The status details of the local deployment.
   */
  public LocalDeployment withDeploymentStatusDetails(
      final DeploymentStatusDetails deploymentStatusDetails) {
    setDeploymentStatusDetails(deploymentStatusDetails);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof LocalDeployment)) return false;
    if (this == rhs) return true;
    final LocalDeployment other = (LocalDeployment)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.deploymentId.equals(other.deploymentId);
    isEquals = isEquals && this.status.equals(other.status);
    isEquals = isEquals && this.createdOn.equals(other.createdOn);
    isEquals = isEquals && this.deploymentStatusDetails.equals(other.deploymentStatusDetails);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(deploymentId, status, createdOn, deploymentStatusDetails);
  }
}
