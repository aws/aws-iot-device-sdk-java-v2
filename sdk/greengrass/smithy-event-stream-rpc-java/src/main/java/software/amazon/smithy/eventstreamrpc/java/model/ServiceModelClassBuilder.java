package software.amazon.smithy.eventstreamrpc.java.model;

import com.squareup.javapoet.*;
import software.amazon.smithy.eventstreamrpc.java.DataModelObject;
import software.amazon.smithy.eventstreamrpc.java.NameUtils;
import software.amazon.smithy.eventstreamrpc.java.PoetryWriter;
import software.amazon.smithy.eventstreamrpc.java.ServiceCodegenContext;
import software.amazon.smithy.model.shapes.OperationShape;

import javax.lang.model.element.Modifier;
import java.util.*;
import java.util.function.Supplier;

public class ServiceModelClassBuilder implements Supplier<JavaFile> {
    private static final String FIELD_SERVICE_NAMESPACE = "SERVICE_NAMESPACE";
    private static final String FIELD_SERVICE_NAME = "SERVICE_NAME";
    private static final String FIELD_SERVICE_OPERATION_SET = "SERVICE_OPERATION_SET";
    private static final String FIELD_SERVICE_OPERATION_MODEL_MAP = "SERVICE_OPERATION_MODEL_MAP";
    private static final String FIELD_SERVICE_OBJECT_MODEL_MAP = "SERVICE_OBJECT_MODEL_MAP";

    private final ServiceCodegenContext context;

    public ServiceModelClassBuilder(ServiceCodegenContext context) {
        this.context = context;
    }

    public static String getOperationModelGetterName(final OperationShape operationShape) {
        return "get" + operationShape.getId().getName() + "ModelContext";
    }

    @Override
    public JavaFile get() {
        final ClassName className = context.getServiceModelClassName();
        final TypeSpec.Builder classBuilder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .superclass(PoetryWriter.CN_EVENT_STREAM_RPC_SERVICE_MODEL);

        classBuilder.addField(FieldSpec.builder(className, "INSTANCE")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer("new $T()", className)
                .build());

        //not quite singleton because we don't really care for getInstance() to
        //control initialization of the
        classBuilder.addMethod(MethodSpec.methodBuilder("getInstance")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addStatement("return INSTANCE")
                .returns(className)
                .build());

        classBuilder.addMethod(MethodSpec.methodBuilder("getServiceName")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return $S", context.getServiceName().toString())
                .returns(ClassName.get(String.class))
                .build());

        //private constructor for some enforcement of only having one around
        classBuilder.addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build());

        classBuilder.addField(FieldSpec.builder(ClassName.get(String.class), FIELD_SERVICE_NAMESPACE,
                Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$S", context.getServiceShape().getId().getNamespace()).build());

        classBuilder.addField(FieldSpec.builder(ClassName.get(String.class), FIELD_SERVICE_NAME,
                Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$L + $S + $S", FIELD_SERVICE_NAMESPACE, "#",
                        context.getServiceShape().getId().getName()).build());

        classBuilder.addField(FieldSpec.builder(
                ParameterizedTypeName.get(ClassName.get(java.util.Set.class), ClassName.get(String.class)),
                FIELD_SERVICE_OPERATION_SET, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                    .initializer("new $T()",
                            ParameterizedTypeName.get(ClassName.get(HashSet.class), ClassName.get(String.class)))
                .build());

        classBuilder.addField(FieldSpec.builder(
                ParameterizedTypeName.get(ClassName.get(java.util.Map.class),
                        ClassName.get(String.class), PoetryWriter.CN_OPERATION_MODEL_CONTEXT),
                FIELD_SERVICE_OPERATION_MODEL_MAP, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer("new $T()",
                        ParameterizedTypeName.get(ClassName.get(HashMap.class),
                                ClassName.get(String.class), PoetryWriter.CN_OPERATION_MODEL_CONTEXT))
                .build());

        classBuilder.addField(FieldSpec.builder(
                ParameterizedTypeName.get(ClassName.get(java.util.Map.class),
                        ClassName.get(String.class), ParameterizedTypeName.get(ClassName.get(Class.class),
                                WildcardTypeName.subtypeOf(PoetryWriter.CN_EVENT_STREAMABLE_JSON_MESSAGE))),
                FIELD_SERVICE_OBJECT_MODEL_MAP, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer("new $T()",
                        ParameterizedTypeName.get(ClassName.get(java.util.HashMap.class),
                                ClassName.get(String.class), ParameterizedTypeName.get(ClassName.get(Class.class),
                                        WildcardTypeName.subtypeOf(PoetryWriter.CN_EVENT_STREAMABLE_JSON_MESSAGE))))
                .build());

        final CodeBlock.Builder staticOperationSetBlockBuilder = CodeBlock.builder();

        //loop through all operations and populate internals accordingly
        context.getAllOperations().stream().forEach(operationShape -> {
            final String operationConstantName = context.getOperationConstantName(operationShape);
            classBuilder.addField(FieldSpec.builder(ClassName.get(String.class),
                    operationConstantName,
                    Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("$L + $S + $S", FIELD_SERVICE_NAMESPACE, "#",
                            operationShape.getId().getName()).build());

            final ClassName operationModelClassName = context.getOperationModelContextClassName(operationShape);
            final String operationModelFieldName = "_" + NameUtils.camelToConstantCase(operationModelClassName.simpleName());
            classBuilder.addField(FieldSpec.builder(operationModelClassName, operationModelFieldName)
                    .addModifiers(Modifier.STATIC, Modifier.FINAL, Modifier.PRIVATE)
                    .initializer("new $T()", operationModelClassName)
                    .build());

            //add operation model context to map
            staticOperationSetBlockBuilder.addStatement("$L.put($L, $L)",
                    FIELD_SERVICE_OPERATION_MODEL_MAP, operationConstantName, operationModelFieldName);

            classBuilder.addMethod(MethodSpec.methodBuilder(getOperationModelGetterName(operationShape))
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addStatement("return $L", operationModelFieldName)
                    .returns(operationModelClassName)
                    .build());

            staticOperationSetBlockBuilder.addStatement("$L.add($L)",
                    FIELD_SERVICE_OPERATION_SET, operationConstantName);
        });

        final Iterator<DataModelObject> dataModelObjectIterator = context.getAllShapesIterator();
        while (dataModelObjectIterator.hasNext()) {
            final DataModelObject dataModelObject = dataModelObjectIterator.next();
            staticOperationSetBlockBuilder.addStatement("$L.put($T.$L, $T.class)",
                    FIELD_SERVICE_OBJECT_MODEL_MAP, dataModelObject.getClassName(),
                    PoetryWriter.FIELD_APPLICATION_MODEL_TYPE, dataModelObject.getClassName());
        }

        classBuilder.addMethod(MethodSpec.methodBuilder("getAllOperations")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addComment("Return a defensive copy so caller cannot change internal structure of service model")
                .addStatement("return new $T($L)",
                        ParameterizedTypeName.get(ClassName.get(HashSet.class), ClassName.get(String.class)),
                        FIELD_SERVICE_OPERATION_SET)
                .returns(ParameterizedTypeName.get(ClassName.get(Collection.class), ClassName.get(String.class)))
        .build());

        //protected abstract Optional<Class<? extends EventStreamJsonMessage>> getServiceClassType(String applicationModelType);
        //need to have a build up of all of the model types in the service
        classBuilder.addMethod(MethodSpec.methodBuilder("getServiceClassType")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PROTECTED)
                .addParameter(ParameterSpec.builder(ClassName.get(String.class), "applicationModelType").build())
                .addCode(CodeBlock.builder()
                        .beginControlFlow("if ($L.containsKey(applicationModelType))", FIELD_SERVICE_OBJECT_MODEL_MAP)
                            .addStatement("return $T.of($L.get(applicationModelType))",
                                    ClassName.get(Optional.class), FIELD_SERVICE_OBJECT_MODEL_MAP)
                        .endControlFlow()
                        .addStatement("return $T.empty()", ClassName.get(Optional.class))
                        .build())
                .returns(ParameterizedTypeName.get(ClassName.get(Optional.class),
                        ParameterizedTypeName.get(ClassName.get(Class.class),
                                WildcardTypeName.subtypeOf(PoetryWriter.CN_EVENT_STREAMABLE_JSON_MESSAGE))))
                .build());

        classBuilder.addStaticBlock(staticOperationSetBlockBuilder.build());

        classBuilder.addMethod(MethodSpec.methodBuilder("getOperationModelContext")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(ClassName.get(String.class), "operationName").build())
                .addStatement("return $L.get(operationName)", FIELD_SERVICE_OPERATION_MODEL_MAP)
                .returns(PoetryWriter.CN_OPERATION_MODEL_CONTEXT)
                .build());

        return JavaFile.builder(className.packageName(), classBuilder.build()).build();
    }
}
