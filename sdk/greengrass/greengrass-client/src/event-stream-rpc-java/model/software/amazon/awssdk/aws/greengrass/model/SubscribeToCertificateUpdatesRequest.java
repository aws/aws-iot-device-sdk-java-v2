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

public class SubscribeToCertificateUpdatesRequest implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#SubscribeToCertificateUpdatesRequest";

  public static final SubscribeToCertificateUpdatesRequest VOID;

  static {
    VOID = new SubscribeToCertificateUpdatesRequest() {
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
  private Optional<CertificateOptions> certificateOptions;

  public SubscribeToCertificateUpdatesRequest() {
    this.certificateOptions = Optional.empty();
  }

  public CertificateOptions getCertificateOptions() {
    if (certificateOptions.isPresent()) {
      return certificateOptions.get();
    }
    return null;
  }

  public void setCertificateOptions(final CertificateOptions certificateOptions) {
    this.certificateOptions = Optional.ofNullable(certificateOptions);
  }

  public SubscribeToCertificateUpdatesRequest withCertificateOptions(
      final CertificateOptions certificateOptions) {
    setCertificateOptions(certificateOptions);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof SubscribeToCertificateUpdatesRequest)) return false;
    if (this == rhs) return true;
    final SubscribeToCertificateUpdatesRequest other = (SubscribeToCertificateUpdatesRequest)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.certificateOptions.equals(other.certificateOptions);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(certificateOptions);
  }
}
