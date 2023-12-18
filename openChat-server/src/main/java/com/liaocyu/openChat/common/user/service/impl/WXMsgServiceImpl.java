package com.liaocyu.openChat.common.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.liaocyu.openChat.common.user.dao.UserDao;
import com.liaocyu.openChat.common.user.domain.entity.User;
import com.liaocyu.openChat.common.user.service.UserService;
import com.liaocyu.openChat.common.user.service.WXMsgServeice;
import com.liaocyu.openChat.common.user.service.adapter.TextBuilder;
import com.liaocyu.openChat.common.user.service.adapter.UserAdapter;
import com.liaocyu.openChat.common.websocket.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/12 10:31
 * @description :
 */
@Service
@Slf4j
public class WXMsgServiceImpl implements WXMsgServeice {

    @Autowired
    private WebSocketService webSocketService;
    /**
     * openID 和登录 code 的关系 map
     */
    public static final ConcurrentHashMap<String , Integer> WAIT_AUTHORIZE_MAP = new ConcurrentHashMap<>();
    @Value("${wx.mp.callback}")
    private String callback; // 扫码成功回调地址

    private static final  String URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";


    @Autowired
    UserDao userDao;

    @Autowired
    UserService userService;

    @Autowired
    @Lazy
    WxMpService wxMpService;


    @Override
    public WxMpXmlOutMessage scan(WxMpXmlMessage wxMpXmlMessage) {
        String openId = wxMpXmlMessage.getFromUser();
        Integer code = getEventKey(wxMpXmlMessage);
        // 说明前端传过来的事件码不符合我们期待之一
        if (Objects.isNull(code)) {
            return null;
        }

        User user = userDao.getByOpenId(openId);
        boolean registered = Objects.nonNull(user);
        boolean authorized = registered && StrUtil.isNotBlank(user.getAvatar());
        // 用户已经注册并授权
        if (registered && authorized) {
            // 走登录成功逻辑 通过code 找到channel 推送消息
            webSocketService.scanLoginSuccess(code , user.getId());
            /*return null;*/
        }
        // 用户没有注册 ， 先注册
        if(!registered) {
            User insert = UserAdapter.buildUserSave(openId);
            userService.register(insert);
        }

        // 推送链接让用户授权
        WAIT_AUTHORIZE_MAP.put(openId , code);
        webSocketService.waitAuthorize(code);
        String authroizeURL = null;
        try {
            authroizeURL = String.format(URL, wxMpService.getWxMpConfigStorage().getAppId(), URLEncoder.encode(callback + "/wx/portal/public/callBack" , "utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        System.out.println(authroizeURL);


        return new TextBuilder().build("请点击链接授权：<a href=\"" + authroizeURL + "\">登录</a>", wxMpXmlMessage);
    }

    @Override
    public void authorize(WxOAuth2UserInfo userInfo) {
        String openid = userInfo.getOpenid();
        User user = userDao.getByOpenId(openid);
        // 更新用户信息
        if(StrUtil.isBlank(user.getAvatar())) {
            fillUserInfo(user.getId() , userInfo);
        }
        // 通过code找到用户 Channel 进行登录
        Integer code = WAIT_AUTHORIZE_MAP.remove(openid);
        webSocketService.scanLoginSuccess(code , user.getId());

    }

    private void fillUserInfo(Long id, WxOAuth2UserInfo userInfo) {
        User user = UserAdapter.buildAuthorizeUser(id, userInfo);
        try {
            // 更新用户新信息
            userDao.updateById(user);
        } catch (DuplicateKeyException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *  处理用户扫码事件和订阅关注事件
     * @param wxMpXmlMessage
     * @return
     */
    private Integer getEventKey(WxMpXmlMessage wxMpXmlMessage) {
        try {
            String eventKey = wxMpXmlMessage.getEventKey();
            String code = eventKey.replace("qrscene_", "");
            return Integer.parseInt(code);
        } catch (NumberFormatException e) {
            log.error("getEventKey error eventKey:{}" , wxMpXmlMessage.getEventKey() , e);
        }
        return null;
    }
}
