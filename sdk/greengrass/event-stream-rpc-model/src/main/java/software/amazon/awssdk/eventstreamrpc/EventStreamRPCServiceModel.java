package software.amazon.awssdk.eventstreamrpc;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import software.amazon.awssdk.eventstreamrpc.model.*;
import software.amazon.awssdk.eventstreamrpc.model.UnsupportedOperationException;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

/**
 * Implementers of this service model are expected to likely be singletons. There
 * should be little value to having more than one, though between different instances
 * properly constructed for a service, they can be used interchangeably
 */
public abstract class EventStreamRPCServiceModel {
    private static final Gson GSON;

    //package visibility so client
    static final String VERSION_HEADER = ":version";
    public static final String CONTENT_TYPE_HEADER = ":content-type";
    public static final String CONTENT_TYPE_APPLICATION_TEXT = "text/plain";
    public static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";
    public static final String SERVICE_MODEL_TYPE_HEADER = "service-model-type";

    static {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapterFactory(new ForceNullsForMapTypeAdapterFactory());
        builder.registerTypeAdapterFactory(OptionalTypeAdapter.FACTORY);
        builder.registerTypeAdapter(byte[].class, new Base64BlobSerializerDeserializer());
        builder.registerTypeAdapter(Instant.class, new InstantSerializerDeserializer());
        GSON = builder.create();
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
     * @param lhs - lhs
     * @param rhs - rhs
     * @return boolean
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
        private static final Base64.Encoder BASE_64_ENCODER = Base64.getEncoder();
        private static final Base64.Decoder BASE_64_DECODER = Base64.getDecoder();

        @Override
        public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return BASE_64_DECODER.decode(json.getAsString());
        }

        @Override
        public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(BASE_64_ENCODER.encodeToString(src));
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
     * For actual
     * @return Service Name
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

    final public Optional<Class<? extends EventStreamJsonMessage>> getApplicationModelClass(final String applicationModelType) {
        final Class<? extends EventStreamJsonMessage> clazz = FRAMEWORK_APPLICATION_MODEL_TYPES.get(applicationModelType);
        if (clazz != null) {
            return Optional.of(clazz);
        }
        return getServiceClassType(applicationModelType);
    }

    /**
     * Retreives all operations on the service
     * @return Collection
     */
    public abstract Collection<String> getAllOperations();

    /**
     * Need to override per specific service type so it can look up all associated types and errors
     * possible.
     *
     * @param applicationModelType
     * @return
     */
    protected abstract Optional<Class<? extends EventStreamJsonMessage>> getServiceClassType(String applicationModelType);

    /**
     * Retrieves the operation model context for a given operation name on the service
     *
     * This may not be a useful interface as generated code will typically pull a known operation model context
     * Public visibility is useful for testing
     * @param operationName operationName
     * @return {@link OperationModelContext}
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

    public String toJsonString(final EventStreamJsonMessage message) {
        return new String(toJson(message), StandardCharsets.UTF_8);
    }

    /**
     * Internal getter method can be used by subclasses of specific service models to override default Gson
     * @return
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
     * Uses this service's specific model class
     * @param applicationModelType - applicationModelType
     * @param payload - payload
     * @return {@link EventStreamJsonMessage}
     */
    public EventStreamJsonMessage fromJson(final String applicationModelType, byte[] payload) {
        final Optional<Class<? extends EventStreamJsonMessage>> clazz = getApplicationModelClass(applicationModelType);
        if (!clazz.isPresent()) {
            throw new UnmappedDataException(applicationModelType);
        }
        final EventStreamJsonMessage msg = fromJson(clazz.get(), payload);
        msg.postFromJson();
        return msg;
    }

    public <T extends EventStreamJsonMessage> T fromJson(final Class<T> clazz, byte[] payload) {
        try {
            final T obj = getGson().fromJson(new String(payload, StandardCharsets.UTF_8), clazz);
            obj.postFromJson();
            return obj;
        } catch (Exception e) {
            throw new DeserializationException(payload, e);
        }
    }
}
