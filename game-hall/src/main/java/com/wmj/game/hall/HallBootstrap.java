package com.wmj.game.hall;

import com.wmj.game.common.service.ServiceName;
import com.wmj.game.engine.GameServer;
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
        gameServer.init(ServiceName.HALL, "127.0.0.1", 8500);
        gameServer.startRpcServer("127.0.0.1", 11080);
        log.info("hall started.");
    }
}
