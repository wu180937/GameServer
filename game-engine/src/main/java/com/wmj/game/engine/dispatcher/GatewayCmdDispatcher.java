package com.wmj.game.engine.dispatcher;

import com.wmj.game.engine.manage.Session;
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
        
    }

    @Override
    protected void gatewayHandler(Session session, int cmd, byte[] data) {
        CmdObject cmdObject = gatewayCmdMap.get(cmd);
        if (cmdObject == null) {
            log.error("未知的cmd : " + cmd);
        }
    }

    @Override
    protected void hallHandler(Session session, int cmd, byte[] data) {

    }

    @Override
    protected void gameHandler(Session session, int cmd, byte[] data) {

    }
}
