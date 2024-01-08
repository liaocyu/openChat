package com.liaocyu.openChat.common.common.event;

import com.liaocyu.openChat.common.user.domain.entity.UserApply;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/8 10:42
 * @description :
 */
@Getter
public class UserApplyEvent extends ApplicationEvent {

    private UserApply userApply;
    public UserApplyEvent(Object source , UserApply userApply) {
        super(source);
        this.userApply = userApply;
    }
}
