package software.amazon.awssdk.awstest.model;

import com.google.gson.annotations.Expose;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class EchoStreamingMessage implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "awstest#EchoStreamingMessage";

  private transient UnionMember setUnionMember;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<MessageData> streamMessage;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<Pair> keyValuePair;

  public EchoStreamingMessage() {
    this.streamMessage = Optional.empty();
    this.keyValuePair = Optional.empty();
  }

  public MessageData getStreamMessage() {
    if (streamMessage.isPresent() && (setUnionMember == UnionMember.STREAM_MESSAGE)) {
      return streamMessage.get();
    }
    return null;
  }

  public void setStreamMessage(final MessageData streamMessage) {
    if (setUnionMember != null) {
      setUnionMember.nullify(this);
    }
    this.streamMessage = Optional.of(streamMessage);
    this.setUnionMember = UnionMember.STREAM_MESSAGE;
  }

  public EchoStreamingMessage withStreamMessage(final MessageData streamMessage) {
    setStreamMessage(streamMessage);
    return this;
  }

  public Pair getKeyValuePair() {
    if (keyValuePair.isPresent() && (setUnionMember == UnionMember.KEY_VALUE_PAIR)) {
      return keyValuePair.get();
    }
    return null;
  }

  public void setKeyValuePair(final Pair keyValuePair) {
    if (setUnionMember != null) {
      setUnionMember.nullify(this);
    }
    this.keyValuePair = Optional.of(keyValuePair);
    this.setUnionMember = UnionMember.KEY_VALUE_PAIR;
  }

  public EchoStreamingMessage withKeyValuePair(final Pair keyValuePair) {
    setKeyValuePair(keyValuePair);
    return this;
  }

  /**
   * Returns an indicator for which enum member is set. Can be used to convert to proper type.
   */
  public UnionMember getSetUnionMember() {
    return setUnionMember;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  public void selfDesignateSetUnionMember() {
    int setCount = 0;
    UnionMember[] members = UnionMember.values();
    for (int memberIdx = 0; memberIdx < UnionMember.values().length; ++memberIdx) {
      if (members[memberIdx].isPresent(this)) {
        ++setCount;
        this.setUnionMember = members[memberIdx];
      }
    }
    // only bad outcome here is if there's more than one member set. It's possible for none to be set
    if (setCount > 1) {
      throw new IllegalArgumentException("More than one union member set for type: " + getApplicationModelType());
    }
  }

  @Override
  public void postFromJson() {
    selfDesignateSetUnionMember();
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof EchoStreamingMessage)) return false;
    if (this == rhs) return true;
    final EchoStreamingMessage other = (EchoStreamingMessage)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.streamMessage.equals(other.streamMessage);
    isEquals = isEquals && this.keyValuePair.equals(other.keyValuePair);
    isEquals = isEquals && this.setUnionMember.equals(other.setUnionMember);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(streamMessage, keyValuePair, setUnionMember);
  }

  public enum UnionMember {
    STREAM_MESSAGE("STREAM_MESSAGE", (software.amazon.awssdk.awstest.model.EchoStreamingMessage obj) -> obj.streamMessage = Optional.empty(), (software.amazon.awssdk.awstest.model.EchoStreamingMessage obj) -> obj.streamMessage != null && obj.streamMessage.isPresent()),

    KEY_VALUE_PAIR("KEY_VALUE_PAIR", (software.amazon.awssdk.awstest.model.EchoStreamingMessage obj) -> obj.keyValuePair = Optional.empty(), (software.amazon.awssdk.awstest.model.EchoStreamingMessage obj) -> obj.keyValuePair != null && obj.keyValuePair.isPresent());

    private String fieldName;

    private Consumer<EchoStreamingMessage> nullifier;

    private Predicate<EchoStreamingMessage> isPresent;

    UnionMember(String fieldName, Consumer<EchoStreamingMessage> nullifier,
        Predicate<EchoStreamingMessage> isPresent) {
      this.fieldName = fieldName;
      this.nullifier = nullifier;
      this.isPresent = isPresent;
    }

    void nullify(EchoStreamingMessage obj) {
      nullifier.accept(obj);
    }

    boolean isPresent(EchoStreamingMessage obj) {
      return isPresent.test(obj);
    }
  }
}
