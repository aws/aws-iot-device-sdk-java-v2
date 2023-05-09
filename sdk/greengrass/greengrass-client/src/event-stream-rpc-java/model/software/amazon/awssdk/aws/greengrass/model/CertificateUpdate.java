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

public class CertificateUpdate implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#CertificateUpdate";

  public static final CertificateUpdate VOID;

  static {
    VOID = new CertificateUpdate() {
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
  private Optional<String> privateKey;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<String> publicKey;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<String> certificate;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<List<String>> caCertificates;

  public CertificateUpdate() {
    this.privateKey = Optional.empty();
    this.publicKey = Optional.empty();
    this.certificate = Optional.empty();
    this.caCertificates = Optional.empty();
  }

  /**
   * The private key in pem format.
   */
  public String getPrivateKey() {
    if (privateKey.isPresent()) {
      return privateKey.get();
    }
    return null;
  }

  /**
   * The private key in pem format.
   */
  public void setPrivateKey(final String privateKey) {
    this.privateKey = Optional.ofNullable(privateKey);
  }

  /**
   * The private key in pem format.
   */
  public CertificateUpdate withPrivateKey(final String privateKey) {
    setPrivateKey(privateKey);
    return this;
  }

  /**
   * The public key in pem format.
   */
  public String getPublicKey() {
    if (publicKey.isPresent()) {
      return publicKey.get();
    }
    return null;
  }

  /**
   * The public key in pem format.
   */
  public void setPublicKey(final String publicKey) {
    this.publicKey = Optional.ofNullable(publicKey);
  }

  /**
   * The public key in pem format.
   */
  public CertificateUpdate withPublicKey(final String publicKey) {
    setPublicKey(publicKey);
    return this;
  }

  /**
   * The certificate in pem format.
   */
  public String getCertificate() {
    if (certificate.isPresent()) {
      return certificate.get();
    }
    return null;
  }

  /**
   * The certificate in pem format.
   */
  public void setCertificate(final String certificate) {
    this.certificate = Optional.ofNullable(certificate);
  }

  /**
   * The certificate in pem format.
   */
  public CertificateUpdate withCertificate(final String certificate) {
    setCertificate(certificate);
    return this;
  }

  /**
   * List of CA certificates in pem format.
   */
  public List<String> getCaCertificates() {
    if (caCertificates.isPresent()) {
      return caCertificates.get();
    }
    return null;
  }

  /**
   * List of CA certificates in pem format.
   */
  public void setCaCertificates(final List<String> caCertificates) {
    this.caCertificates = Optional.ofNullable(caCertificates);
  }

  /**
   * List of CA certificates in pem format.
   */
  public CertificateUpdate withCaCertificates(final List<String> caCertificates) {
    setCaCertificates(caCertificates);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof CertificateUpdate)) return false;
    if (this == rhs) return true;
    final CertificateUpdate other = (CertificateUpdate)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.privateKey.equals(other.privateKey);
    isEquals = isEquals && this.publicKey.equals(other.publicKey);
    isEquals = isEquals && this.certificate.equals(other.certificate);
    isEquals = isEquals && this.caCertificates.equals(other.caCertificates);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(privateKey, publicKey, certificate, caCertificates);
  }
}
