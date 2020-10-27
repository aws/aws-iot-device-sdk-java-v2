package software.amazon.smithy.eventstreamrpc.java.model;

import com.squareup.javapoet.*;
import software.amazon.smithy.eventstreamrpc.java.PoetryWriter;
import software.amazon.smithy.eventstreamrpc.java.ServiceCodegenContext;
import software.amazon.smithy.model.shapes.OperationShape;

import javax.lang.model.element.Modifier;
import java.util.Optional;
import java.util.function.Function;

/**
 * Simple class generator just to hold all of the input/output metadata in Java for
 * an operation attached to a service.
 */
public class OperationModelContextClassBuilder implements Function<OperationShape, JavaFile> {
    private final ServiceCodegenContext context;

    public OperationModelContextClassBuilder(final ServiceCodegenContext context) {
        this.context = context;
    }

    @Override
    public JavaFile apply(final OperationShape operationShape) {
        final ClassName className = context.getOperationModelContextClassName(operationShape);
        //package level visibility should be all that's needed. Only service model should need to
        //instantiate these
        final TypeSpec.Builder classBuilder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC);

        final ClassName requestClassName = context.getOperationRequestClassName(operationShape);
        final ClassName responseClassName = context.getOperationResponseClassName(operationShape);
        final ClassName streamingRequestClassName = context.getOperationStreamingRequestClassName(operationShape);
        final ClassName streamingResponseClassName = context.getOperationStreamingResponseClassName(operationShape);

        classBuilder.addSuperinterface(
                ParameterizedTypeName.get(PoetryWriter.CN_OPERATION_MODEL_CONTEXT,
                        requestClassName, responseClassName, streamingRequestClassName, streamingResponseClassName));

        classBuilder.addMethod(MethodSpec.methodBuilder("getServiceModel")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return $T.getInstance()",
                        context.getServiceModelClassName())
                .returns(PoetryWriter.CN_EVENT_STREAM_RPC_SERVICE_MODEL)
                .build());

        classBuilder.addMethod(MethodSpec.methodBuilder("getOperationName")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return $T.$L",
                        context.getServiceModelClassName(),
                        context.getOperationConstantName(operationShape))
                .returns(ClassName.get(String.class))
                .build());

        classBuilder.addMethod(MethodSpec.methodBuilder("getRequestTypeClass")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return $T.class", requestClassName)
                .returns(ParameterizedTypeName.get(ClassName.get(Class.class),
                        requestClassName))
                    .build());

        classBuilder.addMethod(MethodSpec.methodBuilder("getResponseTypeClass")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return $T.class", responseClassName)
                .returns(ParameterizedTypeName.get(ClassName.get(Class.class),
                        responseClassName))
                .build());

        classBuilder.addMethod(MethodSpec.methodBuilder("getRequestApplicationModelType")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return $T.APPLICATION_MODEL_TYPE", requestClassName)
                .returns(ClassName.get(String.class))
                .build());

        classBuilder.addMethod(MethodSpec.methodBuilder("getResponseApplicationModelType")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return $T.APPLICATION_MODEL_TYPE", responseClassName)
                .returns(ClassName.get(String.class))
                .build());

        final MethodSpec.Builder getStrReqTypeMethodBuilder = MethodSpec.methodBuilder("getStreamingRequestTypeClass")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(ParameterizedTypeName.get(ClassName.get(Optional.class),
                        ParameterizedTypeName.get(ClassName.get(Class.class),
                                streamingRequestClassName)));
        final MethodSpec.Builder getStrReqAppTypeMethodBuilder = MethodSpec.methodBuilder("getStreamingRequestApplicationModelType")
                .addModifiers(Modifier.PUBLIC)
                .returns(ParameterizedTypeName.get(ClassName.get(Optional.class), ClassName.get(String.class)));

        if (context.getInputEventStreamInfo(operationShape).isPresent()) {
            getStrReqTypeMethodBuilder.addStatement("return $T.of($T.class)",
                    ClassName.get(Optional.class), streamingRequestClassName);
            getStrReqAppTypeMethodBuilder.addStatement("return $T.of($T.APPLICATION_MODEL_TYPE)",
                    ClassName.get(Optional.class), streamingRequestClassName);
        } else {
            getStrReqTypeMethodBuilder.addStatement("return $T.empty()", ClassName.get(Optional.class));
            getStrReqAppTypeMethodBuilder.addStatement("return $T.empty()", ClassName.get(Optional.class));
        }

        final MethodSpec.Builder getStrRespTypeMethodBuilder = MethodSpec.methodBuilder("getStreamingResponseTypeClass")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(ParameterizedTypeName.get(ClassName.get(Optional.class),
                        ParameterizedTypeName.get(ClassName.get(Class.class),
                                streamingResponseClassName)));
        final MethodSpec.Builder getStrRespAppTypeMethodBuilder = MethodSpec.methodBuilder("getStreamingResponseApplicationModelType")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(ParameterizedTypeName.get(ClassName.get(Optional.class), ClassName.get(String.class)));
        if (context.getOutputEventStreamInfo(operationShape).isPresent()) {
            getStrRespTypeMethodBuilder.addStatement("return $T.of($T.class)",
                    ClassName.get(Optional.class), streamingResponseClassName);
            getStrRespAppTypeMethodBuilder.addStatement("return $T.of($T.APPLICATION_MODEL_TYPE)",
                    ClassName.get(Optional.class), streamingResponseClassName);
        } else {
            getStrRespTypeMethodBuilder.addStatement("return $T.empty()", ClassName.get(Optional.class));
            getStrRespAppTypeMethodBuilder.addStatement("return $T.empty()", ClassName.get(Optional.class));
        }

        classBuilder.addMethod(getStrReqTypeMethodBuilder.build());
        classBuilder.addMethod(getStrRespTypeMethodBuilder.build());
        classBuilder.addMethod(getStrReqAppTypeMethodBuilder.build());
        classBuilder.addMethod(getStrRespAppTypeMethodBuilder.build());

        return JavaFile.builder(className.packageName(), classBuilder.build()).build();
    }
}
