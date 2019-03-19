package com.wmj.game.gateway.handler;

import com.wmj.game.common.constant.ErrorCode;
import com.wmj.game.common.message.core.Cmd;
import com.wmj.game.common.message.core.GatewayMessage;
import com.wmj.game.engine.annotation.CmdSingletonBean;
import com.wmj.game.engine.annotation.GatewayCmd;
import com.wmj.game.engine.manage.Session;

@CmdSingletonBean
public class PingHandler {
    @GatewayCmd(cmd = Cmd.Ping_VALUE, protoClazz = GatewayMessage.PingReq.class)
    public void ping(Session session, GatewayMessage.PingReq req) {
        System.err.println("收到ping");
        GatewayMessage.PongRes res = GatewayMessage.PongRes.newBuilder().setCmd(Cmd.Pong).setErrorCode(ErrorCode.Success).setSystemTime(System.currentTimeMillis()).build();
        session.send(res.toByteArray());
    }
}
