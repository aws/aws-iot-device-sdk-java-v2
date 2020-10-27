/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.smithy.java.codegen;

public final class NameUtils {
    private NameUtils() {
    }

    public static String uncapitalize(String str) {
        char[] c = str.toCharArray();
        c[0] = Character.toLowerCase(c[0]);
        return new String(c);
    }

    public static String capitalize(String str) {
        char[] c = str.toCharArray();
        c[0] = Character.toUpperCase(c[0]);
        return new String(c);
    }

    /**
     * Takes an expected camelcase string and uses the case change as a word divider,
     * inserts an underscore delimiter, and upper cases all of the letters. This is
     * consistent with Java final constants and enum constants
     *
     * Examples:
     *  exampleString = EXAMPLE_STRING
     *  MyNamedType = MY_NAMED_TYPE
     *
     * @param label CamelCase input string to convert
     * @return string converted to Java constant case
     */
    public static String camelToConstantCase(final String label) {
        final StringBuilder stringBuilder = new StringBuilder();
        char[] characters = label.toCharArray();
        Boolean isPreviousUpper = null; //allows the first character to be whatever
        for (int idx = 0; idx < characters.length; ++idx) {
            boolean isUpper = false;
            isUpper = Character.isUpperCase(characters[idx]);

            if (isPreviousUpper != null && !isPreviousUpper && isUpper) {
                stringBuilder.append('_');
            }
            stringBuilder.append(characters[idx]);
            isPreviousUpper = isUpper;
        }
        return stringBuilder.toString();
    }
}
