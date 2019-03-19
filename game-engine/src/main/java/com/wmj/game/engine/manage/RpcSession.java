package com.wmj.game.engine.manage;

import com.google.common.primitives.Bytes;
import com.google.protobuf.ByteString;
import com.wmj.game.engine.rpc.proto.GameRpc;
import io.grpc.stub.StreamObserver;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: wumingjie
 * @Date: 2019/3/19
 * @Description:
 */
public class RpcSession implements Session {
    private long sessionId;
    private StreamObserver<GameRpc.Response> responseObserver;
    private Map<String, Object> attributeMap;

    public RpcSession(long sessionId, StreamObserver<GameRpc.Response> responseObserver) {
        this.sessionId = sessionId;
        this.responseObserver = responseObserver;
        this.attributeMap = new HashMap<>();
    }

    @Override
    public void send(byte[] data) {
        this.responseObserver.onNext(GameRpc.Response.newBuilder().setSessionId(this.sessionId).setData(ByteString.copyFrom(data)).build());
    }

    @Override
    public long getSessionId() {
        return this.sessionId;
    }

    @Override
    public void close() {
        this.responseObserver = null;
    }

    @Override
    public synchronized void putAttribute(String key, Object value) {
        this.attributeMap.put(key, value);
    }

    @Override
    public synchronized <T> T getAttribute(String key, Class<T> clazz) {
        return clazz.cast(this.attributeMap.get(key));
    }

    @Override
    public synchronized void removeAttribute(String key) {
        this.attributeMap.remove(key);
    }
}
