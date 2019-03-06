package com.wmj.game.engine.webSocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

/**
 * @Auther: wumingjie
 * @Date: 2019/3/5
 * @Description:
 */
public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    private final static Logger log = LoggerFactory.getLogger(WebSocketFrameHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame webSocketFrame) {
        if (webSocketFrame instanceof TextWebSocketFrame) {
            log.info("TextWebSocketFrame");
            String request = ((TextWebSocketFrame) webSocketFrame).text();
            ctx.channel().writeAndFlush(new TextWebSocketFrame(request.toUpperCase(Locale.US)));
        } else if (webSocketFrame instanceof BinaryWebSocketFrame) {
            log.info("BinaryWebSocketFrame");
        } else {
            log.warn("unsupported frame type : " + webSocketFrame.getClass().getName());
        }
    }
}
