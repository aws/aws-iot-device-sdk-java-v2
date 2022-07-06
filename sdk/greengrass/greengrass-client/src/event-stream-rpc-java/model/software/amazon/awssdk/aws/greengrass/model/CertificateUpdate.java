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

  public String getPrivateKey() {
    if (privateKey.isPresent()) {
      return privateKey.get();
    }
    return null;
  }

  public void setPrivateKey(final String privateKey) {
    this.privateKey = Optional.ofNullable(privateKey);
  }

  public CertificateUpdate withPrivateKey(final String privateKey) {
    setPrivateKey(privateKey);
    return this;
  }

  public String getPublicKey() {
    if (publicKey.isPresent()) {
      return publicKey.get();
    }
    return null;
  }

  public void setPublicKey(final String publicKey) {
    this.publicKey = Optional.ofNullable(publicKey);
  }

  public CertificateUpdate withPublicKey(final String publicKey) {
    setPublicKey(publicKey);
    return this;
  }

  public String getCertificate() {
    if (certificate.isPresent()) {
      return certificate.get();
    }
    return null;
  }

  public void setCertificate(final String certificate) {
    this.certificate = Optional.ofNullable(certificate);
  }

  public CertificateUpdate withCertificate(final String certificate) {
    setCertificate(certificate);
    return this;
  }

  public List<String> getCaCertificates() {
    if (caCertificates.isPresent()) {
      return caCertificates.get();
    }
    return null;
  }

  public void setCaCertificates(final List<String> caCertificates) {
    this.caCertificates = Optional.ofNullable(caCertificates);
  }

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
