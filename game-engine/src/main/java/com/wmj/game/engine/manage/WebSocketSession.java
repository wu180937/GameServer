package com.wmj.game.engine.manage;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: wumj
 * @Date: 2019/3/16 17:59
 * @Description:
 */
public class WebSocketSession implements Session {
    private long sessionId;
    private Channel channel;
    private boolean close = false;
    private Map<String, Object> attributeMap;

    public WebSocketSession(long sessionId, Channel channel) {
        this.sessionId = sessionId;
        this.attributeMap = new HashMap<>();
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
        this.attributeMap.clear();
        close = true;
    }

    @Override
    public synchronized void putAttribute(String key, Object value) {
        this.attributeMap.put(key, value);
    }

    @Override
    public synchronized <T> T getAttribute(String key, Class<T> clazz) {
        return clazz.cast(this.attributeMap.get(key));
    }
}
