package com.liaocyu.openChat.common.user.service.impl;

import com.liaocyu.openChat.common.common.domain.vo.req.CursorPageBaseReq;
import com.liaocyu.openChat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.liaocyu.openChat.common.user.domain.vo.resp.FriendResp;
import com.liaocyu.openChat.common.user.service.FriendService;
import org.springframework.stereotype.Service;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/3 14:53
 * @description :
 */
@Service
public class FriendServiceImpl implements FriendService {


    /**
     * 联系人列表
     * @param uid
     * @param request
     * @return
     */
    @Override
    public CursorPageBaseResp<FriendResp> friendList(Long uid, CursorPageBaseReq request) {

        return null;
    }
}
