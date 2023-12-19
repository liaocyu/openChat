package com.liaocyu.openChat.common.websocket.utils;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/18 11:30
 * @description :
 * 在Channel 对象中存储和获取用户的 token 。
 * Channel 是Netty 中用于表示网络连接的概念 ，每个用户连接到服务器都会创建一个对应的Channel 对象
 * 通过使用 Attribute 来存储和获取用户的 token ，可以在后序的处理过程中方便的获取用户的身份信息
 *
 * 存储用户的 token 代码： NettyUtil.setAttr(channel , NettyUtil.TOKEN , "user_token");
 * 获取用户的 token 代码： String userToken = NettyUtil.getAttr(channel , NettyUtil.TOKEN);
 */
public class NettyUtil {
    // 用于标识在 Channel 的属性中存储用户 token 的键
    public static AttributeKey<String> TOKEN = AttributeKey.valueOf("token");

    public static <T> void setAttr(Channel channel , AttributeKey<T> key , T value) {
        Attribute<T> attr = channel.attr(key); // 获取 Attribute 对象
        attr.set(value);
    }

    public static <T> T getAttr(Channel channel , AttributeKey<T> key ) {
        Attribute<T> attr = channel.attr(key);
        return attr.get();
    }
}
