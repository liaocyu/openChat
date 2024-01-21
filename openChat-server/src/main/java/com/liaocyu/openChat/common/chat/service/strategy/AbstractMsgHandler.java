package com.liaocyu.openChat.common.chat.service.strategy;

import cn.hutool.core.bean.BeanUtil;
import com.liaocyu.openChat.common.chat.dao.MessageDao;
import com.liaocyu.openChat.common.chat.domain.entity.Message;
import com.liaocyu.openChat.common.chat.domain.enums.MessageTypeEnum;
import com.liaocyu.openChat.common.chat.domain.vo.req.ChatMessageReq;
import com.liaocyu.openChat.common.chat.service.adapter.MessageAdapter;
import com.liaocyu.openChat.common.chat.service.strategy.msg.MsgHandlerFactory;
import com.liaocyu.openChat.common.common.utils.AssertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.lang.reflect.ParameterizedType;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/19 11:23
 * @description : TODO 消息处理器抽象类
 */
public abstract class AbstractMsgHandler<Req> {
    @Autowired
    private MessageDao messageDao;
    private Class<Req> bodyClass;

    @PostConstruct
    private void init() {
        ParameterizedType genericSuperclass = (ParameterizedType) this.getClass().getGenericSuperclass();
        this.bodyClass = (Class<Req>) genericSuperclass.getActualTypeArguments()[0];
        MsgHandlerFactory.register(getMsgTypeEnum().getType(), this);
    }

    /**
     * 消息类型
     */
    protected abstract MessageTypeEnum getMsgTypeEnum();

    /**
     * 检查消息
     *
     * @param body 消息请求类型
     * @param roomId 房间Id
     * @param uid uid
     */
    protected void checkMsg(Req body, Long roomId, Long uid) {

    }

    @Transactional
    public Long checkAndSaveMsg(ChatMessageReq request, Long uid) {
        Req body = this.toBean(request.getBody()); // 获得具体的消息类型 文本、撤回、语音、文件
        //统一校验
        AssertUtil.allCheckValidateThrow(body); // 断言工具类整体校验
        //子类扩展校验
        checkMsg(body, request.getRoomId(), uid);
        // 统一保存 保存消息到Message表
        Message insert = MessageAdapter.buildMsgSave(request , uid);
        messageDao.save(insert);
        //子类扩展保存
        saveMsg(insert, body);
        return insert.getId();
    }

    private Req toBean(Object body) {
        if (bodyClass.isAssignableFrom(body.getClass())) {
            return (Req) body;
        }
        return BeanUtil.toBean(body, bodyClass);
    }

    /**
     * 保存消息
     *
     * @param message 消息体
     * @param body 请求类型
     */
    protected abstract void saveMsg(Message message, Req body);

    /**
     * 展示消息
     */
    public abstract Object showMsg(Message msg);

    /**
     * 被回复时——展示的消息
     */
    public abstract Object showReplyMsg(Message msg);

    /**
     * 会话列表——展示的消息
     */
    public abstract String showContactMsg(Message msg);

}
