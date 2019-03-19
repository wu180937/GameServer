package com.wmj.game.engine.rpc.server;

import com.wmj.game.engine.manage.RpcSessionManage;
import com.wmj.game.engine.manage.Session;
import com.wmj.game.engine.rpc.proto.GameRpc;
import com.wmj.game.engine.rpc.proto.GameServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Auther: wumingjie
 * @Date: 2019/3/7
 * @Description:
 */
public class RpcGameServiceImpl extends GameServiceGrpc.GameServiceImplBase {
    private final static Logger log = LoggerFactory.getLogger(RpcGameServiceImpl.class);
    private String serviceName;
    private final RpcSessionManage rpcSessionManage;
    private final Set<Long> sessionIdSet = new HashSet<>();
    private final ReentrantLock lock = new ReentrantLock();

    public RpcGameServiceImpl(String serviceName, RpcSessionManage rpcSessionManage) {
        this.serviceName = serviceName;
        this.rpcSessionManage = rpcSessionManage;
    }

    @Override
    public StreamObserver<GameRpc.Request> handle(StreamObserver<GameRpc.Response> responseObserver) {
        return new StreamObserver<GameRpc.Request>() {
            @Override
            public void onNext(GameRpc.Request request) {
                long sessionId = request.getSessionId();
                lock.lock();
                try {
                    if (!sessionIdSet.contains(sessionId)) {

                    }
                } finally {
                    lock.unlock();
                }
                log.info(this.hashCode() + " " + sessionId);
                Session session = rpcSessionManage.getById(sessionId);
//                responseObserver.onNext(GameRpc.Response.newBuilder().setSessionId(sessionId).build());
            }

            @Override
            public void onError(Throwable throwable) {
                log.warn("调用出错:{}", throwable.getMessage());
                throwable.printStackTrace();
            }

            @Override
            public void onCompleted() {
            }
        };
    }
}
