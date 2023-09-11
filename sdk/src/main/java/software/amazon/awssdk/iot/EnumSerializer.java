/* Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.

 * This file is generated
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

/**
 * Class for serializing enums to and from packets
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
