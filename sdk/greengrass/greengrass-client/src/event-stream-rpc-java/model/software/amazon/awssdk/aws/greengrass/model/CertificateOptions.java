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

public class CertificateOptions implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#CertificateOptions";

  public static final CertificateOptions VOID;

  static {
    VOID = new CertificateOptions() {
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
  private Optional<String> certificateType;

  public CertificateOptions() {
    this.certificateType = Optional.empty();
  }

  public CertificateType getCertificateType() {
    if (certificateType.isPresent()) {
      return CertificateType.get(certificateType.get());
    }
    return null;
  }

  /**
   * The types of certificate updates to subscribe to.
   */
  public String getCertificateTypeAsString() {
    if (certificateType.isPresent()) {
      return certificateType.get();
    }
    return null;
  }

  /**
   * The types of certificate updates to subscribe to.
   */
  public void setCertificateType(final String certificateType) {
    this.certificateType = Optional.ofNullable(certificateType);
  }

  /**
   * The types of certificate updates to subscribe to.
   */
  public CertificateOptions withCertificateType(final String certificateType) {
    setCertificateType(certificateType);
    return this;
  }

  /**
   * The types of certificate updates to subscribe to.
   */
  public void setCertificateType(final CertificateType certificateType) {
    this.certificateType = Optional.ofNullable(certificateType.getValue());
  }

  /**
   * The types of certificate updates to subscribe to.
   */
  public CertificateOptions withCertificateType(final CertificateType certificateType) {
    setCertificateType(certificateType);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof CertificateOptions)) return false;
    if (this == rhs) return true;
    final CertificateOptions other = (CertificateOptions)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.certificateType.equals(other.certificateType);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(certificateType);
  }
}
