/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.smithy.java.codegen;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import software.amazon.smithy.codegen.core.CodegenException;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.BigDecimalShape;
import software.amazon.smithy.model.shapes.BigIntegerShape;
import software.amazon.smithy.model.shapes.BlobShape;
import software.amazon.smithy.model.shapes.BooleanShape;
import software.amazon.smithy.model.shapes.ByteShape;
import software.amazon.smithy.model.shapes.DocumentShape;
import software.amazon.smithy.model.shapes.DoubleShape;
import software.amazon.smithy.model.shapes.FloatShape;
import software.amazon.smithy.model.shapes.IntegerShape;
import software.amazon.smithy.model.shapes.ListShape;
import software.amazon.smithy.model.shapes.LongShape;
import software.amazon.smithy.model.shapes.MapShape;
import software.amazon.smithy.model.shapes.MemberShape;
import software.amazon.smithy.model.shapes.OperationShape;
import software.amazon.smithy.model.shapes.ResourceShape;
import software.amazon.smithy.model.shapes.ServiceShape;
import software.amazon.smithy.model.shapes.SetShape;
import software.amazon.smithy.model.shapes.ShapeVisitor;
import software.amazon.smithy.model.shapes.ShortShape;
import software.amazon.smithy.model.shapes.StringShape;
import software.amazon.smithy.model.shapes.StructureShape;
import software.amazon.smithy.model.shapes.TimestampShape;
import software.amazon.smithy.model.shapes.UnionShape;

public class TypeNameShapeVisitor implements ShapeVisitor<TypeName> {
    private final Model model;
    private SymbolProvider symbolProvider;

    public TypeNameShapeVisitor(final Model model, final SymbolProvider symbolProvider) {
        this.model = model;
        this.symbolProvider = symbolProvider;
    }

    public static TypeName optionalWrap(final TypeName typeName) {
        return ParameterizedTypeName.get(ClassName.get(java.util.Optional.class), typeName);
    }

    @Override
    public TypeName blobShape(BlobShape shape) {
        return ClassName.get(java.nio.ByteBuffer.class);
    }

    @Override
    public TypeName booleanShape(BooleanShape shape) {
        return ClassName.get(java.lang.Boolean.class);
    }

    @Override
    public TypeName listShape(ListShape shape) {
        return ParameterizedTypeName.get(ClassName.get(java.util.List.class),
                shape.getMember().accept(this));
    }

    @Override
    public TypeName setShape(SetShape shape) {
        return ParameterizedTypeName.get(ClassName.get(java.util.Set.class),
                shape.getMember().accept(this));
    }

    @Override
    public TypeName mapShape(MapShape shape) {
        return ParameterizedTypeName.get(ClassName.get(java.util.Map.class),
                shape.getKey().accept(this),
                shape.getValue().accept(this));
    }

    @Override
    public TypeName byteShape(ByteShape shape) {
        return ClassName.get(java.lang.Byte.class);
    }

    @Override
    public TypeName shortShape(ShortShape shape) {
        return ClassName.get(java.lang.Short.class);
    }

    @Override
    public TypeName integerShape(IntegerShape shape) {
        return ClassName.get(java.lang.Integer.class);
    }

    @Override
    public TypeName longShape(LongShape shape) {
        return ClassName.get(java.lang.Long.class);
    }

    @Override
    public TypeName floatShape(FloatShape shape) {
        return ClassName.get(java.lang.Integer.class);
    }

    @Override
    public TypeName documentShape(DocumentShape shape) {
        return ParameterizedTypeName.get(ClassName.get(java.util.Map.class),
                ClassName.get(String.class), ClassName.get(Object.class));
    }

    @Override
    public TypeName doubleShape(DoubleShape shape) {
        return ClassName.get(java.lang.Double.class);
    }

    @Override
    public TypeName bigIntegerShape(BigIntegerShape shape) {
        return ClassName.get(java.math.BigInteger.class);
    }

    @Override
    public TypeName bigDecimalShape(BigDecimalShape shape) {
        return ClassName.get(java.math.BigDecimal.class);
    }

    @Override
    public TypeName operationShape(OperationShape shape) {
        throw new CodegenException("Cannot retrieve a Java instantiation type name for: " + shape.getClass().getName());
    }

    @Override
    public TypeName resourceShape(ResourceShape shape) {
        throw new CodegenException("Cannot retrieve a Java instantiation type name for: " + shape.getClass().getName());
    }

    @Override
    public TypeName serviceShape(ServiceShape shape) {
        throw new CodegenException("Cannot retrieve a Java instantiation type name for: " + shape.getClass().getName());
    }

    @Override
    public TypeName stringShape(StringShape shape) {
        return ClassName.get(java.lang.String.class);
    }

    @Override
    public TypeName structureShape(StructureShape shape) {
        final Symbol symbol = symbolProvider.toSymbol(shape);
        return ClassName.get(symbol.getNamespace(), symbol.getName());
    }

    @Override
    public TypeName unionShape(UnionShape shape) {
        final Symbol symbol = symbolProvider.toSymbol(shape);
        return ClassName.get(symbol.getNamespace(), symbol.getName());
    }

    @Override
    public TypeName memberShape(MemberShape shape) {
        //could add some checking here...will throw exception though
        return model.getShape(shape.getTarget()).get().accept(this);
    }

    @Override
    public TypeName timestampShape(TimestampShape shape) {
        return ClassName.get(java.time.Instant.class);
    }
}
