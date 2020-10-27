/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.smithy.java.codegen;

import software.amazon.smithy.codegen.core.CodegenException;
import software.amazon.smithy.codegen.core.ReservedWordSymbolProvider;
import software.amazon.smithy.codegen.core.ReservedWords;
import software.amazon.smithy.codegen.core.ReservedWordsBuilder;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.MapShape;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.traits.EnumTrait;
import software.amazon.smithy.utils.StringUtils;

/**
 * This class is responsible for type mapping and file/identifier formatting.
 */
public final class JavaSymbolProvider implements SymbolProvider {
    private Model model;

    private JavaSymbolProvider(Model model) {
        this.model = model;
    }

    public static SymbolProvider create(Model model) {
        // Wrapped my custom symbol provider with reserved words specific for members of classes.
        return ReservedWordSymbolProvider.builder()
                .nameReservedWords(buildJavaReservedWords())
                .symbolProvider(new JavaSymbolProvider(model))
                .build();
    }

    private String getJavaNamespace(Shape shape) {
        String namespace = "";
        // Convert model namespace to java namespace
        String shapeNamespace = shape.getId().getNamespace();
        if (shapeNamespace.startsWith("aws.")) {
            namespace = shapeNamespace.replaceAll("^aws\\.", "software.amazon.awssdk.iot.");
            if (!shape.isServiceShape()) {
                namespace += ".model";
            }
        }
        return namespace;
    }

    @Override
    public Symbol toSymbol(Shape shape) {
        String typeName;
        String namespace = "java.lang";
        switch (shape.getType()) {
            case BOOLEAN:
                typeName = "Boolean";
                break;
            case STRING:
                typeName = "String";
                if (shape.getTrait(EnumTrait.class).isPresent() && shape.getId().getNamespace().startsWith("aws")) {
                    namespace = getJavaNamespace(shape);
                    typeName = StringUtils.capitalize(shape.getId().getName());
                }
                break;
            case TIMESTAMP:
                namespace = "software.amazon.awssdk.iot";
                typeName = "Timestamp";
                break;
            case BYTE:
                typeName = "Byte";
                break;
            case SHORT:
                typeName = "Short";
                break;
            case INTEGER:
                typeName = "Integer";
                break;
            case LONG:
                typeName = "Long";
                break;
            case FLOAT:
                typeName = "Float";
                break;
            case DOUBLE:
                typeName = "Double";
                break;
            case BIG_DECIMAL:
                namespace = "java.Math";
                typeName = "BigDecimal";
                break;
            case BIG_INTEGER:
                namespace = "java.Math";
                typeName = "BigInteger";
                break;
            case LIST:
                namespace = "java.util";
                typeName = "List<" + toSymbol(shape.asListShape().get().getMember()) + ">";
                break;
            case SET:
                namespace = "java.util";
                typeName = "Set<" + toSymbol(shape.asSetShape().get().getMember()) + ">";
                break;
            case MAP:
                MapShape mapShape = shape.asMapShape().get();
                namespace = "java.util";
                typeName = "HashMap<"
                        + toSymbol(mapShape.getKey()) + ", "
                        + toSymbol(mapShape.getValue()) + ">";
                break;
            case STRUCTURE:
                namespace = getJavaNamespace(shape);
                typeName = StringUtils.capitalize(shape.getId().getName());
                break;
            case UNION:
                namespace = getJavaNamespace(shape);
                typeName = StringUtils.capitalize(shape.getId().getName());
                break;
            case SERVICE:
                namespace = getJavaNamespace(shape);
                typeName = StringUtils.capitalize(shape.getId().getName());
                break;
            case RESOURCE:
                namespace = getJavaNamespace(shape);
                typeName = StringUtils.capitalize(shape.getId().getName());
                break;
            case OPERATION:
                namespace = getJavaNamespace(shape);
                typeName = StringUtils.capitalize(shape.getId().getName());
                break;
            case MEMBER:
                ShapeId targetShapeId = shape.asMemberShape().get().getTarget();
                Shape targetShape = model.getShape(targetShapeId)
                        .orElseThrow(() -> new CodegenException("Shape not found: " + targetShapeId));
                return toSymbol(targetShape);
            case DOCUMENT:
                namespace = "java.util";
                typeName = "HashMap<String, Object>";
                break;
            case BLOB:
                namespace = "java.nio";
                typeName = "ByteBuffer";
                break;
            default:
                throw new CodegenException("Cannot assign symbol name to: " + shape);
        }

        return Symbol.builder()
                .namespace(namespace, ".")
                .name(typeName)
                .build();
    }

    public static ReservedWords buildJavaReservedWords() {
        //snippet generated via cmd:
        //cat java.words | awk '{print ".put(\"" $1 "\", \"_" $1 "\")" }'
        //https://docs.oracle.com/javase/tutorial/java/nutsandbolts/_keywords.html
        //true, false, and null are manually added as they are literal values
        return new ReservedWordsBuilder()
                .put("true", "_true")
                .put("false", "_false")
                .put("null", "_null")
                .put("abstract", "_abstract")
                .put("assert", "_assert")
                .put("boolean", "_boolean")
                .put("break", "_break")
                .put("byte", "_byte")
                .put("case", "_case")
                .put("catch", "_catch")
                .put("char", "_char")
                .put("class", "_class")
                .put("const", "_const")
                .put("continue", "_continue")
                .put("default", "_default")
                .put("do", "_do")
                .put("double", "_double")
                .put("else", "_else")
                .put("enum", "_enum")
                .put("extends", "_extends")
                .put("final", "_final")
                .put("finally", "_finally")
                .put("float", "_float")
                .put("for", "_for")
                .put("goto", "_goto")
                .put("if", "_if")
                .put("implements", "_implements")
                .put("import", "_import")
                .put("instanceof", "_instanceof")
                .put("int", "_int")
                .put("interface", "_interface")
                .put("long", "_long")
                .put("native", "_native")
                .put("new", "_new")
                .put("package", "_package")
                .put("private", "_private")
                .put("protected", "_protected")
                .put("public", "_public")
                .put("return", "_return")
                .put("short", "_short")
                .put("static", "_static")
                .put("strictfp", "_strictfp")
                .put("super", "_super")
                .put("switch", "_switch")
                .put("synchronized", "_synchronized")
                .put("this", "_this")
                .put("throw", "_throw")
                .put("throws", "_throws")
                .put("transient", "_transient")
                .put("try", "_try")
                .put("void", "_void")
                .put("volatile", "_volatile")
                .put("while", "_while")
            .build();
    }
}
