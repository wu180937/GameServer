package com.wmj.game.engine.webSocket;

import com.wmj.game.engine.dispatcher.CmdDispatcher;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;

public class WebSocketServer implements Runnable {
    private final static Logger log = LoggerFactory.getLogger(WebSocketServer.class);
    private String host;
    private int port;
    private boolean ssl;
    private String serviceName;
    private CmdDispatcher cmdDispatcher;

    public WebSocketServer(String serviceName, String host, int port, boolean ssl, CmdDispatcher cmdDispatcher) {
        this.serviceName = serviceName;
        this.host = host;
        this.port = port;
        this.ssl = ssl;
        this.cmdDispatcher = cmdDispatcher;
    }

    @Override
    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
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
                    .childHandler(new WebSocketServerInitializer(sslCtx, this.cmdDispatcher))
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture f = b.bind(host, port).sync();
            log.info("server [" + serviceName + "] webSocket service started bind port : " + port);
            f.channel().closeFuture().sync();
        } catch (InterruptedException | CertificateException | SSLException e) {
            log.error(String.format("server[%s] error", serviceName), e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
