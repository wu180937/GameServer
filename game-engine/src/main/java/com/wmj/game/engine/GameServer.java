package com.wmj.game.engine;

import com.google.common.net.HostAndPort;
import com.orbitz.consul.AgentClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.NotRegisteredException;
import com.orbitz.consul.model.agent.ImmutableRegCheck;
import com.orbitz.consul.model.agent.ImmutableRegistration;
import com.orbitz.consul.model.agent.Registration;
import com.wmj.game.common.service.ServiceName;
import com.wmj.game.common.service.ServiceType;
import com.wmj.game.engine.dispatcher.GatewayCmdDispatcher;
import com.wmj.game.engine.rpc.client.RpcClientPool;
import com.wmj.game.engine.rpc.server.RpcServer;
import com.wmj.game.engine.rpc.server.RpcServerParam;
import com.wmj.game.engine.webSocket.WebSocketServer;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

public class GameServer {
    private final static GameServer INSTANCE = new GameServer();
    private final static Logger log = LoggerFactory.getLogger(GameServer.class);
    private final static ScheduledExecutorService consulHealthExecutor = Executors.newSingleThreadScheduledExecutor(new DefaultThreadFactory("consulHealthExecutor"));
    private boolean init;
    private ServiceName serviceName;
    private HostAndPort consulHostAndPort;
    private Consul consulClient;
    private final ConcurrentHashMap<ServiceName, RpcClientPool> serviceRpcClientMap = new ConcurrentHashMap<>();

    public static GameServer getInstance() {
        return INSTANCE;
    }

    private GameServer() {
    }

    public synchronized void start(ServiceName serviceName, HostAndPort consulHostAndPort) {
        if (init) {
            throw new RuntimeException("GameServer Initialized.");
        }
        this.init = true;
        this.consulHostAndPort = consulHostAndPort;
        this.serviceName = serviceName;
        this.consulClient = newConsulClient();
    }

    public boolean isInit() {
        return init;
    }

    public ServiceName getServiceName() {
        return serviceName;
    }

    public void startGatewayServer(String host, int port, boolean ssl) {
        new Thread(new WebSocketServer(this.getServiceName().getName(), host, port, ssl, new GatewayCmdDispatcher())).start();
        register2Consul(ServiceType.WebSocket.generateServiceName(this.getServiceName()), host, port);
    }

    public void startRpcServer(RpcServerParam... rpcServerParams) {
        if (rpcServerParams == null || rpcServerParams.length == 0) {
            throw new IllegalArgumentException("start startRpcServer rpcServerParams can not null or empty.");
        }
        Arrays.stream(rpcServerParams).forEach(rpcServerParam -> {
            new Thread(new RpcServer(rpcServerParam.getServiceName().getName(), rpcServerParam.getHost(), rpcServerParam.getPort())).start();
            register2Consul(ServiceType.Rpc.generateServiceName(rpcServerParam.getServiceName()), rpcServerParam.getHost(), rpcServerParam.getPort());
        });
    }

    public void startRpcClient(ServiceName... serviceNames) {
        if (serviceNames == null || serviceNames.length == 0) {
            throw new IllegalArgumentException("start RpcClient serviceNames can not null or empty.");
        }
        Arrays.stream(serviceNames).forEach(sn -> {
            this.serviceRpcClientMap.putIfAbsent(sn, RpcClientPool.create(sn, consulClient, consulHealthExecutor));
        });
    }

    private Consul newConsulClient() {
        Consul client = Consul.builder().withPing(true).withHostAndPort(this.consulHostAndPort).build();
        return client;
    }

    private Consul register2Consul(String serviceName, String serviceHost, int servicePort) {
        Consul client = this.consulClient;
        AgentClient agentClient = client.agentClient();
        String serviceId = serviceHost + ":" + servicePort + "_" + serviceName;
        Registration service = ImmutableRegistration.builder()
                .id(serviceId).name(serviceName).address(serviceHost).port(servicePort)
                .check(ImmutableRegCheck.builder().ttl("3s").timeout("1s").deregisterCriticalServiceAfter("10s").build())
                .tags(Collections.singletonList(serviceName)).meta(Collections.emptyMap()).build();
        agentClient.register(service);
        consulHealthExecutor.scheduleAtFixedRate(() -> {
            try {
                agentClient.pass(serviceId);
            } catch (NotRegisteredException e) {
                log.error("出错了!", e);
            }
        }, 1, 1, TimeUnit.SECONDS);
        return client;
    }

    public RpcClientPool getRpcClientPool(ServiceName serviceName) {
        return this.serviceRpcClientMap.get(serviceName);
    }

}
