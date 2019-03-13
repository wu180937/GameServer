package com.wmj.game.gateway.handler;

import com.wmj.game.common.message.core.Cmd;
import com.wmj.game.common.message.core.GatewayMessage;
import com.wmj.game.engine.annotation.CmdBean;
import com.wmj.game.engine.annotation.GatewayCmd;

@CmdBean
public class PingHandler {
    @GatewayCmd(cmd = Cmd.Ping_VALUE, protoClazz = GatewayMessage.PingReq.class)
    public void ping(GatewayMessage.PingReq req) {

    }
}
