package com.liaocyu.openChat.common.user.service.impl;

import com.liaocyu.openChat.common.user.domain.enums.IdempotentEnum;
import com.liaocyu.openChat.common.user.domain.enums.ItemEnum;
import com.liaocyu.openChat.common.user.service.IUserBackpackService;
import com.liaocyu.openChat.common.user.service.LoginService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/25 11:30
 * @description :
 */
@SpringBootTest
public class UserBackpackServiceImplTest {

    private final LoginService loginService;
    private final IUserBackpackService userBackpackService;

    @Autowired
    public UserBackpackServiceImplTest(LoginService loginService ,
                                       IUserBackpackService userBackpackService) {
        this.loginService = loginService;
        this.userBackpackService = userBackpackService;
    }

    @Test
    public void acquireItem() {
        userBackpackService.acquireItem(10443L , ItemEnum.PLANET.getId() , IdempotentEnum.UID , "10443");

    }



}