package com.liaocyu.openChat.common.common.event.listener;

import com.liaocyu.openChat.common.common.event.UserOnlineEvent;
import com.liaocyu.openChat.common.common.event.UserRegisterEvent;
import com.liaocyu.openChat.common.user.dao.UserDao;
import com.liaocyu.openChat.common.user.domain.entity.User;
import com.liaocyu.openChat.common.user.domain.enums.IdempotentEnum;
import com.liaocyu.openChat.common.user.domain.enums.ItemEnum;
import com.liaocyu.openChat.common.user.domain.enums.UserActiveStatusEnum;
import com.liaocyu.openChat.common.user.service.IUserBackpackService;
import com.liaocyu.openChat.common.user.service.IpService;
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
 * @see UserRegisterEvent
 * 用户注册监听 - 消费者
 */
@Component
public class UserOnlineListener {

    private final IpService ipService;
    private final UserDao userDao;

    @Autowired
    public UserOnlineListener(IpService ipService , UserDao userDao) {
        this.ipService = ipService;
        this.userDao = userDao;
    }

    @Async
    @TransactionalEventListener(classes = UserOnlineEvent.class , phase = TransactionPhase.AFTER_COMMIT , fallbackExecution = true)
    public void saveDB(UserOnlineEvent event) {
        User user = event.getUser();
        User update = new User();
        update.setId(user.getId());
        update.setLastOptTime(user.getLastOptTime());
        update.setIpInfo(user.getIpInfo());
        update.setActiveStatus(UserActiveStatusEnum.ONLINE.getStatus());
        userDao.updateById(update);
        // 用户ip详情的解析
        ipService.refreshDetailAsync(user.getId());
    }



}
