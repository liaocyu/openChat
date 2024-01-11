package com.liaocyu.openChat.common.user.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.liaocyu.openChat.common.common.annotation.RedissonLock;
import com.liaocyu.openChat.common.common.domain.vo.req.CursorPageBaseReq;
import com.liaocyu.openChat.common.common.domain.vo.req.PageBaseReq;
import com.liaocyu.openChat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.liaocyu.openChat.common.common.domain.vo.resp.PageBaseResp;
import com.liaocyu.openChat.common.common.event.UserApplyEvent;
import com.liaocyu.openChat.common.common.utils.AssertUtil;
import com.liaocyu.openChat.common.user.dao.UserApplyDao;
import com.liaocyu.openChat.common.user.dao.UserDao;
import com.liaocyu.openChat.common.user.dao.UserFriendDao;
import com.liaocyu.openChat.common.chat.domain.entity.RoomFriend;
import com.liaocyu.openChat.common.user.domain.entity.User;
import com.liaocyu.openChat.common.user.domain.entity.UserApply;
import com.liaocyu.openChat.common.user.domain.entity.UserFriend;
import com.liaocyu.openChat.common.user.domain.vo.req.friend.FriendApplyReq;
import com.liaocyu.openChat.common.user.domain.vo.req.friend.FriendApproveReq;
import com.liaocyu.openChat.common.user.domain.vo.req.friend.FriendCheckReq;
import com.liaocyu.openChat.common.user.domain.vo.resp.FriendApplyResp;
import com.liaocyu.openChat.common.user.domain.vo.resp.FriendCheckResp;
import com.liaocyu.openChat.common.user.domain.vo.resp.FriendResp;
import com.liaocyu.openChat.common.user.domain.vo.resp.FriendUnreadResp;
import com.liaocyu.openChat.common.chat.service.RoomService;
import com.liaocyu.openChat.common.user.service.adapter.FriendAdapter;
import com.liaocyu.openChat.common.user.service.FriendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.liaocyu.openChat.common.user.domain.enums.ApplyStatusEnum.WAIT_APPROVAL;

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
    private final RoomService roomService;
    private final UserApplyDao userApplyDao;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public FriendServiceImpl(UserFriendDao userFriendDao, UserDao userDao, RoomService roomService, UserApplyDao userApplyDao, ApplicationEventPublisher applicationEventPublisher) {
        this.userFriendDao = userFriendDao;
        this.userDao = userDao;
        this.roomService = roomService;
        this.userApplyDao = userApplyDao;
        this.applicationEventPublisher = applicationEventPublisher;
    }


    /**
     * 联系人列表
     *
     * @param uid
     * @param request
     * @return
     */
    @Override
    public CursorPageBaseResp<FriendResp> friendList(Long uid, CursorPageBaseReq request) {
        CursorPageBaseResp<UserFriend> friendPage = userFriendDao.getFriendPage(uid, request);
        if (CollectionUtils.isEmpty(friendPage.getList())) {
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
        if (CollectionUtils.isEmpty(userFriends)) {
            log.info("没有好友关系：{}，{}", uid, friendUid);
            return;
        }
        List<Long> friendRecordIds = userFriends.stream().map(UserFriend::getId).collect(Collectors.toList());
        userFriendDao.removeByIds(friendRecordIds);
        // 禁用房间
        roomService.disableFriendRoom(Arrays.asList(uid, friendUid));

    }

    @Override
    public FriendCheckResp check(Long uid, FriendCheckReq request) {
        // 从前端FriendCheckReq请求中 查出我的好友列表
        List<UserFriend> friendList = userFriendDao.getByFriends(uid, request.getUidList());
        // 返回我的好友UID
        Set<Long> friendUidSet = friendList.stream().map(UserFriend::getFriendUid).collect(Collectors.toSet());
        List<FriendCheckResp.FriendCheck> friendCheckList = request.getUidList().stream().map(friendUid -> {
            // 构造我的好友请求体
            FriendCheckResp.FriendCheck friendCheck = new FriendCheckResp.FriendCheck();
            friendCheck.setUid(friendUid);
            friendCheck.setIsFriend(friendUidSet.contains(friendUid));
            return friendCheck;
        }).collect(Collectors.toList());
        return new FriendCheckResp(friendCheckList);
    }

    /**
     * 申请好友
     *
     * @param uid     uid
     * @param request 请求
     */
    @Override
    @RedissonLock(key = "#uid")
    public void apply(Long uid, FriendApplyReq request) {
        //System.out.println("hahhahhahhahhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhaaaaaa");
        // 是否有好友关系
        UserFriend friend = userFriendDao.getByFriend(uid, request.getTargetUid());
        AssertUtil.isEmpty(friend, "你们已经是好友啦");
        // 是否有待审批的申请记录（自己的）申请状态是待审批 申请code是加好友
        UserApply selfApproving = userApplyDao.getFriendApproving(uid, request.getTargetUid());
        if (Objects.nonNull(selfApproving)) {
            log.info("已有好友申请记录,uid:{}, targetId:{}", uid, request.getTargetUid());
            return;
        }

        // 是否有待审批的申请记录（别人请求自己的）
        UserApply friendApproving = userApplyDao.getFriendApproving(request.getTargetUid(), uid);
        if (Objects.nonNull(friendApproving)) {
            ((FriendService) AopContext.currentProxy()).applyApprove(uid, new FriendApproveReq(friendApproving.getId()));
            return;
        }

        // 申请入库 构造一个申请实体
        UserApply insert = FriendAdapter.buildFriendApply(uid, request);
        userApplyDao.save(insert);
        applicationEventPublisher.publishEvent(new UserApplyEvent(this, insert));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @RedissonLock(key = "#uid")
    public void applyApprove(Long uid, FriendApproveReq request) {
        UserApply userApply = userApplyDao.getById(request.getApplyId());
        AssertUtil.isNotEmpty(userApply, "不存在申请记录");
        AssertUtil.equal(userApply.getTargetId(), uid, "不存在申请记录");
        AssertUtil.equal(userApply.getStatus(), WAIT_APPROVAL.getCode(), "已同意好友申请");
        // 同意申请
        userApplyDao.agree(request.getApplyId());
        // 创建双方好友关系
        createFriend(uid, userApply.getUid());
        // 创建一个聊天房间
        RoomFriend roomFriend = roomService.createFriendRoom(Arrays.asList(uid, userApply.getUid()));
        // TODO 发送一条同意消息。。我们已经是好友了，开始聊天吧
        // chatService.sendMsg(MessageAdapter.buildAgreeMsg(roomFriend.getRoomId()), uid);
    }

    /**
     * 获取我的好友申请列表
     * @param uid 我的uid
     * @param request 请求
     * @return
     * request.plusPage()  page对象 （1，10）
     */
    @Override
    public PageBaseResp<FriendApplyResp> pageApplyFriend(Long uid, PageBaseReq request) {

        IPage<UserApply> userApplyIpage = userApplyDao.friendApplyPage(uid, request.plusPage());
        if (CollectionUtils.isEmpty(userApplyIpage.getRecords())) {
            return PageBaseResp.empty();
        }
        // 将这些申请列表设为已读
        readApples(uid, userApplyIpage);
        // 返回消息
        return PageBaseResp.init(userApplyIpage, FriendAdapter.buildFriendApplyList(userApplyIpage.getRecords()));
    }

    @Override
    public FriendUnreadResp unread(Long uid) {
        Integer unReadCount = userApplyDao.getUnReadCount(uid);
        return new FriendUnreadResp(unReadCount);
    }

    /**
     * userApplyIpage.getRecords() 好友申请列表
     */
    private void readApples(Long uid, IPage<UserApply> userApplyIpage) {
        List<Long> applyIds = userApplyIpage.getRecords()
                .stream().map(UserApply::getId)
                .collect(Collectors.toList());
        // 更新好友申请信息未已读状态
        userApplyDao.readApples(uid, applyIds);
    }

    private void createFriend(Long uid, Long targetUid) {
        UserFriend userFriend = new UserFriend();
        userFriend.setUid(uid);
        userFriend.setFriendUid(targetUid);
        UserFriend userFriend2 = new UserFriend();
        userFriend2.setUid(targetUid);
        userFriend2.setFriendUid(uid);
        userFriendDao.saveBatch(Lists.newArrayList(userFriend, userFriend2));

    }
}
