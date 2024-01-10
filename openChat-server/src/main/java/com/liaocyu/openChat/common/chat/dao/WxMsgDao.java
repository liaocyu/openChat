package com.liaocyu.openChat.common.chat.dao;

import com.liaocyu.openChat.common.chat.domain.entity.WxMsg;
import com.liaocyu.openChat.common.chat.mapper.WxMsgMapper;
import com.liaocyu.openChat.common.chat.service.IWxMsgService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 微信消息表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/liaocyu">liaocyu</a>
 * @since 2024-01-10
 */
@Service
public class WxMsgDao extends ServiceImpl<WxMsgMapper, WxMsg>{

}
