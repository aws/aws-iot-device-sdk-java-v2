/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.iot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonParseException;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class EnumSerializer<E> implements JsonSerializer<E>, JsonDeserializer<E> {
    public JsonElement serialize(E enumValue, Type typeOfEnum, JsonSerializationContext context) {
        return new JsonPrimitive(enumValue.toString());
    }

    private Method fromString;
    public E deserialize(JsonElement json, Type typeOfEnum, JsonDeserializationContext context)
            throws JsonParseException {
        if (fromString == null) {
            Class<?> c = (Class<?>)typeOfEnum;
            for (Method m : c.getDeclaredMethods()) {
                if (m.getName() == "fromString") {
                    fromString = m;
                    fromString.setAccessible(true);
                    break;
                }
            }
        }
        try {
            @SuppressWarnings("unchecked")
            E value = (E) fromString.invoke(null, json.getAsJsonPrimitive().getAsString());
            return value;
        } catch (Exception ex) {
            @SuppressWarnings("unchecked")
            Class<E> c = (Class<E>)typeOfEnum;
            return c.getEnumConstants()[0];
        }
    }
}
