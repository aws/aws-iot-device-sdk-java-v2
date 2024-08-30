/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.iot;

import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonParseException;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Custom JSON serializer for enumerated types within IoT service models
 * @param <E> the enumeration type the serializer should work with
 */
public class EnumSerializer<E> implements JsonSerializer<E>, JsonDeserializer<E> {

    /**
     * Serializes the given enum to a JsonElement
     * @param enumValue The enum to convert
     * @param typeOfEnum The enum to convert type
     * @param context The JsonSerializationContext to use
     * @return The enum as a JsonElement
     */
    public JsonElement serialize(E enumValue, Type typeOfEnum, JsonSerializationContext context) {
        return new JsonPrimitive(enumValue.toString());
    }

    private Method fromString;

    /**
     * Deserializes the JsonElement to an enum
     * @param json The json to convert
     * @param typeOfEnum The type of enum to convert to
     * @param context The JsonDeserializationContext to use
     * @return The enum from the JsonElement data
     */
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
