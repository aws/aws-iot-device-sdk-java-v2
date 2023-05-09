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

public class VerifyClientDeviceIdentityResponse implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#VerifyClientDeviceIdentityResponse";

  public static final VerifyClientDeviceIdentityResponse VOID;

  static {
    VOID = new VerifyClientDeviceIdentityResponse() {
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
  private Optional<Boolean> isValidClientDevice;

  public VerifyClientDeviceIdentityResponse() {
    this.isValidClientDevice = Optional.empty();
  }

  /**
   * Whether the client device's identity is valid.
   */
  public Boolean isIsValidClientDevice() {
    if (isValidClientDevice.isPresent()) {
      return isValidClientDevice.get();
    }
    return null;
  }

  /**
   * Whether the client device's identity is valid.
   */
  public void setIsValidClientDevice(final Boolean isValidClientDevice) {
    this.isValidClientDevice = Optional.ofNullable(isValidClientDevice);
  }

  /**
   * Whether the client device's identity is valid.
   */
  public VerifyClientDeviceIdentityResponse withIsValidClientDevice(
      final Boolean isValidClientDevice) {
    setIsValidClientDevice(isValidClientDevice);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof VerifyClientDeviceIdentityResponse)) return false;
    if (this == rhs) return true;
    final VerifyClientDeviceIdentityResponse other = (VerifyClientDeviceIdentityResponse)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.isValidClientDevice.equals(other.isValidClientDevice);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(isValidClientDevice);
  }
}
