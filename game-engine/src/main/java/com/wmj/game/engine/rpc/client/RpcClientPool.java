package com.wmj.game.engine.rpc.client;

import com.orbitz.consul.Consul;
import com.orbitz.consul.model.health.Service;
import com.orbitz.consul.model.health.ServiceHealth;
import com.wmj.game.common.service.ServiceName;
import com.wmj.game.common.service.ServiceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class RpcClientPool {
    private final static Logger log = LoggerFactory.getLogger(RpcClientPool.class);
    private final static int refreshTimeSec = 10;
    private final String poolName;
    private final AtomicInteger loadBalancingCounter;
    private final List<RpcClient> rpcClients;
    private final List<ServiceInfo> serviceInfos;
    private final ReentrantReadWriteLock readWriteLock;
    private Future<?> refreshFuture = null;
    private boolean destroy = false;

    private RpcClientPool(String poolName) {
        this.poolName = poolName;
        this.readWriteLock = new ReentrantReadWriteLock();
        this.loadBalancingCounter = new AtomicInteger(0);
        this.rpcClients = new ArrayList<>();
        this.serviceInfos = new ArrayList<>();
    }

    public static RpcClientPool create(ServiceName serviceName, Consul consulClient, ScheduledExecutorService executorService) {
        RpcClientPool pool = new RpcClientPool(serviceName.getName() + "-pool");
        pool.refreshFuture = executorService.scheduleWithFixedDelay(() -> {
                    List<ServiceHealth> serviceHealths = consulClient.healthClient()
                            .getHealthyServiceInstances(ServiceType.Rpc.generateServiceName(serviceName)).getResponse();
                    pool.refresh(serviceHealths);
                }
                , 0, refreshTimeSec, TimeUnit.SECONDS);
        return pool;
    }

    private void refresh(List<ServiceHealth> serviceHealths) {
        if (destroy) {
            return;
        }
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
            Set<String> hasServiceSet = serviceInfos.stream().map(ServiceInfo::getServiceId).collect(Collectors.toSet());

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
            rpcClients.addAll(clients);
        } finally {
            this.readWriteLock.writeLock().unlock();
        }
        log.info("RpcClientPool[" + this.poolName + "] add clients : " + clients);
    }


    public RpcClient getRpcClient() {
        if (this.destroy) {
            return null;
        }
        this.readWriteLock.readLock().lock();
        try {
            return this.rpcClients.get(Math.abs(this.loadBalancingCounter.getAndIncrement() % this.rpcClients.size()));
        } finally {
            this.readWriteLock.readLock().unlock();
        }
    }

    public void destroy() {
        this.destroy = true;
        this.refreshFuture.cancel(false);
        this.rpcClients.stream().forEach(RpcClient::close);
    }

    private static class ServiceInfo {
        private String serviceId;
        private String host;
        private int port;

        public ServiceInfo(String serviceId, String host, int port) {
            this.serviceId = serviceId;
            this.host = host;
            this.port = port;
        }

        public String getServiceId() {
            return serviceId;
        }

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }
    }
}
