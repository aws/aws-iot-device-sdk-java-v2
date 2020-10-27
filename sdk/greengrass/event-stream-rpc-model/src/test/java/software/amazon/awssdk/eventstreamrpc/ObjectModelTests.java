package software.amazon.awssdk.eventstreamrpc;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import software.amazon.awssdk.awstest.EchoTestRPCServiceModel;
import software.amazon.awssdk.awstest.model.*;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;

public class ObjectModelTests {
    private static final EventStreamRPCServiceModel SERVICE_MODEL = new EventStreamRPCServiceModel() {
        @Override
        public String getServiceName() { return null; }

        @Override
        public Collection<String> getAllOperations() {
            return new LinkedList<>();
        }

        @Override
        protected Optional<Class<? extends EventStreamJsonMessage>> getServiceClassType(String applicationModelType) {
            return Optional.empty();
        }

        @Override
        public OperationModelContext getOperationModelContext(String operationName) {
            return null;
        }
    };

    @Test
    void testBasicModelSerialize() {
        final String testString  = "fooStringMessage";
        final EchoMessageRequest requestObject = new EchoMessageRequest();
        final MessageData data = new MessageData();
        data.setStringMessage(testString);
        requestObject.setMessage(data);
        final JSONObject jsonObject = new JSONObject(new String(EchoTestRPCServiceModel.getInstance().toJson(requestObject), StandardCharsets.UTF_8));
        Assertions.assertTrue(jsonObject.has("message"));
        Assertions.assertTrue(jsonObject.getJSONObject("message").has("stringMessage"));
        Assertions.assertEquals(testString, jsonObject.getJSONObject("message").getString("stringMessage"));
    }

    @Test
    void testBlobAndDeserializeEquivalence() {
        byte[] testContent = new byte[] { (byte)0xDE, (byte)0xAD, (byte)0xBE, (byte)0xEF };
        final EchoMessageRequest requestObject = new EchoMessageRequest();
        final MessageData data = new MessageData();
        data.setBlobMessage(testContent);
        requestObject.setMessage(data);
        final JSONObject jsonObject = new JSONObject(new String(EchoTestRPCServiceModel.getInstance().toJson(requestObject), StandardCharsets.UTF_8));
        Assertions.assertTrue(jsonObject.has("message"));
        Assertions.assertTrue(jsonObject.getJSONObject("message").has("blobMessage"));
        Assertions.assertEquals(new String(Base64.getEncoder().encode(testContent), StandardCharsets.UTF_8),
                jsonObject.getJSONObject("message").getString("blobMessage"));

        final EchoMessageRequest deserialized = EchoTestRPCServiceModel.getInstance().fromJson(EchoMessageRequest.class,
                jsonObject.toString().getBytes(StandardCharsets.UTF_8));
        Assertions.assertTrue(requestObject.equals(deserialized));
    }

    @Test
    void testEmptyObjectIsNotNullAndIsEmpty() {
        final EchoMessageRequest requestObject = new EchoMessageRequest();
        final JSONObject jsonObject = new JSONObject(new String(EchoTestRPCServiceModel.getInstance().toJson(requestObject), StandardCharsets.UTF_8));
        Assertions.assertTrue(jsonObject.isEmpty());
    }

    @Test
    void testNestedEqualsAndHashCodeObject() {
        final String testString  = "fooStringMessage";
        final EchoMessageRequest requestObject = new EchoMessageRequest();
        final MessageData data = new MessageData();
        data.setStringMessage(testString);
        requestObject.setMessage(data);

        final String testString2  = "fooStringMessage";
        final EchoMessageRequest requestObject2 = new EchoMessageRequest();
        final MessageData data2 = new MessageData();
        data2.setStringMessage(testString2);
        requestObject2.setMessage(data2);

        final String testString3  = "fooStringMessage-changed";
        final EchoMessageRequest requestObject3 = new EchoMessageRequest();
        final MessageData data3 = new MessageData();
        data3.setStringMessage(testString3);
        requestObject3.setMessage(data3);

        //Test equals both ways
        Assertions.assertTrue(requestObject.equals(requestObject2));
        Assertions.assertTrue(requestObject2.equals(requestObject));
        Assertions.assertFalse(requestObject.equals(null));
        Assertions.assertFalse(requestObject2.equals(null));
        Assertions.assertFalse(requestObject.equals(requestObject3));
        Assertions.assertTrue(requestObject.hashCode() == requestObject2.hashCode(),
                "Hash code of nested equivalent objects are not equal!");
        Assertions.assertTrue(requestObject.hashCode() != requestObject3.hashCode(),
                "Hash code of nested different objects should not be equals!");
    }

    @Test
    void testSetRequiredFieldToNull() {
        final Pair pair = new Pair();
        Assertions.assertThrows(NullPointerException.class, () -> pair.setKey(null));
    }

    @Test
    void testUnionShapeSerializeAndDeserialize() {
        final EchoStreamingMessage streamingMessage = new EchoStreamingMessage();
        final Pair pair = new Pair();
        pair.setKey("fooKey");
        pair.setValue("barValue");
        streamingMessage.setKeyValuePair(pair);
        final JSONObject obj = new JSONObject(EchoTestRPCServiceModel.getInstance().toJsonString(streamingMessage));
        Assertions.assertTrue(obj.has("keyValuePair"));
        final EchoStreamingMessage deserialized = EchoTestRPCServiceModel.getInstance()
                .fromJson(EchoStreamingMessage.class, obj.toString().getBytes(StandardCharsets.UTF_8));
        Assertions.assertEquals(EchoStreamingMessage.UnionMember.KEY_VALUE_PAIR, deserialized.getSetUnionMember());
        Assertions.assertTrue(deserialized.getKeyValuePair().equals(pair));
    }

    @Test
    void testEnumSerializeDeserialize() {
        final MessageData data = new MessageData();
        data.setEnumMessage(FruitEnum.BANANA);
        final JSONObject obj = new JSONObject(EchoTestRPCServiceModel.getInstance().toJsonString(data));
        Assertions.assertTrue(obj.has("enumMessage"));
        Assertions.assertEquals(FruitEnum.BANANA.getValue(), obj.get("enumMessage"));
        final MessageData deserialized = EchoTestRPCServiceModel.getInstance()
                .fromJson(MessageData.class, obj.toString().getBytes(StandardCharsets.UTF_8));
        Assertions.assertTrue(data.equals(deserialized));
    }
}
