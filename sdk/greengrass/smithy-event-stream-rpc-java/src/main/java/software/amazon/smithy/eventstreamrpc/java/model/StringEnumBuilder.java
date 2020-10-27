package software.amazon.smithy.eventstreamrpc.java.model;

import com.squareup.javapoet.*;
import software.amazon.smithy.eventstreamrpc.java.PoetryWriter;
import software.amazon.smithy.eventstreamrpc.java.ServiceCodegenContext;
import software.amazon.smithy.model.shapes.StringShape;
import software.amazon.smithy.model.traits.EnumTrait;

import javax.lang.model.element.Modifier;
import java.util.function.Function;
import java.util.logging.Logger;

public class StringEnumBuilder implements Function<StringShape, JavaFile> {
    private static final Logger LOGGER = Logger.getLogger(StringEnumBuilder.class.getName());

    private final ServiceCodegenContext context;

    public StringEnumBuilder(final ServiceCodegenContext context) {
        this.context = context;
    }

    @Override
    public JavaFile apply(StringShape stringShape) {
        final ClassName className = context.getClassName(stringShape);
        LOGGER.fine("Processing string enum shape ID: "
                + stringShape.getId().toString() + "; with symbol: " + className.canonicalName());

        final TypeSpec.Builder enumBuilder
                = TypeSpec.enumBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(PoetryWriter.CN_EVENT_STREAMABLE_JSON_MESSAGE);

        enumBuilder.addField(FieldSpec.builder(ClassName.get(String.class), PoetryWriter.FIELD_APPLICATION_MODEL_TYPE)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$S", stringShape.getId()).build());

        //add constructor and field to set the value
        enumBuilder.addField(FieldSpec.builder(ClassName.get(String.class), "value").build());
        enumBuilder.addMethod(MethodSpec.constructorBuilder()
                .addParameter(ParameterSpec.builder(ClassName.get(String.class), "value").build())
                .addStatement("this.$L = $L", "value", "value")
                .build());
        enumBuilder.addMethod(MethodSpec.methodBuilder("getValue")
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return $L", "value")
                .returns(ClassName.get(String.class)).build());

        enumBuilder.addMethod(MethodSpec.methodBuilder("getApplicationModelType")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return $L", PoetryWriter.FIELD_APPLICATION_MODEL_TYPE)
                .returns(ClassName.get(String.class))
                .build());

        final EnumTrait enumTrait = stringShape.getTrait(EnumTrait.class).get();
        enumTrait.getValues().stream().forEach(enumDefinition -> {
           enumBuilder.addEnumConstant(enumDefinition.getName().get(),
                   TypeSpec.anonymousClassBuilder("$S", enumDefinition.getValue())
                           .addAnnotation(AnnotationSpec.builder(
                                   PoetryWriter.CN_GSON_SERIALIZED_NAME)
                                   .addMember("value", "$S", enumDefinition.getValue())
                           .build())
                   .build());
        });
        return JavaFile.builder(className.packageName(), enumBuilder.build()).build();
    }
}


