package com.wmj.game.engine;

import com.google.common.net.HostAndPort;
import com.orbitz.consul.AgentClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.NotRegisteredException;
import com.orbitz.consul.model.agent.ImmutableRegCheck;
import com.orbitz.consul.model.agent.ImmutableRegistration;
import com.orbitz.consul.model.agent.Registration;
import com.wmj.game.common.rpc.RpcServiceName;
import com.wmj.game.common.service.ServiceName;
import com.wmj.game.common.service.ServiceType;
import com.wmj.game.engine.webSocket.WebSocketServer;
import com.wmj.game.engine.webSocket.WebSocketServerInitializer;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerServiceDefinition;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameServer {
    private final static Logger log = LoggerFactory.getLogger(GameServer.class);
    private ServiceName serviceName;
    private String consulHost;
    private int consulPort;

    public GameServer(ServiceName serviceName, String consulHost, int consulPort) {
        this.serviceName = serviceName;
        this.consulHost = consulHost;
        this.consulPort = consulPort;
    }

    public ServiceName getServiceName() {
        return serviceName;
    }

    public void startWebSocketServer(String host, int port, boolean ssl) {
        new Thread(new WebSocketServer(serviceName.getName(), host, port, ssl)).start();
        register2Consul(generateServiceName(ServiceType.WebSocket), host, port);
    }

    public void startRpcServer(String host, int port) {
        register2Consul(generateServiceName(ServiceType.Rpc), host, port);
    }

    private String generateServiceName(String serviceType) {
        return serviceType + "-" + this.serviceName.getName();
    }

    private Consul register2Consul(String serviceName, String serviceHost, int servicePort) {
        Consul client = Consul.builder().withPing(true).withHostAndPort(HostAndPort.fromParts(consulHost, consulPort)).build();
        AgentClient agentClient = client.agentClient();
        String serviceId = serviceHost + ":" + servicePort + "_" + serviceName;
        Registration service = ImmutableRegistration.builder()
                .id(serviceId)
                .name(serviceName)
                .address(serviceHost)
                .port(servicePort)
                .check(ImmutableRegCheck.builder().tcp(serviceHost + ":" + servicePort)
                        .interval("3s").timeout("1s").deregisterCriticalServiceAfter("10s").build())
                .tags(Collections.singletonList(serviceName))
                .meta(Collections.emptyMap())
                .build();
        agentClient.register(service);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> client.destroy()));
        return client;
    }

    public static GameServer.Builder builder() {
        return new GameServer.Builder();
    }

    public static class Builder {
        private ServiceName serviceName;
        private String consulHost;
        private int consulPort;

        public Builder setServiceName(ServiceName serviceName) {
            this.serviceName = serviceName;
            return this;
        }

        public Builder setConsulHost(String consulHost) {
            this.consulHost = consulHost;
            return this;
        }

        public Builder setConsulPort(int consulPort) {
            this.consulPort = consulPort;
            return this;
        }

        public GameServer build() {
            if (serviceName == null) {
                throw new IllegalArgumentException("serviceName cannot null.");
            }
            if (StringUtils.isEmpty(consulHost)) {
                throw new IllegalArgumentException("consulHost cannot null or empty.");
            }
            if (consulPort < 1) {
                throw new IllegalArgumentException("consulPort is require.");
            }
            return new GameServer(serviceName, consulHost, consulPort);
        }
    }
}
