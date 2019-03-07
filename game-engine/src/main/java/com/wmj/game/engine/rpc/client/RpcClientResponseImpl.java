package com.wmj.game.engine.rpc.client;

import com.wmj.game.engine.rpc.proto.GameRpc;
import io.grpc.stub.StreamObserver;

/**
 * @Auther: wumingjie
 * @Date: 2019/3/8
 * @Description:
 */
public class RpcClientResponseImpl implements StreamObserver<GameRpc.Response> {
    @Override
    public void onNext(GameRpc.Response response) {
        long sessionId = response.getSessionId();
        
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onCompleted() {

    }
}
