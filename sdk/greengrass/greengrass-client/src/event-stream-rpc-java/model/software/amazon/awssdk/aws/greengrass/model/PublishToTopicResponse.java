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

public class PublishToTopicResponse implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#PublishToTopicResponse";

  public static final PublishToTopicResponse VOID;

  static {
    VOID = new PublishToTopicResponse() {
      @Override
      public boolean isVoid() {
        return true;
      }
    };
  }

  public PublishToTopicResponse() {
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof PublishToTopicResponse)) return false;
    if (this == rhs) return true;
    final PublishToTopicResponse other = (PublishToTopicResponse)rhs;
    boolean isEquals = true;
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash();
  }
}
