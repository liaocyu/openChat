package com.liaocyu.openChat.common.chat.service;

import java.util.List;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/26 17:17
 * @description :
 */
public interface WeChatMsgOperationService {
    /**
     * 向被at的用户微信推送群聊消息
     *
     * @param senderUid senderUid
     * @param receiverUidList receiverUidList
     * @param msg msg
     */
    void publishChatMsgToWeChatUser(long senderUid, List<Long> receiverUidList, String msg);
}
