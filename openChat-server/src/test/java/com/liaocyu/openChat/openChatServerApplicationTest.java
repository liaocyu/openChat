package com.liaocyu.openChat;

import com.liaocyu.openChat.common.user.dao.UserDao;
import com.liaocyu.openChat.common.user.domain.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/8 23:09
 * @description :
 */
@SpringBootTest
class openChatServerApplicationTest {
    @Autowired
    private UserDao userDao;

    @Test
    void daoTest() {
        User byId = userDao.getById(1L);
        System.out.println(byId);
    }
}