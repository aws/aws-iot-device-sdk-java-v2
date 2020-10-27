/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.smithy.java.codegen;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.logging.Logger;

import javax.lang.model.element.Modifier;

import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.model.shapes.MemberShape;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.shapes.StructureShape;
import software.amazon.smithy.model.traits.DocumentationTrait;
import software.amazon.smithy.model.traits.RequiredTrait;
import software.amazon.smithy.model.traits.StreamingTrait;

public class StructureClassBuilder implements BiFunction<Symbol, StructureShape, JavaFile> {
    private static final Logger LOGGER = Logger.getLogger(StructureClassBuilder.class.getName());

    private final CodegenContext context;

    public StructureClassBuilder(final CodegenContext context) {
        this.context = context;
    }

    @Override
    public JavaFile apply(final Symbol symbol, final StructureShape shape) {
        LOGGER.fine("Processing shape ID: "
                + shape.getId().toString() + "; with symbol: " + symbol.getFullName());
        final TypeSpec.Builder classBuilder = TypeSpec.classBuilder(symbol.getName());

        final MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC);
        shape.getAllMembers().entrySet().forEach(memberEntry -> {
            final MemberShape memberShape = memberEntry.getValue();
            final ShapeId memberShapeId = memberEntry.getValue().getTarget();
            final Shape memberTypeShape = context.getModel().getShape(memberShapeId).get();
            final String memberName = memberEntry.getKey();
            final TypeName memberTypeName = context.getType(memberTypeShape);

            if (!memberTypeShape.hasTrait(StreamingTrait.class)) {
                classBuilder.addField(FieldSpec.builder(TypeNameShapeVisitor.optionalWrap(memberTypeName),
                        memberName, Modifier.PRIVATE).build()).build();

                final MethodSpec.Builder getterBuilder = MethodSpec.methodBuilder(
                        context.getGetterPrefix(memberTypeShape) + NameUtils.capitalize(memberName))
                        .addModifiers(Modifier.PUBLIC)
                        .beginControlFlow("if ($L.isPresent())", memberName)
                        .addStatement("return $L.get()", memberName)
                        .endControlFlow()
                        .addStatement("return null")
                        .returns(memberTypeName);

                final Optional<DocumentationTrait> docTrait = memberTypeShape.getTrait(DocumentationTrait.class);
                if (docTrait.isPresent()) {
                    getterBuilder.addJavadoc(docTrait.get().getValue());
                }
                classBuilder.addMethod(getterBuilder.build());

                final MethodSpec.Builder setterBuilder = MethodSpec.methodBuilder(
                        context.getSetterPrefix() + NameUtils.capitalize(memberName))
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(ParameterSpec.builder(memberTypeName, memberName, Modifier.FINAL).build());
                if (memberShape.hasTrait(RequiredTrait.class)) {
                    setterBuilder.addStatement("this.$L = $T.of($L)",
                            memberName, TypeName.get(Optional.class), memberName);
                } else {
                    setterBuilder.addStatement("this.$L = $T.ofNullable($L)",
                            memberName, TypeName.get(Optional.class), memberName);
                }

                if (docTrait.isPresent()) {
                    setterBuilder.addJavadoc(docTrait.get().getValue());
                }
                classBuilder.addMethod(setterBuilder.build());
            } //else { //would like to add comment that streaming response outputs exist here
            constructorBuilder.addStatement("this.$L = Optional.empty()", memberName);
        });
        classBuilder.addMethod(constructorBuilder.build());

        return JavaFile.builder(symbol.getNamespace(), classBuilder.build()).build();
    }

}
