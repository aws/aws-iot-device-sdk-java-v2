/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.smithy.java.codegen;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.BiFunction;
import java.util.logging.Logger;

import javax.lang.model.element.Modifier;

import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.model.shapes.OperationShape;
import software.amazon.smithy.model.shapes.ServiceShape;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.traits.StreamingTrait;

public class ServiceClientClassBuilder implements BiFunction<Symbol, ServiceShape, Collection<JavaFile>> {
    private static final Logger LOGGER = Logger.getLogger(ServiceClientClassBuilder.class.getName());

    private final CodegenContext context;

    public ServiceClientClassBuilder(final CodegenContext context) {
        this.context = context;
    }

    @Override
    public Collection<JavaFile> apply(final Symbol symbol, final ServiceShape shape) {
        LOGGER.fine("Processing shape ID: "
                + shape.getId().toString() + "; with symbol: " + symbol.getFullName());
        Collection<JavaFile> outputFiles = new LinkedList<JavaFile>();
        final ClassName className = ClassName.get(symbol.getNamespace(), symbol.getName());
        final TypeSpec.Builder classBuilder = TypeSpec.classBuilder(symbol.getName());

        shape.getAllOperations().stream().forEach(operation -> {
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(NameUtils.uncapitalize(operation.getName()));
            methodBuilder.addModifiers(Modifier.PUBLIC, Modifier.FINAL);

            final OperationShape operationShape = (OperationShape) context.getModel().getShape(operation).get();

            if (operationShape.hasTrait(StreamingTrait.class)) {
                //TODO: will add a stream continuation token parameter
                //first or last?
            } else {
                if (operationShape.getInput().isPresent()) {
                    //if this throws an exception that means we are referencing an input shape that isn't in the model?
                    final Shape inputShape = context.getModel().getShape(operationShape.getInput().get()).get();
                    methodBuilder.addParameter(ParameterSpec.builder(
                            ClassName.get(context.getBaseModelPackage(inputShape), inputShape.getId().getName()),
                            "input", Modifier.FINAL).build());
                } else {
                    //TODO: !! We must auto-generate input types objects even if empty.
                    methodBuilder.addParameter(ParameterSpec.builder(
                            ClassName.get(context.getBaseModelPackage(operationShape),
                                    operationShape.getId().getName() + "Request"),
                            "input", Modifier.FINAL).build());
                }

                if (operationShape.getOutput().isPresent()) {
                    //if this throws an exception that means we are referencing an input shape that isn't in the model?
                    final Shape outputShape = context.getModel().getShape(operationShape.getOutput().get()).get();
                    methodBuilder.returns(
                            ClassName.get(context.getBaseModelPackage(outputShape),
                                    operationShape.getOutput().get().getName()));
                } else {
                    //TODO: !!! We must generate and output/response object even if empty.
                    methodBuilder.returns(
                            ClassName.get(context.getBaseModelPackage(operationShape),
                                    operationShape.getId().getName() + "Response"));
                }
            }
            classBuilder.addMethod(methodBuilder.build());
        });

        outputFiles.add(JavaFile.builder(symbol.getNamespace(), classBuilder.build()).build());
        return outputFiles;
    }
}
