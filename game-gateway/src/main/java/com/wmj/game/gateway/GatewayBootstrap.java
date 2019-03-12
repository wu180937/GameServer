package com.wmj.game.gateway;


import com.wmj.game.common.service.ServiceName;
import com.wmj.game.engine.GameServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Signal;

/**
 * @Auther: wumingjie
 * @Date: 2019/3/5
 * @Description:
 */
public class GatewayBootstrap {
    private final static Logger log = LoggerFactory.getLogger(GatewayBootstrap.class);

    public static void main(String[] args) {
        GameServer gameServer = GameServer.builder().setServiceName(ServiceName.GATEWAY).setConsulHost("127.0.0.1").setConsulPort(8500).build();
        gameServer.startRpcServer("192.168.1.66", 10087);
        gameServer.startRpcClient(ServiceName.HALL);
        gameServer.startWebSocketServer("192.168.1.66", 10086, false);
        log.info("Gateway started.");
    }
}
