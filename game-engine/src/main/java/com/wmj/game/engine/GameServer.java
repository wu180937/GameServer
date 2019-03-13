package com.wmj.game.engine;

import com.google.common.net.HostAndPort;
import com.orbitz.consul.AgentClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.NotRegisteredException;
import com.orbitz.consul.model.agent.ImmutableRegCheck;
import com.orbitz.consul.model.agent.ImmutableRegistration;
import com.orbitz.consul.model.agent.Registration;
import com.orbitz.consul.model.health.ServiceHealth;
import com.wmj.game.common.service.ServiceName;
import com.wmj.game.common.service.ServiceType;
import com.wmj.game.engine.dispatcher.CmdDispatcher;
import com.wmj.game.engine.rpc.client.RpcClient;
import com.wmj.game.engine.rpc.client.RpcClientPool;
import com.wmj.game.engine.rpc.server.RpcServer;
import com.wmj.game.engine.webSocket.WebSocketServer;
import io.netty.util.HashingStrategy;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.apache.commons.lang3.StringUtils;
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
    private String consulHost;
    private int consulPort;
    private Consul consulClient;
    private ServiceName[] rpcClientServiceNames = {};
    private final ConcurrentHashMap<ServiceName, RpcClientPool> serviceRpcClientMap = new ConcurrentHashMap<>();

    public static GameServer getInstance() {
        return INSTANCE;
    }

    private GameServer() {
    }

    public synchronized void init(ServiceName serviceName, String consulHost, int consulPort) {
        if (init) {
            throw new RuntimeException("GameServer Initialized.");
        }
        this.init = true;
        this.serviceName = serviceName;
        this.consulHost = consulHost;
        this.consulPort = consulPort;
        this.consulClient = newConsulClient();
        CmdDispatcher.getInstance().init();//初始化指令分派器
    }

    public boolean isInit() {
        return init;
    }

    public ServiceName getServiceName() {
        return serviceName;
    }

    public void startWebSocketServer(String host, int port, boolean ssl) {
        new Thread(new WebSocketServer(this.getServiceName().getName(), host, port, ssl)).start();
        register2Consul(ServiceType.WebSocket.generateServiceName(this.getServiceName()), host, port);
    }

    public void startRpcServer(String host, int port) {
        new Thread(new RpcServer(this.getServiceName().getName(), host, port)).start();
        register2Consul(ServiceType.Rpc.generateServiceName(this.getServiceName()), host, port);
    }

    public void startRpcClient(ServiceName... serviceNames) {
        if (serviceNames == null || serviceNames.length == 0) {
            throw new IllegalArgumentException("start RpcClient serviceNames can not null or empty.");
        }
        this.rpcClientServiceNames = serviceNames;
        consulHealthExecutor.scheduleWithFixedDelay(() -> {
            Arrays.stream(this.rpcClientServiceNames).forEach(serviceName -> {
                RpcClientPool rpcClientPool = this.serviceRpcClientMap.putIfAbsent(serviceName, new RpcClientPool(serviceName.getName() + "-pool"));
                List<ServiceHealth> serviceHealths = this.consulClient.healthClient()
                        .getHealthyServiceInstances(ServiceType.Rpc.generateServiceName(serviceName)).getResponse();
                consulHealthExecutor.scheduleWithFixedDelay(() -> rpcClientPool.refresh(serviceHealths), 0, 5, TimeUnit.SECONDS);
            });
        }, 0, 10, TimeUnit.SECONDS);
    }

    private Consul newConsulClient() {
        Consul client = Consul.builder().withPing(true).withHostAndPort(HostAndPort.fromParts(consulHost, consulPort)).build();
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

}
