/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.smithy.java.codegen;

import java.util.logging.Logger;

import software.amazon.smithy.build.PluginContext;
import software.amazon.smithy.build.SmithyBuildPlugin;
import software.amazon.smithy.model.Model;

public class JavaCodegenPlugin implements SmithyBuildPlugin {
    private static final Logger LOGGER = Logger.getLogger(JavaCodegenPlugin.class.getName());

    @Override
    public String getName() {
        return "java-iot-codegen";
    }

    @Override
    public void execute(PluginContext context) {
        LOGGER.info("IoT Java code generation Java plugin running");

        final Model model = context.getModel();

        final CodegenVisitor visitor = new CodegenVisitor(context, model);
        visitor.execute();
    }
}
