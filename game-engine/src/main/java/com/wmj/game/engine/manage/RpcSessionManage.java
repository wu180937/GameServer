package com.wmj.game.engine.manage;

import com.wmj.game.engine.rpc.proto.GameRpc;
import io.grpc.stub.StreamObserver;

import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @Auther: wumingjie
 * @Date: 2019/3/19
 * @Description:
 */
public class RpcSessionManage {
    private final ReadWriteLock readWriteLock;
    private final HashMap<Long, Session> sessionHashMap;

    public RpcSessionManage() {
        this.readWriteLock = new ReentrantReadWriteLock();
        this.sessionHashMap = new HashMap<>();
    }

    public Session getById(long sessionId) {
        Lock lock = this.readWriteLock.readLock();
        lock.lock();
        try {
            return this.sessionHashMap.get(sessionId);
        } finally {
            lock.unlock();
        }
    }

    public void add(long sessionId, StreamObserver<GameRpc.Response> responseObserver) {
        Lock lock = this.readWriteLock.writeLock();
        lock.lock();
        try {
            this.sessionHashMap.put(sessionId, new RpcSession(sessionId, responseObserver));
        } finally {
            lock.unlock();
        }
    }

    public void remove(long sessionId) {
        Lock lock = this.readWriteLock.writeLock();
        lock.lock();
        try {
            this.sessionHashMap.remove(sessionId);
        } finally {
            lock.unlock();
        }
    }
}
