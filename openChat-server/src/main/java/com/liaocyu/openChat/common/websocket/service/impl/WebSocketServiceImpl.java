package com.liaocyu.openChat.common.websocket.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.liaocyu.openChat.common.websocket.domian.dto.WSChannelExtraDTO;
import com.liaocyu.openChat.common.websocket.domian.enums.WSRespTypeEnum;
import com.liaocyu.openChat.common.websocket.domian.vo.resp.WSBaseResp;
import com.liaocyu.openChat.common.websocket.domian.vo.resp.ws.WSLoginUrl;
import com.liaocyu.openChat.common.websocket.service.WebSocketService;
import com.liaocyu.openChat.common.websocket.service.adapter.WebSocketAdapter;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/11 13:41
 * @description :
 * 处理用户的登录逻辑、发送消息的逻辑
 * 使用了两个 Map；使用 caffeine 来关联登录码和channel
 *               使用 ConcurrentHashMap 来关联 channel 和 WSChannelExtraDTO 实体类对象
 */
@Service
public class WebSocketServiceImpl implements WebSocketService {

    @Autowired
    WxMpService wxMpService;
    /**
     * 管理所有用户的连接（登录态|游客）
     * WSChannelExtraDTO 服务层 用户中间信息
     */
    private static final ConcurrentHashMap<Channel , WSChannelExtraDTO> ONLINE_WS_MAP = new ConcurrentHashMap<>();

    /**
     * 临时保存登录code和channel的映射关系
     * 存活时间是一个小时
     */
    public static final int MAXIMUM_SIZE = 1000;
    public static final Duration DURATION = Duration.ofHours(1);
    private static final Cache<Integer , Channel> WAIT_LOGIN_MAP = Caffeine.newBuilder()
            .maximumSize(MAXIMUM_SIZE)
            .expireAfterWrite(DURATION)
            .build();

    @Override
    public void connect(Channel channel) {
        ONLINE_WS_MAP.put(channel , new WSChannelExtraDTO());
    }

    /**
     * 前端点击登录 会发出一个请求登录二维码的 webSocket 信息
     * @param channel
     */
    @Override
    public void handleLoginReq(Channel channel) {

        // 1、生成随机码
        Integer code = generateLoginCode(channel);
        // 2、请求微信接口 ，获取登录码地址
        WxMpQrCodeTicket wxMpQrCodeTicket = null;
        try {
            // 微信码凭证
            wxMpQrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(code, (int) DURATION.getSeconds());
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
        // 3、把码推给前端
        sendMsg(channel , WebSocketAdapter.buildResp(wxMpQrCodeTicket));
    }

    @Override
    public void remove(Channel channel) {
        ONLINE_WS_MAP.remove(channel);
        // TODO 用户下线广播
    }

    // 用户发送消息逻辑
    private void sendMsg(Channel channel, WSBaseResp<?> resp) {
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(resp)));
    }

    // 生成唯一的 code 值 ， 并将这个 code和channel关联起来
    private Integer generateLoginCode(Channel channel) {
        Integer code ;
        do {
            code = RandomUtil.randomInt(Integer.MAX_VALUE);
            // WAIT_LOGIN_MAP.asMap() 转为 普通的 map ，putIfAbsent 插入成功返回null ,不成功返回 存在的值
        } while (Objects.nonNull(WAIT_LOGIN_MAP.asMap().putIfAbsent(code , channel)));
        return code ;
    }
}
