package com.wmj.game.engine.manage;

import com.google.protobuf.ByteString;
import com.wmj.game.engine.rpc.proto.GameRpc;
import io.grpc.stub.StreamObserver;

import java.util.*;

/**
 * @Auther: wumingjie
 * @Date: 2019/3/19
 * @Description:
 */
public class RpcSession extends AbstractSession {
    private long sessionId;
    private StreamObserver<GameRpc.Response> responseObserver;

    public RpcSession(long sessionId, StreamObserver<GameRpc.Response> responseObserver) {
        this.sessionId = sessionId;
        this.responseObserver = responseObserver;
    }

    @Override
    public void send(byte[] data) {
        if (close) {
            return;
        }
        this.responseObserver.onNext(GameRpc.Response.newBuilder().setSessionId(this.sessionId).setData(ByteString.copyFrom(data)).build());
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
        this.responseObserver = null;
        super.close();
    }

}
