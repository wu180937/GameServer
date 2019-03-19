package com.wmj.game.engine.dispatcher;

import com.wmj.game.common.message.core.CmdLimit;
import com.wmj.game.engine.annotation.*;
import com.wmj.game.engine.manage.Session;
import liquibase.servicelocator.DefaultPackageScanClassResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther: wumingjie
 * @Date: 2019/3/13
 * @Description:
 */
public abstract class CmdDispatcher {
    private final static Logger log = LoggerFactory.getLogger(CmdDispatcher.class);
    protected final static String SCAN_PACKAGE_NAME = "com.wmj.game";
    protected final static ConcurrentHashMap<Integer, CmdObject> systemCmdMap = new ConcurrentHashMap<>();
    protected final static ConcurrentHashMap<Integer, CmdObject> gatewayCmdMap = new ConcurrentHashMap<>();
    protected final static ConcurrentHashMap<Integer, CmdObject> hallCmdMap = new ConcurrentHashMap<>();

    protected CmdDispatcher() {
        init();
    }

    public void dispatcher(Session session, int cmd, byte[] data) {
        if (isSystemCmd(cmd)) {
            systemHandler(session, cmd, data);
        } else if (isGatewayCmd(cmd)) {
            gatewayHandler(session, cmd, data);
        } else if (isHallCmd(cmd)) {
            hallHandler(session, cmd, data);
        } else if (isGameCmd(cmd)) {
            gameHandler(session, cmd, data);
        } else {
            log.warn("接收到超出限制的cmd : " + cmd);
        }
    }

    public static boolean isSystemCmd(int cmd) {
        return cmd >= CmdLimit.SystemBeginCmd_VALUE && cmd <= CmdLimit.SystemEndCmd_VALUE;
    }

    public static boolean isGatewayCmd(int cmd) {
        return cmd >= CmdLimit.GatewayBeginCmd_VALUE && cmd <= CmdLimit.GatewayEndCmd_VALUE;
    }

    public static boolean isHallCmd(int cmd) {
        return cmd >= CmdLimit.HallBeginCmd_VALUE && cmd <= CmdLimit.HallEndCmd_VALUE;
    }

    public static boolean isGameCmd(int cmd) {
        return cmd >= CmdLimit.GameBeginCmd_VALUE && cmd <= CmdLimit.GameEndCmd_VALUE;
    }

    protected abstract void systemHandler(Session session, int cmd, byte[] data);

    protected abstract void gatewayHandler(Session session, int cmd, byte[] data);

    protected abstract void hallHandler(Session session, int cmd, byte[] data);

    protected abstract void gameHandler(Session session, int cmd, byte[] data);

    private void init() {
        DefaultPackageScanClassResolver classResolver = new DefaultPackageScanClassResolver();
        classResolver.addClassLoader(Thread.currentThread().getContextClassLoader());
        Set<Class<?>> clazzSet = classResolver.findByFilter(
                c -> c.isAnnotationPresent(CmdSingletonBean.class) && !(c.isAnnotation() || c.isEnum() || c.isInterface() || c.isPrimitive()),
                SCAN_PACKAGE_NAME);
        clazzSet.stream().forEach(clazz -> {
            Arrays.stream(clazz.getMethods()).forEach(method -> {
                try {
                    Object obj = clazz.newInstance();
                    if (clazz.isAnnotationPresent(PostConstruct.class)) {// 初始化类
                        method.invoke(obj);
                    }
                    if (method.isAnnotationPresent(SystemCmd.class)) {
                        SystemCmd systemCmd = method.getAnnotation(SystemCmd.class);
                        systemCmdMap.put(systemCmd.cmd(), new CmdObject(systemCmd.cmd(), obj, method, systemCmd.protoClazz()));
                    }
                    if (method.isAnnotationPresent(GatewayCmd.class)) {
                        GatewayCmd gatewayCmd = method.getAnnotation(GatewayCmd.class);
                        gatewayCmdMap.put(gatewayCmd.cmd(), new CmdObject(gatewayCmd.cmd(), obj, method, gatewayCmd.protoClazz()));
                    }
                    if (method.isAnnotationPresent(HallCmd.class)) {
                        HallCmd hallCmd = method.getAnnotation(HallCmd.class);
                        hallCmdMap.put(hallCmd.cmd(), new CmdObject(hallCmd.cmd(), obj, method, hallCmd.protoClazz()));
                    }
                } catch (InstantiationException e) {
                    log.info("", e);
                } catch (IllegalAccessException e) {
                    log.info("", e);
                } catch (InvocationTargetException e) {
                    log.info("", e);
                }
            });
        });
    }
}
