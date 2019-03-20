package com.wmj.game.engine.manage;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

import java.util.*;

/**
 * @Author: wumj
 * @Date: 2019/3/16 17:59
 * @Description:
 */
public class WebSocketSession extends AbstractSession {
    private long sessionId;
    private Channel channel;

    public WebSocketSession(long sessionId, Channel channel) {
        this.sessionId = sessionId;
        this.channel = channel;
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
        super.close();
    }

}
