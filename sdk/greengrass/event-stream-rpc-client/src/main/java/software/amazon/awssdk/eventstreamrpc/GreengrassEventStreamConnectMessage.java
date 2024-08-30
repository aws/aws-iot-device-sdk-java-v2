/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

/* This file is part of greengrass-ipc project. */

package software.amazon.awssdk.eventstreamrpc;

/**
 * A Greengrass EventStream connection message
 */
public class GreengrassEventStreamConnectMessage {

    private String authToken;

    /**
     * Sets the authorization token in the connect message
     * @param authToken the authorization token to use
     */
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    /**
     * Returns the authorization token in the connect message
     * @return authorization token in the connect message
     */
    public String getAuthToken() {
        return this.authToken;
    }
}
