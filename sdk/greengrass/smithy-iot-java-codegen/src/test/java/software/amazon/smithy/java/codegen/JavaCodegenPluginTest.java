/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.smithy.java.codegen;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JavaCodegenPluginTest {
    @Test
    void testGetPluginName() {
        JavaCodegenPlugin classUnderTest = new JavaCodegenPlugin();
        assertEquals("java-iot-codegen", classUnderTest.getName(), "getName() should return 'java-codegen'");
    }
}
