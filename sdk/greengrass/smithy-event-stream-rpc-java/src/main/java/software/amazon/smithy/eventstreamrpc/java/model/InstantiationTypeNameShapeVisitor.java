/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.smithy.eventstreamrpc.java.model;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import software.amazon.smithy.eventstreamrpc.java.ServiceCodegenContext;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.ListShape;
import software.amazon.smithy.model.shapes.MapShape;
import software.amazon.smithy.model.shapes.SetShape;

/**
 * Provides a type name for instantiation internal to data models. Difference maker is for Collection classes
 * where interfaces may say "List" for interface compatibility, but when instantiated must call out a specific
 * implementation like LinkedList.
 */
public class InstantiationTypeNameShapeVisitor extends TypeNameShapeVisitor {
    public InstantiationTypeNameShapeVisitor(final ServiceCodegenContext context, final Model model) {
        super(context, model);
    }

    @Override
    public TypeName listShape(ListShape shape) {
        return ParameterizedTypeName.get(ClassName.get(java.util.ArrayList.class),
                shape.getMember().accept(this));
    }

    @Override
    public TypeName setShape(SetShape shape) {
        return ParameterizedTypeName.get(ClassName.get(java.util.HashSet.class),
                shape.getMember().accept(this));
    }

    @Override
    public TypeName mapShape(MapShape shape) {
        return ParameterizedTypeName.get(ClassName.get(java.util.HashMap.class),
                shape.getKey().accept(this),
                shape.getValue().accept(this));
    }
}
