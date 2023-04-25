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

public class RestartComponentResponse implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#RestartComponentResponse";

  public static final RestartComponentResponse VOID;

  static {
    VOID = new RestartComponentResponse() {
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
  private Optional<String> restartStatus;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<String> message;

  public RestartComponentResponse() {
    this.restartStatus = Optional.empty();
    this.message = Optional.empty();
  }

  public RequestStatus getRestartStatus() {
    if (restartStatus.isPresent()) {
      return RequestStatus.get(restartStatus.get());
    }
    return null;
  }

  /**
   * The status of the restart request.
   */
  public String getRestartStatusAsString() {
    if (restartStatus.isPresent()) {
      return restartStatus.get();
    }
    return null;
  }

  /**
   * The status of the restart request.
   */
  public void setRestartStatus(final String restartStatus) {
    this.restartStatus = Optional.ofNullable(restartStatus);
  }

  /**
   * The status of the restart request.
   */
  public RestartComponentResponse withRestartStatus(final String restartStatus) {
    setRestartStatus(restartStatus);
    return this;
  }

  /**
   * The status of the restart request.
   */
  public void setRestartStatus(final RequestStatus restartStatus) {
    this.restartStatus = Optional.ofNullable(restartStatus.getValue());
  }

  /**
   * The status of the restart request.
   */
  public RestartComponentResponse withRestartStatus(final RequestStatus restartStatus) {
    setRestartStatus(restartStatus);
    return this;
  }

  /**
   * A message about why the component failed to restart, if the request failed.
   */
  public String getMessage() {
    if (message.isPresent()) {
      return message.get();
    }
    return null;
  }

  /**
   * A message about why the component failed to restart, if the request failed.
   */
  public void setMessage(final String message) {
    this.message = Optional.ofNullable(message);
  }

  /**
   * A message about why the component failed to restart, if the request failed.
   */
  public RestartComponentResponse withMessage(final String message) {
    setMessage(message);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof RestartComponentResponse)) return false;
    if (this == rhs) return true;
    final RestartComponentResponse other = (RestartComponentResponse)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.restartStatus.equals(other.restartStatus);
    isEquals = isEquals && this.message.equals(other.message);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(restartStatus, message);
  }
}
