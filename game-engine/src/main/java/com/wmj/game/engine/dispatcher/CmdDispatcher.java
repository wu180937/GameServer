package com.wmj.game.engine.dispatcher;

import com.wmj.game.common.message.core.CmdLimit;
import com.wmj.game.engine.annotation.CmdBean;
import com.wmj.game.engine.annotation.CmdParam;
import liquibase.servicelocator.DefaultPackageScanClassResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
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
        initGatewayCmd();
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

    private void initGatewayCmd() {
        DefaultPackageScanClassResolver classResolver = new DefaultPackageScanClassResolver();
        classResolver.addClassLoader(Thread.currentThread().getContextClassLoader());
        Set<Class<?>> clazzSet = classResolver.findByFilter(c -> c.isAnnotationPresent(CmdBean.class) && !(c.isAnnotation() || c.isEnum() || c.isInterface() || c.isPrimitive()), SCAN_PACKAGE_NAME);
        clazzSet.stream().forEach(clazz -> {

            Arrays.stream(clazz.getMethods()).forEach(method -> {

            });
        });
    }

    private class CmdObject {
        private String cmd;
        private Object object;
        private Method method;

        public CmdObject(String cmd, Object object, Method method) {
            this.cmd = cmd;
            this.object = object;
            this.method = method;
        }

        public String getCmd() {
            return cmd;
        }

        public Object getObject() {
            return object;
        }

        public Method getMethod() {
            return method;
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
