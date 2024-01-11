package com.liaocyu.openChat.common.chat.service.impl;

import com.liaocyu.openChat.common.chat.domain.entity.RoomFriend;
import com.liaocyu.openChat.common.chat.domain.vo.resp.ChatRoomResp;
import com.liaocyu.openChat.common.chat.service.RoomService;
import com.liaocyu.openChat.common.chat.service.RoomAppService;
import com.liaocyu.openChat.common.common.domain.vo.req.CursorPageBaseReq;
import com.liaocyu.openChat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.liaocyu.openChat.common.common.utils.AssertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/10 14:58
 * @description :
 */
@Service("roomAppService")
public class RoomAppServiceImpl implements RoomAppService {

    @Autowired
    private RoomService roomService;

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
        /*CursorPageBaseResp<Long> page;
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
        } else {// 用户未登录，只查全局房间
            CursorPageBaseResp<Pair<Long, Double>> roomCursorPage = hotRoomCache.getRoomCursorPage(request);
            List<Long> roomIds = roomCursorPage.getList().stream().map(Pair::getKey).collect(Collectors.toList());
            page = CursorPageBaseResp.init(roomCursorPage, roomIds);
        }
        // 最后组装会话信息（名称，头像，未读数等）
        List<ChatRoomResp> result = buildContactResp(uid, page.getList());
        return CursorPageBaseResp.init(page, result);*/

        return null;
    }

    @Override
    public ChatRoomResp getContactDetailByFriend(Long uid, Long friendUid) {

        RoomFriend friendRoom = roomService.getFriendRoom(uid, friendUid); // 10443  10003
        AssertUtil.isNotEmpty(friendRoom, "Ta 不是你的好友");
        return buildContactResp(uid, Collections.singletonList(friendRoom.getRoomId())).get(0);
    }

    @NotNull
    private List<ChatRoomResp> buildContactResp(Long uid, List<Long> roomIds) {
        return null;
    }


}
