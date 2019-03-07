package com.wmj.game.engine.rpc.client;

import com.wmj.game.engine.rpc.proto.GameRpc;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther: wumingjie
 * @Date: 2019/3/8
 * @Description:
 */
public class RpcClientResponseImpl implements StreamObserver<GameRpc.Response> {
    private final static Logger log = LoggerFactory.getLogger(RpcClientResponseImpl.class);

    @Override
    public void onNext(GameRpc.Response response) {
        long sessionId = response.getSessionId();

    }

    @Override
    public void onError(Throwable throwable) {
        log.error("grpc返回出错!", throwable);
    }

    @Override
    public void onCompleted() {
        log.info("grpc completed!");
    }
}
