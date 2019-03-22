package com.wmj.game.engine.manage;

import com.wmj.game.common.service.ServiceName;
import com.wmj.game.engine.rpc.client.RpcClient;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.apache.commons.lang3.concurrent.ConcurrentException;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: wumj
 * @Date: 2019/3/16 17:59
 * @Description:
 */
public class WebSocketSession extends AbstractSession {
    private long sessionId;
    private Channel channel;
    private ConcurrentHashMap<ServiceName, RpcClient> rpcClientMap = new ConcurrentHashMap<>();

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
        this.rpcClientMap.values().stream().forEach(rpcClient -> rpcClient.logout(sessionId));
        this.rpcClientMap.clear();
        super.close();
    }

    public void putRpcClient(ServiceName serviceName, RpcClient rpcClient) {
        this.rpcClientMap.put(serviceName, rpcClient);
    }

    public RpcClient getRpcClient(ServiceName serviceName) {
        return this.rpcClientMap.get(serviceName);
    }

    public void removeRpcClient(ServiceName serviceName) {
        this.rpcClientMap.remove(serviceName);
    }
}
