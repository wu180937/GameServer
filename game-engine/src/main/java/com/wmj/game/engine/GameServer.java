package com.wmj.game.engine;

import com.google.common.net.HostAndPort;
import com.orbitz.consul.AgentClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.NotRegisteredException;
import com.orbitz.consul.model.agent.ImmutableRegistration;
import com.orbitz.consul.model.agent.Registration;
import com.wmj.game.common.service.ServiceName;
import com.wmj.game.common.service.ServiceType;
import com.wmj.game.engine.webSocket.WebSocketServerInitializer;
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
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameServer {
    private final ScheduledExecutorService consulHealthExecutor = Executors.newSingleThreadScheduledExecutor();
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

    public void startWebSocket(int port, boolean ssl) {
        new Thread(new WebSocketServer(port, ssl)).start();
    }

    private String generateServiceName(String serviceType) {
        return "<" + serviceType + ">" + this.serviceName.getName();
    }

    private Consul register2Consul(String serviceName, int servicePort) {
        Consul client = Consul.builder().withPing(true).withHostAndPort(HostAndPort.fromParts(consulHost, consulPort)).build();
        AgentClient agentClient = client.agentClient();
        String serviceId = serviceName + ":" + servicePort;
        Registration service = ImmutableRegistration.builder()
                .id(serviceId)
                .name(serviceName)
                .port(servicePort)
                .check(Registration.RegCheck.ttl(3L)) // registers with a TTL of 3 seconds
                .tags(Collections.emptyList())
                .meta(Collections.emptyMap())
                .build();
        agentClient.register(service);
        consulHealthExecutor.scheduleAtFixedRate(() -> {
            try {
                agentClient.pass(serviceId);
            } catch (NotRegisteredException e) {
                log.error("出错了", e);
            }
        }, 0, 1, TimeUnit.SECONDS);
        return client;
    }

    public static GameServer.Builder builder() {
        return new GameServer.Builder();
    }

    private class WebSocketServer implements Runnable {
        private int port;
        private boolean ssl;

        public WebSocketServer(int port, boolean ssl) {
            this.port = port;
            this.ssl = ssl;
        }

        @Override
        public void run() {
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                final SslContext sslCtx;
                if (ssl) {
                    SelfSignedCertificate ssc = new SelfSignedCertificate();
                    sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
                } else {
                    sslCtx = null;
                }
                ServerBootstrap b = new ServerBootstrap();
                b.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .handler(new LoggingHandler(LogLevel.INFO))
                        .childHandler(new WebSocketServerInitializer(sslCtx))
                        .option(ChannelOption.SO_BACKLOG, 128)
                        .childOption(ChannelOption.SO_KEEPALIVE, true);
                ChannelFuture f = b.bind(port).sync();
                register2Consul(generateServiceName(ServiceType.WebSocket), port);
                log.info("webSocket service started bind port : " + port);
                f.channel().closeFuture().sync();
            } catch (InterruptedException | CertificateException | SSLException e) {
                log.error(String.format("server[%s] error", serviceName.getName()), e);
            } finally {
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            }
        }
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
