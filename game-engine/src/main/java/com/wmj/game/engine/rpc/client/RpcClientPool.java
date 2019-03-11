package com.wmj.game.engine.rpc.client;

import com.orbitz.consul.model.health.ServiceHealth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RpcClientPool {
    private final AtomicInteger loadBalancingCounter;
    private final HashMap<String, Integer> serviceIdMap;
    private final ArrayList<RpcClient> rpcClients;
    private final ReadWriteLock readWriteLock;

    public RpcClientPool() {
        this.readWriteLock = new ReentrantReadWriteLock();
        this.serviceIdMap = new HashMap<>();
        this.loadBalancingCounter = new AtomicInteger(0);
        this.rpcClients = new ArrayList<>();
    }

    public void refresh(List<ServiceHealth> serviceHealths) {

    }

    private void addRpcClient(RpcClient client) {
        this.readWriteLock.writeLock().lock();
        try {

        } finally {
            this.readWriteLock.writeLock().unlock();
        }
    }

    public RpcClient getRpcClient() {
        this.readWriteLock.readLock().lock();
        try {
            return this.rpcClients.get(Math.abs(this.loadBalancingCounter.getAndIncrement() % this.rpcClients.size()));
        } finally {
            this.readWriteLock.readLock().unlock();
        }
    }

}
