package com.wmj.game.engine.rpc.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RpcClientPool {
    private final AtomicLong loadBalancingCounter;
    private final HashMap<String, Integer> serviceIdMap;
    private final List<RpcClient> rpcClients;
    private final ReadWriteLock readWriteLock;

    public RpcClientPool() {
        this.readWriteLock = new ReentrantReadWriteLock();
        this.serviceIdMap = new HashMap<>();
        this.loadBalancingCounter = new AtomicLong(0);
        this.rpcClients = new ArrayList<>();
    }

}
