package com.liaocyu.openChat.common.chat.service;

import com.liaocyu.openChat.common.chat.domain.vo.req.MemberReq;
import com.liaocyu.openChat.common.chat.domain.vo.resp.ChatMemberResp;
import com.liaocyu.openChat.common.common.domain.vo.resp.CursorPageBaseResp;

import java.util.List;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/15 15:56
 * @description : 消息处理类
 */
public interface ChatService {

    /**
     * 获取群成员列表
     *
     * @param memberUidList
     * @param request
     * @return
     */
    CursorPageBaseResp<ChatMemberResp> getMemberPage(List<Long> memberUidList, MemberReq request);
}
