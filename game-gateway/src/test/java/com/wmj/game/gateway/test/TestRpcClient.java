package com.wmj.game.gateway.test;

import com.wmj.game.engine.rpc.proto.GameRpc;
import com.wmj.game.engine.rpc.proto.GameServiceGrpc;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.sql.Time;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: wumingjie
 * @Date: 2019/3/7
 * @Description:
 */
public class TestRpcClient {
    public static void main(String[] args) throws InterruptedException {
        new GameRpcClient().handle();
    }

    public static class GameRpcClient {
        private GameServiceGrpc.GameServiceStub gameServiceStub;
        final CountDownLatch latch = new CountDownLatch(1);

        public GameRpcClient() {
            this.gameServiceStub = GameServiceGrpc.newStub(ManagedChannelBuilder
                    .forAddress("192.168.1.66", 10087).usePlaintext()
                    .keepAliveTime(10, TimeUnit.SECONDS)
                    .keepAliveTimeout(5, TimeUnit.SECONDS)
                    .build());
        }

        public void handle() throws InterruptedException {
            StreamObserver<GameRpc.Response> resp = new StreamObserver<GameRpc.Response>() {
                @Override
                public void onNext(GameRpc.Response response) {
                    System.err.println("返回sessionId : " + response.getSessionId());
                    latch.countDown();
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
            StreamObserver<GameRpc.Request> request = this.gameServiceStub.handle(resp);
            for (int i = 0; i < 10000; i++) {
                request.onNext(GameRpc.Request.newBuilder().setSessionId(i).build());
            }
            latch.await();
        }
    }
}
