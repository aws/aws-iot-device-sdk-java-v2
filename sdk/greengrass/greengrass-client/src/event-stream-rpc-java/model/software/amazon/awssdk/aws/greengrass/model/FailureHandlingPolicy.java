/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.aws.greengrass.model;

import com.google.gson.annotations.SerializedName;
import java.lang.Override;
import java.lang.String;
import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public enum FailureHandlingPolicy implements EventStreamJsonMessage {
  @SerializedName("ROLLBACK")
  ROLLBACK("ROLLBACK"),

  @SerializedName("DO_NOTHING")
  DO_NOTHING("DO_NOTHING");

  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#FailureHandlingPolicy";

  private static final Map<String, FailureHandlingPolicy> lookup = new HashMap<String, FailureHandlingPolicy>();

  static {
    for (FailureHandlingPolicy value:FailureHandlingPolicy.values()) {
      lookup.put(value.getValue(), value);
    }
  }

  String value;

  FailureHandlingPolicy(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  public static FailureHandlingPolicy get(String value) {
    return lookup.get(value);
  }
}
