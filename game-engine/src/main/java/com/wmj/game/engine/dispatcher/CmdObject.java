package com.wmj.game.engine.dispatcher;

import com.google.protobuf.GeneratedMessageV3;
import com.wmj.game.common.message.core.GatewayMessage;
import com.wmj.game.engine.manage.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;

public class CmdObject {
    private final static Logger log = LoggerFactory.getLogger(CmdObject.class);
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CmdObject cmdObject = (CmdObject) o;
        return cmd == cmdObject.cmd &&
                Objects.equals(object, cmdObject.object) &&
                Objects.equals(method, cmdObject.method) &&
                Objects.equals(protoClazz, cmdObject.protoClazz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cmd, object, method, protoClazz);
    }

    @Override
    public String toString() {
        return "CmdObject{" +
                "cmd=" + cmd +
                ", object=" + object +
                ", method=" + method +
                ", protoClazz=" + protoClazz +
                '}';
    }

    public void invoke(Session session, byte[] data) {
        try {
            Method protoMethod = this.protoClazz.getMethod("parseFrom", byte[].class);
            Object dataObject = protoMethod.invoke(null, data);
            Parameter[] parameters = method.getParameters();
            Object[] paramObjects = new Object[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                if (Session.class.isAssignableFrom(parameters[i].getType())) {
                    paramObjects[i] = session;
                } else if (GeneratedMessageV3.class.isAssignableFrom(parameters[i].getType())) {
                    paramObjects[i] = dataObject;
                } else {
                    paramObjects[i] = null;
                }
            }
            method.invoke(this.getObject(), paramObjects);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("CmdObject error!", e);
        }
    }

}
