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

public class GetClientDeviceAuthTokenResponse implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#GetClientDeviceAuthTokenResponse";

  public static final GetClientDeviceAuthTokenResponse VOID;

  static {
    VOID = new GetClientDeviceAuthTokenResponse() {
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
  private Optional<String> clientDeviceAuthToken;

  public GetClientDeviceAuthTokenResponse() {
    this.clientDeviceAuthToken = Optional.empty();
  }

  /**
   * The session token for the client device. You can use this session token in subsequent requests to authorize this client device's actions.
   */
  public String getClientDeviceAuthToken() {
    if (clientDeviceAuthToken.isPresent()) {
      return clientDeviceAuthToken.get();
    }
    return null;
  }

  /**
   * The session token for the client device. You can use this session token in subsequent requests to authorize this client device's actions.
   */
  public void setClientDeviceAuthToken(final String clientDeviceAuthToken) {
    this.clientDeviceAuthToken = Optional.ofNullable(clientDeviceAuthToken);
  }

  /**
   * The session token for the client device. You can use this session token in subsequent requests to authorize this client device's actions.
   */
  public GetClientDeviceAuthTokenResponse withClientDeviceAuthToken(
      final String clientDeviceAuthToken) {
    setClientDeviceAuthToken(clientDeviceAuthToken);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof GetClientDeviceAuthTokenResponse)) return false;
    if (this == rhs) return true;
    final GetClientDeviceAuthTokenResponse other = (GetClientDeviceAuthTokenResponse)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.clientDeviceAuthToken.equals(other.clientDeviceAuthToken);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(clientDeviceAuthToken);
  }
}
