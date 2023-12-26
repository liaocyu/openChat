package com.liaocyu.openChat.common.common.event.listener;

import com.liaocyu.openChat.common.common.event.UserRegisterEvent;
import com.liaocyu.openChat.common.user.dao.UserDao;
import com.liaocyu.openChat.common.user.domain.entity.User;
import com.liaocyu.openChat.common.user.domain.enums.IdempotentEnum;
import com.liaocyu.openChat.common.user.domain.enums.ItemEnum;
import com.liaocyu.openChat.common.user.service.IUserBackpackService;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
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
 * @see UserRegisterEvent
 * 用户注册监听 - 消费者
 */
@Component
public class UserRegisterListener {

    private final IUserBackpackService userBackpackService;
    private final UserDao userDao;

    @Autowired
    public UserRegisterListener(IUserBackpackService userBackpackService , UserDao userDao) {
        this.userBackpackService = userBackpackService;
        this.userDao = userDao;
    }

    /**
     * 给新注册的用户发放改名卡
     * @param event 用户注册事件
     */
    // @EventListener(classes = UserRegisterEvent.class)
    @Async
    @TransactionalEventListener(classes = UserRegisterEvent.class , phase = TransactionPhase.AFTER_COMMIT)
    public void sendCard(UserRegisterEvent event) {
        User user = event.getUser();
        // 监听用户注册事件
        // 给用户发一张改名卡
        userBackpackService.acquireItem(user.getId(), ItemEnum.MODIFY_NAME_CARD.getId(), IdempotentEnum.UID, user.getId().toString());
    }

    /**
     * 给前百名注册的用户注册徽章
     * @param event 用户注册事件
     */
    // @EventListener(classes = UserRegisterEvent.class)
    @Async
    @TransactionalEventListener(classes = UserRegisterEvent.class , phase = TransactionPhase.AFTER_COMMIT)
    public void sendBadge(UserRegisterEvent event) {
        User user = event.getUser();
        int registeredCount = userDao.count();
        if(registeredCount < 10) {
            userBackpackService.acquireItem(user.getId() , ItemEnum.REG_TOP10_BADGE.getId(), IdempotentEnum.UID , user.getId().toString());
        } else if (registeredCount < 100){
            userBackpackService.acquireItem(user.getId() , ItemEnum.REG_TOP100_BADGE.getId(), IdempotentEnum.UID , user.getId().toString());
        }
    }



}
