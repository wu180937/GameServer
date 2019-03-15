package com.wmj.game.engine.dispatcher;

import com.google.protobuf.GeneratedMessageV3;
import com.wmj.game.common.message.core.Cmd;
import com.wmj.game.common.message.core.CmdLimit;
import com.wmj.game.engine.annotation.*;
import liquibase.servicelocator.DefaultPackageScanClassResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther: wumingjie
 * @Date: 2019/3/13
 * @Description:
 */
public abstract class CmdDispatcher {
    private final static Logger log = LoggerFactory.getLogger(CmdDispatcher.class);
    private final static String SCAN_PACKAGE_NAME = "com.wmj.game";
    private final static ConcurrentHashMap<Integer, CmdObject> systemCmdMap = new ConcurrentHashMap<>();
    private final static ConcurrentHashMap<Integer, CmdObject> gatewayCmdMap = new ConcurrentHashMap<>();
    private final static ConcurrentHashMap<Integer, CmdObject> hallCmdMap = new ConcurrentHashMap<>();

    protected CmdDispatcher() {
        init();
    }

    public void dispatcher(int cmd, byte[] data) {
        if (cmd >= CmdLimit.SystemBeginCmd_VALUE && cmd <= CmdLimit.SystemEndCmd_VALUE) {
            systemHandler(cmd, data);
        } else if (cmd >= CmdLimit.GateWayBeginCmd_VALUE && cmd <= CmdLimit.GateWayEndCmd_VALUE) {
            gatewayHandler(cmd, data);
        } else if (cmd >= CmdLimit.HallBeginCmd_VALUE && cmd <= CmdLimit.HallEndCmd_VALUE) {
            hallHandler(cmd, data);
        } else if (cmd >= CmdLimit.GameBeginCmd_VALUE && cmd <= CmdLimit.GameEndCmd_VALUE) {
            gameHandler(cmd, data);
        } else {
            log.warn("接收到超出限制的cmd : " + cmd);
        }
    }

    protected void systemHandler(int cmd, byte[] data) {
        CmdObject cmdObject = systemCmdMap.get(cmd);
        if (cmdObject == null) {
            log.error("未知的cmd : " + cmd);
        }
    }

    protected void gatewayHandler(int cmd, byte[] data) {
        CmdObject cmdObject = gatewayCmdMap.get(cmd);
        if (cmdObject == null) {
            log.error("未知的cmd : " + cmd);
        }
    }

    protected void hallHandler(int cmd, byte[] data) {
        CmdObject cmdObject = hallCmdMap.get(cmd);
        if (cmdObject == null) {
            log.error("未知的cmd : " + cmd);
        }
    }

    protected void gameHandler(int cmd, byte[] data) {

    }

    private void init() {
        DefaultPackageScanClassResolver classResolver = new DefaultPackageScanClassResolver();
        classResolver.addClassLoader(Thread.currentThread().getContextClassLoader());
        Set<Class<?>> clazzSet = classResolver.findByFilter(
                c -> c.isAnnotationPresent(CmdBean.class) && !(c.isAnnotation() || c.isEnum() || c.isInterface() || c.isPrimitive()),
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
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    private static class CmdObject {
        private int cmd;
        private Object object;
        private Method method;
        private Class<? extends GeneratedMessageV3> protoClazz;

        public CmdObject(int cmd, Object object, Method method, Class<? extends GeneratedMessageV3> protoClazz) {
            this.cmd = cmd;
            this.object = object;
            this.method = method;
            this.protoClazz = protoClazz;
        }

        public int getCmd() {
            return cmd;
        }

        public Object getObject() {
            return object;
        }

        public Method getMethod() {
            return method;
        }

        public Class<? extends GeneratedMessageV3> getProtoClazz() {
            return protoClazz;
        }

        @Override
        public boolean equals(Object object1) {
            if (this == object1) return true;
            if (object1 == null || getClass() != object1.getClass()) return false;
            CmdObject cmdObject = (CmdObject) object1;
            return Objects.equals(cmd, cmdObject.cmd) &&
                    Objects.equals(object, cmdObject.object) &&
                    Objects.equals(method, cmdObject.method);
        }

        @Override
        public int hashCode() {
            return Objects.hash(cmd, object, method);
        }

        @Override
        public String toString() {
            return "CmdObject{" +
                    "cmd='" + cmd + '\'' +
                    ", object=" + object +
                    ", method=" + method +
                    '}';
        }

        public Object invoke(Map<String, Object> namedParamMap, Map<Class<?>, Object> typeParamMap) throws InvocationTargetException, IllegalAccessException {
            Set<Class<?>> typeSet = typeParamMap.keySet();
            Parameter[] parameters = method.getParameters();
            Object[] paramObjects = new Object[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                if (typeSet.contains(parameter.getType())) {
                    paramObjects[i] = typeParamMap.get(parameter.getType());
                    continue;
                }
                String name = parameter.getAnnotation(CmdParam.class) != null
                        ? parameter.getAnnotation(CmdParam.class).value()
                        : parameter.getName();
                paramObjects[i] = namedParamMap.get(name);
            }
            return method.invoke(this.getObject(), paramObjects);
        }

    }
}
