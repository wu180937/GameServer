package com.wmj.game.engine.rpc.client;

import com.orbitz.consul.model.health.Service;
import com.orbitz.consul.model.health.ServiceHealth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class RpcClientPool {
    private final static Logger log = LoggerFactory.getLogger(RpcClientPool.class);
    private final String poolName;
    private final AtomicInteger loadBalancingCounter;
    private final List<RpcClient> rpcClients;
    private final ReentrantReadWriteLock readWriteLock;

    public RpcClientPool(String poolName) {
        this.poolName = poolName;
        this.readWriteLock = new ReentrantReadWriteLock();
        this.loadBalancingCounter = new AtomicInteger(0);
        this.rpcClients = new ArrayList<>();
    }

    public void refresh(List<ServiceHealth> serviceHealths) {
        List<RpcClient> addList = new ArrayList<>();
        List<RpcClient> removeList = new ArrayList<>();
        this.readWriteLock.readLock().lock();
        try {
            Set<String> css = rpcClients.stream().map(RpcClient::getServiceId).collect(Collectors.toSet());
            addList.addAll(serviceHealths.stream().filter(serviceHealth -> !css.contains(serviceHealth.getService().getId()))
                    .map(serviceHealth -> new RpcClient(serviceHealth.getService().getId(), serviceHealth.getService().getAddress(),
                            serviceHealth.getService().getPort())).collect(Collectors.toList()));
            Set<String> tss = serviceHealths.stream().map(ServiceHealth::getService).map(Service::getId).collect(Collectors.toSet());
            removeList.addAll(rpcClients.stream().filter(client -> !tss.contains(client.getServiceId())).collect(Collectors.toList()));
        } finally {
            this.readWriteLock.readLock().unlock();
        }
        removeRpcClients(removeList);
        addRpcClients(addList);
    }

    private void removeRpcClients(List<RpcClient> clients) {
        if (clients.isEmpty()) {
            return;
        }
        this.readWriteLock.writeLock().lock();
        try {
            rpcClients.removeAll(clients);
        } finally {
            this.readWriteLock.writeLock().unlock();
        }
        log.info("RpcClientPool[" + this.poolName + "] remove clients : " + clients);
    }

    private void addRpcClients(List<RpcClient> clients) {
        if (clients.isEmpty()) {
            return;
        }
        this.readWriteLock.writeLock().lock();
        try {
            System.err.println("10");
            rpcClients.addAll(clients);
        } finally {
            this.readWriteLock.writeLock().unlock();
        }
        log.info("RpcClientPool[" + this.poolName + "] add clients : " + clients);
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
