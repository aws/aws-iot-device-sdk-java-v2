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

public enum PayloadFormat implements EventStreamJsonMessage {
  @SerializedName("0")
  BYTES("0"),

  @SerializedName("1")
  UTF8("1");

  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#PayloadFormat";

  private static final Map<String, PayloadFormat> lookup = new HashMap<String, PayloadFormat>();

  static {
    for (PayloadFormat value:PayloadFormat.values()) {
      lookup.put(value.getValue(), value);
    }
  }

  String value;

  PayloadFormat(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  public static PayloadFormat get(String value) {
    return lookup.get(value);
  }
}
