package com.liaocyu.openChat.common.websocket.service.adapter;

import com.liaocyu.openChat.common.websocket.domian.enums.WSRespTypeEnum;
import com.liaocyu.openChat.common.websocket.domian.vo.resp.WSBaseResp;
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
}
