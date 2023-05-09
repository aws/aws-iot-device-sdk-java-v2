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

public class AuthorizeClientDeviceActionResponse implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#AuthorizeClientDeviceActionResponse";

  public static final AuthorizeClientDeviceActionResponse VOID;

  static {
    VOID = new AuthorizeClientDeviceActionResponse() {
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
  private Optional<Boolean> isAuthorized;

  public AuthorizeClientDeviceActionResponse() {
    this.isAuthorized = Optional.empty();
  }

  /**
   * Whether the client device is authorized to perform the operation on the resource.
   */
  public Boolean isIsAuthorized() {
    if (isAuthorized.isPresent()) {
      return isAuthorized.get();
    }
    return null;
  }

  /**
   * Whether the client device is authorized to perform the operation on the resource.
   */
  public void setIsAuthorized(final Boolean isAuthorized) {
    this.isAuthorized = Optional.ofNullable(isAuthorized);
  }

  /**
   * Whether the client device is authorized to perform the operation on the resource.
   */
  public AuthorizeClientDeviceActionResponse withIsAuthorized(final Boolean isAuthorized) {
    setIsAuthorized(isAuthorized);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof AuthorizeClientDeviceActionResponse)) return false;
    if (this == rhs) return true;
    final AuthorizeClientDeviceActionResponse other = (AuthorizeClientDeviceActionResponse)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.isAuthorized.equals(other.isAuthorized);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(isAuthorized);
  }
}
