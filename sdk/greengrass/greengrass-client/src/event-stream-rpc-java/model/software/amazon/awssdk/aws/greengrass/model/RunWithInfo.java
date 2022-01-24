package software.amazon.awssdk.aws.greengrass.model;

import com.google.gson.annotations.Expose;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class RunWithInfo implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#RunWithInfo";

  public static final RunWithInfo VOID;

  static {
    VOID = new RunWithInfo() {
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
  private Optional<String> posixUser;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<String> windowsUser;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<SystemResourceLimits> systemResourceLimits;

  public RunWithInfo() {
    this.posixUser = Optional.empty();
    this.windowsUser = Optional.empty();
    this.systemResourceLimits = Optional.empty();
  }

  public String getPosixUser() {
    if (posixUser.isPresent()) {
      return posixUser.get();
    }
    return null;
  }

  public RunWithInfo setPosixUser(final String posixUser) {
    this.posixUser = Optional.ofNullable(posixUser);
    return this;
  }

  public String getWindowsUser() {
    if (windowsUser.isPresent()) {
      return windowsUser.get();
    }
    return null;
  }

  public RunWithInfo setWindowsUser(final String windowsUser) {
    this.windowsUser = Optional.ofNullable(windowsUser);
    return this;
  }

  public SystemResourceLimits getSystemResourceLimits() {
    if (systemResourceLimits.isPresent()) {
      return systemResourceLimits.get();
    }
    return null;
  }

  public RunWithInfo setSystemResourceLimits(final SystemResourceLimits systemResourceLimits) {
    this.systemResourceLimits = Optional.ofNullable(systemResourceLimits);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof RunWithInfo)) return false;
    if (this == rhs) return true;
    final RunWithInfo other = (RunWithInfo)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.posixUser.equals(other.posixUser);
    isEquals = isEquals && this.windowsUser.equals(other.windowsUser);
    isEquals = isEquals && this.systemResourceLimits.equals(other.systemResourceLimits);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(posixUser, windowsUser, systemResourceLimits);
  }
}
