package com.liaocyu.openChat.common.chat.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import com.liaocyu.openChat.common.chat.dao.*;
import com.liaocyu.openChat.common.chat.domain.entity.*;
import com.liaocyu.openChat.common.chat.domain.vo.req.ChatMessageMarkReq;
import com.liaocyu.openChat.common.chat.domain.vo.req.ChatMessagePageReq;
import com.liaocyu.openChat.common.chat.domain.vo.req.ChatMessageReq;
import com.liaocyu.openChat.common.chat.domain.vo.req.member.MemberReq;
import com.liaocyu.openChat.common.chat.domain.vo.resp.ChatMemberResp;
import com.liaocyu.openChat.common.chat.domain.vo.resp.ChatMessageResp;
import com.liaocyu.openChat.common.chat.service.ChatService;
import com.liaocyu.openChat.common.chat.service.adapter.MemberAdapter;
import com.liaocyu.openChat.common.chat.service.adapter.MessageAdapter;
import com.liaocyu.openChat.common.chat.service.cache.RoomCache;
import com.liaocyu.openChat.common.chat.service.cache.RoomGroupCache;
import com.liaocyu.openChat.common.chat.service.helper.ChatMemberHelper;
import com.liaocyu.openChat.common.chat.service.strategy.AbstractMsgHandler;
import com.liaocyu.openChat.common.chat.service.strategy.msg.MsgHandlerFactory;
import com.liaocyu.openChat.common.common.annotation.RedissonLock;
import com.liaocyu.openChat.common.common.domain.enums.NormalOrNoEnum;
import com.liaocyu.openChat.common.common.domain.vo.req.CursorPageBaseReq;
import com.liaocyu.openChat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.liaocyu.openChat.common.common.event.MessageSendEvent;
import com.liaocyu.openChat.common.common.utils.AssertUtil;
import com.liaocyu.openChat.common.user.dao.UserDao;
import com.liaocyu.openChat.common.user.domain.entity.User;
import com.liaocyu.openChat.common.websocket.domian.enums.ChatActiveStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/15 15:57
 * @description : 消息处理类
 */
@Service("chatService")
@Slf4j
public class ChatServiceImpl implements ChatService {
    // 系统默认展示房间号
    public static final long ROOM_GROUP_ID = 1L;

    private final UserDao userDao;
    private final RoomGroupDao roomGroupDao;
    private final GroupMemberDao groupMemberDao;
    private final MessageMarkDao messageMarkDao;
    private final MessageDao messageDao;
    private final RoomCache roomCache;
    private final ContactDao contactDao;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final RoomFriendDao roomFriendDao;
    private final RoomGroupCache roomGroupCache;

    @Autowired
    public ChatServiceImpl(UserDao userDao , RoomGroupDao roomGroupDao ,
                           GroupMemberDao groupMemberDao , MessageMarkDao messageMarkDao ,
                           MessageDao messageDao , RoomCache roomCache ,
                           ContactDao contactDao , ApplicationEventPublisher applicationEventPublisher ,
                           RoomFriendDao roomFriendDao , RoomGroupCache roomGroupCache) {
        this.userDao = userDao;
        this.roomGroupDao = roomGroupDao;
        this.groupMemberDao = groupMemberDao ;
        this.messageMarkDao = messageMarkDao ;
        this.messageDao = messageDao;
        this.roomCache = roomCache;
        this.contactDao = contactDao;
        this.applicationEventPublisher = applicationEventPublisher;
        this.roomFriendDao = roomFriendDao;
        this.roomGroupCache = roomGroupCache;
    }


    /**
     * 获取群成员列表
     *
     * @param memberUidList 群成员 Uid 列表
     * @param request 群成员接口请求
     * @return
     */
    @Override
    public CursorPageBaseResp<ChatMemberResp> getMemberPage(List<Long> memberUidList, MemberReq request) {
        // request.getCursor() 请求体当中的游标
        Pair<ChatActiveStatusEnum, String> pair = ChatMemberHelper.getCursorPair(request.getCursor()); // 返回形式 ： Pair [key=ONLINE,value=1698332821589]
        ChatActiveStatusEnum activeStatusEnum = pair.getKey();//  "ONLINE"
        String timeCursor = pair.getValue(); // 时间戳
        List<ChatMemberResp> resultList = new ArrayList<>();// 构造返回列表
        Boolean isLast = Boolean.FALSE;// 先默认不是最后一页
        if (activeStatusEnum == ChatActiveStatusEnum.ONLINE) {//在线列表
            CursorPageBaseResp<User> cursorPage = userDao.getCursorPage(memberUidList, new CursorPageBaseReq(request.getPageSize(), timeCursor), ChatActiveStatusEnum.ONLINE);
            resultList.addAll(MemberAdapter.buildMember(cursorPage.getList()));//添加在线列表
            if (cursorPage.getIsLast()) {//如果是最后一页,从离线列表再补点数据
                activeStatusEnum = ChatActiveStatusEnum.OFFLINE;
                Integer leftSize = request.getPageSize() - cursorPage.getList().size();
                cursorPage = userDao.getCursorPage(memberUidList, new CursorPageBaseReq(leftSize, null), ChatActiveStatusEnum.OFFLINE);
                resultList.addAll(MemberAdapter.buildMember(cursorPage.getList()));//添加离线线列表
            }
            timeCursor = cursorPage.getCursor();
            isLast = cursorPage.getIsLast();
        } else if (activeStatusEnum == ChatActiveStatusEnum.OFFLINE) {//离线列表
            CursorPageBaseResp<User> cursorPage = userDao.getCursorPage(memberUidList, new CursorPageBaseReq(request.getPageSize(), timeCursor), ChatActiveStatusEnum.OFFLINE);
            resultList.addAll(MemberAdapter.buildMember(cursorPage.getList()));//添加离线线列表
            timeCursor = cursorPage.getCursor();
            isLast = cursorPage.getIsLast();
        }
        // 获取群成员角色ID
        List<Long> uidList = resultList.stream().map(ChatMemberResp::getUid).collect(Collectors.toList());
        RoomGroup roomGroup = roomGroupDao.getByRoomId(request.getRoomId());
        Map<Long, Integer> uidMapRole = groupMemberDao.getMemberMapRole(roomGroup.getId(), uidList);
        resultList.forEach(member -> member.setRoleId(uidMapRole.get(member.getUid())));
        //组装结果
        return new CursorPageBaseResp<>(ChatMemberHelper.generateCursor(activeStatusEnum, timeCursor), isLast, resultList);
    }

    @Override
    public ChatMessageResp getMsgResp(Long msgId, Long receiveUid) {
        return null;
    }

    @Override
    public ChatMessageResp getMsgResp(Message message, Long receiveUid) {
        return CollUtil.getFirst(getMsgRespBatch(Collections.singletonList(message), receiveUid));
    }

    @Override
    public CursorPageBaseResp<ChatMessageResp> getMsgPage(ChatMessagePageReq request, @Nullable Long receiveUid) {

        Long lastMsgId = getLastMsgId(request.getRoomId(), receiveUid);
        CursorPageBaseResp<Message> cursorPage = messageDao.getCursorPage(request.getRoomId(), request, lastMsgId);
        if (cursorPage.isEmpty()) {
            return CursorPageBaseResp.empty();
        }
        return CursorPageBaseResp.init(cursorPage, getMsgRespBatch(cursorPage.getList(), receiveUid));
    }

    /**
     * 发送消息
     * @param request
     * @param uid
     * @return
     */
    @Override
    @Transactional
    public Long sendMsg(ChatMessageReq request, Long uid) {
        check(request, uid); // 检查发送信息是否正常
        AbstractMsgHandler<?> msgHandler = MsgHandlerFactory.getStrategyNoNull(request.getMsgType()); // 获得具体的消息处理类
        Long msgId = msgHandler.checkAndSaveMsg(request, uid);
        //发布消息发送事件
        applicationEventPublisher.publishEvent(new MessageSendEvent(this, msgId));
        return msgId;
    }

    @Override
    @RedissonLock(key = "#uid")
    public void setMsgMark(Long uid, ChatMessageMarkReq request) {
        // TODO 设置消息标记
        /*AbstractMsgMarkStrategy strategy = MsgMarkFactory.getStrategyNoNull(request.getMarkType());
        switch (MessageMarkActTypeEnum.of(request.getActType())) {
            case MARK:
                strategy.mark(uid, request.getMsgId());
                break;
            case UN_MARK:
                strategy.unMark(uid, request.getMsgId());
                break;
        }*/
    }

    private void check(ChatMessageReq request, Long uid) {
        Room room = roomCache.get(request.getRoomId());
        if (room.isHotRoom()) {//全员群跳过校验
            return;
        }
        if (room.isRoomFriend()) { // 单聊群
            RoomFriend roomFriend = roomFriendDao.getByRoomId(request.getRoomId());
            AssertUtil.isNotEmpty(roomFriend , "房间不存在"); // TODO检验房间是否合理
            AssertUtil.equal(NormalOrNoEnum.NORMAL.getStatus(), roomFriend.getStatus(), "您已经被对方拉黑");
            AssertUtil.isTrue(uid.equals(roomFriend.getUid1()) || uid.equals(roomFriend.getUid2()), "您已经被对方拉黑");
        }
        if (room.isRoomGroup()) { // 群聊
            RoomGroup roomGroup = roomGroupCache.get(request.getRoomId());
            AssertUtil.isNotEmpty(roomGroup , "群聊房间不存在");
            GroupMember member = groupMemberDao.getMember(roomGroup.getId(), uid);
            AssertUtil.isNotEmpty(member, "您已经被移除该群");
        }

    }

    private Long getLastMsgId(Long roomId, Long receiveUid) {
        Room room = roomCache.get(roomId);
        AssertUtil.isNotEmpty(room, "房间号有误");
        if (room.isHotRoom()) {
            return null;
        }
        AssertUtil.isNotEmpty(receiveUid, "请先登录");
        Contact contact = contactDao.get(receiveUid, roomId);
        return contact.getLastMsgId();
    }

    public List<ChatMessageResp> getMsgRespBatch(List<Message> messages, Long receiveUid) {
        if (CollectionUtil.isEmpty(messages)) {
            return new ArrayList<>();
        }
        //查询消息标志
        List<MessageMark> msgMark = messageMarkDao.getValidMarkByMsgIdBatch(messages.stream().map(Message::getId).collect(Collectors.toList()));
        return MessageAdapter.buildMsgResp(messages, msgMark, receiveUid);
    }
}
