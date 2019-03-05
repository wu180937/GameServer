package com.wmj.game.gateway;


import com.wmj.game.common.service.ServiceName;
import com.wmj.game.engine.GameServer;

/**
 * @Auther: wumingjie
 * @Date: 2019/3/5
 * @Description:
 */
public class GatewayBootstrap {
    public static void main(String[] args) {
        GameServer gameServer = GameServer.builder().setServiceName(ServiceName.GATEWAY).setConsulHost("127.0.0.1").setConsulPort(8500).build();
        gameServer.startWebSocket(9000, false);
    }
}
