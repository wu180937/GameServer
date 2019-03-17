package com.wmj.game.engine.dispatcher;

import com.google.protobuf.GeneratedMessageV3;
import com.wmj.game.engine.annotation.CmdParam;
import com.wmj.game.engine.manage.Session;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class CmdObject {
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

    public void invoke(Session session, byte[] data) throws InvocationTargetException, IllegalAccessException {
        try {
            Method protoMethod = this.protoClazz.getMethod("parseFrom", byte[].class);
            Object dataObject = method.invoke(null, data);
            Parameter[] parameters = method.getParameters();
            Object[] paramObjects = new Object[parameters.length];
            Arrays.stream(parameters).forEach(parameter -> {

            });
            method.invoke(this.getObject(), paramObjects);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
//        Set<Class<?>> typeSet = typeParamMap.keySet();
//        Parameter[] parameters = method.getParameters();
//        Object[] paramObjects = new Object[parameters.length];
//        for (int i = 0; i < parameters.length; i++) {
//            Parameter parameter = parameters[i];
//            if (typeSet.contains(parameter.getType())) {
//                paramObjects[i] = typeParamMap.get(parameter.getType());
//                continue;
//            }
//            String name = parameter.getAnnotation(CmdParam.class) != null
//                    ? parameter.getAnnotation(CmdParam.class).value()
//                    : parameter.getName();
//            paramObjects[i] = namedParamMap.get(name);     }
    }

}
