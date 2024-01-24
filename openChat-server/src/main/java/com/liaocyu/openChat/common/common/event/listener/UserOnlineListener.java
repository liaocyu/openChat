package com.liaocyu.openChat.common.common.event.listener;

import com.liaocyu.openChat.common.common.event.UserOnlineEvent;
import com.liaocyu.openChat.common.common.event.UserRegisterEvent;
import com.liaocyu.openChat.common.user.dao.UserDao;
import com.liaocyu.openChat.common.user.domain.entity.User;
import com.liaocyu.openChat.common.user.domain.enums.UserActiveStatusEnum;
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
 *
 *
 * @TransactionalEventListener: 这个注解标识 saveDB() 方法作为一个事务事件的监听者,配置了以下属性：
 *      classes: 指定要监听的事件类型，这里是 UserOnlineEvent。
 *      phase: 指定监听器的执行阶段，这里是 TransactionPhase.AFTER_COMMIT，表示在事务提交后执行。
 *          注：这里使用的是 TransactionPhase.AFTER_COMMIT ,因为本身这里使用的事务监听的形式，确保在事务提交成功后在执行监听器方法
 *          如果事务在提交之前发生回滚，监听器方法将不会执行，从而避免了在可能会回滚的事务中执行一些不必要的操作
 *          如果将监听器方法配置为在事务提交前执行（例如，使用 TransactionPhase.BEFORE_COMMIT 或默认值 TransactionPhase.DURING)
 *          可能会导致数据不一致性：如果监听器方法在事务提交前执行，而后续事务发生了回滚，那么监听器方法中对数据库的操作将不会回滚，导致数据不一致性。
 *          依赖不完整：如果监听器方法在事务提交前执行，并且依赖其他事务中的数据或操作结果，但后续事务发生了回滚，那么监听器方法可能会使用不完整或不准确的数据，导致错误的结果。
 *      fallbackExecution: 如果监听器执行失败，是否回退到同步执行的方式。
 *
 * @Async: 这个注解表示 saveDB() 方法将在一个单独的线程中异步执行。这可以提高性能，使方法在后台执行而不会阻塞主线程。
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
