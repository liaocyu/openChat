package com.liaocyu.openChat.common.chat.service.impl;

import com.liaocyu.openChat.common.chat.dao.ContactDao;
import com.liaocyu.openChat.common.chat.dao.GroupMemberDao;
import com.liaocyu.openChat.common.chat.dao.RoomDao;
import com.liaocyu.openChat.common.chat.dao.RoomGroupDao;
import com.liaocyu.openChat.common.chat.domain.entity.Room;
import com.liaocyu.openChat.common.chat.domain.entity.RoomGroup;
import com.liaocyu.openChat.common.chat.domain.vo.req.admin.AdminAddReq;
import com.liaocyu.openChat.common.chat.domain.vo.req.admin.AdminRevokeReq;
import com.liaocyu.openChat.common.chat.domain.vo.req.member.MemberExitReq;
import com.liaocyu.openChat.common.chat.service.GroupMemberService;
import com.liaocyu.openChat.common.chat.service.adapter.MemberAdapter;
import com.liaocyu.openChat.common.chat.service.cache.GroupMemberCache;
import com.liaocyu.openChat.common.common.exception.CommonErrorEnum;
import com.liaocyu.openChat.common.common.exception.GroupErrorEnum;
import com.liaocyu.openChat.common.common.utils.AssertUtil;
import com.liaocyu.openChat.common.websocket.domian.vo.resp.WSBaseResp;
import com.liaocyu.openChat.common.websocket.domian.vo.resp.ws.WSMemberChange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static com.liaocyu.openChat.common.chat.constant.GroupConst.MAX_MANAGE_COUNT;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/12 15:34
 * @description :
 */
@Service("groupMemberService")
public class GroupMemberServiceImpl implements GroupMemberService {

    private final RoomGroupDao roomGroupDao;
    private final RoomDao roomDao;
    private final GroupMemberDao groupMemberDao;
    private final ContactDao contactDao;
    private final GroupMemberCache groupMemberCache;
    private final PushService pushService;

    @Autowired
    public GroupMemberServiceImpl(RoomGroupDao roomGroupDao , RoomDao roomDao , GroupMemberDao groupMemberDao, ContactDao contactDao , GroupMemberCache groupMemberCache , PushService pushService) {
        this.roomGroupDao = roomGroupDao;
        this.roomDao = roomDao;
        this.groupMemberDao = groupMemberDao;
        this.contactDao = contactDao;
        this.groupMemberCache = groupMemberCache;
        this.pushService = pushService;
    }
    // TODO 删除全成员

    /**
     * 退出群聊
     *
     * @param uid     需要退出的用户ID
     * @param request 请求信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void exitGroup(Long uid, MemberExitReq request) {
        Long roomId = request.getRoomId();
        // 1. 判断群聊是否存在
        RoomGroup roomGroup = roomGroupDao.getByRoomId(roomId);
        AssertUtil.isNotEmpty(roomGroup , GroupErrorEnum.GROUP_NOT_EXIST);

        // 2. 判断房间是否是大群聊  （大群聊禁止退出）
        Room room = roomDao.getById(roomId);
        AssertUtil.isFalse(room.isHotRoom() , GroupErrorEnum.NOT_ALLOWED_FOR_EXIT_GROUP);

        // 3、判断群成员是否在群中
        Boolean isGroupShip = groupMemberDao.isGroupShip(roomGroup.getRoomId() , Collections.singletonList(uid));
        AssertUtil.isTrue(isGroupShip, GroupErrorEnum.USER_NOT_IN_GROUP);

        // 4、判断该用户是否是群主
        Boolean isLord = groupMemberDao.isLord(roomGroup.getId(), uid);
        if (isLord) {
            // 4.1 删除房间
            boolean isDelRoom = roomDao.removeById(roomId);
            AssertUtil.isTrue(isDelRoom, CommonErrorEnum.SYSTEM_ERROR);
            // 4.2 删除会话
            Boolean isDelContact = contactDao.removeByRoomId(roomId , Collections.EMPTY_LIST);
            AssertUtil.isTrue(isDelContact , CommonErrorEnum.SYSTEM_ERROR);
            // 4.3 删除群成员
            Boolean isDelGroupMember = groupMemberDao.removeByGroupId(roomGroup.getId() ,Collections.EMPTY_LIST);
            AssertUtil.isTrue(isDelGroupMember , CommonErrorEnum.SYSTEM_ERROR);
            // 4.4 TODO 删除消息记录（逻辑）
            /*Boolean isDelMessage = messageDao.removeByRoomId(roomId, Collections.EMPTY_LIST);
            AssertUtil.isTrue(isDelMessage, CommonErrorEnum.SYSTEM_ERROR);*/
            // TODO 这里也可以告知群成员 群聊已被删除的消息
        } else {
            // 4.5 删除会话
            Boolean isDelContact = contactDao.removeByRoomId(roomId, Collections.singletonList(uid));
            AssertUtil.isTrue(isDelContact, CommonErrorEnum.SYSTEM_ERROR);
            // 4.6 删除群成员
            Boolean isDelGroupMember = groupMemberDao.removeByGroupId(roomGroup.getId(), Collections.singletonList(uid));
            AssertUtil.isTrue(isDelGroupMember, CommonErrorEnum.SYSTEM_ERROR);
            // 4.7 发送移除事件告知群成员
            List<Long> memberUidList = groupMemberCache.getMemberUidList(roomGroup.getRoomId());
            WSBaseResp<WSMemberChange> ws = MemberAdapter.buildMemberRemoveWS(roomGroup.getRoomId(), uid);
            pushService.sendPushMsg(ws, memberUidList);
            groupMemberCache.evictMemberUidList(room.getId());
        }
    }

    /**
     * 增加管理员
     *
     * @param uid     用户ID
     * @param request 请求信息 roomId ，被邀请的称为管理员的Uid列表
     */
    @Override
    public void addAdmin(Long uid, AdminAddReq request) {
        // 1. 判断群聊是否存在
        RoomGroup roomGroup = roomGroupDao.getByRoomId(request.getRoomId());
        AssertUtil.isNotEmpty(roomGroup, GroupErrorEnum.GROUP_NOT_EXIST);

        // 2. 判断该用户是否是群主
        Boolean isLord = groupMemberDao.isLord(roomGroup.getId(), uid);
        AssertUtil.isTrue(isLord, GroupErrorEnum.NOT_ALLOWED_OPERATION);

        // 3. 判断群成员是否在群中
        Boolean isGroupShip = groupMemberDao.isGroupShip(roomGroup.getRoomId(), request.getUidList());
        AssertUtil.isTrue(isGroupShip, GroupErrorEnum.USER_NOT_IN_GROUP);

        // 4. 判断管理员数量是否达到上限
        // 4.1 查询现有管理员数量
        List<Long> manageUidList = groupMemberDao.getManageUidList(roomGroup.getId());
        // 4.2 去重
        HashSet<Long> manageUidSet = new HashSet<>(manageUidList);
        manageUidSet.addAll(request.getUidList());
        AssertUtil.isFalse(manageUidSet.size() > MAX_MANAGE_COUNT, GroupErrorEnum.MANAGE_COUNT_EXCEED);

        // 5. 增加管理员
        groupMemberDao.addAdmin(roomGroup.getId(), request.getUidList());
    }

    /**
     * 撤销管理员
     *
     * @param uid     用户ID
     * @param request 请求信息
     */
    @Override
    public void revokeAdmin(Long uid, AdminRevokeReq request) {
        // 1. 判断群聊是否存在
        RoomGroup roomGroup = roomGroupDao.getByRoomId(request.getRoomId());
        AssertUtil.isNotEmpty(roomGroup, GroupErrorEnum.GROUP_NOT_EXIST);

        // 2. 判断该用户是否是群主
        Boolean isLord = groupMemberDao.isLord(roomGroup.getId(), uid);
        AssertUtil.isTrue(isLord, GroupErrorEnum.NOT_ALLOWED_OPERATION);

        // 3. 判断群成员是否在群中
        Boolean isGroupShip = groupMemberDao.isGroupShip(roomGroup.getRoomId(), request.getUidList());
        AssertUtil.isTrue(isGroupShip, GroupErrorEnum.USER_NOT_IN_GROUP);

        // 4. 撤销管理员
        groupMemberDao.revokeAdmin(roomGroup.getId(), request.getUidList());
    }
}
