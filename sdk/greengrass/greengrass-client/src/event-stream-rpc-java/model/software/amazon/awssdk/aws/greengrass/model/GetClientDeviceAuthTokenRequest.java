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

public class GetClientDeviceAuthTokenRequest implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#GetClientDeviceAuthTokenRequest";

  public static final GetClientDeviceAuthTokenRequest VOID;

  static {
    VOID = new GetClientDeviceAuthTokenRequest() {
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
  private Optional<CredentialDocument> credential;

  public GetClientDeviceAuthTokenRequest() {
    this.credential = Optional.empty();
  }

  /**
   * The client device's credentials.
   */
  public CredentialDocument getCredential() {
    if (credential.isPresent()) {
      return credential.get();
    }
    return null;
  }

  /**
   * The client device's credentials.
   */
  public void setCredential(final CredentialDocument credential) {
    this.credential = Optional.ofNullable(credential);
  }

  /**
   * The client device's credentials.
   */
  public GetClientDeviceAuthTokenRequest withCredential(final CredentialDocument credential) {
    setCredential(credential);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof GetClientDeviceAuthTokenRequest)) return false;
    if (this == rhs) return true;
    final GetClientDeviceAuthTokenRequest other = (GetClientDeviceAuthTokenRequest)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.credential.equals(other.credential);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(credential);
  }
}
