package com.liaocyu.openChat.common.chat.service;

import com.liaocyu.openChat.common.chat.domain.dto.MsgReadInfoDTO;
import com.liaocyu.openChat.common.chat.domain.entity.Message;
import com.liaocyu.openChat.common.chat.domain.vo.req.*;
import com.liaocyu.openChat.common.chat.domain.vo.req.member.MemberReq;
import com.liaocyu.openChat.common.chat.domain.vo.resp.*;
import com.liaocyu.openChat.common.common.domain.vo.resp.CursorPageBaseResp;

import javax.annotation.Nullable;
import java.util.Collection;
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

    /**
     * 根据消息获取消息前端展示的物料
     *
     * @param msgId
     * @param receiveUid 接受消息的uid，可null
     * @return
     */
    ChatMessageResp getMsgResp(Long msgId, Long receiveUid);

    ChatMessageResp getMsgResp(Message message, Long receiveUid);

    CursorPageBaseResp<ChatMessageResp> getMsgPage(ChatMessagePageReq request, @Nullable Long receiveUid);

    /**
     * 发送消息
     */
    Long sendMsg(ChatMessageReq request, Long uid);

    void setMsgMark(Long uid, ChatMessageMarkReq request);

    ChatMemberStatisticResp getMemberStatistic();

    /**
     * 撤回消息
     *
     * @param uid uid
     * @param request ChatMessageBaseReq(msgId , roomId)
     */
    void recallMsg(Long uid, ChatMessageBaseReq request);

    List<ChatMemberListResp> getMemberList(ChatMessageMemberReq chatMessageMemberReq);

    CursorPageBaseResp<ChatMessageReadResp> getReadPage(Long uid, ChatMessageReadReq request);

    /**
     * 获取已读消息的条数
     *
     * @param uid uid
     * @param request ChatMessageReadInfoReq(List<Long> msgIds)
     * @return
     */
    Collection<MsgReadInfoDTO> getMsgReadInfo(Long uid, ChatMessageReadInfoReq request);

    void msgRead(Long uid, ChatMessageMemberReq request);
}
