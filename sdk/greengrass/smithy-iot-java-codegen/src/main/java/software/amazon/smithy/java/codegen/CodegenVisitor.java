/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.smithy.java.codegen;

import com.squareup.javapoet.JavaFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Logger;

import software.amazon.smithy.build.PluginContext;
import software.amazon.smithy.codegen.core.CodegenException;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.ServiceShape;
import software.amazon.smithy.model.shapes.StructureShape;
import software.amazon.smithy.model.shapes.UnionShape;

public class CodegenVisitor {
    public static final String SMITHY_NAMESPACE_PREFIX = "smithy";

    private static final Logger LOGGER = Logger.getLogger(CodegenVisitor.class.getName());

    private final PluginContext pluginContext;
    private final Model model;

    CodegenVisitor(
            final PluginContext context,
            final Model model) {
        this.pluginContext = context;
        this.model = model;
    }

    public void execute() {
        final SymbolProvider symbolProvider = JavaSymbolProvider.create(model);
        final CodegenContext context = new CodegenContext(pluginContext, model, symbolProvider);

        final Collection<JavaFile> outputFiles = new LinkedList<>();
        final UnionClassBuilder omClassBuilder = new UnionClassBuilder(context);
        final StructureClassBuilder structureClassBuilder = new StructureClassBuilder(context);
        final ServiceClientClassBuilder serviceClassBuilder = new ServiceClientClassBuilder(context);

        model.shapes()
                //following filter may be something that's supposed to happen via other mechanisms
                .filter(s -> !s.getId().getNamespace().startsWith(SMITHY_NAMESPACE_PREFIX))
                .forEach(shape -> {
            LOGGER.info("Processing shape ID: " + shape.getId().toString());

            if (shape instanceof UnionShape) {
                final Symbol objectSymbol = symbolProvider.toSymbol(shape);
                outputFiles.add(omClassBuilder.apply(objectSymbol, (UnionShape) shape));
            } else if (shape instanceof StructureShape) {
                final Symbol objectSymbol = symbolProvider.toSymbol(shape);
                outputFiles.add(structureClassBuilder.apply(objectSymbol, (StructureShape) shape));
            } else if (shape instanceof ServiceShape) {
                final Symbol serviceSymbol = symbolProvider.toSymbol(shape);
                outputFiles.addAll(serviceClassBuilder.apply(serviceSymbol, (ServiceShape) shape));
            } else {
                LOGGER.finest("Unhandled shape: " + shape.getId().toString());
            }
        });

        //output all files and add to manifest
        outputFiles.stream().forEach(outputFile -> {
            try {
                final Path fileOutput = outputFile.writeToPath(pluginContext.getFileManifest().getBaseDir());
                pluginContext.getFileManifest().addFile(fileOutput);
                LOGGER.info("File created: " + fileOutput.toAbsolutePath().toString());
            } catch (IOException e) {
                throw new CodegenException(e);
            }
        });
    }
}
