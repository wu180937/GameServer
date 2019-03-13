package com.wmj.game.gateway;


import com.wmj.game.common.service.ServiceName;
import com.wmj.game.engine.GameServer;
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
        gameServer.init(ServiceName.GATEWAY, "127.0.0.1", 8500);
        gameServer.startRpcServer("127.0.0.1", 10001);
        gameServer.startRpcClient(ServiceName.HALL);
        gameServer.startWebSocketServer("127.0.0.1", 10000, false);
        log.info("Gateway started.");
    }
}
