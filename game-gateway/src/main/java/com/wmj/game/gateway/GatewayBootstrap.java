package com.wmj.game.gateway;


import com.wmj.game.common.service.ServiceName;
import com.wmj.game.engine.GameServer;
import com.wmj.game.engine.rpc.server.RpcServerParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther: wumingjie
 * @Date: 2019/3/5
 * @Description:
 */
public class GatewayBootstrap {
    private final static Logger log = LoggerFactory.getLogger(GatewayBootstrap.class);

    public static void main(String[] args) {
        GameServer gameServer = GameServer.getInstance();
        gameServer.start(ServiceName.GATEWAY, "127.0.0.1", 8500);
        gameServer.startRpcServer(new RpcServerParam(ServiceName.GATEWAY, "127.0.0.1", 10001));
        gameServer.startRpcClient(ServiceName.HALL);
        gameServer.startGatewayServer("127.0.0.1", 10000, false);
        log.info("Gateway started.");
    }
}
