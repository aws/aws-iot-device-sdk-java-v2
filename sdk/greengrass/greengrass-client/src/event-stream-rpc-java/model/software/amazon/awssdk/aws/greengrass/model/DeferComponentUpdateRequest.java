/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.aws.greengrass.model;

import com.google.gson.annotations.Expose;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class DeferComponentUpdateRequest implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#DeferComponentUpdateRequest";

  public static final DeferComponentUpdateRequest VOID;

  static {
    VOID = new DeferComponentUpdateRequest() {
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
  private Optional<String> message;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<Long> recheckAfterMs;

  public DeferComponentUpdateRequest() {
    this.deploymentId = Optional.empty();
    this.message = Optional.empty();
    this.recheckAfterMs = Optional.empty();
  }

  /**
   * The ID of the AWS IoT Greengrass deployment to defer.
   */
  public String getDeploymentId() {
    if (deploymentId.isPresent()) {
      return deploymentId.get();
    }
    return null;
  }

  /**
   * The ID of the AWS IoT Greengrass deployment to defer.
   */
  public void setDeploymentId(final String deploymentId) {
    this.deploymentId = Optional.ofNullable(deploymentId);
  }

  /**
   * The ID of the AWS IoT Greengrass deployment to defer.
   */
  public DeferComponentUpdateRequest withDeploymentId(final String deploymentId) {
    setDeploymentId(deploymentId);
    return this;
  }

  /**
   * (Optional) The name of the component for which to defer updates. Defaults to the name of the component that makes the request.
   */
  public String getMessage() {
    if (message.isPresent()) {
      return message.get();
    }
    return null;
  }

  /**
   * (Optional) The name of the component for which to defer updates. Defaults to the name of the component that makes the request.
   */
  public void setMessage(final String message) {
    this.message = Optional.ofNullable(message);
  }

  /**
   * (Optional) The name of the component for which to defer updates. Defaults to the name of the component that makes the request.
   */
  public DeferComponentUpdateRequest withMessage(final String message) {
    setMessage(message);
    return this;
  }

  /**
   * The amount of time in milliseconds for which to defer the update. Greengrass waits for this amount of time and then sends another PreComponentUpdateEvent
   */
  public Long getRecheckAfterMs() {
    if (recheckAfterMs.isPresent()) {
      return recheckAfterMs.get();
    }
    return null;
  }

  /**
   * The amount of time in milliseconds for which to defer the update. Greengrass waits for this amount of time and then sends another PreComponentUpdateEvent
   */
  public void setRecheckAfterMs(final Long recheckAfterMs) {
    this.recheckAfterMs = Optional.ofNullable(recheckAfterMs);
  }

  /**
   * The amount of time in milliseconds for which to defer the update. Greengrass waits for this amount of time and then sends another PreComponentUpdateEvent
   */
  public DeferComponentUpdateRequest withRecheckAfterMs(final Long recheckAfterMs) {
    setRecheckAfterMs(recheckAfterMs);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof DeferComponentUpdateRequest)) return false;
    if (this == rhs) return true;
    final DeferComponentUpdateRequest other = (DeferComponentUpdateRequest)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.deploymentId.equals(other.deploymentId);
    isEquals = isEquals && this.message.equals(other.message);
    isEquals = isEquals && this.recheckAfterMs.equals(other.recheckAfterMs);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(deploymentId, message, recheckAfterMs);
  }
}
