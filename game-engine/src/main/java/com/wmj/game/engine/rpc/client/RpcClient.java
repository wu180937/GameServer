package com.wmj.game.engine.rpc.client;

import com.google.protobuf.ByteString;
import com.wmj.game.engine.rpc.proto.GameRpc;
import com.wmj.game.engine.rpc.proto.GameServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: wumingjie
 * @Date: 2019/3/7
 * @Description:
 */
public class RpcClient {
    private final static Logger log = LoggerFactory.getLogger(RpcClient.class);
    private GameServiceGrpc.GameServiceStub gameServiceStub;
    private String serviceId;
    private String host;
    private int port;
    private ManagedChannel managedChannel;
    private StreamObserver<GameRpc.Request> requestStream;
    private StreamObserver<GameRpc.Response> responseStream;
    private boolean active = true;
    protected ConcurrentHashMap<Long, Runnable> closeRunnableMap = new ConcurrentHashMap<>();

    public RpcClient(String serviceId, String host, int port) {
        this.serviceId = serviceId;
        this.host = host;
        this.port = port;
        this.managedChannel = ManagedChannelBuilder
                .forAddress(this.host, this.port).usePlaintext()
                .keepAliveTime(10, TimeUnit.SECONDS)
                .keepAliveTimeout(5, TimeUnit.SECONDS)
                .enableRetry()
                .build();
        this.gameServiceStub = GameServiceGrpc.newStub(managedChannel);
        this.responseStream = new RpcClientResponseImpl();
        this.requestStream = this.gameServiceStub.handle(this.responseStream);
    }

    public String getServiceId() {
        return serviceId;
    }

    public void logout(long sessionId) {
        if (!isActive()) {
            log.debug("RpcClient[" + this.getServiceId() + "] is not active.");
            return;
        }
        GameRpc.Request req = GameRpc.Request.newBuilder().setSessionId(sessionId).setBehavior(GameRpc.Behavior.Logout).build();
        this.requestStream.onNext(req);
        removeCloseHook(sessionId);
    }

    public void send(long sessionId, byte[] data) {
        if (!isActive()) {
            log.debug("RpcClient[" + this.getServiceId() + "] is not active.");
            return;
        }
        GameRpc.Request req = GameRpc.Request.newBuilder().setSessionId(sessionId).setBehavior(GameRpc.Behavior.Handle).setData(ByteString.copyFrom(data)).build();
        this.requestStream.onNext(req);
    }


    public synchronized void close() {
        if (!isActive()) {
            return;
        }
        active = false;
        this.responseStream.onCompleted();
        this.closeRunnableMap.values().stream().forEach(Runnable::run);
        this.closeRunnableMap.clear();
    }

    public void addCloseHook(long sessionId, Runnable runnable) {
        this.closeRunnableMap.put(sessionId, runnable);
    }

    public void removeCloseHook(long sessionId) {
        this.closeRunnableMap.remove(sessionId);
    }

    @Override
    public String toString() {
        return "RpcClient{" +
                "serviceId='" + serviceId + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                '}';
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

    public boolean isActive() {
        return active;
    }

    private class RpcClientResponseImpl implements StreamObserver<GameRpc.Response> {

        @Override
        public void onNext(GameRpc.Response response) {

        }

        @Override
        public void onError(Throwable throwable) {
            close();
        }

        @Override
        public void onCompleted() {
            close();
        }
    }
}
