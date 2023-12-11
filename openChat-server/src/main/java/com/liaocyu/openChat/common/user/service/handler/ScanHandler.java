package com.liaocyu.openChat.common.user.service.handler;

import com.liaocyu.openChat.common.user.service.adapter.TextBuilder;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * 用户扫码处理器
 */
@Component
public class ScanHandler extends AbstractHandler {

    @Value("${wx.mp.callback}")
    private String callback; // 扫码成功回调地址

    private static final  String URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";

    /**
     * 扫码处理逻辑
     * @param wxMpXmlMessage      微信推送消息
     * @param map        上下文，如果handler或interceptor之间有信息要传递，可以用这个
     * @param wxMpService    服务类
     * @param wxSessionManager session管理器
     * @return
     * @throws WxErrorException
     */
    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMpXmlMessage, Map<String, Object> map,
                                    WxMpService wxMpService, WxSessionManager wxSessionManager) throws WxErrorException {
        // 扫码事件处理
/*        return wxMsgService.scan(wxMpService, wxMpXmlMessage);*/
        String eventKey = wxMpXmlMessage.getEventKey();
        String openId = wxMpXmlMessage.getFromUser();
        String authroizeURL = null;
        try {
            authroizeURL = String.format(URL, wxMpService.getWxMpConfigStorage().getAppId(), URLEncoder.encode(callback + "/wx/portal/public/callBack" , "utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }


        // TODO 用户扫码
        return new TextBuilder().build("请点击链接授权：<a href=\"" + authroizeURL + "\">登录</a>", wxMpXmlMessage);
/*        return TextBuilder.build("你好啊，liaocyu" , wxMpXmlMessage);*/

    }

}
