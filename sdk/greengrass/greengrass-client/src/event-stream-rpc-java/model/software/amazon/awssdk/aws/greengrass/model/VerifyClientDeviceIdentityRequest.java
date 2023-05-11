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

public class VerifyClientDeviceIdentityRequest implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#VerifyClientDeviceIdentityRequest";

  public static final VerifyClientDeviceIdentityRequest VOID;

  static {
    VOID = new VerifyClientDeviceIdentityRequest() {
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
  private Optional<ClientDeviceCredential> credential;

  public VerifyClientDeviceIdentityRequest() {
    this.credential = Optional.empty();
  }

  /**
   * The client device's credentials.
   */
  public ClientDeviceCredential getCredential() {
    if (credential.isPresent()) {
      return credential.get();
    }
    return null;
  }

  /**
   * The client device's credentials.
   */
  public void setCredential(final ClientDeviceCredential credential) {
    this.credential = Optional.ofNullable(credential);
  }

  /**
   * The client device's credentials.
   */
  public VerifyClientDeviceIdentityRequest withCredential(final ClientDeviceCredential credential) {
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
    if (!(rhs instanceof VerifyClientDeviceIdentityRequest)) return false;
    if (this == rhs) return true;
    final VerifyClientDeviceIdentityRequest other = (VerifyClientDeviceIdentityRequest)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.credential.equals(other.credential);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(credential);
  }
}
