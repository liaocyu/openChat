package com.liaocyu.openChat.common.websocket.service;

import io.netty.channel.Channel;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/11 13:42
 * @description :
 */
public interface WebSocketService {

    void connect(Channel channel);

    void handleLoginReq(Channel channel);

    void remove(Channel channel);

    void scanLoginSuccess(Integer code, Long id);

    /**
     * 等待用户授权
     * @param code 授权码
     */
    void waitAuthorize(Integer code);
}