/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.smithy.java.codegen;

import com.squareup.javapoet.ClassName;
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
import software.amazon.smithy.model.shapes.UnionShape;
import software.amazon.smithy.model.traits.DocumentationTrait;

public class UnionClassBuilder implements BiFunction<Symbol, UnionShape, JavaFile> {
    private static final Logger LOGGER = Logger.getLogger(UnionClassBuilder.class.getName());

    private static final String UNION_MEMBER_TYPE_NAME = "UnionMember";
    private static final String UNION_SET_MEMBER_FIELD_NAME = "setUnionMember";

    private static final String UNION_VALUE_FIELD_NAME = "value";

    private final CodegenContext context;

    public UnionClassBuilder(final CodegenContext context) {
        this.context = context;
    }

    @Override
    public JavaFile apply(final Symbol symbol, final UnionShape shape) {
        LOGGER.fine("Processing union shape ID: "
                + shape.getId().toString() + "; with symbol: " + symbol.getFullName());
        final TypeSpec.Builder classBuilder
                = TypeSpec.classBuilder(ClassName.get(symbol.getNamespace(), symbol.getName()));
        final TypeSpec.Builder enumMemberBuilder = TypeSpec.enumBuilder(UNION_MEMBER_TYPE_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC);

        classBuilder.addMethod(MethodSpec.constructorBuilder()
                .addStatement("this.$L = Optional.empty()", UNION_VALUE_FIELD_NAME)
                .addModifiers(Modifier.PUBLIC).build());

        classBuilder.addField(FieldSpec.builder(ClassName.get("", UNION_MEMBER_TYPE_NAME),
                UNION_SET_MEMBER_FIELD_NAME, Modifier.PRIVATE).build());
        classBuilder.addField(FieldSpec.builder(TypeNameShapeVisitor.optionalWrap(ClassName.get(Object.class)),
                UNION_VALUE_FIELD_NAME, Modifier.PRIVATE).build());

        shape.getAllMembers().entrySet().forEach(memberEntry -> {
            final MemberShape memberShape = memberEntry.getValue();
            final ShapeId memberShapeId = memberEntry.getValue().getTarget();
            final Shape memberTypeShape = context.getModel().getShape(memberShapeId).get();
            final String memberName = memberEntry.getKey();
            final TypeName memberTypeName = context.getType(memberTypeShape);
            final String enumConstantName = NameUtils.camelToConstantCase(memberName).toUpperCase();

            final MethodSpec.Builder getterBuilder = MethodSpec.methodBuilder(
                    context.getGetterPrefix(memberTypeShape) + NameUtils.capitalize(memberName))
                    .addModifiers(Modifier.PUBLIC)
                    .beginControlFlow("if ($L.isPresent() && ($L.get() == $L.$L))",
                            UNION_VALUE_FIELD_NAME, UNION_SET_MEMBER_FIELD_NAME,
                            UNION_MEMBER_TYPE_NAME, enumConstantName)
                    .addStatement("return ($T)$L.get()", memberTypeName, UNION_VALUE_FIELD_NAME)
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
            //union member set cannot be null based on this logic
            setterBuilder.addStatement("this.$L = $T.of($L)", UNION_VALUE_FIELD_NAME,
                        TypeName.get(Optional.class), memberName);
            //set the unionSetMember field
            setterBuilder.addStatement("this.$L = $L.$L", UNION_SET_MEMBER_FIELD_NAME,
                    UNION_MEMBER_TYPE_NAME, enumConstantName);

            if (docTrait.isPresent()) {
                setterBuilder.addJavadoc(docTrait.get().getValue());
            }
            classBuilder.addMethod(setterBuilder.build());


            enumMemberBuilder.addEnumConstant(enumConstantName);
        });
        classBuilder.addType(enumMemberBuilder.build());

        classBuilder.addMethod(MethodSpec.methodBuilder("getSetUnionMember")
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc("Returns an indicator for which enum member is set. Can be used to convert to proper type.")
                .addStatement("return $L", UNION_SET_MEMBER_FIELD_NAME)
                .returns(ClassName.get("", UNION_MEMBER_TYPE_NAME)).build());

        return JavaFile.builder(symbol.getNamespace(), classBuilder.build()).build();
    }
}
