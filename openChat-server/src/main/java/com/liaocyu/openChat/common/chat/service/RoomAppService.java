package com.liaocyu.openChat.common.chat.service;

import com.liaocyu.openChat.common.chat.domain.vo.req.*;
import com.liaocyu.openChat.common.chat.domain.vo.req.member.MemberAddReq;
import com.liaocyu.openChat.common.chat.domain.vo.req.member.MemberDelReq;
import com.liaocyu.openChat.common.chat.domain.vo.req.member.MemberReq;
import com.liaocyu.openChat.common.chat.domain.vo.resp.ChatMemberListResp;
import com.liaocyu.openChat.common.chat.domain.vo.resp.ChatMemberResp;
import com.liaocyu.openChat.common.chat.domain.vo.resp.ChatRoomResp;
import com.liaocyu.openChat.common.chat.domain.vo.resp.MemberResp;
import com.liaocyu.openChat.common.common.domain.vo.req.CursorPageBaseReq;
import com.liaocyu.openChat.common.common.domain.vo.resp.CursorPageBaseResp;

import java.util.List;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/10 14:56
 * @description : 聊天室应用总接口
 */
public interface RoomAppService {


    /**
     * 获取会话列表--支持未登录态
     */
    CursorPageBaseResp<ChatRoomResp> getContactPage(CursorPageBaseReq request, Long uid);

    ChatRoomResp getContactDetailByFriend(Long uid, Long friendUid);

    ChatRoomResp getContactDetail(Long uid, Long id);

    /**
     * 群组详情
     * @param uid uid
     * @param roomId 房间Id
     * @return 返回群组详情
     */
    MemberResp getGroupDetail(Long uid, Long roomId);

    CursorPageBaseResp<ChatMemberResp> getMemberPage(MemberReq request);

    /**
     * 删除群成员
     * @param uid uid
     * @param request MemberDelReq={roomId , uid(被移除的uid)}
     */
    void delMember(Long uid, MemberDelReq request);

    /**
     *  IdRespVO 群组Id
     * @param request 被邀请的uid列表
     * @return
     */
    Long addGroup(Long uid, GroupAddReq request);

    void addMember(Long uid, MemberAddReq request);

    List<ChatMemberListResp> getMemberList(ChatMessageMemberReq request);
}
