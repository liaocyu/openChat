package com.liaocyu.openChat.common.websocket.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.liaocyu.openChat.common.common.event.UserOfflineEvent;
import com.liaocyu.openChat.common.common.event.UserOnlineEvent;
import com.liaocyu.openChat.common.user.dao.UserDao;
import com.liaocyu.openChat.common.user.domain.entity.User;
import com.liaocyu.openChat.common.user.domain.enums.RoleEnum;
import com.liaocyu.openChat.common.user.service.RoleService;
import com.liaocyu.openChat.common.user.service.LoginService;
import com.liaocyu.openChat.common.user.service.adapter.WSAdapter;
import com.liaocyu.openChat.common.user.service.cache.UserCache;
import com.liaocyu.openChat.common.websocket.domian.dto.WSChannelExtraDTO;
import com.liaocyu.openChat.common.websocket.domian.vo.resp.WSBaseResp;
import com.liaocyu.openChat.common.websocket.service.WebSocketService;
import com.liaocyu.openChat.common.websocket.service.adapter.WebSocketAdapter;
import com.liaocyu.openChat.common.websocket.utils.NettyUtil;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/11 13:41
 * @description :
 * 处理用户的登录逻辑、发送消息的逻辑
 * 使用了两个 Map；使用 caffeine 来关联登录码code和channel
 * 使用 ConcurrentHashMap 来关联 channel 和 WSChannelExtraDTO 实体类对象
 */
@Component("webSocketService")
@Slf4j
public class WebSocketServiceImpl implements WebSocketService {

    @Autowired
    @Lazy
    WxMpService wxMpService;

    @Autowired
    UserDao userDao;

    @Autowired
    private UserCache userCache;

    @Autowired
    LoginService loginService;

    @Autowired
    ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    RoleService roleService;

    // 注入 webSocketExecutor 线程池配置
    @Autowired
    @Qualifier("websocketExecutor")
    ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * 管理所有用户的连接（登录态|游客）
     * WSChannelExtraDTO 服务层 用户中间信息
     */
    private static final ConcurrentHashMap<Channel, WSChannelExtraDTO> ONLINE_WS_MAP = new ConcurrentHashMap<>();


    /**
     * 所有在线的用户和对应的socket
     */
    private static final ConcurrentHashMap<Long, CopyOnWriteArrayList<Channel>> ONLINE_UID_MAP = new ConcurrentHashMap<>();


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
        // 3、把码推给前端 TODO 这里也许有问题
        // sendMsg(channel, WebSocketAdapter.buildResp(wxMpQrCodeTicket));
        sendMsg(channel, WSAdapter.buildLoginResp(wxMpQrCodeTicket));
    }

    @Override
    public void remove(Channel channel) {
        WSChannelExtraDTO wsChannelExtraDTO = ONLINE_WS_MAP.get(channel);
        Optional<Long> uidOptional = Optional.ofNullable(wsChannelExtraDTO)
                .map(WSChannelExtraDTO::getUid);
        boolean offlineAll = offline(channel, uidOptional);
        if (uidOptional.isPresent() && offlineAll) {//已登录用户断连,并且全下线成功
            User user = new User();
            user.setId(uidOptional.get());
            user.setLastOptTime(new Date());
            applicationEventPublisher.publishEvent(new UserOfflineEvent(this, user));
        }
    }

    /**
     * 用户下线
     * return 是否全下线成功
     */
    private boolean offline(Channel channel, Optional<Long> uidOptional) {
        ONLINE_WS_MAP.remove(channel);
        if (uidOptional.isPresent()) {
            CopyOnWriteArrayList<Channel> channels = ONLINE_UID_MAP.get(uidOptional.get());
            if (CollectionUtil.isNotEmpty(channels)) {
                channels.removeIf(ch -> Objects.equals(ch, channel));
            }
            return CollectionUtil.isEmpty(ONLINE_UID_MAP.get(uidOptional.get()));
        }
        return true;
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

    // TODO 如果报错，就删掉
    @Override
    public Boolean scanSuccess(Integer loginCode) {
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(loginCode);
        if (Objects.nonNull(channel)) {
            sendMsg(channel, WSAdapter.buildScanSuccessResp());
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
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

    //entrySet的值不是快照数据,但是它支持遍历，所以无所谓了，不用快照也行。
    @Override
    public void sendToAllOnline(WSBaseResp<?> wsBaseResp, Long skipUid) {
        ONLINE_WS_MAP.forEach((channel, ext) -> {
            if (Objects.nonNull(skipUid) && Objects.equals(ext.getUid(), skipUid)) {
                return;
            }
            threadPoolTaskExecutor.execute(() -> sendMsg(channel, wsBaseResp));
        });
    }

    @Override
    public void sendToUid(WSBaseResp<?> wsBaseResp, Long uid) {
        CopyOnWriteArrayList<Channel> channels = ONLINE_UID_MAP.get(uid);
        if (CollectionUtil.isEmpty(channels)) {
            log.info("用户：{}不在线", uid);
            return;
        }
        channels.forEach(channel -> {
            threadPoolTaskExecutor.execute(() -> sendMsg(channel, wsBaseResp));
        });
    }

    /**
     * @param channel websocket 连接
     * @param user    当前登录用户
     * @param token   用户token
     */
    /*private void loginSuccess(Channel channel, User user, String token) {

        // 用户上线成功
        // 1、保存channel的对应的 uid
        WSChannelExtraDTO wsChannelExtraDTO = ONLINE_WS_MAP.get(channel);
        wsChannelExtraDTO.setUid(*//*user.getId()*//*Optional.ofNullable(user).map(User::getId).orElse(null));
        // 2、推送成功消息
        // 判断用户是否有权限
        sendMsg(channel, WebSocketAdapter.buildResp(user, token, roleService.hasPower(user.getId(), RoleEnum.CHAT_MANAGER)));
        // 用户上线成功的事件
        user.setLastOptTime(new Date());
        user.refreshIp(NettyUtil.getAttr(channel, NettyUtil.IP));

        applicationEventPublisher.publishEvent(new UserOnlineEvent(this, user));
    }*/

    // TODO 不行就换成上面的 取消注释掉
    private void loginSuccess(Channel channel, User user, String token) {
        //更新上线列表
        online(channel, user.getId());
        //返回给用户登录成功
        boolean hasPower = roleService.hasPower(user.getId(), RoleEnum.CHAT_MANAGER);
        //发送给对应的用户
        sendMsg(channel, WSAdapter.buildLoginSuccessResp(user, token, hasPower));
        //发送用户上线事件
        boolean online = userCache.isOnline(user.getId());
        if (!online) {
            user.setLastOptTime(new Date());
            user.refreshIp(NettyUtil.getAttr(channel, NettyUtil.IP));
            applicationEventPublisher.publishEvent(new UserOnlineEvent(this, user));
        }
    }

    private void online(Channel channel, Long uid) {
        getOrInitChannelExt(channel).setUid(uid);
        ONLINE_UID_MAP.putIfAbsent(uid, new CopyOnWriteArrayList<>());
        ONLINE_UID_MAP.get(uid).add(channel);
        NettyUtil.setAttr(channel, NettyUtil.UID, uid);
    }

    private WSChannelExtraDTO getOrInitChannelExt(Channel channel) {
        WSChannelExtraDTO wsChannelExtraDTO =
                ONLINE_WS_MAP.getOrDefault(channel, new WSChannelExtraDTO());
        WSChannelExtraDTO old = ONLINE_WS_MAP.putIfAbsent(channel, wsChannelExtraDTO);
        return ObjectUtil.isNull(old) ? wsChannelExtraDTO : old;
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
