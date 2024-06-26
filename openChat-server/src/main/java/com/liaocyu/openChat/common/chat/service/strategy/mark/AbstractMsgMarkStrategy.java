package com.liaocyu.openChat.common.chat.service.strategy.mark;

import com.liaocyu.openChat.common.chat.dao.MessageMarkDao;
import com.liaocyu.openChat.common.chat.domain.dto.ChatMessageMarkDTO;
import com.liaocyu.openChat.common.chat.domain.entity.MessageMark;
import com.liaocyu.openChat.common.chat.domain.enums.MessageMarkActTypeEnum;
import com.liaocyu.openChat.common.chat.domain.enums.MessageMarkTypeEnum;
import com.liaocyu.openChat.common.common.domain.enums.YesOrNoEnum;
import com.liaocyu.openChat.common.common.event.MessageMarkEvent;
import com.liaocyu.openChat.common.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Objects;
import java.util.Optional;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/21 16:16
 * @description : 消息标记抽象类
 */
@NoArgsConstructor
public abstract class AbstractMsgMarkStrategy {

    private MessageMarkDao messageMarkDao;
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    public void setMessageMarkDao(MessageMarkDao messageMarkDao) {
        this.messageMarkDao = messageMarkDao;
    }

    @Autowired
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }


    /**
     * 消息类型
     *
     * @return 返回消息类型
     */
    protected abstract MessageMarkTypeEnum getTypeEnum();

    /**
     * 消息标记
     *
     * @param uid uid
     * @param msgId 消息Id
     */
    @Transactional
    public void mark(Long uid, Long msgId) {
        doMark(uid, msgId);
    }

    /**
     * 消息标记取消
     *
     * @param uid uid
     * @param msgId 消息Id
     */
    @Transactional
    public void unMark(Long uid, Long msgId) {
        doUnMark(uid, msgId);
    }

    @PostConstruct
    private void init() {
        MsgMarkFactory.register(getTypeEnum().getType(), this);
    }

    protected void doMark(Long uid, Long msgId) {
        exec(uid, msgId, MessageMarkActTypeEnum.MARK);
    }

    protected void doUnMark(Long uid, Long msgId) {
        exec(uid, msgId, MessageMarkActTypeEnum.UN_MARK);
    }

    protected void exec(Long uid, Long msgId, MessageMarkActTypeEnum actTypeEnum) {
        Integer markType = getTypeEnum().getType();
        Integer actType = actTypeEnum.getType();
        MessageMark oldMark = messageMarkDao.get(uid, msgId, markType);
        if (Objects.isNull(oldMark) && actTypeEnum == MessageMarkActTypeEnum.UN_MARK) {
            //取消的类型，数据库一定有记录，没有就直接跳过操作
            return;
        }
        //插入一条新消息,或者修改一条消息
        MessageMark insertOrUpdate = MessageMark.builder()
                .id(Optional.ofNullable(oldMark).map(MessageMark::getId).orElse(null))
                .uid(uid)
                .msgId(msgId)
                .type(markType)
                .status(transformAct(actType))
                .build();
        boolean modify = messageMarkDao.saveOrUpdate(insertOrUpdate);
        if (modify) {
            //修改成功才发布消息标记事件
            ChatMessageMarkDTO dto = new ChatMessageMarkDTO(uid, msgId, markType, actType);
            applicationEventPublisher.publishEvent(new MessageMarkEvent(this, dto));
        }
    }

    private Integer transformAct(Integer actType) {
        if (actType == 1) {
            return YesOrNoEnum.NO.getStatus();
        } else if (actType == 2) {
            return YesOrNoEnum.YES.getStatus();
        }
        throw new BusinessException("动作类型 1确认 2取消");
    }

}

