package com.wmj.game.gateway.test;

import okhttp3.*;
import okio.ByteString;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;

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
                ByteBuffer byteBuffer = ByteBuffer.allocate(4);
                byteBuffer.putInt(1);
                webSocket.send(ByteString.of(byteBuffer.array()));
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
