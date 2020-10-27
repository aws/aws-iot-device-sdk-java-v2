/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.smithy.eventstreamrpc.java.model;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;

import software.amazon.smithy.eventstreamrpc.java.ServiceCodegenContext;
import software.amazon.smithy.eventstreamrpc.java.NameUtils;
import software.amazon.smithy.eventstreamrpc.java.PoetryWriter;
import software.amazon.smithy.model.shapes.*;
import software.amazon.smithy.model.traits.DocumentationTrait;

public class UnionClassBuilder implements Function<UnionShape, JavaFile> {
    private static final Logger LOGGER = Logger.getLogger(UnionClassBuilder.class.getName());

    private static final String UNION_MEMBER_TYPE_NAME = "UnionMember";
    private static final String UNION_SET_MEMBER_FIELD_NAME = "setUnionMember";

    private final ServiceCodegenContext context;

    public UnionClassBuilder(final ServiceCodegenContext context) {
        this.context = context;
    }

    @Override
    public JavaFile apply(final UnionShape shape) {
        final ClassName className = context.getClassName(shape);
        LOGGER.fine("Processing union shape ID: "
                + shape.getId().toString() + "; with symbol: " + className.canonicalName());
        final TypeSpec.Builder classBuilder
                = TypeSpec.classBuilder(className)
                .addSuperinterface(PoetryWriter.CN_EVENT_STREAMABLE_JSON_MESSAGE)
                .addModifiers(Modifier.PUBLIC);
        final TypeSpec.Builder enumMemberBuilder = TypeSpec.enumBuilder(UNION_MEMBER_TYPE_NAME)
                .addField(FieldSpec.builder(ClassName.get(String.class), "fieldName")
                        .addModifiers(Modifier.PRIVATE).build())
                .addField(FieldSpec.builder(ParameterizedTypeName.get(ClassName.get(Consumer.class), className), "nullifier")
                        .addModifiers(Modifier.PRIVATE).build())
                .addField(FieldSpec.builder(ParameterizedTypeName.get(ClassName.get(Predicate.class), className), "isPresent")
                        .addModifiers(Modifier.PRIVATE).build())
                .addMethod(MethodSpec.constructorBuilder()
                        .addParameter(ParameterSpec.builder(ClassName.get(String.class), "fieldName")
                                .build())
                        .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(ClassName.get(Consumer.class), className), "nullifier")
                                .build())
                        .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(ClassName.get(Predicate.class), className), "isPresent")
                                .build())
                        .addStatement("this.$L = $L", "fieldName", "fieldName")
                        .addStatement("this.$L = $L", "nullifier", "nullifier")
                        .addStatement("this.$L = $L", "isPresent", "isPresent")
                    .build())
                .addMethod(MethodSpec.methodBuilder("nullify")
                        .addParameter(ParameterSpec.builder(className, "obj").build())
                        .addStatement("nullifier.accept(obj)")
                        .build())
                .addMethod(MethodSpec.methodBuilder("isPresent")
                        .addParameter(ParameterSpec.builder(className, "obj").build())
                        .addStatement("return isPresent.test(obj)")
                        .returns(TypeName.BOOLEAN)
                        .build())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC);

        final MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC);

        classBuilder.addField(FieldSpec.builder(ClassName.get("", UNION_MEMBER_TYPE_NAME),
                UNION_SET_MEMBER_FIELD_NAME, Modifier.PRIVATE, Modifier.TRANSIENT)
                .build());

        shape.getAllMembers().entrySet().forEach(memberEntry -> {
            final ShapeId memberShapeId = memberEntry.getValue().getTarget();
            final Shape memberTypeShape = context.getModel().getShape(memberShapeId).get();
            final String memberName = memberEntry.getKey();
            final TypeName memberTypeName = context.getTypeName(memberTypeShape);
            final String enumConstantName = NameUtils.camelToConstantCase(memberName).toUpperCase();

            classBuilder.addField(PoetryWriter
                    .buildStandardMemberField(memberTypeName, memberName, memberTypeShape).build());

            constructorBuilder.addStatement("this.$L = Optional.empty()", memberName);

            final MethodSpec.Builder getterBuilder = MethodSpec.methodBuilder(
                    context.getGetterPrefix(memberTypeShape) + NameUtils.capitalize(memberName))
                    .addModifiers(Modifier.PUBLIC)
                    .beginControlFlow("if ($L.isPresent() && ($L == $L.$L))",
                            memberName, UNION_SET_MEMBER_FIELD_NAME,
                            UNION_MEMBER_TYPE_NAME, enumConstantName)
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
            //union member set cannot be null based on this logic
            setterBuilder.addStatement("this.$L = $T.of($L)", memberName,
                        TypeName.get(Optional.class), memberName);
            //set the unionSetMember field
            setterBuilder.addStatement("this.$L = $L.$L", UNION_SET_MEMBER_FIELD_NAME,
                    UNION_MEMBER_TYPE_NAME, enumConstantName);

            if (docTrait.isPresent()) {
                setterBuilder.addJavadoc(docTrait.get().getValue());
            }
            classBuilder.addMethod(setterBuilder.build());

            enumMemberBuilder.addEnumConstant(enumConstantName,
                    TypeSpec.anonymousClassBuilder(
                        "$S, ($L obj) -> obj.$L = Optional.empty(), ($L obj) -> obj.$L != null && obj.$L.isPresent()",
                            enumConstantName, className, memberName,
                            className, memberName, memberName).build());
        });
        classBuilder.addMethod(constructorBuilder.build());
        classBuilder.addType(enumMemberBuilder.build());

        classBuilder.addMethod(MethodSpec.methodBuilder("getSetUnionMember")
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc("Returns an indicator for which enum member is set. Can be used to convert to proper type.")
                .addStatement("return $L", UNION_SET_MEMBER_FIELD_NAME)
                .returns(ClassName.get("", UNION_MEMBER_TYPE_NAME)).build());

        //application type is the smithy model ID
        final MethodSpec.Builder getApplicationModelTypeBuilder = MethodSpec.methodBuilder("getApplicationModelType")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addStatement("return $L", PoetryWriter.FIELD_APPLICATION_MODEL_TYPE)
                .returns(ClassName.get(String.class));
        classBuilder.addMethod(getApplicationModelTypeBuilder.build());

        classBuilder.addField(FieldSpec.builder(ClassName.get(String.class), PoetryWriter.FIELD_APPLICATION_MODEL_TYPE)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                .initializer("$S", shape.getId().toString())
                .build());

        //special method for unions needed for deserialization to determine which member is set and set the field
        //indicating which member is set. Necessary for proper in-memory object use after deserialization
        final MethodSpec.Builder selfDesignateBuilder = MethodSpec.methodBuilder("selfDesignateSetUnionMember")
                .addStatement("int setCount = 0")
                .addStatement("UnionMember[] members = UnionMember.values()")
                .addCode(CodeBlock.builder()
                        .beginControlFlow("for (int memberIdx = 0; memberIdx < UnionMember.values().length; ++memberIdx)")
                            .add(CodeBlock.builder()
                                    .beginControlFlow("if (members[memberIdx].isPresent(this))")
                                    .addStatement("++setCount")
                                    .addStatement("this.setUnionMember = members[memberIdx]")
                                    .endControlFlow()
                                    .build())
                        .endControlFlow()
                        .build())
                .addComment("only bad outcome here is if there's more than one member set. It's possible for none to be set")
                .addCode(CodeBlock.builder()
                        .beginControlFlow("if (setCount > 1)")
                        .addStatement("throw new IllegalArgumentException(\"More than one union member set for type: \" + getApplicationModelType())")
                        .endControlFlow().build())
                .addModifiers(Modifier.PUBLIC);
        classBuilder.addMethod(selfDesignateBuilder.build());

        classBuilder.addMethod(MethodSpec.methodBuilder("postFromJson")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("selfDesignateSetUnionMember()")
                .returns(TypeName.VOID)
                .build());

        //add equals
        classBuilder.addMethod(buildModelEquals(className, shape).build());
        //add hashCode
        classBuilder.addMethod(buildModelHashCode(shape).build());

        return JavaFile.builder(className.packageName(), classBuilder.build()).build();
    }

    /**
     * Note: This is different from the StructureBuilder in that it does not filter members out for streaming trait
     * @param shape
     * @return
     */
    private MethodSpec.Builder buildModelHashCode(UnionShape shape) {
        final MethodSpec.Builder hashCodeBuilder = MethodSpec.methodBuilder("hashCode")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.INT);

        //appending union set member with string append like below is only safe if the union
        //is non-empty. This is certainly true by Smithy model grammar rules
        final String memberArgsCollected = shape.getAllMembers().entrySet().stream()
                .map(memberEntry -> memberEntry.getValue().getMemberName())
                .collect(Collectors.joining(", ")) + ", " + UNION_SET_MEMBER_FIELD_NAME;

        hashCodeBuilder.addStatement(String.format("return $T.hash(%s)", memberArgsCollected), ClassName.get(Objects.class));
        return hashCodeBuilder;
    }

    /**
     * Note: This is different from the StructureBuilder in that it does not filter members out for streaming trait
     * @param className
     * @param shape
     * @return
     */
    private MethodSpec.Builder buildModelEquals(final ClassName className, final UnionShape shape) {
        final String rhsParam = "rhs";
        final String otherObj = "other";
        final String returnFlag = "isEquals";
        final MethodSpec.Builder equalsBuilder = MethodSpec.methodBuilder("equals")
                .addAnnotation(Override.class)
                .addParameter(ParameterSpec.builder(ClassName.get(Object.class), rhsParam).build())
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.BOOLEAN);
        //check if rhs is null
        equalsBuilder.addStatement("if ($L == null) return false", rhsParam);
        //check if rhs is instance of this class
        equalsBuilder.addStatement("if (!($L instanceof $T)) return false", rhsParam, className);
        //short circuit if the instances are exactly the same
        equalsBuilder.addStatement("if ($L == $L) return true", "this", rhsParam);
        //get typed local variable with direct private member access to RHS
        equalsBuilder.addStatement("final $T $L = ($T)$L", className, otherObj, className, rhsParam);
        //declare return flag
        equalsBuilder.addStatement("$T $L = true", TypeName.BOOLEAN, returnFlag);
        shape.getAllMembers().entrySet().forEach(memberEntry -> {
                final MemberShape memberShape = memberEntry.getValue();
                final Shape memberTypeShape = context.getModel().getShape(memberShape.getTarget()).get();
                if (memberTypeShape.isBlobShape()) {
                    //should never get here for a Union as smithy only allows structure
                    //members in a union
                    equalsBuilder.addStatement("$L = $L && $T.blobTypeEquals(this.$L, $L.$L)",
                            returnFlag, returnFlag, PoetryWriter.CN_EVENT_STREAM_RPC_SERVICE_MODEL,
                            memberShape.getMemberName(), otherObj, memberShape.getMemberName());
                } else {
                    equalsBuilder.addStatement("$L = $L && this.$L.equals($L.$L)",
                            returnFlag, returnFlag, memberShape.getMemberName(), otherObj, memberShape.getMemberName());
                }
            });
        equalsBuilder.addStatement("$L = $L && this.$L.equals($L.$L)",
                returnFlag, returnFlag, UNION_SET_MEMBER_FIELD_NAME, otherObj, UNION_SET_MEMBER_FIELD_NAME);

        equalsBuilder.addStatement("return $L", returnFlag);
        return equalsBuilder;
    }
}
