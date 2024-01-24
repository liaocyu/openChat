package com.liaocyu.openChat.common.common.event.listener;

import com.liaocyu.openChat.common.chat.service.impl.PushService;
import com.liaocyu.openChat.common.common.event.UserApplyEvent;
import com.liaocyu.openChat.common.user.dao.UserApplyDao;
import com.liaocyu.openChat.common.user.domain.entity.UserApply;
import com.liaocyu.openChat.common.user.service.adapter.WSAdapter;
import com.liaocyu.openChat.common.websocket.domian.vo.resp.ws.WSFriendApply;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/22 14:54
 * @description :
 */
@Component("userApplyListener")
@Slf4j
public class UserApplyListener {

    private final UserApplyDao userApplyDao;
    private final PushService pushService;

    @Autowired
    public UserApplyListener(UserApplyDao userApplyDao , PushService pushService) {
        this.userApplyDao = userApplyDao;
        this.pushService = pushService;
    }

    @Async
    @TransactionalEventListener(classes = UserApplyEvent.class, fallbackExecution = true)
    public void notifyFriend(UserApplyEvent event) {
        UserApply userApply = event.getUserApply();
        Integer unReadCount = userApplyDao.getUnReadCount(userApply.getTargetId());
        pushService.sendPushMsg(WSAdapter.buildApplySend(new WSFriendApply(userApply.getUid(), unReadCount)), userApply.getTargetId());
    }
}
