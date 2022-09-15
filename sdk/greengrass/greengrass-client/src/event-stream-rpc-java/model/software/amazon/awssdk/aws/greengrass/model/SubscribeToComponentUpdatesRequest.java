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

public class SubscribeToComponentUpdatesRequest implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#SubscribeToComponentUpdatesRequest";

  public static final SubscribeToComponentUpdatesRequest VOID;

  static {
    VOID = new SubscribeToComponentUpdatesRequest() {
      @Override
      public boolean isVoid() {
        return true;
      }
    };
  }

  public SubscribeToComponentUpdatesRequest() {
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof SubscribeToComponentUpdatesRequest)) return false;
    if (this == rhs) return true;
    final SubscribeToComponentUpdatesRequest other = (SubscribeToComponentUpdatesRequest)rhs;
    boolean isEquals = true;
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash();
  }
}
