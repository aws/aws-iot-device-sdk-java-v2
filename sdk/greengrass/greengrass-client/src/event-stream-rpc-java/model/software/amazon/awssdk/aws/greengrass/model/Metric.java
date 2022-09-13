/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.aws.greengrass.model;

import com.google.gson.annotations.Expose;
import java.lang.Double;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class Metric implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#Metric";

  public static final Metric VOID;

  static {
    VOID = new Metric() {
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
  private Optional<String> name;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<String> unit;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<Double> value;

  public Metric() {
    this.name = Optional.empty();
    this.unit = Optional.empty();
    this.value = Optional.empty();
  }

  public String getName() {
    if (name.isPresent()) {
      return name.get();
    }
    return null;
  }

  public void setName(final String name) {
    this.name = Optional.ofNullable(name);
  }

  public Metric withName(final String name) {
    setName(name);
    return this;
  }

  public MetricUnitType getUnit() {
    if (unit.isPresent()) {
      return MetricUnitType.get(unit.get());
    }
    return null;
  }

  public String getUnitAsString() {
    if (unit.isPresent()) {
      return unit.get();
    }
    return null;
  }

  public void setUnit(final String unit) {
    this.unit = Optional.ofNullable(unit);
  }

  public Metric withUnit(final String unit) {
    setUnit(unit);
    return this;
  }

  public void setUnit(final MetricUnitType unit) {
    this.unit = Optional.ofNullable(unit.getValue());
  }

  public Metric withUnit(final MetricUnitType unit) {
    setUnit(unit);
    return this;
  }

  public Double getValue() {
    if (value.isPresent()) {
      return value.get();
    }
    return null;
  }

  public void setValue(final Double value) {
    this.value = Optional.ofNullable(value);
  }

  public Metric withValue(final Double value) {
    setValue(value);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof Metric)) return false;
    if (this == rhs) return true;
    final Metric other = (Metric)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.name.equals(other.name);
    isEquals = isEquals && this.unit.equals(other.unit);
    isEquals = isEquals && this.value.equals(other.value);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, unit, value);
  }
}
