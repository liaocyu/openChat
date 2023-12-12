package com.liaocyu.openChat.common.user.service;

import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/12 10:32
 * @description :
 */
public interface WXMsgServeice {
    /**
     * 用户扫码
     *
     * @param wxMpXmlMessage
     * @return
     */
    WxMpXmlOutMessage scan(WxMpXmlMessage wxMpXmlMessage);

    /**
     * 用户授权
     * @param userInfo
     */
    void authorize(WxOAuth2UserInfo userInfo);
}
