package com.liaocyu.openChat.common.user.service.impl;

import com.liaocyu.openChat.common.common.domain.vo.req.CursorPageBaseReq;
import com.liaocyu.openChat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.liaocyu.openChat.common.user.dao.UserFriendDao;
import com.liaocyu.openChat.common.user.domain.entity.UserFriend;
import com.liaocyu.openChat.common.user.domain.vo.resp.FriendResp;
import com.liaocyu.openChat.common.user.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/3 14:53
 * @description :
 */
@Service
public class FriendServiceImpl implements FriendService {

    private final UserFriendDao userFriendDao;

    @Autowired
    public FriendServiceImpl(UserFriendDao userFriendDao) {
        this.userFriendDao = userFriendDao;
    }


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


    /**
     * 删除好友
     *
     * @param uid       uid
     * @param friendUid 朋友uid
     */
    @Override
    public void deleteFriend(Long uid, Long friendUid) {
        // 查询好友uid 是否存在
        List<UserFriend> userFriends = userFriendDao.getUserFriend(uid, friendUid);


    }
}
