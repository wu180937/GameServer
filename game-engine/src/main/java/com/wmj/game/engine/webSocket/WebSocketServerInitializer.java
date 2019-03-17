package com.wmj.game.engine.webSocket;

import com.wmj.game.engine.dispatcher.CmdDispatcher;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * @Auther: wumingjie
 * @Date: 2019/3/5
 * @Description:
 */
public class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {
    private static final String WEBSOCKET_PATH = "/websocket";
    private final SslContext sslCtx;
    private CmdDispatcher cmdDispatcher;

    public WebSocketServerInitializer(SslContext sslCtx, CmdDispatcher cmdDispatcher) {
        this.sslCtx = sslCtx;
        this.cmdDispatcher = cmdDispatcher;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(socketChannel.alloc()));
        }
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new WebSocketServerCompressionHandler());
        pipeline.addLast(new IdleStateHandler(10, 0, 0));
        pipeline.addLast(new WebSocketServerProtocolHandler(WEBSOCKET_PATH, null, true));
//        pipeline.addLast(new WebSocketIndexPageHandler(WEBSOCKET_PATH));
        pipeline.addLast(new WebSocketFrameHandler(this.cmdDispatcher));
    }
}
