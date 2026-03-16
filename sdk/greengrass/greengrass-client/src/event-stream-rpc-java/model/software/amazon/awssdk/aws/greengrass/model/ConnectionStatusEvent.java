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

public class ConnectionStatusEvent implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#ConnectionStatusEvent";

  public static final ConnectionStatusEvent VOID;

  static {
    VOID = new ConnectionStatusEvent() {
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
  private Optional<String> status;

  public ConnectionStatusEvent() {
    this.status = Optional.empty();
  }

  public ConnectionStatus getStatus() {
    if (status.isPresent()) {
      return ConnectionStatus.get(status.get());
    }
    return null;
  }

  /**
   * The connection status.
   */
  public String getStatusAsString() {
    if (status.isPresent()) {
      return status.get();
    }
    return null;
  }

  /**
   * The connection status.
   */
  public void setStatus(final String status) {
    this.status = Optional.ofNullable(status);
  }

  /**
   * The connection status.
   */
  public ConnectionStatusEvent withStatus(final String status) {
    setStatus(status);
    return this;
  }

  /**
   * The connection status.
   */
  public void setStatus(final ConnectionStatus status) {
    this.status = Optional.ofNullable(status.getValue());
  }

  /**
   * The connection status.
   */
  public ConnectionStatusEvent withStatus(final ConnectionStatus status) {
    setStatus(status);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof ConnectionStatusEvent)) return false;
    if (this == rhs) return true;
    final ConnectionStatusEvent other = (ConnectionStatusEvent)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.status.equals(other.status);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(status);
  }
}
