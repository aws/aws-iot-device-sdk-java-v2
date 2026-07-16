/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.iot;

import java.util.ArrayList;
import java.util.List;

import software.amazon.awssdk.crt.iot.AWSIoTMetrics;
import software.amazon.awssdk.crt.iot.IoTMetricsMetadata;

/**
 * Provides SDK-level metadata (version info) to the CRT layer.
 * The CRT handles all feature detection (certificate source, TLS settings, etc.)
 * and embeds the combined metrics in the MQTT CONNECT packet username field.
 */
class IoTSdkMetrics {

    /**
     * The current version of the IoT SDK metrics format.
     * This must match the version expected by the CRT layer.
     */
    private static final String IOT_SDK_METRICS_VERSION = "1";

    /**
     * Returns the installed SDK version string.
     *
     * <p>Attempts to read the specification version from the package manifest first,
     * falling back to the implementation version. Returns {@code "dev"} if the package
     * metadata is unavailable (e.g. when running from a source checkout without installing).
     *
     * @return a version string such as {@code "1.32.0"} or {@code "dev"}
     */
    private static String getSdkVersion() {
        try {
            Package pkg = IoTSdkMetrics.class.getPackage();
            String version = pkg.getSpecificationVersion();
            if (version == null) {
                version = pkg.getImplementationVersion();
            }
            if (version == null) {
                version = "dev";
            }
            return version;
        } catch (Exception e) {
            return "dev";
        }
    }

    /**
     * Builds the SDK-level {@link AWSIoTMetrics} payload that is passed down to the CRT layer.
     *
     * <p>The returned object carries SDK identity and the metrics format version via two metadata entries:
     * <ul>
     *   <li>{@code IoTSDKVersion} — the installed SDK package version, used to identify the
     *       SDK release on the server side.</li>
     *   <li>{@code IoTSDKMetricsVersion} — the metrics format version this SDK supports.</li>
     * </ul>
     *
     * <p>The CRT layer is responsible for detecting connection-level features (protocol version,
     * certificate source, TLS settings, proxy type, etc.) and appending them to the metadata
     * before embedding the result in the MQTT CONNECT packet username field.
     *
     * @return a populated {@link AWSIoTMetrics} object ready to attach to an
     *         MQTT5 client or MQTT3 connection configuration
     */
    static AWSIoTMetrics buildSdkMetrics() {
        List<IoTMetricsMetadata> metadata = new ArrayList<>();
        metadata.add(new IoTMetricsMetadata("IoTSDKVersion", getSdkVersion()));
        metadata.add(new IoTMetricsMetadata("IoTSDKMetricsVersion", IOT_SDK_METRICS_VERSION));
        AWSIoTMetrics metrics = new AWSIoTMetrics();
        metrics.setMetadataEntries(metadata);
        return metrics;
    }

}
