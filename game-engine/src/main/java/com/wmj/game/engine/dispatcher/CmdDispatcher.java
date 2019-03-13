package com.wmj.game.engine.dispatcher;

import com.wmj.game.common.message.core.CmdLimit;
import com.wmj.game.common.message.core.GatewayMessage;
import liquibase.servicelocator.DefaultPackageScanClassResolver;
import liquibase.servicelocator.PackageScanFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * @Auther: wumingjie
 * @Date: 2019/3/13
 * @Description:
 */
public class CmdDispatcher {
    private final static Logger log = LoggerFactory.getLogger(CmdDispatcher.class);
    private final static CmdDispatcher INSTANCE = new CmdDispatcher();
    private final static String SCAN_PACKAGE_NAME = "com.wmj.game";

    private CmdDispatcher() {
    }

    public static CmdDispatcher getInstance() {
        return INSTANCE;
    }

    public void init() {
        initSystemCmd();
    }

    public void dispatcher(int cmd, byte[] data) {
        if (cmd >= CmdLimit.SystemBeginCmd_VALUE && cmd <= CmdLimit.SystemEndCmd_VALUE) {

        } else if (cmd >= CmdLimit.GateWayBeginCmd_VALUE && cmd <= CmdLimit.GateWayEndCmd_VALUE) {

        } else if (cmd >= CmdLimit.HallBeginCmd_VALUE && cmd <= CmdLimit.HallEndCmd_VALUE) {

        } else if (cmd >= CmdLimit.GameBeginCmd_VALUE && cmd <= CmdLimit.GameEndCmd_VALUE) {

        } else {
            log.warn("接收到超出限制的cmd : " + cmd);
        }
    }

    private void initSystemCmd() {
        DefaultPackageScanClassResolver classResolver = new DefaultPackageScanClassResolver();
        classResolver.addClassLoader(Thread.currentThread().getContextClassLoader());
        Set<Class<?>> clazzSet = classResolver.findByFilter(c -> !(c.isAnnotation() || c.isEnum() || c.isInterface() || c.isPrimitive()), SCAN_PACKAGE_NAME);
        clazzSet.stream();
    }

}
