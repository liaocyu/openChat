package com.liaocyu.openChat.common.user.service.impl;

import com.liaocyu.openChat.common.user.domain.enums.IdempotentEnum;
import com.liaocyu.openChat.common.user.domain.enums.ItemEnum;
import com.liaocyu.openChat.common.user.service.UserBackpackService;
import com.liaocyu.openChat.common.user.service.LoginService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/25 11:30
 * @description :
 */
@SpringBootTest
public class UserBackpackServiceImplTest {

    private final LoginService loginService;
    private final UserBackpackService userBackpackService;

    @Autowired
    public UserBackpackServiceImplTest(LoginService loginService ,
                                       UserBackpackService userBackpackService) {
        this.loginService = loginService;
        this.userBackpackService = userBackpackService;
    }

    @Test
    public void acquireItem() {
        userBackpackService.acquireItem(10443L , ItemEnum.REG_TOP100_BADGE.getId() , IdempotentEnum.UID , "10443");
    }


    @Test
    void getUser() {
        String login = loginService.login(10443L);
        System.out.println(login);
    }



}