/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.iot;

import java.util.ArrayList;
import java.util.List;

import software.amazon.awssdk.crt.internal.IoTDeviceSDKMetrics;
import software.amazon.awssdk.crt.internal.IoTMetricsMetadata;
import software.amazon.awssdk.crt.internal.IoTMetricEncoder;

/**
 * Builds SDK-layer metrics for embedding in the MQTT CONNECT packet username field.
 * Collects SDK-layer feature usage (e.g., CERTIFICATE_SOURCE) and packages it
 * into an IoTDeviceSDKMetrics object for the CRT layer to merge with CRT features.
 */
public class IoTSdkMetrics {

    private static final String CERTIFICATE_SOURCE = "I";

    /**
     * Returns the installed SDK version string.
     */
    private static String getSdkVersion(){
        try{
            Package pkg = IoTSdkMetrics.class.getPackage();
            String version = pkg.getSpecificationVersion();
            if(version == null){
                version = pkg.getImplementationVersion();
            }
            if(version == null){
                version = "dev";
            }
            return version;
        }catch (Exception e){
            return "dev";
        }
    }

    /**
     * Encodes SDK features into "ID/Value" format.
     * @param certificateSource the certificate method in use or null if none
     * @return encoded feature string (e.g., "I/A"), or empty string if no feature.
     */
    private static String encodedFeatureList(CertificateSource certificateSource){
        if(certificateSource!=null){
            return CERTIFICATE_SOURCE + "/" + certificateSource.getValue();
        }
        return "";
    }

    /**
     * Builds an IoTDeviceSDKMetrics instance for CRT layer.
     * Always include IoTSDKVersion. When a certificate source is provided,
     * also includes IoTSDKFeature and IoTSDKMetricsVersion.
     *
     * @param certificateSource the certificate method used, or null for connections
     *                           without client certs (websocket, custom auth)
     * @return metrics object ready to pass to CRT via withMetrics() or setMetrics()
     */
    public static IoTDeviceSDKMetrics buildSdkMetrics(CertificateSource certificateSource) {
        List<IoTMetricsMetadata> metadata = new ArrayList<>();

        metadata.add(new IoTMetricsMetadata("IoTSDKVersion", getSdkVersion()));

        String featureList = encodedFeatureList(certificateSource);
        if (!featureList.isEmpty()) {
            metadata.add(new IoTMetricsMetadata("IoTSDKFeature", featureList));
            metadata.add(new IoTMetricsMetadata("IoTSDKMetricsVersion",
            String.valueOf(IoTMetricEncoder.IOT_SDK_METRICS_FEATURE_VERSION)));
        }
        return new IoTDeviceSDKMetrics("IoTDeviceSDK/Java", metadata);
    }

}
