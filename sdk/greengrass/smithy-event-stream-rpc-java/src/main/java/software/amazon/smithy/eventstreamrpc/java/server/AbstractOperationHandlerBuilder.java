package software.amazon.smithy.eventstreamrpc.java.server;

import com.squareup.javapoet.*;
import software.amazon.smithy.eventstreamrpc.java.NameUtils;
import software.amazon.smithy.eventstreamrpc.java.PoetryWriter;
import software.amazon.smithy.eventstreamrpc.java.ServiceCodegenContext;
import software.amazon.smithy.model.shapes.OperationShape;

import javax.lang.model.element.Modifier;
import java.util.function.Function;
import java.util.logging.Logger;

public class AbstractOperationHandlerBuilder implements Function<OperationShape, JavaFile> {
    private static final Logger LOGGER = Logger.getLogger(AbstractOperationHandlerBuilder.class.getName());

    private final ServiceCodegenContext context;

    public AbstractOperationHandlerBuilder(final ServiceCodegenContext context) {
        this.context = context;
    }

    @Override
    public JavaFile apply(final OperationShape operationShape) {
        final ClassName className = OperationContinuationHandlerFactoryBuilder
                .getOperationAbstractHandlerClassName(context, operationShape);

        final ClassName requestClassName = context.getOperationRequestClassName(operationShape);
        final ClassName responseClassName = context.getOperationResponseClassName(operationShape);
        final ClassName streamingRequestClassName = context.getOperationStreamingRequestClassName(operationShape);
        final ClassName streamingResponseClassName = context.getOperationStreamingResponseClassName(operationShape);

        final TypeSpec.Builder classBuilder = TypeSpec.classBuilder(className.simpleName())
                .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                .superclass(ParameterizedTypeName.get(PoetryWriter.CN_OPERATION_CONTINUATION_HANDLER,
                    requestClassName, responseClassName, streamingRequestClassName, streamingResponseClassName));

        classBuilder.addMethod(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PROTECTED)
                .addParameter(
                        ParameterSpec.builder(PoetryWriter.CN_OPERATION_CONTINUATION_HANDLER_CONTEXT, "context")
                                .build())
                .addStatement("super(context)")
                .build());

        classBuilder.addMethod(MethodSpec.methodBuilder("getOperationModelContext")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return $T.$L()",
                        context.getServiceModelClassName(),
                        "get" + NameUtils.capitalize(operationShape.getId().getName()) + "ModelContext")
                .returns(ParameterizedTypeName.get(PoetryWriter.CN_OPERATION_MODEL_CONTEXT,
                        requestClassName, responseClassName, streamingRequestClassName, streamingResponseClassName))
                .build());

        return JavaFile.builder(className.packageName(), classBuilder.build()).build();
    }


}
