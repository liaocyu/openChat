package com.liaocyu.openChat.common.user.service;

import com.liaocyu.openChat.common.common.domain.vo.req.CursorPageBaseReq;
import com.liaocyu.openChat.common.common.domain.vo.req.PageBaseReq;
import com.liaocyu.openChat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.liaocyu.openChat.common.common.domain.vo.resp.PageBaseResp;
import com.liaocyu.openChat.common.user.domain.vo.req.friend.FriendApplyReq;
import com.liaocyu.openChat.common.user.domain.vo.req.friend.FriendApproveReq;
import com.liaocyu.openChat.common.user.domain.vo.req.friend.FriendCheckReq;
import com.liaocyu.openChat.common.user.domain.vo.resp.FriendApplyResp;
import com.liaocyu.openChat.common.user.domain.vo.resp.FriendCheckResp;
import com.liaocyu.openChat.common.user.domain.vo.resp.FriendResp;
import com.liaocyu.openChat.common.user.domain.vo.resp.FriendUnreadResp;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/3 14:53
 * @description :
 */
public interface FriendService {

    CursorPageBaseResp<FriendResp> friendList(Long uid, CursorPageBaseReq request);

    /**
     * 删除好友
     *
     * @param uid       uid
     * @param friendUid 朋友uid
     */
    void deleteFriend(Long uid, Long friendUid);

    /**
     * 检查是否是自己好友
     * @param uid uid
     * @param request 请求
     * @return
     */
    FriendCheckResp check(Long uid, FriendCheckReq request);

    /**
     * 申请好友
     * @param uid uid
     * @param request 请求
     */
    void apply(Long uid, FriendApplyReq request);

    void applyApprove(Long uid, FriendApproveReq friendApproveReq);

    PageBaseResp<FriendApplyResp> pageApplyFriend(Long uid, PageBaseReq request);

    FriendUnreadResp unread(Long uid);
}
