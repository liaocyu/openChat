package com.liaocyu.openChat.common.websocket.service.adapter;

import com.liaocyu.openChat.common.common.domain.enums.YesOrNoEnum;
import com.liaocyu.openChat.common.user.domain.entity.User;
import com.liaocyu.openChat.common.websocket.domian.enums.WSRespTypeEnum;
import com.liaocyu.openChat.common.websocket.domian.vo.resp.WSBaseResp;
import com.liaocyu.openChat.common.websocket.domian.vo.resp.ws.WSBlack;
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

    /**
     *
     * @param user 用户
     * @param token 用户凭证
     * @param power 用户权限
     * @return
     */
    public static WSBaseResp<?> buildResp(User user, String token , boolean power) {
        WSBaseResp<WSLoginSuccess> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.LOGIN_SUCCESS.getType());
        WSLoginSuccess build = WSLoginSuccess.builder()
                        .avatar(user.getAvatar())
                        .name(user.getName())
                        .token(token)
                        .uid(user.getId()) // 超级管理员
                        .power(power ? YesOrNoEnum.YES.getStatus() : YesOrNoEnum.NO.getStatus())
                        .build();
        resp.setData(build);
        return resp;
    }

    public static WSBaseResp<?> buildwaitAuthorize() {
        WSBaseResp<WSLoginUrl> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.LOGIN_SCAN_SUCCESS.getType());
        return resp;
    }
    // 通知前端清除token
    public static WSBaseResp<?> buildInvalidToeknResp() {
        WSBaseResp<WSLoginUrl> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.INVALIDATE_TOKEN.getType());
        return resp;
    }

    // 给前端拉黑用户的返回对象
    public static WSBaseResp<?> buildBlack(User user) {
        WSBaseResp<WSBlack> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.BLACK.getType());
        WSBlack build = WSBlack.builder()
                .uid(user.getId())
                .build();
        resp.setData(build);
        return resp;
    }
}
