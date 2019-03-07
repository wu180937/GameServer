package com.wmj.game.engine.rpc;

import com.wmj.game.engine.rpc.proto.GameRpc;
import com.wmj.game.engine.rpc.proto.GameServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther: wumingjie
 * @Date: 2019/3/7
 * @Description:
 */
public class RpcGameServiceImpl extends GameServiceGrpc.GameServiceImplBase {
    private final static Logger log = LoggerFactory.getLogger(RpcGameServiceImpl.class);

    @Override
    public StreamObserver<GameRpc.Request> handle(StreamObserver<GameRpc.Response> responseObserver) {
        return new StreamObserver<GameRpc.Request>() {
            @Override
            public void onNext(GameRpc.Request request) {
                log.info("sessionId : " + request.getSessionId());
                GameRpc.Response resp = GameRpc.Response.newBuilder().setSessionId(2).build();
                responseObserver.onNext(resp);
            }

            @Override
            public void onError(Throwable throwable) {
                log.warn("调用出错:{}", throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
}
