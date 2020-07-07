/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.iot;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;
import java.util.Date;

public class Timestamp extends java.util.Date {
    public static class Serializer implements JsonSerializer<Timestamp> {
        public JsonElement serialize(Timestamp src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getTime() / 1000); // convert from ms to seconds
        }
    }
    public static class Deserializer implements JsonDeserializer<Timestamp> {
        public Timestamp deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return new Timestamp(new Date(json.getAsJsonPrimitive().getAsLong() * 1000));
        }
    }

    public Timestamp(Date date) {
        super(date.getTime());
    }
}
