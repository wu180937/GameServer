package com.wmj.game.engine.dispatcher;

import com.wmj.game.common.service.ServiceName;
import com.wmj.game.engine.GameServer;
import com.wmj.game.engine.manage.Session;
import com.wmj.game.engine.rpc.client.RpcClientPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ForkJoinPool;

public class GatewayCmdDispatcher extends CmdDispatcher {
    private final static Logger log = LoggerFactory.getLogger(GatewayCmdDispatcher.class);

    public GatewayCmdDispatcher() {
        super();
    }

    @Override
    protected void systemHandler(Session session, int cmd, byte[] data) {
        CmdObject cmdObject = systemCmdMap.get(cmd);
        if (cmdObject == null) {
            log.error("未知的cmd : " + cmd);
        }
        cmdObject.invoke(session, data);
    }

    @Override
    protected void gatewayHandler(Session session, int cmd, byte[] data) {
        CmdObject cmdObject = gatewayCmdMap.get(cmd);
        if (cmdObject == null) {
            log.error("未知的cmd : " + cmd);
        }
        cmdObject.invoke(session, data);
    }

    @Override
    protected void hallHandler(Session session, int cmd, byte[] data) {
        RpcClientPool clientPool = GameServer.getInstance().getRpcClientPool(ServiceName.HALL);
        if (clientPool == null) {

        }
    }

    @Override
    protected void gameHandler(Session session, int cmd, byte[] data) {

    }
}
