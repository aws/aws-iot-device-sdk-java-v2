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

public class DeploymentStatusDetails implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#DeploymentStatusDetails";

  public static final DeploymentStatusDetails VOID;

  static {
    VOID = new DeploymentStatusDetails() {
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
  private Optional<String> detailedDeploymentStatus;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<List<String>> deploymentErrorStack;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<List<String>> deploymentErrorTypes;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<String> deploymentFailureCause;

  public DeploymentStatusDetails() {
    this.detailedDeploymentStatus = Optional.empty();
    this.deploymentErrorStack = Optional.empty();
    this.deploymentErrorTypes = Optional.empty();
    this.deploymentFailureCause = Optional.empty();
  }

  public DetailedDeploymentStatus getDetailedDeploymentStatus() {
    if (detailedDeploymentStatus.isPresent()) {
      return DetailedDeploymentStatus.get(detailedDeploymentStatus.get());
    }
    return null;
  }

  /**
   * The detailed deployment status of the local deployment.
   */
  public String getDetailedDeploymentStatusAsString() {
    if (detailedDeploymentStatus.isPresent()) {
      return detailedDeploymentStatus.get();
    }
    return null;
  }

  /**
   * The detailed deployment status of the local deployment.
   */
  public void setDetailedDeploymentStatus(final String detailedDeploymentStatus) {
    this.detailedDeploymentStatus = Optional.ofNullable(detailedDeploymentStatus);
  }

  /**
   * The detailed deployment status of the local deployment.
   */
  public DeploymentStatusDetails withDetailedDeploymentStatus(
      final String detailedDeploymentStatus) {
    setDetailedDeploymentStatus(detailedDeploymentStatus);
    return this;
  }

  /**
   * The detailed deployment status of the local deployment.
   */
  public void setDetailedDeploymentStatus(final DetailedDeploymentStatus detailedDeploymentStatus) {
    this.detailedDeploymentStatus = Optional.ofNullable(detailedDeploymentStatus.getValue());
  }

  /**
   * The detailed deployment status of the local deployment.
   */
  public DeploymentStatusDetails withDetailedDeploymentStatus(
      final DetailedDeploymentStatus detailedDeploymentStatus) {
    setDetailedDeploymentStatus(detailedDeploymentStatus);
    return this;
  }

  /**
   * (Optional) The list of local deployment errors
   */
  public List<String> getDeploymentErrorStack() {
    if (deploymentErrorStack.isPresent()) {
      return deploymentErrorStack.get();
    }
    return null;
  }

  /**
   * (Optional) The list of local deployment errors
   */
  public void setDeploymentErrorStack(final List<String> deploymentErrorStack) {
    this.deploymentErrorStack = Optional.ofNullable(deploymentErrorStack);
  }

  /**
   * (Optional) The list of local deployment errors
   */
  public DeploymentStatusDetails withDeploymentErrorStack(final List<String> deploymentErrorStack) {
    setDeploymentErrorStack(deploymentErrorStack);
    return this;
  }

  /**
   * (Optional) The list of local deployment error types
   */
  public List<String> getDeploymentErrorTypes() {
    if (deploymentErrorTypes.isPresent()) {
      return deploymentErrorTypes.get();
    }
    return null;
  }

  /**
   * (Optional) The list of local deployment error types
   */
  public void setDeploymentErrorTypes(final List<String> deploymentErrorTypes) {
    this.deploymentErrorTypes = Optional.ofNullable(deploymentErrorTypes);
  }

  /**
   * (Optional) The list of local deployment error types
   */
  public DeploymentStatusDetails withDeploymentErrorTypes(final List<String> deploymentErrorTypes) {
    setDeploymentErrorTypes(deploymentErrorTypes);
    return this;
  }

  /**
   * (Optional) The cause of local deployment failure
   */
  public String getDeploymentFailureCause() {
    if (deploymentFailureCause.isPresent()) {
      return deploymentFailureCause.get();
    }
    return null;
  }

  /**
   * (Optional) The cause of local deployment failure
   */
  public void setDeploymentFailureCause(final String deploymentFailureCause) {
    this.deploymentFailureCause = Optional.ofNullable(deploymentFailureCause);
  }

  /**
   * (Optional) The cause of local deployment failure
   */
  public DeploymentStatusDetails withDeploymentFailureCause(final String deploymentFailureCause) {
    setDeploymentFailureCause(deploymentFailureCause);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof DeploymentStatusDetails)) return false;
    if (this == rhs) return true;
    final DeploymentStatusDetails other = (DeploymentStatusDetails)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.detailedDeploymentStatus.equals(other.detailedDeploymentStatus);
    isEquals = isEquals && this.deploymentErrorStack.equals(other.deploymentErrorStack);
    isEquals = isEquals && this.deploymentErrorTypes.equals(other.deploymentErrorTypes);
    isEquals = isEquals && this.deploymentFailureCause.equals(other.deploymentFailureCause);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(detailedDeploymentStatus, deploymentErrorStack, deploymentErrorTypes, deploymentFailureCause);
  }
}
