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
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class PutComponentMetricRequest implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#PutComponentMetricRequest";

  public static final PutComponentMetricRequest VOID;

  static {
    VOID = new PutComponentMetricRequest() {
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
  private Optional<List<Metric>> metrics;

  public PutComponentMetricRequest() {
    this.metrics = Optional.empty();
  }

  public List<Metric> getMetrics() {
    if (metrics.isPresent()) {
      return metrics.get();
    }
    return null;
  }

  public void setMetrics(final List<Metric> metrics) {
    this.metrics = Optional.ofNullable(metrics);
  }

  public PutComponentMetricRequest withMetrics(final List<Metric> metrics) {
    setMetrics(metrics);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof PutComponentMetricRequest)) return false;
    if (this == rhs) return true;
    final PutComponentMetricRequest other = (PutComponentMetricRequest)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.metrics.equals(other.metrics);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(metrics);
  }
}
