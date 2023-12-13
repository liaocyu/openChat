package com.liaocyu.openChat;

import com.liaocyu.openChat.common.common.thread.MyUncaughtExceptionHandler;
import com.liaocyu.openChat.common.user.dao.UserDao;
import com.liaocyu.openChat.common.user.domain.entity.User;
import com.liaocyu.openChat.common.common.utils.JwtUtils;
import com.liaocyu.openChat.common.common.utils.RedisUtils;
import com.liaocyu.openChat.common.user.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/8 23:09
 * @description :
 */
@SpringBootTest
@Slf4j
class openChatServerApplicationTest {
    @Autowired
    private UserDao userDao;

    @Autowired
    WxMpService wxMpService;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    LoginService loginService;

    @Autowired
    ThreadPoolTaskExecutor threadPoolTaskExecutor;


    @Test
    void daoTest() {
        User byId = userDao.getById(1L);
        System.out.println(byId);
    }

    /**
     * 获取临时的二维码
     * @throws WxErrorException
     */
    /*@Test
    void test() throws WxErrorException {
        WxMpQrCodeTicket wxMpQrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(1, 10000);
        String url = wxMpQrCodeTicket.getUrl();
        System.out.println(url);
    }*/

    @Test
    void jwtTest() {
        System.out.println(jwtUtils.createToken(1L));
        System.out.println(jwtUtils.createToken(1L));
        System.out.println(jwtUtils.createToken(1L));
    }

    @Test
    void redisTest() {
        RedisUtils.set("name" , "卷心菜");
        String name = RedisUtils.getStr("name");
        System.out.println(name);
/*
        RedisUtils.get("")*/
    }

    @Test
    void redissonClientTest() {
        RLock lock = redissonClient.getLock("123");
        lock.lock();
        System.out.println();
        lock.unlock();
    }

    @Test
    void getTokenUid() {
        String s = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1aWQiOjEwNDQzLCJjcmVhdGVUaW1lIjoxNzAyMzcyOTk1fQ.jhMpF-gjpMBKr_p1-m-eXp5zZO5kVUvoHeUYPfxwFtg";
        Long validUid = loginService.getValidUid(s);
        System.out.println(validUid);
    }

    @Test
    void thread() throws InterruptedException {
        threadPoolTaskExecutor.execute(() -> {
            if(1==1) {
                log.error("123");
                throw new RuntimeException("1234");
            }
        });

        Thread.sleep(2000);
    }

}