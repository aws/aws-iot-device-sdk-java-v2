/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.smithy.java.codegen;

import com.squareup.javapoet.TypeName;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

import software.amazon.smithy.build.PluginContext;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.BooleanShape;
import software.amazon.smithy.model.shapes.MemberShape;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.shapes.UnionShape;

public class CodegenContext {
    private final PluginContext pluginContext;
    private final Model model;
    private final SymbolProvider javaSymbolProvider;
    private final TypeNameShapeVisitor typeNameShapeVisitor;
    private final InstantiationTypeNameShapeVisitor instantiationTypeNameShapeVisitor;
    private final Map<ShapeId, Map.Entry<UnionShape, MemberShape>> unionShapeTypeMap;

    public CodegenContext(PluginContext pluginContext, Model model, SymbolProvider javaSymbolProvider) {
        this.pluginContext = pluginContext;
        this.model = model;
        this.javaSymbolProvider = javaSymbolProvider;

        typeNameShapeVisitor = new TypeNameShapeVisitor(model, javaSymbolProvider);
        instantiationTypeNameShapeVisitor = new InstantiationTypeNameShapeVisitor(model, javaSymbolProvider);

        unionShapeTypeMap = new HashMap<>();
        //first pass of all shapes to build some knowledge about the entire model
        //and make it available in the code generation context
        model.shapes()
            //following filter may be something that's supposed to happen via other mechanisms
            .filter(s -> !s.getId().getNamespace().startsWith(CodegenVisitor.SMITHY_NAMESPACE_PREFIX))
            .forEach(shape -> {
                if (shape instanceof UnionShape) {
                    final UnionShape unionShape = (UnionShape) shape;
                    unionShape.getAllMembers().values().forEach(memberShape -> {
                        unionShapeTypeMap.put(memberShape.getTarget(),
                                new AbstractMap.SimpleImmutableEntry(unionShape, memberShape));
                    });
                }
            });
    }

    public String getBaseModelPackage(final Shape shape) {
        return pluginContext.getSettings().getStringMemberOrDefault("javaBasePackage", "")
                + ".model."  + shape.getId().getNamespace();
    }

    public Model getModel() {
        return model;
    }

    public TypeName getType(final Shape shape) {
        return shape.accept(typeNameShapeVisitor);
    }

    public Map.Entry<UnionShape, MemberShape> getUnionParent(final ShapeId shapeId) {
        return unionShapeTypeMap.get(shapeId);
    }

    public String getGetterPrefix(final Shape shape) {
        if (shape instanceof BooleanShape) {
            return "is";
        }
        return "get";
    }

    public String getSetterPrefix() {
        return "set";
    }
}
