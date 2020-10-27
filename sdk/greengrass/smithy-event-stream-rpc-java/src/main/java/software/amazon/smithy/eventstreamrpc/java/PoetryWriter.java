package software.amazon.smithy.eventstreamrpc.java;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeName;
import software.amazon.smithy.eventstreamrpc.java.model.TypeNameShapeVisitor;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.traits.JsonNameTrait;

import javax.lang.model.element.Modifier;

/**
 * Right now this may be a bit of a junk yard for 'common' code generation logic that
 * may be more logically divided between model, server, or client generation responsibilities
 * or along the lines of shared symbols in the target generated code.
 */
public class PoetryWriter {
    private static final String EVENT_STREAM_RPC_PACKAGE = "software.amazon.awssdk.eventstreamrpc";
    private static final String EVENT_STREAM_RPC_MODEL_PACKAGE = EVENT_STREAM_RPC_PACKAGE + ".model";

    public static final ClassName CN_GSON_SERIALIZED_NAME
            = ClassName.get("com.google.gson.annotations",
            "SerializedName");

    public static final ClassName CN_EVENT_STREAMABLE_JSON_MESSAGE
            = ClassName.get(EVENT_STREAM_RPC_MODEL_PACKAGE,
            "EventStreamJsonMessage");

    public static final ClassName CN_EVENT_STREAM_OPERATION_ERROR
            = ClassName.get(EVENT_STREAM_RPC_MODEL_PACKAGE,
            "EventStreamOperationError");

    public static final ClassName CN_EVENT_STREAM_RPC_SERVICE_HANDLER
            = ClassName.get(EVENT_STREAM_RPC_PACKAGE,
            "EventStreamRPCServiceHandler");

    public static final ClassName CN_EVENT_STREAM_RPC_SERVICE_MODEL
            = ClassName.get(EVENT_STREAM_RPC_PACKAGE,
            "EventStreamRPCServiceModel");

    public static final ClassName CN_OPERATION_MODEL_CONTEXT
            = ClassName.get(EVENT_STREAM_RPC_PACKAGE,
            "OperationModelContext");

    public static final ClassName CN_SERVER_CONNECTION_CONTINUATION_HANDLER
            = ClassName.get("software.amazon.awssdk.crt.eventstream",
            "ServerConnectionContinuationHandler");

    public static final ClassName CN_OPERATION_CONTINUATION_HANDLER
            = ClassName.get(EVENT_STREAM_RPC_PACKAGE,
            "OperationContinuationHandler");

    public static final ClassName CN_OPERATION_CONTINUATION_HANDLER_CONTEXT
            = ClassName.get(EVENT_STREAM_RPC_PACKAGE,
            "OperationContinuationHandlerContext");

    public static final ClassName CN_STREAM_RESPONSE
            = ClassName.get(EVENT_STREAM_RPC_PACKAGE,
            "StreamResponse");

    public static final ClassName CN_OPERATION_RESPONSE
            = ClassName.get(EVENT_STREAM_RPC_PACKAGE,
            "OperationResponse");

    public static final ClassName CN_STREAM_RESPONSE_HANDLER
            = ClassName.get(EVENT_STREAM_RPC_PACKAGE,
            "StreamResponseHandler");

    public static final ClassName CN_EVENT_STREAM_RPC_CLIENT
            = ClassName.get(EVENT_STREAM_RPC_PACKAGE,
            "EventStreamRPCClient");

    public static final ClassName CN_EVENT_STREAM_RPC_CONNECTION
            = ClassName.get(EVENT_STREAM_RPC_PACKAGE,
            "EventStreamRPCConnection");

    public static final String FIELD_APPLICATION_MODEL_TYPE = "APPLICATION_MODEL_TYPE";
    public static final String FIELD_VOID = "VOID";

    public static FieldSpec.Builder buildStandardMemberField(final TypeName memberTypeName, final String memberName, final Shape memberTypeShape) {
        final FieldSpec.Builder memberFieldBuilder = FieldSpec.builder(
                TypeNameShapeVisitor.optionalWrap(memberTypeName),
                memberName, Modifier.PRIVATE)
                .addAnnotation(AnnotationSpec.builder(ClassName
                        .get("com.google.gson.annotations", "Expose"))
                        .addMember("serialize", "true")
                        .addMember("deserialize", "true")
                        .build());
        if (memberTypeShape.hasTrait(JsonNameTrait.class)) {
            final JsonNameTrait nameTrait = memberTypeShape.getTrait(JsonNameTrait.class).get();
            memberFieldBuilder.addAnnotation(AnnotationSpec.builder(
                    PoetryWriter.CN_GSON_SERIALIZED_NAME)
                    .addMember("$S", nameTrait.getValue())
                    .build());
        }
        return memberFieldBuilder;
    }



}
