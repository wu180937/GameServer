package com.wmj.game.common.test;

import com.google.protobuf.InvalidProtocolBufferException;
import com.wmj.game.common.message.core.Cmd;
import com.wmj.game.common.message.core.GatewayMessage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @Auther: wumingjie
 * @Date: 2019/3/13
 * @Description:
 */
public class ProtoTest {
    public static void main(String[] args) throws InvalidProtocolBufferException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        byte[] ping = GatewayMessage.PingReq.newBuilder().setCmd(Cmd.UnKnow).build().toByteArray();
        System.err.println(Arrays.toString(ping));
        System.err.println(GatewayMessage.PingReq.parseFrom(ping).getCmd().getNumber());
        long time = System.currentTimeMillis();
        byte[] pong = GatewayMessage.PongRes.newBuilder().setCmd(Cmd.Pong).setSystemTime(time).build().toByteArray();
        System.err.println(Arrays.toString(pong));
        System.err.println(time + " : " + GatewayMessage.PongRes.parseFrom(pong));
        Method method = GatewayMessage.PongRes.class.getMethod("parseFrom", byte[].class);
        System.err.println(method.invoke(null, pong));
    }
}
