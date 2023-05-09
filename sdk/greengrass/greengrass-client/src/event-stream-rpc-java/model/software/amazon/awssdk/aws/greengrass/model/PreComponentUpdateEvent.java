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

public class PreComponentUpdateEvent implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#PreComponentUpdateEvent";

  public static final PreComponentUpdateEvent VOID;

  static {
    VOID = new PreComponentUpdateEvent() {
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
  private Optional<Boolean> isGgcRestarting;

  public PreComponentUpdateEvent() {
    this.deploymentId = Optional.empty();
    this.isGgcRestarting = Optional.empty();
  }

  /**
   * The ID of the AWS IoT Greengrass deployment that updates the component.
   */
  public String getDeploymentId() {
    if (deploymentId.isPresent()) {
      return deploymentId.get();
    }
    return null;
  }

  /**
   * The ID of the AWS IoT Greengrass deployment that updates the component.
   */
  public void setDeploymentId(final String deploymentId) {
    this.deploymentId = Optional.ofNullable(deploymentId);
  }

  /**
   * The ID of the AWS IoT Greengrass deployment that updates the component.
   */
  public PreComponentUpdateEvent withDeploymentId(final String deploymentId) {
    setDeploymentId(deploymentId);
    return this;
  }

  /**
   * Whether or not Greengrass needs to restart to apply the update.
   */
  public Boolean isIsGgcRestarting() {
    if (isGgcRestarting.isPresent()) {
      return isGgcRestarting.get();
    }
    return null;
  }

  /**
   * Whether or not Greengrass needs to restart to apply the update.
   */
  public void setIsGgcRestarting(final Boolean isGgcRestarting) {
    this.isGgcRestarting = Optional.ofNullable(isGgcRestarting);
  }

  /**
   * Whether or not Greengrass needs to restart to apply the update.
   */
  public PreComponentUpdateEvent withIsGgcRestarting(final Boolean isGgcRestarting) {
    setIsGgcRestarting(isGgcRestarting);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof PreComponentUpdateEvent)) return false;
    if (this == rhs) return true;
    final PreComponentUpdateEvent other = (PreComponentUpdateEvent)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.deploymentId.equals(other.deploymentId);
    isEquals = isEquals && this.isGgcRestarting.equals(other.isGgcRestarting);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(deploymentId, isGgcRestarting);
  }
}
