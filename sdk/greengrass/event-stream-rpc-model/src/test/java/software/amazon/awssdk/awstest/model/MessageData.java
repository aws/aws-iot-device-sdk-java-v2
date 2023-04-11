package software.amazon.awssdk.awstest.model;

import com.google.gson.annotations.Expose;
import java.lang.Boolean;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCServiceModel;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class MessageData implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "awstest#MessageData";

  public static final MessageData VOID;

  static {
    VOID = new MessageData() {
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
  private Optional<String> stringMessage;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<Boolean> booleanMessage;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<Instant> timeMessage;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<Map<String, Object>> documentMessage;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<String> enumMessage;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<byte[]> blobMessage;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<List<String>> stringListMessage;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<List<Pair>> keyValuePairList;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<Map<String, Product>> stringToValue;

  public MessageData() {
    this.stringMessage = Optional.empty();
    this.booleanMessage = Optional.empty();
    this.timeMessage = Optional.empty();
    this.documentMessage = Optional.empty();
    this.enumMessage = Optional.empty();
    this.blobMessage = Optional.empty();
    this.stringListMessage = Optional.empty();
    this.keyValuePairList = Optional.empty();
    this.stringToValue = Optional.empty();
  }

  public String getStringMessage() {
    if (stringMessage.isPresent()) {
      return stringMessage.get();
    }
    return null;
  }

  public void setStringMessage(final String stringMessage) {
    this.stringMessage = Optional.ofNullable(stringMessage);
  }

  public MessageData withStringMessage(final String stringMessage) {
    setStringMessage(stringMessage);
    return this;
  }

  public Boolean isBooleanMessage() {
    if (booleanMessage.isPresent()) {
      return booleanMessage.get();
    }
    return null;
  }

  public void setBooleanMessage(final Boolean booleanMessage) {
    this.booleanMessage = Optional.ofNullable(booleanMessage);
  }

  public MessageData withBooleanMessage(final Boolean booleanMessage) {
    setBooleanMessage(booleanMessage);
    return this;
  }

  public Instant getTimeMessage() {
    if (timeMessage.isPresent()) {
      return timeMessage.get();
    }
    return null;
  }

  public void setTimeMessage(final Instant timeMessage) {
    this.timeMessage = Optional.ofNullable(timeMessage);
  }

  public MessageData withTimeMessage(final Instant timeMessage) {
    setTimeMessage(timeMessage);
    return this;
  }

  public Map<String, Object> getDocumentMessage() {
    if (documentMessage.isPresent()) {
      return documentMessage.get();
    }
    return null;
  }

  public void setDocumentMessage(final Map<String, Object> documentMessage) {
    this.documentMessage = Optional.ofNullable(documentMessage);
  }

  public MessageData withDocumentMessage(final Map<String, Object> documentMessage) {
    setDocumentMessage(documentMessage);
    return this;
  }

  public FruitEnum getEnumMessage() {
    if (enumMessage.isPresent()) {
      return FruitEnum.get(enumMessage.get());
    }
    return null;
  }

  public String getEnumMessageAsString() {
    if (enumMessage.isPresent()) {
      return enumMessage.get();
    }
    return null;
  }

  public void setEnumMessage(final String enumMessage) {
    this.enumMessage = Optional.ofNullable(enumMessage);
  }

  public MessageData withEnumMessage(final String enumMessage) {
    setEnumMessage(enumMessage);
    return this;
  }

  public void setEnumMessage(final FruitEnum enumMessage) {
    this.enumMessage = Optional.ofNullable(enumMessage.getValue());
  }

  public MessageData withEnumMessage(final FruitEnum enumMessage) {
    setEnumMessage(enumMessage);
    return this;
  }

  public byte[] getBlobMessage() {
    if (blobMessage.isPresent()) {
      return blobMessage.get();
    }
    return null;
  }

  public void setBlobMessage(final byte[] blobMessage) {
    this.blobMessage = Optional.ofNullable(blobMessage);
  }

  public MessageData withBlobMessage(final byte[] blobMessage) {
    setBlobMessage(blobMessage);
    return this;
  }

  public List<String> getStringListMessage() {
    if (stringListMessage.isPresent()) {
      return stringListMessage.get();
    }
    return null;
  }

  public void setStringListMessage(final List<String> stringListMessage) {
    this.stringListMessage = Optional.ofNullable(stringListMessage);
  }

  public MessageData withStringListMessage(final List<String> stringListMessage) {
    setStringListMessage(stringListMessage);
    return this;
  }

  public List<Pair> getKeyValuePairList() {
    if (keyValuePairList.isPresent()) {
      return keyValuePairList.get();
    }
    return null;
  }

  public void setKeyValuePairList(final List<Pair> keyValuePairList) {
    this.keyValuePairList = Optional.ofNullable(keyValuePairList);
  }

  public MessageData withKeyValuePairList(final List<Pair> keyValuePairList) {
    setKeyValuePairList(keyValuePairList);
    return this;
  }

  public Map<String, Product> getStringToValue() {
    if (stringToValue.isPresent()) {
      return stringToValue.get();
    }
    return null;
  }

  public void setStringToValue(final Map<String, Product> stringToValue) {
    this.stringToValue = Optional.ofNullable(stringToValue);
  }

  public MessageData withStringToValue(final Map<String, Product> stringToValue) {
    setStringToValue(stringToValue);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof MessageData)) return false;
    if (this == rhs) return true;
    final MessageData other = (MessageData)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.stringMessage.equals(other.stringMessage);
    isEquals = isEquals && this.booleanMessage.equals(other.booleanMessage);
    isEquals = isEquals && this.timeMessage.equals(other.timeMessage);
    isEquals = isEquals && this.documentMessage.equals(other.documentMessage);
    isEquals = isEquals && this.enumMessage.equals(other.enumMessage);
    isEquals = isEquals && EventStreamRPCServiceModel.blobTypeEquals(this.blobMessage, other.blobMessage);
    isEquals = isEquals && this.stringListMessage.equals(other.stringListMessage);
    isEquals = isEquals && this.keyValuePairList.equals(other.keyValuePairList);
    isEquals = isEquals && this.stringToValue.equals(other.stringToValue);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(stringMessage, booleanMessage, timeMessage, documentMessage, enumMessage, blobMessage, stringListMessage, keyValuePairList, stringToValue);
  }
}
