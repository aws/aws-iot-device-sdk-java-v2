/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.iot;

import software.amazon.awssdk.iot.iotshadow.model.ShadowState;

import java.io.IOException;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * Factory class for converting ShadowStates to and from packet payloads
 */
public class ShadowStateFactory implements TypeAdapterFactory {

    /**
     * Creates a new TypeAdapter for conversion to and from packet payloads
     */
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {

        Class<T> rawType = (Class<T>)type.getRawType();
        if (rawType != ShadowState.class) {
            return null;
        }

        final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);

        return new TypeAdapter<T>() {

            /**
             * Writes the type to the packet payload (JsonWriter)
             * @param out The JsonWriter to output the type data to
             * @param shadowValue The shadow value containing the data to convert
             */
            public void write(JsonWriter out, T shadowValue) throws IOException {
                // Are null values present? If so, we need to process this differently
                ShadowState shadow = (ShadowState)shadowValue;
                if (shadow.desiredIsNullable == true || shadow.reportedIsNullable == true) {
                    out.setSerializeNulls(true);

                    // If a property is null but null is not valid for it, then just send an empty HashMap
                    if (shadow.desired == null && shadow.desiredIsNullable == false) {
                        shadow.desired = new HashMap<String, Object>();
                    }
                    if (shadow.reported == null && shadow.reportedIsNullable == false) {
                        shadow.reported = new HashMap<String, Object>();
                    }

                    delegate.write(out, shadowValue);
                    out.setSerializeNulls(false);
                }
                else
                {
                    delegate.write(out, shadowValue);
                }
            }

            /**
             * Reads the type from the packet payload (JsonReader)
             * @param in The JsonReader containing the packet payload data
             * @return The type created from the packet payload data
             */
            public T read(JsonReader in) throws IOException {
                T returnType = delegate.read(in);
                return returnType;
            }
        };
    }
}
