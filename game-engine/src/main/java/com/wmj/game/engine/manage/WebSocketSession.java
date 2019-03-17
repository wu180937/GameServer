package com.wmj.game.engine.manage;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

import java.nio.ByteBuffer;

/**
 * @Author: wumj
 * @Date: 2019/3/16 17:59
 * @Description:
 */
public class WebSocketSession implements Session {
    private long sessionId;
    private Channel channel;
    private boolean close = false;

    public WebSocketSession(long sessionId, Channel channel) {
        this.sessionId = sessionId;
    }

    @Override
    public void send(byte[] data) {
        if (close) {
            return;
        }
        BinaryWebSocketFrame frame = new BinaryWebSocketFrame(Unpooled.copiedBuffer(data));
        this.channel.writeAndFlush(frame);
    }

    @Override
    public long getSessionId() {
        return this.sessionId;
    }

    @Override
    public synchronized void close() {
        if (close) {
            return;
        }
        if (this.channel.isActive()) {
            this.channel.close();
        }
        close = true;
    }
}
