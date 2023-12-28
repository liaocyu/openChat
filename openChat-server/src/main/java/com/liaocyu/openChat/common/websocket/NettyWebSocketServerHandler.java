package com.liaocyu.openChat.common.websocket;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.liaocyu.openChat.common.websocket.domian.enums.WSReqTypeEnum;
import com.liaocyu.openChat.common.websocket.domian.vo.req.WSBaseReq;
import com.liaocyu.openChat.common.websocket.service.WebSocketService;
import com.liaocyu.openChat.common.websocket.utils.NettyUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义webSocket请求处理器
 */
@Slf4j
@Sharable
public class NettyWebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private WebSocketService webSocketService;
    /**
     * webSocket 连接激活时触发的函数
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        webSocketService = SpringUtil.getBean(WebSocketService.class);
        // 进行连接的保存
        webSocketService.connect(ctx.channel());
    }

    /**
     *  websocket 断开连接操作
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.userOffline(ctx.channel());
    }

    /**
     * 用户事件处理器
     * @param ctx
     * @param evt 表示触发的自定义事件对象，根据具体的事件类型进行处理
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 如果这个事件是一个握手完成的事件
        if(evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            String token = NettyUtil.getAttr(ctx.channel(), NettyUtil.TOKEN);
            if(StrUtil.isNotBlank(token)) {
                webSocketService.authorize(ctx.channel() , token);
            }
            // 这个事件是一个断开连接的事件
        } else if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if(event.state() == IdleState.READER_IDLE) {
                System.out.println("读空闲");
                userOffline(ctx.channel());
            }
        }
    }

    /**
     * 处理 webSocket 抛出的异常
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("exceptionCaught" , cause);
        super.exceptionCaught(ctx, cause);
    }

    /**
     * 用户下线操作
     * 从ONLINE_MAP中移除用户的map 、关闭 channel
     * @param channel
     */
    private void userOffline(Channel channel) {
        webSocketService.remove(channel);
        channel.close();
    }

    /**
     * 该方法 在接收到新的消息时被调用
     * 处理客户端发送的消息，解析消息内处理业务逻给客户发送响应等操作
     * @param channelHandlerContext
     * @param textWebSocketFrame netty 提供的用于处理文本WebSocket 帧的特定类型
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        String text = textWebSocketFrame.text();
        // 解析前端请求体
        WSBaseReq wsBaseReq = JSONUtil.toBean(text, WSBaseReq.class);
        switch (WSReqTypeEnum.of(wsBaseReq.getType())) {
            case AUTHORIZE:
                // wsBaseReq.getData() 用户传入进来的 token
                // 使用token保存 websocket 连接
                webSocketService.authorize(channelHandlerContext.channel() , wsBaseReq.getData());
                break;
            case HEARTBEAT:
                break;
            case LOGIN:
                webSocketService.handleLoginReq(channelHandlerContext.channel());

        }
    }
}
