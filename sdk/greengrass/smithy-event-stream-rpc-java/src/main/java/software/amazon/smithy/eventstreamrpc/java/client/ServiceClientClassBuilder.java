/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.smithy.eventstreamrpc.java.client;

import com.squareup.javapoet.*;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import javax.lang.model.element.Modifier;

import software.amazon.smithy.eventstreamrpc.java.PoetryWriter;
import software.amazon.smithy.eventstreamrpc.java.ServiceCodegenContext;
import software.amazon.smithy.eventstreamrpc.java.NameUtils;
import software.amazon.smithy.eventstreamrpc.java.model.ServiceModelClassBuilder;
import software.amazon.smithy.model.shapes.OperationShape;
import software.amazon.smithy.model.shapes.ServiceShape;

/**
 * Builds the service client interface, the service client generated implementation of said interface,
 * and all of the operation response handlers that are used as return types
 */
public class ServiceClientClassBuilder implements Function<ServiceShape, Collection<JavaFile>> {
    private final ServiceCodegenContext context;

    public ServiceClientClassBuilder(final ServiceCodegenContext context) {
        this.context = context;
    }

    public ClassName getClientImplClassName(final ServiceShape shape) {
        return ClassName.get(context.getBaseServicePackage(), shape.getId().getName() + "Client");
    }

    public ClassName getClientInterfaceClassName(final ServiceShape shape) {
        return ClassName.get(context.getBaseServicePackage(), shape.getId().getName());
    }

    public ClassName getOperationResponseType(final OperationShape shape) {
        return ClassName.get(context.getBaseServicePackage(), shape.getId().getName() + "ResponseHandler");
    }

    @Override
    public Collection<JavaFile> apply(final ServiceShape shape) {
        final Collection<JavaFile> outputFiles = new LinkedList<>();

        final ClassName implClassName = getClientImplClassName(shape);
        final ClassName interfaceClassName = getClientInterfaceClassName(shape);

        final TypeSpec.Builder interfaceClassBuilder = TypeSpec.interfaceBuilder(interfaceClassName)
                .addModifiers(Modifier.PUBLIC);
        final TypeSpec.Builder implClassBuilder = TypeSpec.classBuilder(implClassName)
                .addModifiers(Modifier.PUBLIC)
                .superclass(PoetryWriter.CN_EVENT_STREAM_RPC_CLIENT)
                .addSuperinterface(interfaceClassName);

        implClassBuilder.addMethod(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(PoetryWriter.CN_EVENT_STREAM_RPC_CONNECTION,
                        "connection", Modifier.FINAL).build())
                .addStatement("super($L)", "connection")
                .build());

        context.getAllOperations().stream().forEach(operationShape -> {
            final ClassName requestClassName = context.getOperationRequestClassName(operationShape);
            final ClassName responseClassName = context.getOperationResponseClassName(operationShape);
            final ClassName streamingRequestClassName = context.getOperationStreamingRequestClassName(operationShape);
            final ClassName streamingResponseClassName = context.getOperationStreamingResponseClassName(operationShape);
            final ClassName operationReturnType = getOperationResponseType(operationShape);

            final TypeName streamingResponseHandlerType = ParameterizedTypeName.get(ClassName.get(Optional.class),
                    ParameterizedTypeName.get(PoetryWriter.CN_STREAM_RESPONSE_HANDLER, streamingResponseClassName));

            final String operationName = NameUtils.uncapitalize(operationShape.getId().getName());

            final String requestParamName = "request";
            final String streamHandlerParamName = "streamResponseHandler";
            final ParameterSpec.Builder requestParam = ParameterSpec.builder(requestClassName, requestParamName, Modifier.FINAL);
            final ParameterSpec.Builder streamingHandlerParam = ParameterSpec.builder(
                    streamingResponseHandlerType, streamHandlerParamName, Modifier.FINAL);

            interfaceClassBuilder.addMethod(MethodSpec.methodBuilder(operationName)
                    .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                    .addParameter(requestParam.build())
                    .addParameter(streamingHandlerParam.build())
                    .returns(operationReturnType)
                    .build());

            implClassBuilder.addMethod(MethodSpec.methodBuilder(operationName)
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(requestParam.build())
                    .addParameter(streamingHandlerParam.build())
                    .addStatement("final $T operationContext = $T.$L()",
                            context.getOperationModelContextClassName(operationShape),
                            context.getServiceModelClassName(),
                            ServiceModelClassBuilder.getOperationModelGetterName(operationShape))
                    .addStatement("return new $T(doOperationInvoke(operationContext, $L, $L))",
                            operationReturnType, requestParamName, streamHandlerParamName)
                    .returns(operationReturnType)
                    .build());
            //return new SubscribeToTopicResponseHandler(doOperationInvoke(operationContext, request, streamResponseHandler));

            outputFiles.add(generateResponseHandlerClass(operationReturnType, responseClassName, streamingRequestClassName));
        });

        outputFiles.add(JavaFile.builder(implClassName.packageName(), implClassBuilder.build()).build());
        outputFiles.add(JavaFile.builder(interfaceClassName.packageName(), interfaceClassBuilder.build()).build());

        return outputFiles;
    }

    private JavaFile generateResponseHandlerClass(final ClassName operationResponseHandlerClassName,
            final ClassName responseClassName, final ClassName streamingRequestClassName) {
        final TypeName operationReturnBaseType = ParameterizedTypeName.get(PoetryWriter.CN_STREAM_RESPONSE,
                responseClassName, streamingRequestClassName);

        final TypeName operationResponseType = ParameterizedTypeName.get(PoetryWriter.CN_OPERATION_RESPONSE,
                responseClassName, streamingRequestClassName);
        final TypeSpec.Builder classBuilder = TypeSpec.classBuilder(operationResponseHandlerClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(operationReturnBaseType);

        final String FIELD_OPERATION_RESPONSE = "operationResponse";
        final TypeName COMPLETABLE_FUTURE_VOID = ParameterizedTypeName.get(
                ClassName.get(CompletableFuture.class), ClassName.get(Void.class));

        classBuilder.addField(FieldSpec.builder(operationResponseType, FIELD_OPERATION_RESPONSE,
                Modifier.FINAL, Modifier.PRIVATE).build());

        classBuilder.addMethod(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(operationResponseType, FIELD_OPERATION_RESPONSE, Modifier.FINAL).build())
                .addStatement("this.$L = $L", FIELD_OPERATION_RESPONSE, FIELD_OPERATION_RESPONSE)
                .build());

        classBuilder.addMethod(MethodSpec.methodBuilder("getRequestFlushFuture")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return $L.getRequestFlushFuture()", FIELD_OPERATION_RESPONSE)
                .returns(COMPLETABLE_FUTURE_VOID)
                .build());

        classBuilder.addMethod(MethodSpec.methodBuilder("getResponse")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return $L.getResponse()", FIELD_OPERATION_RESPONSE)
                .returns(ParameterizedTypeName.get(ClassName.get(CompletableFuture.class), responseClassName))
                .build());

        classBuilder.addMethod(MethodSpec.methodBuilder("sendStreamEvent")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(streamingRequestClassName, "event", Modifier.FINAL).build())
                .addStatement("return $L.sendStreamEvent($L)", FIELD_OPERATION_RESPONSE, "event")
                .returns(COMPLETABLE_FUTURE_VOID)
                .build());

        classBuilder.addMethod(MethodSpec.methodBuilder("closeStream")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return $L.closeStream()", FIELD_OPERATION_RESPONSE)
                .returns(COMPLETABLE_FUTURE_VOID)
                .build());

        classBuilder.addMethod(MethodSpec.methodBuilder("isClosed")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return $L.isClosed()", FIELD_OPERATION_RESPONSE)
                .returns(TypeName.BOOLEAN)
                .build());

        return JavaFile.builder(operationResponseHandlerClassName.packageName(), classBuilder.build()).build();
    }
}
