package com.wmj.game.engine;

import com.google.common.net.HostAndPort;
import com.orbitz.consul.Consul;
import com.wmj.game.common.service.ServiceName;
import com.wmj.game.common.service.ServiceType;
import com.wmj.game.engine.webSocket.WebSocketServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
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

public class Server {
    private final static Logger log = LoggerFactory.getLogger(Server.class);
    private ServiceName serviceName;
    private String consulHost;
    private int consulPort;

    public Server(ServiceName serviceName, String consulHost, int consulPort) {
        this.serviceName = serviceName;
        this.consulHost = consulHost;
        this.consulPort = consulPort;
    }

    public ServiceName getServiceName() {
        return serviceName;
    }

    public void startWebSocket(int port, boolean ssl) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
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
            log.info("websocket service started bind port : " + port);
            f.channel().closeFuture().sync();
        } catch (InterruptedException | CertificateException | SSLException e) {
            log.error(String.format("server[%s] error", this.serviceName.getName()), e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    private String generateServiceName(String serviceType) {
        return "<" + serviceType + ">" + this.serviceName.getName();
    }

    private void register2Consul(String serviceName, int servicePort) {
        Consul consul = Consul.builder().withPing(true).withHostAndPort(HostAndPort.fromParts(consulHost, consulPort)).build();
    }

    public static Server.Builder builder() {
        return new Server.Builder();
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

        public Server build() {
            if (serviceName == null) {
                throw new IllegalArgumentException("serviceName cannot null.");
            }
            if (StringUtils.isEmpty(consulHost)) {
                throw new IllegalArgumentException("consulHost cannot null or empty.");
            }
            if (consulPort < 1) {
                throw new IllegalArgumentException("consulPort is require.");
            }
            return new Server(serviceName, consulHost, consulPort);
        }
    }
}
