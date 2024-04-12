/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.eventstreamrpc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import software.amazon.awssdk.eventstreamrpc.model.AccessDeniedException;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;
import software.amazon.awssdk.eventstreamrpc.model.UnsupportedOperationException;
import software.amazon.awssdk.eventstreamrpc.model.ValidationException;
import software.amazon.awssdk.crt.utils.StringUtils;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Implementers of this service model are expected to likely be singletons. There
 * should be little value to having more than one, though between different instances
 * properly constructed for a service, they can be used interchangeably
 */
public abstract class EventStreamRPCServiceModel {
    private static final Gson GSON;

    /**
     * Version header string
     */
    static final String VERSION_HEADER = ":version";

    /**
     * Content type header string
     */
    public static final String CONTENT_TYPE_HEADER = ":content-type";

    /**
     * Content type application text string
     */
    public static final String CONTENT_TYPE_APPLICATION_TEXT = "text/plain";

    /**
     * Content type application json string
     */
    public static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";

    /**
     * Service model type header
     */
    public static final String SERVICE_MODEL_TYPE_HEADER = "service-model-type";

    static {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapterFactory(new ForceNullsForMapTypeAdapterFactory());
        builder.registerTypeAdapterFactory(OptionalTypeAdapter.FACTORY);
        builder.registerTypeAdapterFactory(EventStreamPostFromJsonTypeAdapter.FACTORY);
        builder.registerTypeAdapter(byte[].class, new Base64BlobSerializerDeserializer());
        builder.registerTypeAdapter(Instant.class, new InstantSerializerDeserializer());
        builder.excludeFieldsWithoutExposeAnnotation();
        GSON = builder.create();
    }

    // Type adapter to automatically call "postFromJson" on all instances of EventStreamJsonMessage we construct
    private static class EventStreamPostFromJsonTypeAdapter<E extends EventStreamJsonMessage> extends TypeAdapter<E> {
        public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
            @Override
            public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
                if (EventStreamJsonMessage.class.isAssignableFrom(type.getRawType())) {
                    final TypeAdapter<?> delegate = gson.getDelegateAdapter(this, type);
                    return new EventStreamPostFromJsonTypeAdapter(delegate);
                }

                return null;
            }
        };

        private final TypeAdapter<E> adapter;

        public EventStreamPostFromJsonTypeAdapter(TypeAdapter<E> adapter) {
            this.adapter = adapter;
        }

        @Override
        public void write(JsonWriter out, E value) throws IOException {
            adapter.write(out, value);
        }

        @Override
        public E read(JsonReader in) throws IOException {
            E obj = adapter.read(in);
            if (obj != null) {
                // Call postFromJson to finalize the deserialization. Especially important for unions to have their
                // member get set correctly.
                obj.postFromJson();
            }
            return obj;
        }
    }

    private static class ForceNullsForMapTypeAdapterFactory implements TypeAdapterFactory {

        public final <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            if (Map.class.isAssignableFrom(type.getRawType())) {
                final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
                return createCustomTypeAdapter(delegate);
            }

            return null;
        }

        private <T> TypeAdapter<T> createCustomTypeAdapter(TypeAdapter<T> delegate) {
            return new TypeAdapter<T>() {
                @Override
                public void write(JsonWriter out, T value) throws IOException {
                    final boolean serializeNulls = out.getSerializeNulls();
                    try {
                        out.setSerializeNulls(true);
                        delegate.write(out, value);
                    } finally {
                        out.setSerializeNulls(serializeNulls);
                    }
                }

                @Override
                public T read(JsonReader in) throws IOException {
                    return delegate.read(in);
                }
            };
        }
    }

    private static class OptionalTypeAdapter<E> extends TypeAdapter<Optional<E>> {
        public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
            @Override
            public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
                Class<T> rawType = (Class<T>) type.getRawType();
                if (rawType != Optional.class) {
                    return null;
                }
                final ParameterizedType parameterizedType = (ParameterizedType) type.getType();
                final Type actualType = parameterizedType.getActualTypeArguments()[0];
                final TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(actualType));
                return new OptionalTypeAdapter(adapter);
            }
        };

        private final TypeAdapter<E> adapter;

        public OptionalTypeAdapter(TypeAdapter<E> adapter) {
            this.adapter = adapter;
        }

        @Override
        public void write(JsonWriter out, Optional<E> value) throws IOException {
            if (value.isPresent()){
                adapter.write(out, value.get());
            } else if (value != null) {
                out.nullValue();
            } else { }
        }

        @Override
        public Optional<E> read(JsonReader in) throws IOException {
            return Optional.ofNullable(adapter.read(in));
        }
    }

    /**
     * Used to compare two members of a blob shape for equality. Array equals nesting
     * inside of an Optional doesn't work
     *
     * Note: Generated code for equals method of Smithy shapes relies on this
     *
     * @param lhs The first to compare
     * @param rhs The second to compare
     * @return True if both are equal, false otherwise
     */
    public static boolean blobTypeEquals(Optional<byte[]> lhs, Optional<byte[]> rhs) {
        if (lhs.equals(rhs)) {
            //both are same instance, both are same contained array, or both are empty
            return true;
        }
        if (!lhs.isPresent() || !rhs.isPresent()) {
            //if just one or the other is empty at this point
            return false;
        }
        //now we know both are present so compare the arrays
        return Arrays.equals(lhs.get(), rhs.get());
    }

    private static class Base64BlobSerializerDeserializer implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {
        @Override
        public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return StringUtils.base64Decode(json.getAsString().getBytes());
        }

        @Override
        public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(new String(StringUtils.base64Encode(src)));
        }
    }

    private static class InstantSerializerDeserializer implements JsonSerializer<Instant>, JsonDeserializer<Instant> {
        @Override
        public Instant deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            double fSecondsEpoch = json.getAsDouble();
            long secondsEpoch = (long)fSecondsEpoch;
            long nanoEpoch = (long)((fSecondsEpoch - secondsEpoch) * 1_000_000_000.);
            return Instant.ofEpochSecond(secondsEpoch, nanoEpoch);
        }

        @Override
        public JsonElement serialize(Instant src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive((double)src.getEpochSecond() + (double)src.getNano() / 1000000000.);
        }
    }

    /**
     * For getting the actual service name
     * @return The name of the service as a string
     */
    public abstract String getServiceName();

    private static final Map<String, Class<? extends EventStreamJsonMessage>> FRAMEWORK_APPLICATION_MODEL_TYPES
            = new HashMap<>();
    static {
        //TODO: find a reliable way to verify all of these are set? reflection cannot scan a package
        FRAMEWORK_APPLICATION_MODEL_TYPES.put(AccessDeniedException.ERROR_CODE, AccessDeniedException.class);
        FRAMEWORK_APPLICATION_MODEL_TYPES.put(UnsupportedOperationException.ERROR_CODE, UnsupportedOperationException.class);
        FRAMEWORK_APPLICATION_MODEL_TYPES.put(ValidationException.ERROR_CODE, ValidationException.class);
    }

    /**
     * Returns the application model class
     * @param applicationModelType The application model
     * @return The class of the given application model
     */
    final public Optional<Class<? extends EventStreamJsonMessage>> getApplicationModelClass(final String applicationModelType) {
        final Class<? extends EventStreamJsonMessage> clazz = FRAMEWORK_APPLICATION_MODEL_TYPES.get(applicationModelType);
        if (clazz != null) {
            return Optional.of(clazz);
        }
        return getServiceClassType(applicationModelType);
    }

    /**
     * Retreives all operations on the service
     * @return All operations on the service
     */
    public abstract Collection<String> getAllOperations();

    /**
     * Need to override per specific service type so it can look up all associated types and errors
     * possible.
     *
     * @param applicationModelType The application model
     * @return The service class type of the given application model
     */
    protected abstract Optional<Class<? extends EventStreamJsonMessage>> getServiceClassType(String applicationModelType);

    /**
     * Retrieves the operation model context for a given operation name on the service
     *
     * This may not be a useful interface as generated code will typically pull a known operation model context
     * Public visibility is useful for testing
     *
     * @param operationName The name of the operation
     * @return The operation context associated with the given operation name
     */
    public abstract OperationModelContext getOperationModelContext(String operationName);

    public byte[] toJson(final EventStreamJsonMessage message) {
        try {
            final byte[] json = message.toPayload(getGson());
            final String stringJson = new String(json, StandardCharsets.UTF_8);
            //this feels like a hack. I'd prefer if java objects with no fields set serialized to being an empty object
            //rather than "null"
            if (null == stringJson || "null".equals(stringJson) || stringJson.isEmpty()) {
                return "{}".getBytes(StandardCharsets.UTF_8);
            }
            return json;
        } catch (Exception e) {
            throw new SerializationException(message, e);
        }
    }

    /**
     * Converts the given EventStreamJsonMessage to a JSON string
     * @param message The message to convert
     * @return A JSON string
     */
    public String toJsonString(final EventStreamJsonMessage message) {
        return new String(toJson(message), StandardCharsets.UTF_8);
    }

    /**
     * Internal getter method can be used by subclasses of specific service models to override default Gson
     * @return Returns GSON context
     */
    protected Gson getGson() {
        return GSON;
    }

    /**
     * In situations where the framework needs to do some JSON processing
     * without a specific service/operation in context
     *
     * @return the static Gson instance capable of processing the basics of EventStreamableJsonMessage
     */
    public static Gson getStaticGson() {
        return GSON;
    }

    /**
     * Creates a EventStreamJsonMessage from the given application model type string and payload.
     * Uses this service's specific model class to create the EventStreamJsonMessage.
     * @param applicationModelType The application model type string
     * @param payload The payload
     * @return A EventStreamMessage
     */
    public EventStreamJsonMessage fromJson(final String applicationModelType, byte[] payload) {
        final Optional<Class<? extends EventStreamJsonMessage>> clazz = getApplicationModelClass(applicationModelType);
        if (!clazz.isPresent()) {
            throw new UnmappedDataException(applicationModelType);
        }
        return fromJson(clazz.get(), payload);
    }

    /**
     * Creates a EventStreamJsonMessage of type T from the given application model
     * class and payload.
     * @param <T> The type to convert the result to
     * @param clazz The class
     * @param payload The payload
     * @return A EventStreamMessage of type T
     */
    public <T extends EventStreamJsonMessage> T fromJson(final Class<T> clazz, byte[] payload) {
        try {
            return getGson().fromJson(new String(payload, StandardCharsets.UTF_8), clazz);
        } catch (Exception e) {
            throw new DeserializationException(payload, e);
        }
    }
}
