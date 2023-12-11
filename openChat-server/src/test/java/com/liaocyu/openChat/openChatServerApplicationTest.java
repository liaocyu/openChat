package com.liaocyu.openChat;

import com.liaocyu.openChat.common.user.dao.UserDao;
import com.liaocyu.openChat.common.user.domain.entity.User;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
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

    @Autowired
    WxMpService wxMpService;

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


}