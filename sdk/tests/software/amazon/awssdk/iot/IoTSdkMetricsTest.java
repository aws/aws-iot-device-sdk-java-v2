/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */
package software.amazon.awssdk.iot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

import software.amazon.awssdk.crt.iot.AWSIoTMetrics;
import software.amazon.awssdk.crt.iot.IoTMetricsMetadata;

import java.util.List;

public class IoTSdkMetricsTest {

    private String findMetadataValue(List<IoTMetricsMetadata> entries, String key) {
        for (IoTMetricsMetadata entry : entries) {
            if (key.equals(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Test
    public void testBuildSdkMetricsReturnsValidObject() {
        AWSIoTMetrics metrics = IoTSdkMetrics.buildSdkMetrics();
        assertNotNull(metrics);
    }

    @Test
    public void testLibraryName() {
        AWSIoTMetrics metrics = IoTSdkMetrics.buildSdkMetrics();
        assertEquals("IoTDeviceSDK/Java", metrics.getLibraryName());
    }

    @Test
    public void testSdkVersionPresent() {
        AWSIoTMetrics metrics = IoTSdkMetrics.buildSdkMetrics();
        String version = findMetadataValue(metrics.getMetadataEntries(), "IoTSDKVersion");
        assertNotNull(version);
        assertFalse(version.isEmpty());
    }
}
