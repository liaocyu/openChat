package com.liaocyu.openChat.common.user.service.impl;

import com.liaocyu.openChat.common.common.domain.vo.req.CursorPageBaseReq;
import com.liaocyu.openChat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.liaocyu.openChat.common.user.dao.UserDao;
import com.liaocyu.openChat.common.user.dao.UserFriendDao;
import com.liaocyu.openChat.common.user.domain.entity.User;
import com.liaocyu.openChat.common.user.domain.entity.UserFriend;
import com.liaocyu.openChat.common.user.domain.vo.req.FriendCheckReq;
import com.liaocyu.openChat.common.user.domain.vo.resp.FriendCheckResp;
import com.liaocyu.openChat.common.user.domain.vo.resp.FriendResp;
import com.liaocyu.openChat.common.user.service.IRoomService;
import com.liaocyu.openChat.common.user.service.adapter.FriendAdapter;
import com.liaocyu.openChat.common.user.service.FriendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/3 14:53
 * @description :
 */
@Service
@Slf4j
public class FriendServiceImpl implements FriendService {

    private final UserFriendDao userFriendDao;
    private final UserDao userDao;
    private final IRoomService roomService;

    @Autowired
    public FriendServiceImpl(UserFriendDao userFriendDao , UserDao userDao , IRoomService roomService) {
        this.userFriendDao = userFriendDao;
        this.userDao = userDao;
        this.roomService = roomService;
    }


    /**
     * 联系人列表
     * @param uid
     * @param request
     * @return
     */
    @Override
    public CursorPageBaseResp<FriendResp> friendList(Long uid, CursorPageBaseReq request) {
        CursorPageBaseResp<UserFriend> friendPage = userFriendDao.getFriendPage(uid, request);
        if(CollectionUtils.isEmpty(friendPage.getList())) {
            return CursorPageBaseResp.empty();
        }
        List<Long> friendUids = friendPage.getList()
                .stream().map(UserFriend::getFriendUid)
                .collect(Collectors.toList());
        List<User> userList = userDao.getFriendList(friendUids);
        return CursorPageBaseResp.init(friendPage, FriendAdapter.buildFriend(friendPage.getList(), userList));
    }


    /**
     * 删除好友
     *
     * @param uid       uid
     * @param friendUid 朋友uid
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFriend(Long uid, Long friendUid) {
        // 查询好友
        List<UserFriend> userFriends = userFriendDao.getUserFriend(uid, friendUid);
        if(CollectionUtils.isEmpty(userFriends)) {
            log.info("没有好友关系：{}，{}",uid, friendUid);
            return ;
        }
        List<Long> friendRecordIds = userFriends.stream().map(UserFriend::getId).collect(Collectors.toList());
        userFriendDao.removeByIds(friendRecordIds);
        // 禁用房间 TODO
        roomService.disableFriendRoom(Arrays.asList(uid, friendUid));

    }

    @Override
    public FriendCheckResp check(Long uid, FriendCheckReq request) {

        List<UserFriend> friendList = userFriendDao.getByFriends(uid, request.getUidList());
        Set<Long> friendUidSet = friendList.stream().map(UserFriend::getFriendUid).collect(Collectors.toSet());
        List<FriendCheckResp.FriendCheck> friendCheckList = request.getUidList().stream().map(friendUid -> {
            FriendCheckResp.FriendCheck friendCheck = new FriendCheckResp.FriendCheck();
            friendCheck.setUid(friendUid);
            friendCheck.setIsFriend(friendUidSet.contains(friendUid));
            return friendCheck;
        }).collect(Collectors.toList());
        return new FriendCheckResp(friendCheckList);
    }
}
