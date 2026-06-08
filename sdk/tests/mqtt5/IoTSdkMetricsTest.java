/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import software.amazon.awssdk.crt.internal.IoTDeviceSDKMetrics;
import software.amazon.awssdk.crt.internal.IoTMetricsMetadata;
import software.amazon.awssdk.iot.CertificateSource;
import software.amazon.awssdk.iot.IoTSdkMetrics;

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

    // ======================== Certificate Source Encoding ========================

    @Test
    public void testCertificateSourceValues() {
        assertEquals("A", CertificateSource.CERTIFICATE_FILES.getValue());
        assertEquals("B", CertificateSource.PKCS11.getValue());
        assertEquals("C", CertificateSource.WINDOWS_CERT_STORE.getValue());
        assertEquals("D", CertificateSource.JAVA_KEYSTORE.getValue());
        assertEquals("E", CertificateSource.PKCS12_FILE.getValue());
    }

    // ======================== buildSdkMetrics with certificate sources ========================

    @Test
    public void testBuildSdkMetricsWithCertFiles() {
        IoTDeviceSDKMetrics metrics = IoTSdkMetrics.buildSdkMetrics(CertificateSource.CERTIFICATE_FILES);

        assertEquals("IoTDeviceSDK/Java", metrics.getLibraryName());
        assertEquals("I/A", findMetadataValue(metrics.getMetadataEntries(), "IoTSDKFeature"));
        assertEquals("1", findMetadataValue(metrics.getMetadataEntries(), "IoTSDKMetricsVersion"));
        assertNotNull(findMetadataValue(metrics.getMetadataEntries(), "IoTSDKVersion"));
    }

    @Test
    public void testBuildSdkMetricsWithPkcs11() {
        IoTDeviceSDKMetrics metrics = IoTSdkMetrics.buildSdkMetrics(CertificateSource.PKCS11);
        assertEquals("I/B", findMetadataValue(metrics.getMetadataEntries(), "IoTSDKFeature"));
    }

    @Test
    public void testBuildSdkMetricsWithWindowsCertStore() {
        IoTDeviceSDKMetrics metrics = IoTSdkMetrics.buildSdkMetrics(CertificateSource.WINDOWS_CERT_STORE);
        assertEquals("I/C", findMetadataValue(metrics.getMetadataEntries(), "IoTSDKFeature"));
    }

    @Test
    public void testBuildSdkMetricsWithJavaKeystore() {
        IoTDeviceSDKMetrics metrics = IoTSdkMetrics.buildSdkMetrics(CertificateSource.JAVA_KEYSTORE);
        assertEquals("I/D", findMetadataValue(metrics.getMetadataEntries(), "IoTSDKFeature"));
    }

    @Test
    public void testBuildSdkMetricsWithPkcs12() {
        IoTDeviceSDKMetrics metrics = IoTSdkMetrics.buildSdkMetrics(CertificateSource.PKCS12_FILE);
        assertEquals("I/E", findMetadataValue(metrics.getMetadataEntries(), "IoTSDKFeature"));
    }

    // ======================== buildSdkMetrics with null (websocket/custom auth) ========================

    @Test
    public void testBuildSdkMetricsWithNullCertSource() {
        IoTDeviceSDKMetrics metrics = IoTSdkMetrics.buildSdkMetrics(null);

        assertEquals("IoTDeviceSDK/Java", metrics.getLibraryName());
        assertNotNull(findMetadataValue(metrics.getMetadataEntries(), "IoTSDKVersion"));
        // No feature or version when no certificate source
        assertEquals(null, findMetadataValue(metrics.getMetadataEntries(), "IoTSDKFeature"));
        assertEquals(null, findMetadataValue(metrics.getMetadataEntries(), "IoTSDKMetricsVersion"));
    }

    // ======================== SDK version always present ========================

    @Test
    public void testSdkVersionAlwaysPresent() {
        IoTDeviceSDKMetrics withCert = IoTSdkMetrics.buildSdkMetrics(CertificateSource.CERTIFICATE_FILES);
        IoTDeviceSDKMetrics withoutCert = IoTSdkMetrics.buildSdkMetrics(null);

        String v1 = findMetadataValue(withCert.getMetadataEntries(), "IoTSDKVersion");
        String v2 = findMetadataValue(withoutCert.getMetadataEntries(), "IoTSDKVersion");

        assertNotNull(v1);
        assertNotNull(v2);
        assertFalse(v1.isEmpty());
        assertFalse(v2.isEmpty());
    }
}
