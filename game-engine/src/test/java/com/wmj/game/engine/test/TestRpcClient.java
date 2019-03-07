package com.wmj.game.engine.test;

import com.wmj.game.engine.rpc.proto.GameRpc;
import com.wmj.game.engine.rpc.proto.GameServiceGrpc;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

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

        public GameRpcClient() {
            this.gameServiceStub = GameServiceGrpc.newStub(ManagedChannelBuilder.forAddress("192.168.1.66", 10087).usePlaintext().build());
        }

        public void handle() {
            StreamObserver<GameRpc.Response> resp = new StreamObserver<GameRpc.Response>() {
                @Override
                public void onNext(GameRpc.Response response) {
                    System.err.println("返回sessionId : " + response.getSessionId());
                }

                @Override
                public void onError(Throwable throwable) {
                    System.err.println("报错");
                }

                @Override
                public void onCompleted() {
                    System.err.println("完成");
                }
            };
            StreamObserver<GameRpc.Request> request = this.gameServiceStub.handle(resp);
            request.onNext(GameRpc.Request.newBuilder().setSessionId(1l).build());
        }
    }
}
