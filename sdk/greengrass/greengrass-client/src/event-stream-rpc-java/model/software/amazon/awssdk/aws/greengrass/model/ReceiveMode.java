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

public enum ReceiveMode implements EventStreamJsonMessage {
  @SerializedName("RECEIVE_ALL_MESSAGES")
  RECEIVE_ALL_MESSAGES("RECEIVE_ALL_MESSAGES"),

  @SerializedName("RECEIVE_MESSAGES_FROM_OTHERS")
  RECEIVE_MESSAGES_FROM_OTHERS("RECEIVE_MESSAGES_FROM_OTHERS");

  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#ReceiveMode";

  private static final Map<String, ReceiveMode> lookup = new HashMap<String, ReceiveMode>();

  static {
    for (ReceiveMode value:ReceiveMode.values()) {
      lookup.put(value.getValue(), value);
    }
  }

  String value;

  ReceiveMode(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  public static ReceiveMode get(String value) {
    return lookup.get(value);
  }
}
