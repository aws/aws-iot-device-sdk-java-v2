/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

/* This file is part of greengrass-ipc project. */

package software.amazon.awssdk.eventstreamrpc;

import software.amazon.awssdk.crt.io.ClientBootstrap;
import software.amazon.awssdk.crt.io.ClientTlsContext;
import software.amazon.awssdk.crt.io.EventLoopGroup;
import software.amazon.awssdk.crt.io.SocketOptions;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * The closeable elements inside the EventStreamRPCConnectionConfig are not cleaned up when
 * this config object is done. It is still up to the caller of the constructor to clean up
 * resources that are associated in the config.
 *
 * The connect message transformer is used to supply additional connect message headers
 * and supply the payload of the connect message. This is to be used to supply authentication
 * information on the connect
 */
public class EventStreamRPCConnectionConfig {
    private final ClientBootstrap clientBootstrap;
    private final EventLoopGroup eventLoopGroup;
    private final SocketOptions socketOptions;
    private final ClientTlsContext tlsContext;
    private final String host;
    private final int port;

    /**
     * MessageAmendInfo here is used to add supplied headers to the Connect message, and
     * set the payload of that message as well.
     */
    private final Supplier<CompletableFuture<MessageAmendInfo>> connectMessageAmender;

    /**
     * Creates a new EventStreamRPCConnectionConfig with the given data
     * @param clientBootstrap The ClientBootstrap to use
     * @param eventLoopGroup The EventLoopGroup to use
     * @param socketOptions The SocketOptions to use
     * @param tlsContext The TlsContext to use
     * @param host The host name to use
     * @param port The host port to use
     * @param connectMessageAmender The connect message amender to use
     */
    public EventStreamRPCConnectionConfig(ClientBootstrap clientBootstrap, EventLoopGroup eventLoopGroup,
                                          SocketOptions socketOptions, ClientTlsContext tlsContext,
                                          String host, int port, Supplier<CompletableFuture<MessageAmendInfo>> connectMessageAmender) {
        this.clientBootstrap = clientBootstrap;
        this.eventLoopGroup = eventLoopGroup;
        this.socketOptions = socketOptions;
        this.tlsContext = tlsContext;
        this.host = host;
        this.port = port;
        this.connectMessageAmender = connectMessageAmender;

        //bit of C++ RAII here, validate what we can
        if (clientBootstrap == null || eventLoopGroup == null || socketOptions == null ||
            host == null || host.isEmpty()) {
            throw new IllegalArgumentException("EventStreamRPCConnectionConfig values are invalid!");
        }
    }

    /**
     * Returns the ClientBootstrap associated with the EventStreamRPCConnectionConfig
     * @return the ClientBootstrap associated with the EventStreamRPCConnectionConfig
     */
    public ClientBootstrap getClientBootstrap() {
        return clientBootstrap;
    }

    /**
     * Returns the EventLoopGroup associated with the EventStreamRPCConnectionConfig
     * @return the EventLoopGroup associated with the EventStreamRPCConnectionConfig
     */
    public EventLoopGroup getEventLoopGroup() {
        return eventLoopGroup;
    }

    /**
     * Returns the SocketOptions associated with the EventStreamRPCConnectionConfig
     * @return The SocketOptions associated with the EventStreamRPCConnectionConfig
     */
    public SocketOptions getSocketOptions() {
        return socketOptions;
    }

    /**
     * Returns the TlsContext associated with the EventStreamRPCConnectionConfig
     * @return The TlsContext associated with the EventStreamRPCConnectionConfig
     */
    public ClientTlsContext getTlsContext() {
        return tlsContext;
    }

    /**
     * Returns the host name associated with the EventStreamRPCConnectionConfig
     * @return The host name associated with the EventStreamRPCConnectionConfig
     */
    public String getHost() {
        return host;
    }

    /**
     * Returns the port associated with the EventStreamRPCConnectionConfig
     * @return The port associated with the EventStreamRPCConnectionConfig
     */
    public int getPort() {
        return port;
    }

    /**
     * Returns the connect message amender associated with the EventStreamRPCConnectionConfig
     * @return The connect message amender associated with the EventStreamRPCConnectionConfig
     */
    public Supplier<CompletableFuture<MessageAmendInfo>> getConnectMessageAmender() {
        return connectMessageAmender;
    }
}
