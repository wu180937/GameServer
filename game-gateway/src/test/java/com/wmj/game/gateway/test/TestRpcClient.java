package com.wmj.game.gateway.test;

import com.wmj.game.engine.rpc.proto.GameRpc;
import com.wmj.game.engine.rpc.proto.GameServiceGrpc;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.sql.Time;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: wumingjie
 * @Date: 2019/3/7
 * @Description:
 */
public class TestRpcClient {
    public static void main(String[] args) throws InterruptedException {
        GameServiceGrpc.GameServiceStub gameServiceStub = GameServiceGrpc.newStub(ManagedChannelBuilder
                .forAddress("127.0.0.1", 10001).usePlaintext()
                .keepAliveTime(10, TimeUnit.SECONDS)
                .keepAliveTimeout(5, TimeUnit.SECONDS)
                .build());
        StreamObserver<GameRpc.Response> resp = new StreamObserver<GameRpc.Response>() {
            @Override
            public void onNext(GameRpc.Response response) {
                System.err.println("返回sessionId : " + response.getSessionId());
            }

            @Override
            public void onError(Throwable throwable) {
                System.err.println("报错");
                throwable.printStackTrace();
            }

            @Override
            public void onCompleted() {
                System.err.println("完成");
            }
        };
        StreamObserver<GameRpc.Request> request = gameServiceStub.handle(resp);
        final CountDownLatch latch = new CountDownLatch(1);
        new GameRpcClient(request).handle(1);
        new GameRpcClient(request).handle(2);
        new GameRpcClient(request).handle(3);
        latch.await();
    }

    public static class GameRpcClient {
        private StreamObserver<GameRpc.Request> request;

        public GameRpcClient(StreamObserver<GameRpc.Request> request) {
            this.request = request;
        }

        public void handle(long id) throws InterruptedException {
            Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(() -> {
                request.onNext(GameRpc.Request.newBuilder().setSessionId(id).build());
            }, 0, 5, TimeUnit.SECONDS);
        }
    }
}
