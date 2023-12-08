package com.liaocyu.openChat.common.websocket;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.liaocyu.openChat.common.websocket.domian.enums.WSReqTypeEnum;
import com.liaocyu.openChat.common.websocket.domian.vo.req.WSBaseReq;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * webSocket请求处理器
 */
@Slf4j
@Sharable
public class NettyWebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 如果这个事件是一个握手完成的事件
        if(evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            System.out.println("握手完成");
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        String text = textWebSocketFrame.text();
        WSBaseReq wsBaseReq = JSONUtil.toBean(text, WSBaseReq.class);
        switch (WSReqTypeEnum.of(wsBaseReq.getType())) {
            case AUTHORIZE:
            case HEARTBEAT:
                break;
            case LOGIN:
                System.out.println("请求二维码");
                channelHandlerContext.channel().writeAndFlush(new TextWebSocketFrame("123"));
        }
    }
}
