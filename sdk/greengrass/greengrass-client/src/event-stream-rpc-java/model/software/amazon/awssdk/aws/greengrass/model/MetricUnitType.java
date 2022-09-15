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

public enum MetricUnitType implements EventStreamJsonMessage {
  @SerializedName("BYTES")
  BYTES("BYTES"),

  @SerializedName("BYTES_PER_SECOND")
  BYTES_PER_SECOND("BYTES_PER_SECOND"),

  @SerializedName("COUNT")
  COUNT("COUNT"),

  @SerializedName("COUNT_PER_SECOND")
  COUNT_PER_SECOND("COUNT_PER_SECOND"),

  @SerializedName("MEGABYTES")
  MEGABYTES("MEGABYTES"),

  @SerializedName("SECONDS")
  SECONDS("SECONDS");

  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#MetricUnitType";

  private static final Map<String, MetricUnitType> lookup = new HashMap<String, MetricUnitType>();

  static {
    for (MetricUnitType value:MetricUnitType.values()) {
      lookup.put(value.getValue(), value);
    }
  }

  String value;

  MetricUnitType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  public static MetricUnitType get(String value) {
    return lookup.get(value);
  }
}
