package software.amazon.awssdk.aws.greengrass.model;

import com.google.gson.annotations.Expose;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class CreateDebugPasswordResponse implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#CreateDebugPasswordResponse";

  public static final CreateDebugPasswordResponse VOID;

  static {
    VOID = new CreateDebugPasswordResponse() {
      @Override
      public boolean isVoid() {
        return true;
      }
    };
  }

  @Expose(serialize = true, deserialize = true)
  private Optional<String> password;

  @Expose(serialize = true, deserialize = true)
  private Optional<String> username;

  @Expose(serialize = true, deserialize = true)
  private Optional<Instant> passwordExpiration;

  @Expose(serialize = true, deserialize = true)
  private Optional<String> certificateSignature;

  public CreateDebugPasswordResponse() {
    this.password = Optional.empty();
    this.username = Optional.empty();
    this.passwordExpiration = Optional.empty();
    this.certificateSignature = Optional.empty();
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

  public String getUsername() {
    if (username.isPresent()) {
      return username.get();
    }
    return null;
  }

  public void setUsername(final String username) {
    this.username = Optional.ofNullable(username);
  }

  public Instant getPasswordExpiration() {
    if (passwordExpiration.isPresent()) {
      return passwordExpiration.get();
    }
    return null;
  }

  public void setPasswordExpiration(final Instant passwordExpiration) {
    this.passwordExpiration = Optional.ofNullable(passwordExpiration);
  }

  public String getCertificateSignature() {
    if (certificateSignature.isPresent()) {
      return certificateSignature.get();
    }
    return null;
  }

  public void setCertificateSignature(final String certificateSignature) {
    this.certificateSignature = Optional.ofNullable(certificateSignature);
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null)
      return false;
    if (!(rhs instanceof CreateDebugPasswordResponse))
      return false;
    if (this == rhs)
      return true;
    final CreateDebugPasswordResponse other = (CreateDebugPasswordResponse) rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.password.equals(other.password);
    isEquals = isEquals && this.username.equals(other.username);
    isEquals = isEquals && this.passwordExpiration.equals(other.passwordExpiration);
    isEquals = isEquals && this.certificateSignature.equals(other.certificateSignature);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(password, username, passwordExpiration, certificateSignature);
  }
}
