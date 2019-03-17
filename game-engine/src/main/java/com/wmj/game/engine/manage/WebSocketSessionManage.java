package com.wmj.game.engine.manage;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @Author: wumj
 * @Date: 2019/3/16 16:51
 * @Description:
 */
public class WebSocketSessionManage {
    private final static AtomicLong SESSION_ID_COUNTER = new AtomicLong();
    private final ReadWriteLock readWriteLock;
    private final HashMap<Long, Session> sessionHashMap;
    private final HashMap<String, Long> channelId2SessionIdMap;

    public WebSocketSessionManage() {
        this.readWriteLock = new ReentrantReadWriteLock();
        this.sessionHashMap = new HashMap<>();
        this.channelId2SessionIdMap = new HashMap<>();
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

    public Session getByChannel(Channel channel) {
        Lock lock = this.readWriteLock.readLock();
        lock.lock();
        try {
            Long sessionId = this.channelId2SessionIdMap.get(channel.id().asLongText());
            if (sessionId == null) {
                return null;
            }
            return this.sessionHashMap.get(sessionId);
        } finally {
            lock.unlock();
        }
    }

    public void add(Channel channel) {
        Lock lock = this.readWriteLock.writeLock();
        lock.lock();
        try {

        } finally {
            lock.unlock();
        }
    }

    public void remove(Channel channel) {
        Lock lock = this.readWriteLock.writeLock();
        lock.lock();
        try {

        } finally {
            lock.unlock();
        }
    }

}
