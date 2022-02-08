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

public class ShadowStateFactory implements TypeAdapterFactory {

    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {

        Class<T> rawType = (Class<T>)type.getRawType();
        if (rawType != ShadowState.class) {
            return null;
        }

        final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);

        return new TypeAdapter<T>() {
            public void write(JsonWriter out, T shadowValue) throws IOException {
                // Are null values allowed?
                ShadowState shadow = (ShadowState)shadowValue;
                if (shadow.desiredNullIsValid == true || shadow.reportedNullIsValid == true) {
                    out.setSerializeNulls(true);
                }
                // If a property is null but null is not valid for it, then just send an empty HashMap
                if (shadow.desired == null && shadow.desiredNullIsValid == false) {
                    shadow.desired = new HashMap<String, Object>();
                }
                if (shadow.reported == null && shadow.reportedNullIsValid == false) {
                    shadow.reported = new HashMap<String, Object>();
                }
                delegate.write(out, shadowValue);
                if (shadow.desiredNullIsValid == true || shadow.reportedNullIsValid == true) {
                    out.setSerializeNulls(false);
                }
            }
            public T read(JsonReader in) throws IOException {
                // No post-processing needed
                return delegate.read(in);
            }
        };
    }
}
