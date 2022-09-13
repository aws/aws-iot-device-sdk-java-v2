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

public class ValidateAuthorizationTokenResponse implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#ValidateAuthorizationTokenResponse";

  public static final ValidateAuthorizationTokenResponse VOID;

  static {
    VOID = new ValidateAuthorizationTokenResponse() {
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
  private Optional<Boolean> isValid;

  public ValidateAuthorizationTokenResponse() {
    this.isValid = Optional.empty();
  }

  public Boolean isIsValid() {
    if (isValid.isPresent()) {
      return isValid.get();
    }
    return null;
  }

  public void setIsValid(final Boolean isValid) {
    this.isValid = Optional.ofNullable(isValid);
  }

  public ValidateAuthorizationTokenResponse withIsValid(final Boolean isValid) {
    setIsValid(isValid);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof ValidateAuthorizationTokenResponse)) return false;
    if (this == rhs) return true;
    final ValidateAuthorizationTokenResponse other = (ValidateAuthorizationTokenResponse)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.isValid.equals(other.isValid);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(isValid);
  }
}
