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
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class SendConfigurationValidityReportRequest implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#SendConfigurationValidityReportRequest";

  public static final SendConfigurationValidityReportRequest VOID;

  static {
    VOID = new SendConfigurationValidityReportRequest() {
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
  private Optional<ConfigurationValidityReport> configurationValidityReport;

  public SendConfigurationValidityReportRequest() {
    this.configurationValidityReport = Optional.empty();
  }

  /**
   * The report that tells Greengrass whether or not the configuration update is valid.
   */
  public ConfigurationValidityReport getConfigurationValidityReport() {
    if (configurationValidityReport.isPresent()) {
      return configurationValidityReport.get();
    }
    return null;
  }

  /**
   * The report that tells Greengrass whether or not the configuration update is valid.
   */
  public void setConfigurationValidityReport(
      final ConfigurationValidityReport configurationValidityReport) {
    this.configurationValidityReport = Optional.ofNullable(configurationValidityReport);
  }

  /**
   * The report that tells Greengrass whether or not the configuration update is valid.
   */
  public SendConfigurationValidityReportRequest withConfigurationValidityReport(
      final ConfigurationValidityReport configurationValidityReport) {
    setConfigurationValidityReport(configurationValidityReport);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof SendConfigurationValidityReportRequest)) return false;
    if (this == rhs) return true;
    final SendConfigurationValidityReportRequest other = (SendConfigurationValidityReportRequest)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.configurationValidityReport.equals(other.configurationValidityReport);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(configurationValidityReport);
  }
}
