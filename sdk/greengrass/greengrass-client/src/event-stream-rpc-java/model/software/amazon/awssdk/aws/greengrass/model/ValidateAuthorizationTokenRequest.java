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

public class ValidateAuthorizationTokenRequest implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#ValidateAuthorizationTokenRequest";

  public static final ValidateAuthorizationTokenRequest VOID;

  static {
    VOID = new ValidateAuthorizationTokenRequest() {
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
  private Optional<String> token;

  public ValidateAuthorizationTokenRequest() {
    this.token = Optional.empty();
  }

  public String getToken() {
    if (token.isPresent()) {
      return token.get();
    }
    return null;
  }

  public void setToken(final String token) {
    this.token = Optional.ofNullable(token);
  }

  public ValidateAuthorizationTokenRequest withToken(final String token) {
    setToken(token);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof ValidateAuthorizationTokenRequest)) return false;
    if (this == rhs) return true;
    final ValidateAuthorizationTokenRequest other = (ValidateAuthorizationTokenRequest)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.token.equals(other.token);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(token);
  }
}
