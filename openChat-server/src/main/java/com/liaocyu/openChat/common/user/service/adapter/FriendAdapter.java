package com.liaocyu.openChat.common.user.service.adapter;

import com.liaocyu.openChat.common.user.domain.entity.User;
import com.liaocyu.openChat.common.user.domain.entity.UserApply;
import com.liaocyu.openChat.common.user.domain.entity.UserFriend;
import com.liaocyu.openChat.common.user.domain.vo.req.friend.FriendApplyReq;
import com.liaocyu.openChat.common.user.domain.vo.resp.FriendApplyResp;
import com.liaocyu.openChat.common.user.domain.vo.resp.FriendResp;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.liaocyu.openChat.common.user.domain.enums.ApplyReadStatusEnum.UNREAD;
import static com.liaocyu.openChat.common.user.domain.enums.ApplyStatusEnum.WAIT_APPROVAL;
import static com.liaocyu.openChat.common.user.domain.enums.ApplyTypeEnum.ADD_FRIEND;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/4 10:08
 * @description :
 */
public class FriendAdapter {

    public static UserApply buildFriendApply(Long uid, FriendApplyReq request) {
        UserApply userApplyNew = new UserApply();
        userApplyNew.setUid(uid);
        userApplyNew.setMsg(request.getMsg());
        userApplyNew.setType(ADD_FRIEND.getCode()); // 申请类型 - 加好友
        userApplyNew.setTargetId(request.getTargetUid());
        userApplyNew.setStatus(WAIT_APPROVAL.getCode()); // 申请状态 - 待审批
        userApplyNew.setReadStatus(UNREAD.getCode()); // 审批状态 - 未读
        return userApplyNew;
    }

    public static List<FriendResp> buildFriend(List<UserFriend> list, List<User> userList) {
        Map<Long, User> userMap = userList.stream().collect(Collectors.toMap(User::getId, user -> user));
        return list.stream().map(userFriend -> {
            FriendResp resp = new FriendResp();
            resp.setUid(userFriend.getFriendUid());
            User user = userMap.get(userFriend.getFriendUid());
            if (Objects.nonNull(user)) {
                resp.setActiveStatus(user.getActiveStatus());
            }
            return resp;
        }).collect(Collectors.toList());
    }


    public static List<FriendApplyResp> buildFriendApplyList(List<UserApply> records) {
        return records.stream().map(userApply -> {
            FriendApplyResp friendApplyResp = new FriendApplyResp();
            friendApplyResp.setUid(userApply.getUid());
            friendApplyResp.setType(userApply.getType());
            friendApplyResp.setApplyId(userApply.getId());
            friendApplyResp.setMsg(userApply.getMsg());
            friendApplyResp.setStatus(userApply.getStatus());
            return friendApplyResp;
        }).collect(Collectors.toList());

    }
}
