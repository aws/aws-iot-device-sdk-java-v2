/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.aws.greengrass.model;

import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Objects;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class SendConfigurationValidityReportResponse implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#SendConfigurationValidityReportResponse";

  public static final SendConfigurationValidityReportResponse VOID;

  static {
    VOID = new SendConfigurationValidityReportResponse() {
      @Override
      public boolean isVoid() {
        return true;
      }
    };
  }

  public SendConfigurationValidityReportResponse() {
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof SendConfigurationValidityReportResponse)) return false;
    if (this == rhs) return true;
    final SendConfigurationValidityReportResponse other = (SendConfigurationValidityReportResponse)rhs;
    boolean isEquals = true;
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash();
  }
}
