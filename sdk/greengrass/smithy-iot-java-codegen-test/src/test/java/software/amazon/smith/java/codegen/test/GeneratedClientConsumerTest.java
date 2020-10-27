/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.smith.java.codegen.test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GeneratedClientConsumerTest {
    @Test
    void testVerifyClient() {
        GeneratedClientConsumer classUnderTest = new GeneratedClientConsumer();
        assertTrue(classUnderTest.verifyClient(), "verifyClient() should return 'true'");
    }
}
