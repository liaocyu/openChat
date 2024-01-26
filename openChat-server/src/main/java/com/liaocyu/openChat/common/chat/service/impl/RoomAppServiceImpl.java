package com.liaocyu.openChat.common.chat.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import com.liaocyu.openChat.common.chat.dao.ContactDao;
import com.liaocyu.openChat.common.chat.dao.GroupMemberDao;
import com.liaocyu.openChat.common.chat.dao.MessageDao;
import com.liaocyu.openChat.common.chat.domain.dto.RoomBaseInfo;
import com.liaocyu.openChat.common.chat.domain.entity.*;
import com.liaocyu.openChat.common.chat.domain.enums.GroupRoleAPPEnum;
import com.liaocyu.openChat.common.chat.domain.enums.GroupRoleEnum;
import com.liaocyu.openChat.common.chat.domain.enums.HotFlagEnum;
import com.liaocyu.openChat.common.chat.domain.enums.RoomTypeEnum;
import com.liaocyu.openChat.common.chat.domain.vo.req.*;
import com.liaocyu.openChat.common.chat.domain.vo.req.member.MemberAddReq;
import com.liaocyu.openChat.common.chat.domain.vo.req.member.MemberDelReq;
import com.liaocyu.openChat.common.chat.domain.vo.req.member.MemberReq;
import com.liaocyu.openChat.common.chat.domain.vo.resp.ChatMemberListResp;
import com.liaocyu.openChat.common.chat.domain.vo.resp.ChatMemberResp;
import com.liaocyu.openChat.common.chat.domain.vo.resp.ChatRoomResp;
import com.liaocyu.openChat.common.chat.domain.vo.resp.MemberResp;
import com.liaocyu.openChat.common.chat.service.ChatService;
import com.liaocyu.openChat.common.chat.service.RoomService;
import com.liaocyu.openChat.common.chat.service.RoomAppService;
import com.liaocyu.openChat.common.chat.service.adapter.MemberAdapter;
import com.liaocyu.openChat.common.chat.service.adapter.RoomAdapter;
import com.liaocyu.openChat.common.chat.service.cache.*;
import com.liaocyu.openChat.common.chat.service.strategy.AbstractMsgHandler;
import com.liaocyu.openChat.common.chat.service.strategy.msg.MsgHandlerFactory;
import com.liaocyu.openChat.common.common.annotation.RedissonLock;
import com.liaocyu.openChat.common.common.domain.vo.req.CursorPageBaseReq;
import com.liaocyu.openChat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.liaocyu.openChat.common.common.event.GroupMemberAddEvent;
import com.liaocyu.openChat.common.common.exception.GroupErrorEnum;
import com.liaocyu.openChat.common.common.utils.AssertUtil;
import com.liaocyu.openChat.common.user.dao.UserDao;
import com.liaocyu.openChat.common.user.domain.entity.User;
import com.liaocyu.openChat.common.user.domain.enums.RoleEnum;
import com.liaocyu.openChat.common.user.service.RoleService;
import com.liaocyu.openChat.common.user.service.adapter.ChatAdapter;
import com.liaocyu.openChat.common.user.service.cache.UserCache;
import com.liaocyu.openChat.common.user.service.cache.UserInfoCache;
import com.liaocyu.openChat.common.websocket.domian.vo.resp.WSBaseResp;
import com.liaocyu.openChat.common.websocket.domian.vo.resp.ws.WSMemberChange;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/10 14:58
 * @description :
 */
@Service("roomAppService")
@RequiredArgsConstructor
public class RoomAppServiceImpl implements RoomAppService {

    private final RoomService roomService;
    private final RoomCache roomCache;
    private final UserCache userCache;
    private final RoomGroupCache roomGroupCache;
    private final GroupMemberDao groupMemberDao;
    private final UserDao userDao;
    private final RoleService roleService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ChatService chatService;
    private final GroupMemberCache groupMemberCache;
    private final PushService pushService;
    private final ContactDao contactDao;
    private final UserInfoCache userInfoCache;
    private final HotRoomCache hotRoomCache;
    private final MessageDao messageDao;
    private final RoomFriendCache roomFriendCache;

    /**
     * 获取会话列表
     *
     * @param request
     * @param uid
     * @return
     */
    @Override
    public CursorPageBaseResp<ChatRoomResp> getContactPage(CursorPageBaseReq request, Long uid) {
        // 查出用户要展示的会话列表
        CursorPageBaseResp<Long> page;
        if (Objects.nonNull(uid)) {
            Double hotEnd = getCursorOrNull(request.getCursor());
            Double hotStart = null;
            // 用户基础会话
            CursorPageBaseResp<Contact> contactPage = contactDao.getContactPage(uid, request);
            List<Long> baseRoomIds = contactPage.getList().stream().map(Contact::getRoomId).collect(Collectors.toList());
            if (!contactPage.getIsLast()) {
                hotStart = getCursorOrNull(contactPage.getCursor());
            }
            // 热门房间
            Set<ZSetOperations.TypedTuple<String>> typedTuples = hotRoomCache.getRoomRange(hotStart, hotEnd);
            List<Long> hotRoomIds = typedTuples.stream().map(ZSetOperations.TypedTuple::getValue).filter(Objects::nonNull).map(Long::parseLong).collect(Collectors.toList());
            baseRoomIds.addAll(hotRoomIds);
            // 基础会话和热门房间合并
            page = CursorPageBaseResp.init(contactPage, baseRoomIds);
        } else {// 用户未登录，只查全局房间(热点房间)
            CursorPageBaseResp<Pair<Long, Double>> roomCursorPage = hotRoomCache.getRoomCursorPage(request);
            List<Long> roomIds = roomCursorPage.getList().stream().map(Pair::getKey).collect(Collectors.toList());
            page = CursorPageBaseResp.init(roomCursorPage, roomIds);
        }
        // 最后组装会话信息（名称，头像，未读数等）
        List<ChatRoomResp> result = buildContactResp(uid, page.getList());
        return CursorPageBaseResp.init(page, result);
    }

    private Double getCursorOrNull(String cursor) {
        return Optional.ofNullable(cursor).map(Double::parseDouble).orElse(null);
    }

    @Override
    public ChatRoomResp getContactDetailByFriend(Long uid, Long friendUid) {

        RoomFriend friendRoom = roomService.getFriendRoom(uid, friendUid); // 10443  10003
        AssertUtil.isNotEmpty(friendRoom, "Ta 不是你的好友");
        return buildContactResp(uid, Collections.singletonList(friendRoom.getRoomId())).get(0);
    }

    @Override
    public ChatRoomResp getContactDetail(Long uid, Long roomId) {
        // 查询好友之间的聊天室
        Room room = roomCache.get(roomId);
        AssertUtil.isNotEmpty(room , "房间号不能为空");
        return buildContactResp(uid, Collections.singletonList(roomId)).get(0);
    }

    /**
     * 查询群组的详情
     * @param uid uid
     * @param roomId 房间id
     * @return
     */
    @Override
    public MemberResp getGroupDetail(Long uid, Long roomId) {
        // 1、查询群聊房间信息
        RoomGroup roomGroup = roomGroupCache.get(roomId);
        // 2、查询房间表信息
        Room room = roomCache.get(roomId);
        AssertUtil.isNotEmpty(roomGroup , "roomId有误");
        Long onlineNum;
        if (isHotGroup(room)) { // 判断是否是热点群聊
            // 3.1、热点群从redis取人数
            onlineNum = userCache.getOnlineNum();
        } else {
            // 3.2、查询群成员 uid 列表
            List<Long> memberUidList = groupMemberDao.getMemberUidList(roomGroup.getId()); // roomGroup.getId()
            // 3.3、从用户表中查询在线用户人数
            onlineNum = userDao.getOnlineCount(memberUidList).longValue();
        }
        // uid: uid  roomGroup: 群聊表   room: 房间表
        GroupRoleAPPEnum groupRole = getGroupRole(uid , roomGroup , room); // 查询群聊成员角色
        return MemberResp.builder() // builder 模式构造群成员实体返回
                .avatar(roomGroup.getAvatar())
                .roomId(roomId)
                .groupName(roomGroup.getName())
                .onlineNum(onlineNum)
                .role(groupRole.getType())
                .build();
    }

    @Override
    public CursorPageBaseResp<ChatMemberResp> getMemberPage(MemberReq request) {
        // 防御性编程 查询房间信息
        Room room = roomCache.get(request.getRoomId());
        AssertUtil.isNotEmpty(room, "房间号有误");
        List<Long> memberUidList;
        if (isHotGroup(room)) {// 全员群展示所有用户
            memberUidList = null;
        } else {// 只展示房间内的群成员
            RoomGroup roomGroup = roomGroupCache.get(request.getRoomId());
            memberUidList = groupMemberDao.getMemberUidList(roomGroup.getId());
        }
        return chatService.getMemberPage(memberUidList, request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delMember(Long uid, MemberDelReq request) {
        // 获取房间号
        Room room = roomCache.get(request.getRoomId());
        AssertUtil.isNotEmpty(room , "房间号有误");
        RoomGroup roomGroup = roomGroupCache.get(request.getRoomId());
        AssertUtil.isNotEmpty(roomGroup , "房间号有误");
        GroupMember self = groupMemberDao.getMember(roomGroup.getId(), uid);
        AssertUtil.isNotEmpty(self , GroupErrorEnum.USER_NOT_IN_GROUP);
        // 1. 判断被移除的人是否是群主或者管理员  （群主不可以被移除，管理员只能被群主移除）
        Long removedUid = request.getUid();
        // 群主   非法操作
        AssertUtil.isFalse(groupMemberDao.isLord(roomGroup.getId() , removedUid) , GroupErrorEnum.NOT_ALLOWED_FOR_REMOVE);
        // 判断删除的人是否是管理员 ，判断是否是群主操作
        if (groupMemberDao.isManager(roomGroup.getId() , removedUid)) {
            boolean isLord = groupMemberDao.isLord(roomGroup.getId(), uid);
            AssertUtil.isTrue(isLord , GroupErrorEnum.NOT_ALLOWED_FOR_REMOVE);
        }
        // 普通成员 判断是否有权限操作
        AssertUtil.isTrue(hasPower(self) , GroupErrorEnum.NOT_ALLOWED_FOR_REMOVE);
        GroupMember member = groupMemberDao.getMember(roomGroup.getId(), removedUid);
        AssertUtil.isNotEmpty(member , "用户已经移除");
        groupMemberDao.removeById(member.getId());
        // 发送移除事件告知群成员
        List<Long> memberUidList = groupMemberCache.getMemberUidList(roomGroup.getRoomId());
        WSBaseResp<WSMemberChange> ws = MemberAdapter.buildMemberRemoveWS(roomGroup.getRoomId(), member.getUid());
        pushService.sendPushMsg(ws, memberUidList);
        groupMemberCache.evictMemberUidList(room.getId());
    }

    @Override
    @Transactional
    public Long addGroup(Long uid, GroupAddReq request) {
        // 添加群成员入群，但是因为每个群成员只能创建一个群
        RoomGroup roomGroup = roomService.createGroupRoom(uid);
        // 批量保存群成员
        List<GroupMember> groupMembers = RoomAdapter.buildGroupMemberBatch(request.getUidList(), roomGroup.getId());
        groupMemberDao.saveBatch(groupMembers);
        // 发送邀请加群消息==> 触发每个人的会话
        applicationEventPublisher.publishEvent(new GroupMemberAddEvent(this, roomGroup, groupMembers, uid));
        return roomGroup.getRoomId();
    }

    @Override
    @RedissonLock(key = "#request.roomId")
    @Transactional(rollbackFor = Exception.class)
    public void addMember(Long uid, MemberAddReq request) {
        Room room = roomCache.get(request.getRoomId());
        AssertUtil.isNotEmpty(room, "房间号有误");
        AssertUtil.isFalse(isHotGroup(room), "全员群无需邀请好友");
        RoomGroup roomGroup = roomGroupCache.get(request.getRoomId());
        AssertUtil.isNotEmpty(roomGroup, "房间号有误");

        GroupMember self = groupMemberDao.getMember(roomGroup.getId(), uid);
        AssertUtil.isNotEmpty(self, "您不是群成员");
        // 批量查询 群成员列表——查询的是被邀请的uid列表里面的群成员
        List<Long> memberBatch = groupMemberDao.getMemberBatch(roomGroup.getId(), request.getUidList());
        Set<Long> existUid = new HashSet<>(memberBatch);
        List<Long> waitAddUidList = request.getUidList().stream().filter(a -> !existUid.contains(a)).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(waitAddUidList)) {
            return;
        }
        // 建造者模式构建实体列表
        List<GroupMember> groupMembers = MemberAdapter.buildMemberAdd(roomGroup.getId(), waitAddUidList);
        groupMemberDao.saveBatch(groupMembers);
        // 发送群成员邀请事件
        applicationEventPublisher.publishEvent(new GroupMemberAddEvent(this, roomGroup, groupMembers, uid));
    }

    @Override
    @Cacheable(cacheNames = "member", key = "'memberList.'+#request.roomId")
    public List<ChatMemberListResp> getMemberList(ChatMessageMemberReq request) {
        Room room = roomCache.get(request.getRoomId());
        AssertUtil.isNotEmpty(room, "房间号有误");
        if (isHotGroup(room)) {// 全员群展示所有用户100名 因为全员群只有一个 不需要传入参数
            List<User> memberList = userDao.getMemberList();
            return MemberAdapter.buildMemberList(memberList);
        } else {
            RoomGroup roomGroup = roomGroupCache.get(request.getRoomId());
            AssertUtil.isNotEmpty(roomGroup , "群聊房间错误");
            List<Long> memberUidList = groupMemberDao.getMemberUidList(roomGroup.getId());
            Map<Long, User> batch = userInfoCache.getBatch(memberUidList);
            return MemberAdapter.buildMemberList(batch);
        }
    }

    private boolean hasPower(GroupMember self) {
        return Objects.equals(self.getRole() , GroupRoleEnum.LEADER.getType())
                || Objects.equals(self.getRole() , GroupRoleEnum.MANAGER.getType())
                || roleService.hasPower(self.getUid() , RoleEnum.ADMIN);
    }


    private GroupRoleAPPEnum getGroupRole(Long uid, RoomGroup roomGroup, Room room) {
        // 查询群聊成员
        GroupMember member = Objects.isNull(uid) ? null : groupMemberDao.getMember(roomGroup.getId() , uid);
        // 获取群聊成员角色信息
        if(Objects.nonNull(member)) {
            return GroupRoleAPPEnum.of(member.getRole());
        } else if(isHotGroup(room)) {
            // 热点群聊普通成员
            return GroupRoleAPPEnum.MEMBER;
        } else {
            return GroupRoleAPPEnum.REMOVE;
        }
    }

    private boolean isHotGroup(Room room) {
        return HotFlagEnum.YES.getType().equals(room.getHotFlag());
    }

    @NotNull
    private List<ChatRoomResp> buildContactResp(Long uid, List<Long> roomIds) {
        // 表情和头像
        Map<Long, RoomBaseInfo> roomBaseInfoMap = getRoomBaseInfoMap(roomIds, uid);
        // 最后一条消息
        List<Long> msgIds = roomBaseInfoMap.values().stream().map(RoomBaseInfo::getLastMsgId).collect(Collectors.toList());
        List<Message> messages = CollectionUtil.isEmpty(msgIds) ? new ArrayList<>() :  messageDao.listByIds(msgIds);
        Map<Long, Message> msgMap = messages.stream().collect(Collectors.toMap(Message::getId, Function.identity()));
        Map<Long, User> lastMsgUidMap = userInfoCache.getBatch(messages.stream().map(Message::getFromUid).collect(Collectors.toList()));
        // 消息未读数
        Map<Long, Integer> unReadCountMap = getUnReadCountMap(uid, roomIds);
        return roomBaseInfoMap.values().stream().map(room -> {
                    ChatRoomResp resp = new ChatRoomResp();
                    RoomBaseInfo roomBaseInfo = roomBaseInfoMap.get(room.getRoomId());
                    resp.setAvatar(roomBaseInfo.getAvatar());
                    resp.setRoomId(room.getRoomId());
                    resp.setActiveTime(room.getActiveTime());
                    resp.setHot_Flag(roomBaseInfo.getHotFlag());
                    resp.setType(roomBaseInfo.getType());
                    resp.setName(roomBaseInfo.getName());
                    Message message = msgMap.get(room.getLastMsgId());
                    if (Objects.nonNull(message)) {
                        AbstractMsgHandler strategyNoNull = MsgHandlerFactory.getStrategyNoNull(message.getType());
                        resp.setText(lastMsgUidMap.get(message.getFromUid()).getName() + ":" + strategyNoNull.showContactMsg(message));
                    }
                    resp.setUnreadCount(unReadCountMap.getOrDefault(room.getRoomId(), 0));
                    return resp;
                }).sorted(Comparator.comparing(ChatRoomResp::getActiveTime).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 获取消息未读数
     */
    private Map<Long, Integer> getUnReadCountMap(Long uid, List<Long> roomIds) {
        if (Objects.isNull(uid)) {
            return new HashMap<>();
        }
        List<Contact> contacts = contactDao.getByRoomIds(roomIds, uid);
        return contacts.parallelStream()
                .map(contact -> Pair.of(contact.getRoomId(), messageDao.getUnReadCount(contact.getRoomId(), contact.getReadTime())))
                .collect(Collectors.toMap(Pair::getKey , Pair::getValue));
    }

    private Map<Long, RoomBaseInfo> getRoomBaseInfoMap(List<Long> roomIds, Long uid) {
        Map<Long, Room> roomMap = roomCache.getBatch(roomIds);
        // 房间根据好友和群组类型分组
        Map<Integer, List<Long>> groupRoomIdMap = roomMap.values().stream().collect(Collectors.groupingBy(Room::getType,
                Collectors.mapping(Room::getId, Collectors.toList())));
        // 获取群组信息
        List<Long> groupRoomId = groupRoomIdMap.get(RoomTypeEnum.GROUP.getType());
        Map<Long, RoomGroup> roomInfoBatch = roomGroupCache.getBatch(groupRoomId);
        // 获取好友信息
        List<Long> friendRoomId = groupRoomIdMap.get(RoomTypeEnum.FRIEND.getType());
        Map<Long, User> friendRoomMap = getFriendRoomMap(friendRoomId, uid);

        return roomMap.values().stream().map(room -> {
            RoomBaseInfo roomBaseInfo = new RoomBaseInfo();
            roomBaseInfo.setRoomId(room.getId());
            roomBaseInfo.setType(room.getType());
            roomBaseInfo.setHotFlag(room.getHotFlag());
            roomBaseInfo.setLastMsgId(room.getLastMsgId());
            roomBaseInfo.setActiveTime(room.getActiveTime());
            if (RoomTypeEnum.of(room.getType()) == RoomTypeEnum.GROUP) {
                RoomGroup roomGroup = roomInfoBatch.get(room.getId());
                roomBaseInfo.setName(roomGroup.getName());
                roomBaseInfo.setAvatar(roomGroup.getAvatar());
            } else if (RoomTypeEnum.of(room.getType()) == RoomTypeEnum.FRIEND) {
                User user = friendRoomMap.get(room.getId());
                roomBaseInfo.setName(user.getName());
                roomBaseInfo.setAvatar(user.getAvatar());
            }
            return roomBaseInfo;
        }).collect(Collectors.toMap(RoomBaseInfo::getRoomId, Function.identity()));
    }

    private Map<Long, User> getFriendRoomMap(List<Long> roomIds, Long uid) {
        if (CollectionUtil.isEmpty(roomIds)) {
            return new HashMap<>();
        }
        Map<Long, RoomFriend> roomFriendMap = roomFriendCache.getBatch(roomIds);
        Set<Long> friendUidSet = ChatAdapter.getFriendUidSet(roomFriendMap.values(), uid);
        Map<Long, User> userBatch = userInfoCache.getBatch(new ArrayList<>(friendUidSet));
        return roomFriendMap.values()
                .stream()
                .collect(Collectors.toMap(RoomFriend::getRoomId, roomFriend -> {
                    Long friendUid = ChatAdapter.getFriendUid(roomFriend, uid);
                    return userBatch.get(friendUid);
                }));
    }

    private List<Contact> buildContact(List<Pair<Long, Double>> list, Long uid) {
        List<Long> roomIds = list.stream().map(Pair::getKey).collect(Collectors.toList());
        Map<Long, Room> batch = roomCache.getBatch(roomIds);
        Map<Long, Contact> contactMap = new HashMap<>();
        if (Objects.nonNull(uid)) {
            List<Contact> byRoomIds = contactDao.getByRoomIds(roomIds, uid);
            contactMap = byRoomIds.stream().collect(Collectors.toMap(Contact::getRoomId, Function.identity()));
        }
        Map<Long, Contact> finalContactMap = contactMap;
        return list.stream().map(pair -> {
            Long roomId = pair.getKey();
            Contact contact = finalContactMap.get(roomId);
            if (Objects.isNull(contact)) {
                contact = new Contact();
                contact.setRoomId(pair.getKey());
                Room room = batch.get(roomId);
                contact.setLastMsgId(room.getLastMsgId());
            }
            contact.setActiveTime(new Date(pair.getValue().longValue()));
            return contact;
        }).collect(Collectors.toList());
    }


}
