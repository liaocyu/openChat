package com.liaocyu.openChat.common.user.dao;

import com.liaocyu.openChat.common.user.domain.entity.Message;
import com.liaocyu.openChat.common.user.mapper.MessageMapper;
import com.liaocyu.openChat.common.user.service.IMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 消息表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/liaocyu">liaocyu</a>
 * @since 2024-01-03
 */
@Service
public class MessageDao extends ServiceImpl<MessageMapper, Message> {

}
