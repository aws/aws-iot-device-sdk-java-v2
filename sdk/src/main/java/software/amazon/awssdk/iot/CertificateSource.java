/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.iot;

/**
 * Certificate source identifiers for metrics feature I.
 * Each value corresponds to a specific authentication method used by MQTT connection.
 * The single-character value is what gets encoded into the metrics string.
 */
public enum CertificateSource {
    /** Client certificate and private key provided as file paths or in-memory bytes.*/
    CERTIFICATE_FILES("A"),

    /** Private key stored in a PKCS#11 compatible hardware security module. */
    PKCS11("B"),

    /** Certificate retrieved from the windows system certificate source. */
    WINDOWS_CERT_STORE("C"),

    /** Certificate and private key loaded from a Java keyStore. */
    JAVA_KEYSTORE("D"),

    /** Certificate and private key bundled in a PKCS#12 file. */
    PKCS12_FILE("E");

    private final String value;

    CertificateSource(String value) {
        this.value = value;
    }

    /**
     * @return the single-character identifier encoded into the metrics string.
     */
    public String getValue() {
        return value;
    }
}
