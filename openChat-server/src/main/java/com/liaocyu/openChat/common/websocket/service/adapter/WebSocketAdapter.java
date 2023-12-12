package com.liaocyu.openChat.common.websocket.service.adapter;

import com.liaocyu.openChat.common.user.domain.entity.User;
import com.liaocyu.openChat.common.websocket.domian.enums.WSRespTypeEnum;
import com.liaocyu.openChat.common.websocket.domian.vo.resp.WSBaseResp;
import com.liaocyu.openChat.common.websocket.domian.vo.resp.ws.WSLoginSuccess;
import com.liaocyu.openChat.common.websocket.domian.vo.resp.ws.WSLoginUrl;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/11 15:47
 * @description :
 * WSBaseResp ： 返回给前端的实体对象
 * @see WSBaseResp
 */
public class WebSocketAdapter {
    public static WSBaseResp<?> buildResp(WxMpQrCodeTicket wxMpQrCodeTicket) {
        // 返回给前端的是一个登录地址
        WSBaseResp<WSLoginUrl> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.LOGIN_URL.getType());
        resp.setData(new WSLoginUrl(wxMpQrCodeTicket.getUrl()));
        return resp;
    }

    public static WSBaseResp<?> buildResp(User user, String token) {
        WSBaseResp<WSLoginSuccess> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.LOGIN_SUCCESS.getType());
        WSLoginSuccess build = WSLoginSuccess.builder()
                        .avatar(user.getAvatar())
                        .name(user.getName())
                        .token(token)
                        .uid(user.getId())
                        .build();
        resp.setData(build);
        return resp;
    }

    public static WSBaseResp<?> buildwaitAuthorize() {
        WSBaseResp<WSLoginUrl> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.LOGIN_SCAN_SUCCESS.getType());
        return resp;
    }
}