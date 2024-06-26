package com.liaocyu.openChat.common.websocket.service;

import com.liaocyu.openChat.common.websocket.domian.vo.resp.WSBaseResp;
import com.liaocyu.openChat.common.websocket.domian.vo.resp.ws.WSOnlineOfflineNotify;
import io.netty.channel.Channel;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/11 13:42
 * @description :
 */
public interface WebSocketService {

    void connect(Channel channel);

    /**
     * 处理用户首次登录
     * @param channel
     */
    void handleLoginReq(Channel channel);

    /**
     * 处理ws断开连接的事件
     *
     * @param channel
     */
    void remove(Channel channel);

    void scanLoginSuccess(Integer code, Long id);

    /**
     * 等待用户授权
     * @param code 授权码
     */
    void waitAuthorize(Integer code);

    /**
     * 握手认证
     *
     * @param channel 用户的连接
     * @param token 用户传入的 toekn
     */
    void authorize(Channel channel, String token);

    Boolean scanSuccess(Integer loginCode);

    void sendMsgToAll(WSBaseResp<?> msg);

    /**
     * 推动消息给所有在线的人
     *
     * @param wsBaseResp 发送的消息体
     * @param skipUid    需要跳过的人
     */
    void sendToAllOnline(WSBaseResp<?> wsBaseResp, Long skipUid);

    void sendToUid(WSBaseResp<?> wsBaseMsg, Long uid);
}
