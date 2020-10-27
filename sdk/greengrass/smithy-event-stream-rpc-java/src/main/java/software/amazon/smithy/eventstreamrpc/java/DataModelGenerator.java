package software.amazon.smithy.eventstreamrpc.java;

import com.squareup.javapoet.*;
import software.amazon.smithy.codegen.core.CodegenException;
import software.amazon.smithy.eventstreamrpc.java.model.*;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.*;
import software.amazon.smithy.model.traits.EnumTrait;

import javax.lang.model.element.Modifier;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DataModelGenerator implements JavaFileGenerator {
    private static final Logger LOGGER = Logger.getLogger(DataModelGenerator.class.getName());

    private final ServiceCodegenContext context;
    private final Model model;

    public DataModelGenerator(final ServiceCodegenContext context, final Model model) {
        this.context = context;
        this.model = model;
    }

    /**
     * Kind of a dumb way of saying "produce Java files here for this service, and don't worry about it"
     *
     * Cenerating a data model for a service includes:
     *
     * 1. All structures and unions used as inputs and outputs to operations on the service
     * 2. All errors producable by the service's operations
     * 3. All nested structures that are a part of #1 and #2
     * 4. Default input's and outputs for non-specified operation inputs and outputs (framework backwards
     *    -compatibility protection)
     * 5. Service level error type that all explicit error types inherit from so client code can have more
     *    generic exception handling
     *
     * Edge cases:
     * Any streaming child shape
     *
     * @param javaFileConsumer
     */
    @Override
    public void accept(final Consumer<JavaFile> javaFileConsumer) {
        final UnionClassBuilder unionClassBuilder = new UnionClassBuilder(context);
        final StructureClassBuilder structureClassBuilder = new StructureClassBuilder(context);
        final OperationModelContextClassBuilder omcClassBuilder = new OperationModelContextClassBuilder(context);
        final ServiceModelClassBuilder smClassBuilder = new ServiceModelClassBuilder(context);
        final StringEnumBuilder stringEnumBuilder = new StringEnumBuilder(context);
        final Collection<Shape> shapesToGenerate = new LinkedList<>();

        LOGGER.info("Generating data model for service: " + context.getServiceShape().getId().toString());

        javaFileConsumer.accept(smClassBuilder.get());

        //first collect all direct operation inputs, outputs, and errors from the top
        context.getAllOperations().stream().forEach(operationShape -> {
            if (operationShape.getInput().isPresent()) {
                shapesToGenerate.add(model.getShape(operationShape.getInput().get()).get());
            }
            else {
                final TypeSpec.Builder requestClassBuilder = emptyRequestClassBuilder(operationShape);
                requestClassBuilder.addJavadoc("Generated empty model type not defined in model");
                javaFileConsumer.accept(JavaFile.builder(context.getBaseModelPackage(),
                        requestClassBuilder.build()).build());
            }

            if (operationShape.getOutput().isPresent()) {
                shapesToGenerate.add(model.getShape(operationShape.getOutput().get()).get());
            }
            else {
                final TypeSpec.Builder responseClassBuilder = emptyResponseClassBuilder(operationShape);
                responseClassBuilder.addJavadoc("Generated empty model type not defined in model");
                javaFileConsumer.accept(JavaFile.builder(context.getBaseModelPackage(),
                        responseClassBuilder.build()).build());
            }

            //add all error shapes
            shapesToGenerate.addAll(operationShape.getErrors().stream()
                    .map(errorShapeId -> model.getShape(errorShapeId).get())
                    .collect(Collectors.toList()));

            javaFileConsumer.accept(omcClassBuilder.apply(operationShape));
        });

        final Iterator<DataModelObject> itr = context.getAllShapesIterator();
        while (itr.hasNext()) {
            final DataModelObject dataModelObject = itr.next();
            if (dataModelObject.getDataShape().isPresent()) {
                final Shape shape = dataModelObject.getDataShape().get();
                if (shape.getType().equals(ShapeType.STRUCTURE)) {
                    final StructureShape structureShape = (StructureShape)shape;
                    javaFileConsumer.accept(structureClassBuilder.apply(context.getClassName(shape), structureShape));
                } else if (shape.getType().equals(ShapeType.UNION)) {
                    final UnionShape unionShape = (UnionShape)shape;
                    javaFileConsumer.accept(unionClassBuilder.apply(unionShape));
                } else if (shape.getType().equals(ShapeType.STRING) && shape.hasTrait(EnumTrait.class)) {
                    final StringShape stringShape = (StringShape)shape;
                    javaFileConsumer.accept(stringEnumBuilder.apply(stringShape));
                } else if (shape.getId().getNamespace().equals(context.getServiceShape().getId().getNamespace())
                        && !(shape instanceof CollectionShape) &&  !(shape instanceof MapShape)) {
                    //we are concerned about shape definitions with local type names that we aren't generating
                    //anything for. Collection type aliases, we ignore and just cut through to their raw
                    //types always. Only their field names are meaningful. If we haven't caught a type defined
                    //in the model that may need generation otherwise, throw exception
                    throw new CodegenException("No generator for type with shape ID: " + shape.getId().toString());
                }
            } else {
                //empty shapes are always basic objects extending from EventStreamableJsonMessage
                /* //a valid way to generate empty types that may not be associated with operations?
                final TypeSpec.Builder emptyModel = TypeSpec.classBuilder(dataModelObject.getClassName())
                        .addSuperinterface(PoetryWriter.CN_EVENT_STREAMABLE_JSON_MESSAGE)
                        .addModifiers(Modifier.PUBLIC);
                populateEmptyClassCore(dataModelObject.getClassName(), emptyModel, dataModelObject.getApplicationModelType());
                javaFileConsumer.accept(JavaFile.builder(dataModelObject.getClassName().packageName(), emptyModel.build()).build());
                */
            }
        }

        //generate base error type (cannot be serialized over the wire
        LOGGER.fine("Generating base error type for service: " + context.getServiceShape().getId().toString());
        javaFileConsumer.accept(generateServiceBaseErrorType());
    }

    @Override
    public String getOutputSubdirectory() {
        return "model";
    }

    private JavaFile generateServiceBaseErrorType() {
        final ClassName errorClassName = context.getServiceBaseErrorClassName();
        final TypeSpec.Builder errorClassBuilder = TypeSpec.classBuilder(errorClassName)
                .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                .addSuperinterface(PoetryWriter.CN_EVENT_STREAMABLE_JSON_MESSAGE)
                .superclass(PoetryWriter.CN_EVENT_STREAM_OPERATION_ERROR)
                .addMethod(MethodSpec.constructorBuilder()
                        .addParameter(ParameterSpec.builder(ClassName.get(String.class), "errorCode").build())
                        .addParameter(ParameterSpec.builder(ClassName.get(String.class), "errorMessage").build())
                        .addStatement("super($S, $L, $L)", context.getServiceName(), "errorCode", "errorMessage")
                        .build())
                //getErrorTypeString() method must be implemented in the StructureClassBuilder if it is an error
                .addMethod(MethodSpec.methodBuilder("getErrorTypeString")
                        .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                        .returns(ClassName.get(String.class)).build())
                .addMethod(MethodSpec.methodBuilder("isRetryable")
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("return getErrorTypeString().equals(\"server\")")
                        .returns(TypeName.BOOLEAN).build())
                .addMethod(MethodSpec.methodBuilder("isServerError")
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("return getErrorTypeString().equals(\"server\")")
                        .returns(TypeName.BOOLEAN).build())
                .addMethod(MethodSpec.methodBuilder("isClientError")
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("return getErrorTypeString().equals(\"client\")")
                        .returns(TypeName.BOOLEAN).build());

        return JavaFile.builder(context.getBaseModelPackage(), errorClassBuilder.build()).build();
    }

    private TypeSpec.Builder emptyRequestClassBuilder(final OperationShape operationShape) {
        final ClassName emptyModelClassName = context.getEmptyRequestClassName(operationShape);
        final TypeSpec.Builder classBuilder = TypeSpec.classBuilder(emptyModelClassName)
                .addSuperinterface(PoetryWriter.CN_EVENT_STREAMABLE_JSON_MESSAGE)
                .addModifiers(Modifier.PUBLIC);

        populateEmptyClassCore(emptyModelClassName, classBuilder, context.getEmptyRequestApplicationType(operationShape));

        return classBuilder;
    }

    private TypeSpec.Builder emptyResponseClassBuilder(final OperationShape operationShape) {
        final ClassName emptyModelClassName = context.getEmptyResponseClassName(operationShape);
        final TypeSpec.Builder classBuilder = TypeSpec.classBuilder(emptyModelClassName)
                .addSuperinterface(PoetryWriter.CN_EVENT_STREAMABLE_JSON_MESSAGE)
                .addModifiers(Modifier.PUBLIC);

        populateEmptyClassCore(emptyModelClassName, classBuilder, context.getEmptyResponseApplicationType(operationShape));

        return classBuilder;
    }

    public static void populateEmptyClassCore(final ClassName className, final TypeSpec.Builder classBuilder,
                final String applicationType) {
        //required static field even for generated model types
        classBuilder.addField(FieldSpec.builder(ClassName.get(String.class), PoetryWriter.FIELD_APPLICATION_MODEL_TYPE)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$S", applicationType)
                .build());

        //required static field even for generated model types
        classBuilder.addField(FieldSpec.builder(className, PoetryWriter.FIELD_VOID)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL).build());
        classBuilder.addStaticBlock(CodeBlock.builder()
                .addStatement("$L = $L", PoetryWriter.FIELD_VOID,
                        TypeSpec.anonymousClassBuilder("")
                                .superclass(className)
                                .addMethod(MethodSpec.methodBuilder("isVoid")
                                        .addAnnotation(Override.class)
                                        .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                                        .addStatement("return true")
                                        .returns(TypeName.BOOLEAN)
                                        .build())
                                .build())
                .build());

        //application type is the anticipated smithy model ID
        final MethodSpec.Builder getApplicationModelTypeBuilder = MethodSpec.methodBuilder("getApplicationModelType")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addStatement("return $L", PoetryWriter.FIELD_APPLICATION_MODEL_TYPE)
                .returns(ClassName.get(String.class));
        classBuilder.addMethod(getApplicationModelTypeBuilder.build());

        //empty responses are considered to be void. These are likely the only types where they are wanted
        //to be void
        classBuilder.addMethod(MethodSpec.methodBuilder("isVoid")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return true")
                .returns(TypeName.BOOLEAN)
                .build());

        //hash code for empty object types is that they all have the same hash value as the class itself
        //so every instance is equivalent as a key in a hash structure
        classBuilder.addMethod(MethodSpec.methodBuilder("hashCode")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return $T.hash($T.class)", ClassName.get(Objects.class), className)
                .returns(TypeName.INT)
                .build());

        //logic for equals on an empty object types is that it is always equal
        final String rhsParam = "rhs";
        classBuilder.addMethod(MethodSpec.methodBuilder("equals")
                .addAnnotation(Override.class)
                .addParameter(ParameterSpec.builder(ClassName.get(Object.class), rhsParam).build())
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.BOOLEAN)
                .addStatement("if ($L == null) return false", rhsParam)
                .addStatement("return ($L instanceof $T)", rhsParam, className)
                .build());
    }
}
