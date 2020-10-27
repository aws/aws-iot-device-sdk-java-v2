/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.smithy.eventstreamrpc.java.model;

import com.squareup.javapoet.*;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;

import software.amazon.smithy.eventstreamrpc.java.ServiceCodegenContext;
import software.amazon.smithy.eventstreamrpc.java.NameUtils;
import software.amazon.smithy.eventstreamrpc.java.PoetryWriter;
import software.amazon.smithy.model.shapes.*;
import software.amazon.smithy.model.traits.DocumentationTrait;
import software.amazon.smithy.model.traits.ErrorTrait;
import software.amazon.smithy.model.traits.RequiredTrait;
import software.amazon.smithy.model.traits.StreamingTrait;

public class StructureClassBuilder implements BiFunction<ClassName, StructureShape, JavaFile> {
    private static final Logger LOGGER = Logger.getLogger(StructureClassBuilder.class.getName());
    private static final String FIELD_NAME_VOID = "VOID";

    private final ServiceCodegenContext context;

    public StructureClassBuilder(final ServiceCodegenContext context) {
        this.context = context;
    }

    @Override
    public JavaFile apply(final ClassName className, final StructureShape shape) {
        LOGGER.fine("Processing shape ID: "
                + shape.getId().toString() + "; with symbol: " + className.canonicalName());
        final TypeSpec.Builder classBuilder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(PoetryWriter.CN_EVENT_STREAMABLE_JSON_MESSAGE);
        final MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC);

        final Optional<ServiceShape> errorForService = context.getServiceShapeForError(shape.getId());
        if (errorForService.isPresent()) {
            classBuilder.superclass(context.getServiceBaseErrorClassName());
            String errorTypeString = "unknown";   //TODO: verify if this is a sensible default if model omitted
            if (shape.hasTrait(ErrorTrait.class)) {
                final ErrorTrait errorTrait = shape.getTrait(ErrorTrait.class).get();
                errorTypeString = errorTrait.getValue();
            }
            classBuilder.addMethod(MethodSpec.methodBuilder("getErrorTypeString")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("return \"$L\"", errorTypeString)
                    .returns(ClassName.get(String.class)).build());

            //must satisfy service-level error constructor parent
            constructorBuilder.addStatement("super($S, $S)", className.simpleName(), "").build();

            final MethodSpec.Builder msgConstructor = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(ParameterSpec.builder(ClassName.get(String.class), "errorMessage").build())
                    .addStatement("super($S, $L)", className.simpleName(), "errorMessage");
            //!!! edge case being smoothed-out here:
            //Smithy model may have message explicitly set on an error object
            //if this is true, we will fold the constructor passed in message to the member field
            if (shape.getMember("message").isPresent()) {
                msgConstructor.addStatement("this.$L = $T.ofNullable($L)",
                        "message", TypeName.get(Optional.class), "errorMessage");
            }
            classBuilder.addMethod(msgConstructor.build());
        }   //end of error structure condition

        shape.getAllMembers().entrySet().forEach(memberEntry -> {
            final MemberShape memberShape = memberEntry.getValue();
            final ShapeId memberShapeId = memberEntry.getValue().getTarget();
            final Shape memberTypeShape = context.getModel().getShape(memberShapeId).get();
            final String memberName = memberEntry.getKey();
            final TypeName memberTypeName = context.getTypeName(memberTypeShape);

            if (!memberTypeShape.hasTrait(StreamingTrait.class)) {
                classBuilder.addField(PoetryWriter
                        .buildStandardMemberField(memberTypeName, memberName, memberTypeShape).build());

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
                constructorBuilder.addStatement("this.$L = Optional.empty()", memberName);
            } //else { //would like to add comment that streaming response outputs exist here
        });
        classBuilder.addMethod(constructorBuilder.build());

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

        classBuilder.addField(FieldSpec.builder(className, FIELD_NAME_VOID)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
            .build());
        classBuilder.addStaticBlock(CodeBlock.builder()
                .addStatement("$L = $L", FIELD_NAME_VOID,
                    TypeSpec.anonymousClassBuilder("")
                            .addSuperinterface(className)
                            .addMethod(MethodSpec.methodBuilder("isVoid")
                                    .addAnnotation(Override.class)
                                    .addModifiers(Modifier.PUBLIC)
                                    .addStatement("return true")
                                    .returns(TypeName.BOOLEAN)
                                    .build())
                            .build())
            .build());

        //Equals method added
        classBuilder.addMethod(buildModelEquals(className, shape).build());
        //add hash code method
        classBuilder.addMethod(buildModelHashCode(shape).build());

        return JavaFile.builder(className.packageName(), classBuilder.build()).build();
    }

    private MethodSpec.Builder buildModelHashCode(StructureShape shape) {
        final MethodSpec.Builder hashCodeBuilder = MethodSpec.methodBuilder("hashCode")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.INT);

        final String memberArgsCollected = shape.getAllMembers().entrySet().stream()
                .filter(memberEntry -> {
                    final MemberShape memberShape = memberEntry.getValue();
                    final Shape memberTypeShape = context.getModel().getShape(memberShape.getTarget()).get();
                    return !memberTypeShape.hasTrait(StreamingTrait.class);
                })
                .map(memberEntry -> memberEntry.getValue().getMemberName())
                .collect(Collectors.joining(", "));

        hashCodeBuilder.addStatement(String.format("return $T.hash(%s)", memberArgsCollected), ClassName.get(Objects.class));
        return hashCodeBuilder;
    }

    private MethodSpec.Builder buildModelEquals(final ClassName className, final StructureShape shape) {
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
            if (memberTypeShape.hasTrait(StreamingTrait.class)) {
                return; //skip streaming members here too
            } else {
                if (memberTypeShape.isBlobShape()) {
                    equalsBuilder.addStatement("$L = $L && $T.blobTypeEquals(this.$L, $L.$L)",
                            returnFlag, returnFlag, PoetryWriter.CN_EVENT_STREAM_RPC_SERVICE_MODEL,
                            memberShape.getMemberName(), otherObj, memberShape.getMemberName());
                } else {
                    equalsBuilder.addStatement("$L = $L && this.$L.equals($L.$L)",
                            returnFlag, returnFlag, memberShape.getMemberName(), otherObj, memberShape.getMemberName());
                }
            }
        });

        equalsBuilder.addStatement("return $L", returnFlag);
        return equalsBuilder;
    }
}
