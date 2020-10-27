/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.smithy.eventstreamrpc.java;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Logger;

import software.amazon.smithy.build.PluginContext;
import software.amazon.smithy.build.SmithyBuildPlugin;
import software.amazon.smithy.codegen.core.CodegenException;
import software.amazon.smithy.model.Model;

public class JavaCodegenPlugin implements SmithyBuildPlugin {
    private static final Logger LOGGER = Logger.getLogger(JavaCodegenPlugin.class.getSimpleName());

    public static final String SMITHY_NAMESPACE_PREFIX = "smithy";

    @Override
    public String getName() {
        return "event-stream-rpc-java";
    }

    @Override
    public void execute(PluginContext pluginContext) {
        LOGGER.info("EventStream RPC Java code generation plugin running");

        final Model model = pluginContext.getModel();

        final ServiceCodegenContext context = new ServiceCodegenContext(pluginContext, model,
                pluginContext.getSettings().getStringMember("serviceShapeId").get().getValue());

        //code generation for the model is always happening so trigger that first
        final Collection<JavaFileGenerator> generators = new LinkedList<>();

        //model generator is always the output since both client and server both require it as well
        //and both can be turned off and the model is the implied desired output
        generators.add(new DataModelGenerator(context, model));

        if (pluginContext.getSettings()
                .getBooleanMemberOrDefault("generateClientStubs", Boolean.FALSE).booleanValue()) {
            generators.add(new ClientGenerator(context, model));
        }
        if (pluginContext.getSettings()
                .getBooleanMemberOrDefault("generateServerStubs", Boolean.FALSE).booleanValue()) {
            generators.add(new ServiceModelGenerator(context, model));
        }

        //go through every generator and add it's files to the manifest and write them out
        generators.forEach(generator -> generator.accept(outputFile -> {
                try {
                    if (pluginContext.getFileManifest().getBaseDir().resolve(
                            generator.getOutputSubdirectory()).resolve(outputFile.toJavaFileObject().getName())
                                .toFile().exists()) {
                        LOGGER.warning("Writing to an already existing file: " +
                                pluginContext.getFileManifest().getBaseDir().resolve(
                                        generator.getOutputSubdirectory()).resolve(outputFile.toJavaFileObject().getName())
                                        .toAbsolutePath().toString() + ". Check code generation logic if generation output dir was cleaned first");
                    } else {
                        final Path fileOutput = outputFile.writeToPath(
                                pluginContext.getFileManifest().getBaseDir().resolve(generator.getOutputSubdirectory()));
                        pluginContext.getFileManifest().addFile(fileOutput);
                        LOGGER.info("File created: " + fileOutput.toAbsolutePath().toString());
                    }

                } catch (IOException e) {
                    throw new CodegenException(e);
                }
            }));
    }
}
