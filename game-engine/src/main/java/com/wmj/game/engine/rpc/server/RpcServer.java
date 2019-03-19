package com.wmj.game.engine.rpc.server;

import com.wmj.game.engine.manage.RpcSessionManage;
import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class RpcServer implements Runnable {
    private final static Logger log = LoggerFactory.getLogger(RpcServer.class);
    private String serviceName;
    private String host;
    private int port;
    private final RpcSessionManage rpcSessionManage;

    public RpcServer(String serviceName, String host, int port) {
        this.serviceName = serviceName;
        this.host = host;
        this.port = port;
        this.rpcSessionManage = new RpcSessionManage();
    }

    @Override
    public void run() {
        final Server server = NettyServerBuilder.forAddress(new InetSocketAddress(host, port))
                .addService(new RpcGameServiceImpl(this.serviceName, this.rpcSessionManage).bindService())
                .permitKeepAliveTime(10, TimeUnit.SECONDS)
                .build();
        try {
            server.start();
            log.info("server [" + serviceName + "] rpc service started bind port : " + port);
            server.awaitTermination();
        } catch (IOException | InterruptedException e) {
            log.error("出错了", e);
        }
    }
}
