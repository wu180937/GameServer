package com.wmj.game.gateway.test;

import com.google.protobuf.InvalidProtocolBufferException;
import com.wmj.game.common.message.core.Cmd;
import com.wmj.game.common.message.core.GatewayMessage;

import java.util.Arrays;

public class TestCounter {
    public static void main(String[] args) throws InvalidProtocolBufferException {
        GatewayMessage.PingReq pingReq = GatewayMessage.PingReq.newBuilder().setCmd(Cmd.Ping).build();
        System.err.println(Arrays.toString(pingReq.toByteArray()));
        GatewayMessage.PongRes pongRes = GatewayMessage.PongRes.newBuilder().setCmd(Cmd.Pong).setSystemTime(System.currentTimeMillis()).build();
        System.err.println(Arrays.toString(pongRes.toByteArray()));
        byte[] src = pingReq.toByteArray();
        System.err.println(test(1, src));
    }

    public static int test(int pos, byte[] data) {
        fastPath:
        {
            int tempPos = pos;
            int x;
            if ((x = data[tempPos++]) >= 0) {
                return x;
            } else if ((x ^= (data[tempPos++] << 7)) < 0) {
                x ^= (~0 << 7);
            } else if ((x ^= (data[tempPos++] << 14)) >= 0) {
                x ^= (~0 << 7) ^ (~0 << 14);
            } else if ((x ^= (data[tempPos++] << 21)) < 0) {
                x ^= (~0 << 7) ^ (~0 << 14) ^ (~0 << 21);
            } else {
                int y = data[tempPos++];
                x ^= y << 28;
                x ^= (~0 << 7) ^ (~0 << 14) ^ (~0 << 21) ^ (~0 << 28);
                if (y < 0
                        && data[tempPos++] < 0
                        && data[tempPos++] < 0
                        && data[tempPos++] < 0
                        && data[tempPos++] < 0
                        && data[tempPos++] < 0) {
                    break fastPath;
                }
            }
            return x;
        }
        return 0;
    }
}
