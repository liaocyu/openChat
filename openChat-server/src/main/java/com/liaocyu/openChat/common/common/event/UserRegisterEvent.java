package com.liaocyu.openChat.common.common.event;

import com.liaocyu.openChat.common.user.domain.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/25 17:18
 * @description :
 * 用户注册事件
 */
@Getter
public class UserRegisterEvent extends ApplicationEvent {
    private User user;

    public UserRegisterEvent(Object source , User user) {
        super(source);
        this.user = user;
    }
}
