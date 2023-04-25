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

public class StopComponentResponse implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#StopComponentResponse";

  public static final StopComponentResponse VOID;

  static {
    VOID = new StopComponentResponse() {
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
  private Optional<String> stopStatus;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<String> message;

  public StopComponentResponse() {
    this.stopStatus = Optional.empty();
    this.message = Optional.empty();
  }

  public RequestStatus getStopStatus() {
    if (stopStatus.isPresent()) {
      return RequestStatus.get(stopStatus.get());
    }
    return null;
  }

  /**
   * The status of the stop request.
   */
  public String getStopStatusAsString() {
    if (stopStatus.isPresent()) {
      return stopStatus.get();
    }
    return null;
  }

  /**
   * The status of the stop request.
   */
  public void setStopStatus(final String stopStatus) {
    this.stopStatus = Optional.ofNullable(stopStatus);
  }

  /**
   * The status of the stop request.
   */
  public StopComponentResponse withStopStatus(final String stopStatus) {
    setStopStatus(stopStatus);
    return this;
  }

  /**
   * The status of the stop request.
   */
  public void setStopStatus(final RequestStatus stopStatus) {
    this.stopStatus = Optional.ofNullable(stopStatus.getValue());
  }

  /**
   * The status of the stop request.
   */
  public StopComponentResponse withStopStatus(final RequestStatus stopStatus) {
    setStopStatus(stopStatus);
    return this;
  }

  /**
   * A message about why the component failed to stop, if the request failed.
   */
  public String getMessage() {
    if (message.isPresent()) {
      return message.get();
    }
    return null;
  }

  /**
   * A message about why the component failed to stop, if the request failed.
   */
  public void setMessage(final String message) {
    this.message = Optional.ofNullable(message);
  }

  /**
   * A message about why the component failed to stop, if the request failed.
   */
  public StopComponentResponse withMessage(final String message) {
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
    if (!(rhs instanceof StopComponentResponse)) return false;
    if (this == rhs) return true;
    final StopComponentResponse other = (StopComponentResponse)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.stopStatus.equals(other.stopStatus);
    isEquals = isEquals && this.message.equals(other.message);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(stopStatus, message);
  }
}
