package com.liaocyu.openChat.common.common.event.listener;

import com.liaocyu.openChat.common.common.event.UserBlackEvent;
import com.liaocyu.openChat.common.common.event.UserOnlineEvent;
import com.liaocyu.openChat.common.common.event.UserRegisterEvent;
import com.liaocyu.openChat.common.user.dao.UserDao;
import com.liaocyu.openChat.common.user.domain.entity.User;
import com.liaocyu.openChat.common.user.domain.enums.UserActiveStatusEnum;
import com.liaocyu.openChat.common.user.service.IpService;
import com.liaocyu.openChat.common.websocket.service.WebSocketService;
import com.liaocyu.openChat.common.websocket.service.adapter.WebSocketAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/25 17:24
 * @description :
 * 用户监听事件应用程序
 * @see com.liaocyu.openChat.common.common.event.UserBlackEvent
 * 用户注册监听 - 消费者
 */
@Component
public class UserBlackListener {

    private final WebSocketService webSocketService;
    private final UserDao userDao;

    @Autowired
    public UserBlackListener(WebSocketService webSocketService , UserDao userDao) {
        this.webSocketService = webSocketService;
        this.userDao = userDao;
    }

    /**
     * fallbackExecution = true
     * 这样配置 能够让没有进入事务时也能够生效
     * 给前端返回拉黑用户的信息
     * @param event 相应的事件
     */
    @Async
    @TransactionalEventListener(classes = UserBlackEvent.class , phase = TransactionPhase.AFTER_COMMIT , fallbackExecution = true)
    public void sendMsg(UserBlackEvent event) {
        User user = event.getUser();
        webSocketService.sendMsgToAll(WebSocketAdapter.buildBlack(user));
    }

    @Async
    @TransactionalEventListener(classes = UserBlackEvent.class , phase = TransactionPhase.AFTER_COMMIT , fallbackExecution = true)
    public void changeUserStatus(UserBlackEvent event) {
        userDao.invalidUid(event.getUser().getId());
    }



}
