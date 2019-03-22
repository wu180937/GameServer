package com.wmj.game.hall;

import com.google.common.net.HostAndPort;
import com.wmj.game.common.service.ServiceName;
import com.wmj.game.engine.GameServer;
import com.wmj.game.engine.rpc.server.RpcServerParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther: wumingjie
 * @Date: 2019/3/12
 * @Description:
 */
public class HallBootstrap {
    private final static Logger log = LoggerFactory.getLogger(HallBootstrap.class);

    public static void main(String[] args) {
        GameServer gameServer = GameServer.getInstance();
        gameServer.start(ServiceName.HALL, HostAndPort.fromParts("127.0.0.1", 8500));
        gameServer.startRpcServer(new RpcServerParam(ServiceName.HALL, "127.0.0.1", 11080));
        log.info("hall started.");
    }
}
