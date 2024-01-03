package com.liaocyu.openChat.common.user.service;

import com.liaocyu.openChat.common.common.domain.vo.req.CursorPageBaseReq;
import com.liaocyu.openChat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.liaocyu.openChat.common.user.domain.vo.resp.FriendResp;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/3 14:53
 * @description :
 */
public interface FriendService {

    CursorPageBaseResp<FriendResp> friendList(Long uid, CursorPageBaseReq request);
}
