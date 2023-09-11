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
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * A wrapper for the java.util.Date class that allows for serializing and deseralizing to JSON
 */
public class Timestamp extends java.util.Date {
    /**
     * Serializer to convert Timestamp to JSON
     */
    public static class Serializer implements JsonSerializer<Timestamp> {
        /**
         * Serializes a Timestamp to JSON
         * @param src The Timestamp to convert
         * @param typeOfSrc The Type to use
         * @param context The JsonSerializationContext to use
         * @return A JsonElement containing the Timestamp data
         */
        public JsonElement serialize(Timestamp src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getTime() / 1000); // convert from ms to seconds
        }
    }

    /**
     * Deserializer to convert JSON to Timestamp
     */
    public static class Deserializer implements JsonDeserializer<Timestamp> {
        /**
         * Deserializes JSON to a Timestamp
         * @param json The JsonElement containing the Timestamp
         * @param typeOfT The Type to use
         * @param context The JsonDeserializationContext to use
         * @return A Timestamp containing the data in the JsonElement
         */
        public Timestamp deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return new Timestamp(new Date(json.getAsJsonPrimitive().getAsLong() * 1000));
        }
    }

    /**
     * Timestamp constructor
     * @param date The date to use
     */
    public Timestamp(Date date) {
        super(date.getTime());
    }
}
