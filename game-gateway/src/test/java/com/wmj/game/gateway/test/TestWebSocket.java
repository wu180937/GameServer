package com.wmj.game.gateway.test;

import com.google.protobuf.Descriptors;
import com.wmj.game.common.message.core.Cmd;
import com.wmj.game.common.message.core.GatewayMessage;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import okhttp3.*;
import okio.ByteString;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestWebSocket {
    public static void main(String[] args) {
        OkHttpClient client = new OkHttpClient.Builder().build();
        //构造request对象
        Request request = new Request.Builder()
                .header("Upgrade", "websocket")
                .header("Connection", "Upgrade")
                .header("Sec-WebSocket-Version", "13")
                .url("ws://127.0.0.1:10000/websocket")
                .build();
        WebSocket webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                System.err.println("连接上了");
                Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(() -> {
                    byte[] data = GatewayMessage.PingReq.newBuilder().setCmd(Cmd.Ping).build().toByteArray();
                    webSocket.send(ByteString.of(data));
                }, 0, 5, TimeUnit.SECONDS);
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
                System.err.println("连接出错");
                t.printStackTrace();
            }
        });

    }
}
