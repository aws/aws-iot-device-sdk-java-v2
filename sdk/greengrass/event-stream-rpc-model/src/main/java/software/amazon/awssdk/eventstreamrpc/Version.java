/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.eventstreamrpc;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * https://semver.org/ minus the labels
 */
public class Version implements Comparable<Version> {
    private final static String PATTERN_REGEX_STRING = "^(\\d+)\\.(\\d+)\\.(\\d+)$";
    /**
     * The Regex pattern used to parse versions from strings
     */
    public final static Pattern PATTERN = Pattern.compile(PATTERN_REGEX_STRING);

    /**
     * Default major version number. Defaults to 0
     */
    public static final int MAJOR = 0;

    /**
     * Default minor version number. Defaults to 1
     */
    public static final int MINOR = 1;

    /**
     * Default patch version number. Defaults to 0
     */
    public static final int PATCH = 0;

    private static final Version INSTANCE = new Version(MAJOR, MINOR, PATCH);

    /**
     * @return Returns an instance of the version
     */
    public static Version getInstance() { return INSTANCE; }

    private final int major;
    private final int minor;
    private final int patch;

    private Version(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    /**
     * Returns the Version in string representation in the format: Major.Minor.Patch.
     * @return The Version converted to a string
     */
    public String getVersionString() {
        return String.format("%d.%d.%d", MAJOR, MINOR, PATCH);
    }

    @Override
    public String toString() {
        return getVersionString();
    }

    /**
     * Returns a new Version class from the given version string.
     * Will throw an exception if it cannot convert.
     *
     * @param versionString The version string to convert
     * @return The Version class created from the string
     */
    public static Version fromString(final String versionString) {
        if (versionString == null) {
            throw new IllegalArgumentException("Cannot extract version from null string");
        }
        final Matcher matcher = PATTERN.matcher(versionString);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Version string does not match regex: " + PATTERN_REGEX_STRING);
        }
        return new Version(Integer.parseInt(matcher.group(1)),
                Integer.parseInt(matcher.group(2)),
                Integer.parseInt(matcher.group(3)));
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, patch);
    }

    @Override
    public boolean equals(final Object rhs) {
        if (rhs == null) {
            return false;
        }
        if (rhs instanceof Version) {
            return compareTo((Version)rhs) == 0;
        }
        return false;
    }

    @Override
    public int compareTo(final Version rhs) {
        if (rhs == null) {
            throw new IllegalArgumentException("Cannot compare to null version!");
        }
        if (major - rhs.major == 0) {
            if (minor - rhs.minor == 0) {
                return patch - rhs.patch;
            }
            return minor - rhs.minor;
        }
        return major - rhs.major;
    }
}
