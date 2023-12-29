package com.liaocyu.openChat.common.common.event;

import com.liaocyu.openChat.common.user.domain.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/26 15:42
 * @description :
 * 拉黑用户事件
 */
@Getter
public class UserBlackEvent extends ApplicationEvent {

    private User user;

    public UserBlackEvent(Object source , User user) {
        super(source);
        this.user = user ;
    }
}
