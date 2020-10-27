package software.amazon.smithy.eventstreamrpc.java.server;

import com.squareup.javapoet.*;
import software.amazon.smithy.eventstreamrpc.java.PoetryWriter;
import software.amazon.smithy.eventstreamrpc.java.ServiceCodegenContext;
import software.amazon.smithy.model.shapes.OperationShape;

import javax.lang.model.element.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * Produces the file that is used as the
 */
public class OperationContinuationHandlerFactoryBuilder implements Supplier<JavaFile> {
    private static final Logger LOGGER = Logger.getLogger(OperationContinuationHandlerFactoryBuilder.class.getName());

    private static final String FIELD_SERVICE_NAMESPACE = "SERVICE_NAMESPACE";
    private static final String FIELD_SERVICE_OPERATION_SET = "SERVICE_OPERATION_SET";
    private static final String FIELD_OPERATION_SUPPLIER_MAP = "operationSupplierMap";

    private final ServiceCodegenContext context;

    public OperationContinuationHandlerFactoryBuilder(ServiceCodegenContext context) {
        this.context = context;
    }

    public static ClassName getOperationAbstractHandlerClassName(ServiceCodegenContext context, OperationShape opShape) {
        return ClassName.get(context.getBaseServicePackage(), "GeneratedAbstract" + opShape.getId().getName() + "OperationHandler");
    }

    public static ClassName getContinuationHandlerClassName(final ServiceCodegenContext context) {
        return ClassName.get(context.getBaseServicePackage(),
                context.getServiceName().getName() + "Service");
    }

    @Override
    public JavaFile get() {
        final ClassName className = getContinuationHandlerClassName(context);
        final TypeSpec.Builder classBuilder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .superclass(PoetryWriter.CN_EVENT_STREAM_RPC_SERVICE_HANDLER);

        classBuilder.addField(FieldSpec.builder(ClassName.get(String.class), FIELD_SERVICE_NAMESPACE,
                Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$S", context.getServiceShape().getId().getNamespace()).build());

        classBuilder.addField(FieldSpec.builder(
                ParameterizedTypeName.get(ClassName.get(java.util.Set.class), ClassName.get(String.class)),
                FIELD_SERVICE_OPERATION_SET, Modifier.PROTECTED, Modifier.STATIC, Modifier.FINAL).build());

        classBuilder.addMethod(MethodSpec.methodBuilder("getServiceModel")
                .addAnnotation(Override.class)
                .addStatement("return $T.getInstance()", context.getServiceModelClassName())
                .returns(PoetryWriter.CN_EVENT_STREAM_RPC_SERVICE_MODEL)
                .addModifiers(Modifier.PUBLIC)
            .build());

        final CodeBlock.Builder allOperationsBlockBuilder = CodeBlock.builder();
        allOperationsBlockBuilder.addStatement("$L = new $T()", FIELD_SERVICE_OPERATION_SET, ClassName.get(HashSet.class));

        //iterate through all operations and add String constants for them
        context.getServiceShape().getAllOperations().stream()
                .map(opShapeId -> (OperationShape)context.getModel().getShape(opShapeId).get())
                .forEach(operationShape -> {
                    final String operationConstantName = context.getOperationConstantName(operationShape);
                    classBuilder.addField(FieldSpec.builder(ClassName.get(String.class),
                            operationConstantName, Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                            .initializer("$L + $S", FIELD_SERVICE_NAMESPACE, "#" + operationShape.getId().getName())
                            .build());

                    allOperationsBlockBuilder
                            .addStatement("$L.add($L)", FIELD_SERVICE_OPERATION_SET, operationConstantName);

                    final String handlerSetName = "set" + operationShape.getId().getName() + "Handler";
                    classBuilder.addMethod(MethodSpec.methodBuilder(handlerSetName)
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(ParameterSpec.builder(
                                    ParameterizedTypeName.get(ClassName.get(java.util.function.Function.class),
                                    PoetryWriter.CN_OPERATION_CONTINUATION_HANDLER_CONTEXT,
                                    getOperationAbstractHandlerClassName(context, operationShape)), "handler").build())
                            .addStatement("$L.put($L, handler)", FIELD_OPERATION_SUPPLIER_MAP, operationConstantName)
                            .build());
                });

        //add static block which populates all operations to static set
        classBuilder.addStaticBlock(allOperationsBlockBuilder.build());

        classBuilder.addField(FieldSpec.builder(
                ParameterizedTypeName.get(ClassName.get(java.util.Map.class), ClassName.get(String.class),
                        ParameterizedTypeName.get(ClassName.get(java.util.function.Function.class),
                                PoetryWriter.CN_OPERATION_CONTINUATION_HANDLER_CONTEXT,
                                WildcardTypeName.subtypeOf(PoetryWriter.CN_SERVER_CONNECTION_CONTINUATION_HANDLER))
                ), FIELD_OPERATION_SUPPLIER_MAP, Modifier.FINAL, Modifier.PRIVATE).build());

        //initialize operation supplier map instance
        classBuilder.addMethod(MethodSpec.constructorBuilder()
                .addStatement("this.$L = new $T()", FIELD_OPERATION_SUPPLIER_MAP, HashMap.class)
                .addModifiers(Modifier.PUBLIC).build());

        classBuilder.addMethod(MethodSpec.methodBuilder("getAllOperations")
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return $L", FIELD_SERVICE_OPERATION_SET)
                .addAnnotation(Override.class)
                .returns(ParameterizedTypeName.get(ClassName.get(java.util.Set.class), ClassName.get(String.class)))
                    .build());

        classBuilder.addMethod(MethodSpec.methodBuilder("hasHandlerForOperation")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(ClassName.get(String.class), "operation").build())
                .addStatement("return $L.containsKey(operation)", FIELD_OPERATION_SUPPLIER_MAP)
                .addAnnotation(Override.class)
                .returns(TypeName.BOOLEAN)
                .build());

        classBuilder.addMethod(MethodSpec.methodBuilder("getOperationHandler")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(ClassName.get(String.class), "operation").build())
                .addStatement("return $L.get(operation)", FIELD_OPERATION_SUPPLIER_MAP)
                .addAnnotation(Override.class)
                .returns(ParameterizedTypeName.get(ClassName.get(java.util.function.Function.class),
                                PoetryWriter.CN_OPERATION_CONTINUATION_HANDLER_CONTEXT,
                                WildcardTypeName.subtypeOf(PoetryWriter.CN_SERVER_CONNECTION_CONTINUATION_HANDLER)))
                .build());

        //add a generic set operation handler method. Though intended for debugging/convenience in non-normal
        //cases, it could do some runtime interesting things and a service could add an operation handler at
        //runtime intentionally
        classBuilder.addMethod(MethodSpec.methodBuilder("setOperationHandler")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(ClassName.get(String.class), "operation").build())
                .addParameter(ParameterizedTypeName.get(ClassName.get(java.util.function.Function.class),
                        PoetryWriter.CN_OPERATION_CONTINUATION_HANDLER_CONTEXT,
                        WildcardTypeName.subtypeOf(PoetryWriter.CN_SERVER_CONNECTION_CONTINUATION_HANDLER)), "handler")
                .addStatement("$L.put(operation, handler)", FIELD_OPERATION_SUPPLIER_MAP)
                .build());

        return JavaFile.builder(className.packageName(), classBuilder.build()).build();
    }
}
