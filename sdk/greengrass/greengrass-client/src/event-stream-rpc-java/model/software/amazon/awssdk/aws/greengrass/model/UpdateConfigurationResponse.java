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

public class UpdateConfigurationResponse implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#UpdateConfigurationResponse";

  public static final UpdateConfigurationResponse VOID;

  static {
    VOID = new UpdateConfigurationResponse() {
      @Override
      public boolean isVoid() {
        return true;
      }
    };
  }

  public UpdateConfigurationResponse() {
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof UpdateConfigurationResponse)) return false;
    if (this == rhs) return true;
    final UpdateConfigurationResponse other = (UpdateConfigurationResponse)rhs;
    boolean isEquals = true;
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash();
  }
}
