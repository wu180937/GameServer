package com.wmj.game.engine.rpc;

import com.wmj.game.common.rpc.RpcServiceName;
import com.wmj.game.engine.GameServer;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerServiceDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RpcServer {
    private final static Logger log = LoggerFactory.getLogger(RpcServer.class);
    private String serviceName;
    private int port;

    public RpcServer(String serviceName, int port) {
        this.serviceName = serviceName;
        this.port = port;
    }

    public void start() {
        ServerServiceDefinition.Builder builder = ServerServiceDefinition.builder(RpcServiceName.GAME_RPC_SERVICE_NAME);
        final Server server = ServerBuilder.forPort(port).addService(builder.build()).build();
        try {
            server.start();
            log.info("server [" + serviceName + "] rpc service started bind port : " + port);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> server.shutdown()));
        } catch (IOException e) {
            log.error("出错了", e);
        }
    }
}
