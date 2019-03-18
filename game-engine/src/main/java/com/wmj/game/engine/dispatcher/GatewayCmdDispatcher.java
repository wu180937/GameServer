package com.wmj.game.engine.dispatcher;

import com.google.protobuf.ByteString;
import com.wmj.game.common.service.ServiceName;
import com.wmj.game.common.session.SessionKey;
import com.wmj.game.engine.GameServer;
import com.wmj.game.engine.manage.Session;
import com.wmj.game.engine.rpc.client.RpcClient;
import com.wmj.game.engine.rpc.client.RpcClientPool;
import com.wmj.game.engine.rpc.proto.GameRpc;
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
        RpcClient rpcClient = session.getAttribute(SessionKey.RPC_HALL_CLIENT_KEY, RpcClient.class);
        synchronized (session) {
            if (rpcClient == null) {
                RpcClientPool clientPool = GameServer.getInstance().getRpcClientPool(ServiceName.HALL);
                if (clientPool == null) {
                    log.error("没有可用的hall服务");
                    return;
                }
                rpcClient = clientPool.getRpcClient();
                session.putAttribute(SessionKey.RPC_HALL_CLIENT_KEY, rpcClient);
            }
        }
        rpcClient.send(GameRpc.Request.newBuilder().setSessionId(session.getSessionId()).setData(ByteString.copyFrom(data)).build());
    }

    @Override
    protected void gameHandler(Session session, int cmd, byte[] data) {

    }
}
