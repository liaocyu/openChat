package com.liaocyu.openChat.common.chat.service;

import com.liaocyu.openChat.common.chat.domain.entity.RoomFriend;
import com.liaocyu.openChat.common.chat.domain.vo.resp.ChatRoomResp;
import com.liaocyu.openChat.common.common.domain.vo.req.CursorPageBaseReq;
import com.liaocyu.openChat.common.common.domain.vo.resp.CursorPageBaseResp;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/10 14:56
 * @description :
 */
public interface RoomAppService {


    /**
     * 获取会话列表--支持未登录态
     */
    CursorPageBaseResp<ChatRoomResp> getContactPage(CursorPageBaseReq request, Long uid);

    ChatRoomResp getContactDetailByFriend(Long uid, Long friendUid);
}
