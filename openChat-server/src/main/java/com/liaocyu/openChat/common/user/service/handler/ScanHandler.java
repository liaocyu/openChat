package com.liaocyu.openChat.common.user.service.handler;

import com.liaocyu.openChat.common.user.service.WXMsgServeice;
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

    @Autowired
    WXMsgServeice wxMsgServeice;
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
        return wxMsgServeice.scan(wxMpXmlMessage);

    }

}
