package com.wmj.game.engine.rpc.server;

import com.wmj.game.common.constant.ErrorCode;
import com.wmj.game.common.util.VarintUtil;
import com.wmj.game.engine.dispatcher.RpcServerCmdDispatcher;
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
    private final String serviceName;
    private final RpcSessionManage rpcSessionManage;
    private final Set<Long> sessionIdSet = new HashSet<>();
    private final RpcServerCmdDispatcher rpcServerCmdDispatcher;

    public RpcGameServiceImpl(String serviceName, RpcSessionManage rpcSessionManage) {
        this.serviceName = serviceName;
        this.rpcSessionManage = rpcSessionManage;
        this.rpcServerCmdDispatcher = new RpcServerCmdDispatcher();
    }

    @Override
    public StreamObserver<GameRpc.Request> handle(StreamObserver<GameRpc.Response> responseObserver) {
        return new StreamObserver<GameRpc.Request>() {
            @Override
            public void onNext(GameRpc.Request request) {
                long sessionId = request.getSessionId();
                log.info(this.hashCode() + " " + sessionId);
                switch (request.getBehavior()) {
                    case Logout: {
                        if (!sessionIdSet.contains(sessionId)) {
                            log.warn("sessionId[" + sessionId + "] already logout.");
                            return;
                        }
                        sessionIdSet.remove(sessionId);
                        rpcSessionManage.remove(sessionId);
                        break;
                    }
                    case Handle: {
                        synchronized (sessionIdSet) {
                            if (!sessionIdSet.contains(sessionId)) {
                                sessionIdSet.add(sessionId);
                                rpcSessionManage.add(sessionId, responseObserver);
                            }
                        }
                        Session session = rpcSessionManage.getById(sessionId);
                        if (session == null) {
                            return;
                        }
                        byte[] dataBytes = request.toByteArray();
                        int cmd = VarintUtil.rawVarint32(dataBytes, 1);
                        rpcServerCmdDispatcher.dispatcher(session, cmd, dataBytes);
                        break;
                    }
                    default:
                }
            }

            @Override
            public void onError(Throwable throwable) {
                rpcSessionManage.removeAll(sessionIdSet);
                log.error("调用出错:", throwable);
            }

            @Override
            public void onCompleted() {
                rpcSessionManage.removeAll(sessionIdSet);
            }
        };
    }

    public String getServiceName() {
        return serviceName;
    }
}
