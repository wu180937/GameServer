package com.wmj.game.engine.rpc.client;

import com.wmj.game.engine.rpc.proto.GameRpc;
import com.wmj.game.engine.rpc.proto.GameServiceGrpc;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: wumingjie
 * @Date: 2019/3/7
 * @Description:
 */
public class RpcClient {
    private GameServiceGrpc.GameServiceStub gameServiceStub;
    private String serviceId;
    private String host;
    private int port;
    private StreamObserver<GameRpc.Request> requestStream;
    private StreamObserver<GameRpc.Response> responseStream;

    public RpcClient(String serviceId, String host, int port) {
        this.serviceId = serviceId;
        this.host = host;
        this.port = port;
        this.gameServiceStub = GameServiceGrpc.newStub(ManagedChannelBuilder
                .forAddress(this.host, this.port).usePlaintext()
                .keepAliveTime(10, TimeUnit.SECONDS)
                .keepAliveTimeout(5, TimeUnit.SECONDS)
                .build());
        this.responseStream = new RpcClientResponseImpl();
        this.requestStream = this.gameServiceStub.handle(this.responseStream);
    }

    public String getServiceId() {
        return serviceId;
    }

    public void send(GameRpc.Request request) {
        this.requestStream.onNext(request);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RpcClient rpcClient = (RpcClient) o;
        return Objects.equals(serviceId, rpcClient.serviceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceId);
    }
}
