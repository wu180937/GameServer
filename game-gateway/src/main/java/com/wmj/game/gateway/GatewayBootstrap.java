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
        gameServer.startWebSocketServer("192.168.1.66", 10086, false);
        gameServer.startRpcServer("192.168.1.66", 10087);
        System.err.println("启动");
    }
}
