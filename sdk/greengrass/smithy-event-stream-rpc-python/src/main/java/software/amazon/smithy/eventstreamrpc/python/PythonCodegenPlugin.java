/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.smithy.eventstreamrpc.python;

import java.io.File;
import java.util.logging.Logger;

import software.amazon.smithy.build.PluginContext;
import software.amazon.smithy.build.SmithyBuildPlugin;
import software.amazon.smithy.model.Model;

public class PythonCodegenPlugin implements SmithyBuildPlugin {
    private static final Logger LOGGER = Logger.getLogger(PythonCodegenPlugin.class.getSimpleName());

    public static final String SMITHY_NAMESPACE_PREFIX = "smithy";

    @Override
    public String getName() {
        return "event-stream-rpc-python";
    }

    @Override
    public void execute(PluginContext pluginContext) {
        LOGGER.info("EventStream RPC Python code generation plugin running");

        final Model model = pluginContext.getModel();

        if (pluginContext.getSettings()
                .getBooleanMemberOrDefault("generateClientStubs", Boolean.FALSE).booleanValue()) {
        }
        if (pluginContext.getSettings()
                .getBooleanMemberOrDefault("generateServerStubs", Boolean.FALSE).booleanValue()) {
            throw new UnsupportedOperationException("Server stub generation not implemented yet!");
        }

        pluginContext.getFileManifest().writeFile(new File("python-src/hello.py").toPath(), "Python test output\n");
    }
}
