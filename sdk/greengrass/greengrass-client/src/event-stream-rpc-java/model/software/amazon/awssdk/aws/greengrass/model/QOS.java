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

public enum QOS implements EventStreamJsonMessage {
  @SerializedName("0")
  AT_MOST_ONCE("0"),

  @SerializedName("1")
  AT_LEAST_ONCE("1");

  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#QOS";

  private static final Map<String, QOS> lookup = new HashMap<String, QOS>();

  static {
    for (QOS value:QOS.values()) {
      lookup.put(value.getValue(), value);
    }
  }

  String value;

  QOS(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  public static QOS get(String value) {
    return lookup.get(value);
  }
}
