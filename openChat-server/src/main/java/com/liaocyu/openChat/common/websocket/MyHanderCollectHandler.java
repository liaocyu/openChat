package com.liaocyu.openChat.common.websocket;

import cn.hutool.core.net.url.UrlBuilder;
import com.liaocyu.openChat.common.websocket.utils.NettyUtil;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Optional;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/17 15:31
 * @description :
 * netty 请求头配置处理器
 */
public class MyHanderCollectHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            UrlBuilder urlBuilder = UrlBuilder.ofHttp(request.getUri());
            Optional<String> tokenOptional = Optional.of(urlBuilder)
                    .map(UrlBuilder::getQuery)
                    .map(k -> k.get("token"))
                    .map(CharSequence::toString);
            // 如果token存在
            tokenOptional.ifPresent(s -> NettyUtil.setAttr(ctx.channel(), NettyUtil.TOKEN, s));
            // 移除后面拼接的所有参数
            request.setUri(urlBuilder.getPath().toString());
            // 获取用户的ip
            String ip = request.headers().get("X-Real-IP");
            if (StringUtils.isBlank(ip)) {
                SocketAddress socketAddress = ctx.channel().remoteAddress();
                InetSocketAddress address = (InetSocketAddress) socketAddress;
                ip = address.getAddress().getHostAddress();
            }
            // 保存到channel 附件中
            NettyUtil.setAttr(ctx.channel(), NettyUtil.IP, ip);
            // 处理器需要用一次，移除自己
            ctx.pipeline().remove(this);
        }
        ctx.fireChannelRead(msg);
    }
}
