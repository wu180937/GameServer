package com.wmj.game.engine.rpc.server;

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
                long sessionId = request.getSessionId();
                log.info("" + sessionId);
//                responseObserver.onNext(GameRpc.Response.newBuilder().setSessionId(sessionId).build());
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
