package com.liaocyu.openChat.common.websocket.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.liaocyu.openChat.common.common.event.UserOnlineEvent;
import com.liaocyu.openChat.common.user.dao.UserDao;
import com.liaocyu.openChat.common.user.domain.entity.User;
import com.liaocyu.openChat.common.user.domain.enums.RoleEnum;
import com.liaocyu.openChat.common.user.service.RoleService;
import com.liaocyu.openChat.common.user.service.LoginService;
import com.liaocyu.openChat.common.websocket.domian.dto.WSChannelExtraDTO;
import com.liaocyu.openChat.common.websocket.domian.vo.resp.WSBaseResp;
import com.liaocyu.openChat.common.websocket.service.WebSocketService;
import com.liaocyu.openChat.common.websocket.service.adapter.WebSocketAdapter;
import com.liaocyu.openChat.common.websocket.utils.NettyUtil;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/11 13:41
 * @description :
 * 处理用户的登录逻辑、发送消息的逻辑
 * 使用了两个 Map；使用 caffeine 来关联登录码code和channel
 * 使用 ConcurrentHashMap 来关联 channel 和 WSChannelExtraDTO 实体类对象
 */
@Service
public class WebSocketServiceImpl implements WebSocketService {

    @Autowired
    @Lazy
    WxMpService wxMpService;

    @Autowired
    UserDao userDao;

    @Autowired
    LoginService loginService;

    @Autowired
    ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    RoleService roleService;

    // 注入 webSocketExecutor 线程池配置
    @Qualifier("websocketExecutor")
    @Autowired
    ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * 管理所有用户的连接（登录态|游客）
     * WSChannelExtraDTO 服务层 用户中间信息
     */
    private static final ConcurrentHashMap<Channel, WSChannelExtraDTO> ONLINE_WS_MAP = new ConcurrentHashMap<>();

    /**
     * 临时保存登录code和channel的映射关系
     * 存活时间是一个小时
     */
    public static final int MAXIMUM_SIZE = 1000;
    public static final Duration DURATION = Duration.ofHours(1);
    private static final Cache<Integer, Channel> WAIT_LOGIN_MAP = Caffeine.newBuilder()
            .maximumSize(MAXIMUM_SIZE)
            .expireAfterWrite(DURATION)
            .build();

    @Override
    public void connect(Channel channel) {
        ONLINE_WS_MAP.put(channel, new WSChannelExtraDTO());
    }

    /**
     * 处理用户的首次登录请求
     * 前端点击登录 会发出一个请求登录二维码的 webSocket 信息
     * 使用 caffeine ，将code 为键 ，channel 为值 保存用户的 channel
     *
     * @param channel
     */
    @Override
    public void handleLoginReq(Channel channel) {

        // 1、生成随机码
        Integer code = generateLoginCode(channel);
        // 2、请求微信接口 ，获取登录码地址
        WxMpQrCodeTicket wxMpQrCodeTicket = null;
        try {
            // 微信码凭证【票据】
            wxMpQrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(code, (int) DURATION.getSeconds());
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
        // 3、把码推给前端
        sendMsg(channel, WebSocketAdapter.buildResp(wxMpQrCodeTicket));
    }

    @Override
    public void remove(Channel channel) {
        ONLINE_WS_MAP.remove(channel);
        // TODO 用户下线广播
    }

    @Override
    public void scanLoginSuccess(Integer code, Long id) {
        // 确认连接在机器上
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(code);
        if (Objects.isNull(channel)) {
            return;
        }
        User user = userDao.getById(id);
        // 移除code
        WAIT_LOGIN_MAP.invalidate(code);
        // 调用用户登录模块获取token
        String token = loginService.login(id);
        // 用户登录
        // sendMsg(channel , WebSocketAdapter.buildResp(user , token));
        loginSuccess(channel, user, token);
    }

    @Override
    public void waitAuthorize(Integer code) {
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(code);
        if (Objects.isNull(channel)) {
            return;
        }
        sendMsg(channel, WebSocketAdapter.buildwaitAuthorize());
    }

    // 用户token 认证
    @Override
    public void authorize(Channel channel, String token) {
        // 首先判断token 是否有效
        Long validUid = loginService.getValidUid(token);
        if (Objects.nonNull(channel)) {
            // 登录成功 获取用户相关信息
            User user = userDao.getById(validUid);
            loginSuccess(channel, user, token);
            // 给前端返回登录成功通知
            /*            sendMsg(channel , WebSocketAdapter.buildResp(user , token));*/
        } else {
            // 如果token 不存在，给前端通知清除token ，重新登录
            sendMsg(channel, WebSocketAdapter.buildInvalidToeknResp());
        }
    }

    /**
     * 发送消息给所有在线用户
     *
     * @param msg
     */
    @Override
    public void sendMsgToAll(WSBaseResp<?> msg) {
        // 获取所有连接的用户 并将消息进行批量推送
        ONLINE_WS_MAP.forEach((channel, ext) -> {
            threadPoolTaskExecutor.execute(() -> sendMsg(channel, msg));
        });
    }

    /**
     * @param channel websocket 连接
     * @param user    当前登录用户
     * @param token   用户token
     */
    private void loginSuccess(Channel channel, User user, String token) {

        // 用户上线成功
        // 1、保存channel的对应的 uid
        WSChannelExtraDTO wsChannelExtraDTO = ONLINE_WS_MAP.get(channel);
        wsChannelExtraDTO.setUid(/*user.getId()*/Optional.ofNullable(user).map(User::getId).orElse(null));
        // 2、推送成功消息
        // 判断用户是否有权限
        sendMsg(channel, WebSocketAdapter.buildResp(user, token, roleService.hasPower(user.getId(), RoleEnum.CHAT_MANAGER)));
        // 用户上线成功的事件
        user.setLastOptTime(new Date());
        user.refreshIp(NettyUtil.getAttr(channel, NettyUtil.IP));

        applicationEventPublisher.publishEvent(new UserOnlineEvent(this, user));
    }

    // 用户发送消息逻辑
    private void sendMsg(Channel channel, WSBaseResp<?> resp) {
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(resp)));
    }

    // 生成唯一的 code 值 ， 并将这个 code和channel关联起来
    private Integer generateLoginCode(Channel channel) {
        Integer code;
        do {
            code = RandomUtil.randomInt(Integer.MAX_VALUE);
            // WAIT_LOGIN_MAP.asMap() 转为 普通的 map ，putIfAbsent 插入成功返回null ,不成功返回 存在的值
        } while (Objects.nonNull(WAIT_LOGIN_MAP.asMap().putIfAbsent(code, channel)));
        return code;
    }
}
