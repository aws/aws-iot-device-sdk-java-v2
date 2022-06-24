package software.amazon.awssdk.aws.greengrass.model;

import com.google.gson.annotations.Expose;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class MQTTCredential implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#MQTTCredential";

  public static final MQTTCredential VOID;

  static {
    VOID = new MQTTCredential() {
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
  private Optional<String> clientId;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<String> certificatePem;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<String> username;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<String> password;

  public MQTTCredential() {
    this.clientId = Optional.empty();
    this.certificatePem = Optional.empty();
    this.username = Optional.empty();
    this.password = Optional.empty();
  }

  public String getClientId() {
    if (clientId.isPresent()) {
      return clientId.get();
    }
    return null;
  }

  public void setClientId(final String clientId) {
    this.clientId = Optional.ofNullable(clientId);
  }

  public MQTTCredential withClientId(final String clientId) {
    setClientId(clientId);
    return this;
  }

  public String getCertificatePem() {
    if (certificatePem.isPresent()) {
      return certificatePem.get();
    }
    return null;
  }

  public void setCertificatePem(final String certificatePem) {
    this.certificatePem = Optional.ofNullable(certificatePem);
  }

  public MQTTCredential withCertificatePem(final String certificatePem) {
    setCertificatePem(certificatePem);
    return this;
  }

  public String getUsername() {
    if (username.isPresent()) {
      return username.get();
    }
    return null;
  }

  public void setUsername(final String username) {
    this.username = Optional.ofNullable(username);
  }

  public MQTTCredential withUsername(final String username) {
    setUsername(username);
    return this;
  }

  public String getPassword() {
    if (password.isPresent()) {
      return password.get();
    }
    return null;
  }

  public void setPassword(final String password) {
    this.password = Optional.ofNullable(password);
  }

  public MQTTCredential withPassword(final String password) {
    setPassword(password);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof MQTTCredential)) return false;
    if (this == rhs) return true;
    final MQTTCredential other = (MQTTCredential)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.clientId.equals(other.clientId);
    isEquals = isEquals && this.certificatePem.equals(other.certificatePem);
    isEquals = isEquals && this.username.equals(other.username);
    isEquals = isEquals && this.password.equals(other.password);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(clientId, certificatePem, username, password);
  }
}
