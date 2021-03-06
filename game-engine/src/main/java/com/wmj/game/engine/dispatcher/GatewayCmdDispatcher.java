package com.wmj.game.engine.dispatcher;

import com.wmj.game.common.message.core.Cmd;
import com.wmj.game.common.message.core.GatewayMessage;
import com.wmj.game.common.service.ServiceName;
import com.wmj.game.engine.GameServer;
import com.wmj.game.engine.manage.Session;
import com.wmj.game.engine.manage.WebSocketSession;
import com.wmj.game.engine.rpc.client.RpcClient;
import com.wmj.game.engine.rpc.client.RpcClientPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        WebSocketSession wsSession = WebSocketSession.class.cast(session);
        RpcClient rpcClient = wsSession.getRpcClient(ServiceName.HALL);
        synchronized (session) {
            if (rpcClient == null) {
                RpcClientPool clientPool = GameServer.getInstance().getRpcClientPool(ServiceName.HALL);
                if (clientPool == null) {
                    log.error("没有可用的hall服务");
                    return;
                }
                rpcClient = clientPool.getRpcClient();
                wsSession.putRpcClient(ServiceName.HALL, rpcClient);
                rpcClient.addCloseHook(session.getSessionId(), () -> wsSession.close());
            }
        }
        if (!rpcClient.isActive()) {
            GatewayMessage.ReconnectRes reconnectRes = GatewayMessage.ReconnectRes.newBuilder().setCmd(Cmd.Reconnect).build();
            session.send(reconnectRes.toByteArray());
            return;
        }
        rpcClient.send(session.getSessionId(), data);
    }

    @Override
    protected void gameHandler(Session session, int cmd, byte[] data) {

    }
}
