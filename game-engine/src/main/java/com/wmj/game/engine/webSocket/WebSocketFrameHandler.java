package com.wmj.game.engine.webSocket;

import com.wmj.game.engine.dispatcher.CmdDispatcher;
import com.wmj.game.engine.manage.Session;
import com.wmj.game.engine.manage.WebSocketSessionManage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Locale;

/**
 * @Auther: wumingjie
 * @Date: 2019/3/5
 * @Description:
 */
public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    private final static Logger log = LoggerFactory.getLogger(WebSocketFrameHandler.class);
    private CmdDispatcher cmdDispatcher;
    private WebSocketSessionManage webSocketSessionManage;

    public WebSocketFrameHandler(CmdDispatcher cmdDispatcher) {
        this.cmdDispatcher = cmdDispatcher;
        this.webSocketSessionManage = new WebSocketSessionManage();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame webSocketFrame) {
        if (webSocketFrame instanceof BinaryWebSocketFrame) {
            BinaryWebSocketFrame frame = BinaryWebSocketFrame.class.cast(webSocketFrame);
            ByteBuf byteBuf = frame.content();
            if (byteBuf.capacity() < 4) {
                return;
            }
            int cmd = byteBuf.readInt();
            byteBuf.resetReaderIndex();
            byte[] dataBytes;
            if (byteBuf.hasArray()) {
                dataBytes = byteBuf.array();
            } else {
                int length = byteBuf.readableBytes();
                dataBytes = new byte[length];
                byteBuf.getBytes(byteBuf.readerIndex(), dataBytes);
            }
            Session session = webSocketSessionManage.getByChannel(ctx.channel());
            this.cmdDispatcher.dispatcher(session, cmd, dataBytes);
        } else if (webSocketFrame instanceof TextWebSocketFrame) {
            log.info("TextWebSocketFrame不处理");
        } else {
            log.warn("unsupported frame type : " + webSocketFrame.getClass().getName());
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.webSocketSessionManage.add(ctx.channel());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        this.webSocketSessionManage.remove(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

}
