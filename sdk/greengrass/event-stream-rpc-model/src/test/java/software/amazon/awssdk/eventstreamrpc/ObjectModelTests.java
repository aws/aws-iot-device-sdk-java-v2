/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.eventstreamrpc;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import software.amazon.awssdk.awstest.EchoTestRPCServiceModel;
import software.amazon.awssdk.awstest.model.*;
import software.amazon.awssdk.crt.eventstream.Header;
import software.amazon.awssdk.crt.eventstream.MessageType;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamError;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

public class ObjectModelTests {
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
    void testEnumGetter() {
        MessageData data = new MessageData();
        for(FruitEnum value:FruitEnum.values()) {
            data.setEnumMessage(value);
            FruitEnum enumGet = data.getEnumMessage();
        }
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
        //"key" setKey() is a required field in the presence of a Pair
        //however, it should never be verified client side via object model
        final Pair pair = new Pair();
        pair.setKey(null);
        pair.setValue(null);
    }

    @Test
    void testInstantSerialization() {
        final MessageData data = new MessageData();
        final Instant someInstant = Instant.ofEpochSecond(1606173648);
        data.setTimeMessage(someInstant);

        final JSONObject jsonObject = new JSONObject(EchoTestRPCServiceModel.getInstance().toJsonString(data));
        final MessageData dataDeserialized = EchoTestRPCServiceModel.getInstance().fromJson(MessageData.class,
                jsonObject.toString().getBytes(StandardCharsets.UTF_8));

        //Timestamp comparison is susceptible to precision issues due to double and serialization to JSON
        Assertions.assertEquals(Math.abs(data.getTimeMessage().toEpochMilli()), Math.abs(dataDeserialized.getTimeMessage().toEpochMilli()));
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

        streamingMessage.selfDesignateSetUnionMember(); //shouldn't throw exception
        Assertions.assertEquals(streamingMessage, deserialized);    //and should remain the same internally

        //changing the set member should cause prior member to be null
        //and self designating should not see more than one member
        final MessageData data = new MessageData();
        data.setEnumMessage(FruitEnum.BANANA);
        streamingMessage.setStreamMessage(data);
        Assertions.assertNull(streamingMessage.getKeyValuePair());
        streamingMessage.selfDesignateSetUnionMember();
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

    @Test
    void testDocumentNullSerialize() {
        final MessageData data = new MessageData();
        final Map<String, Object> docPart = new HashMap<>();
        //TODO: Gson deserializes integers into float and causes equals checks to fail later
        //      questionable if this is considered improper
        //docPart.put("int", Integer.valueOf(1));
        docPart.put("string", "bar");
        docPart.put("null", null);
        docPart.put("nullStringValueLiteral", "null");
        data.setDocumentMessage(docPart);

        final JSONObject obj = new JSONObject(EchoTestRPCServiceModel.getInstance().toJsonString(data));
        final MessageData deserialized = EchoTestRPCServiceModel.getInstance()
                .fromJson(MessageData.class, obj.toString().getBytes(StandardCharsets.UTF_8));

        Assertions.assertTrue(data.equals(deserialized));
        //verifies that the null deserialized back
        Assertions.assertTrue(deserialized.getDocumentMessage().containsKey("null"));
        Assertions.assertFalse(deserialized.getDocumentMessage().containsKey("nullNotPresent"));
    }

    @Test
    void testDocumentNullDeserialize() {
        final EchoMessageRequest data = new EchoMessageRequest();
        Map<String, Product> sTV = new HashMap<String, Product>();
        Product p = new Product();
        p.setPrice(1.f);
        // leaving product's name as null for previously found issue
        sTV.put("A", p);
        MessageData m = new MessageData();
        data.setMessage(m);
        m.setStringToValue(sTV);

        final JSONObject obj = new JSONObject(EchoTestRPCServiceModel.getInstance().toJsonString(data));
        final EchoMessageRequest deserialized = EchoTestRPCServiceModel.getInstance()
                .fromJson(EchoMessageRequest.class, obj.toString().getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void testBadJsonDeserialize() {
        List<Header> headers = new ArrayList<>();
        byte[] badJsonPayload = "{\"derp\":\"value\"; }".getBytes(StandardCharsets.UTF_8);
        EventStreamError errorMessage = EventStreamError.create(headers, badJsonPayload, MessageType.ProtocolError);
        Assertions.assertNotNull(errorMessage);
    }
}
