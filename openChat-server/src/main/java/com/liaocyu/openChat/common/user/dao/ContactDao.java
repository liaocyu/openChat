package com.liaocyu.openChat.common.user.dao;

import com.liaocyu.openChat.common.user.domain.entity.Contact;
import com.liaocyu.openChat.common.user.mapper.ContactMapper;
import com.liaocyu.openChat.common.user.service.IContactService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 会话列表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/liaocyu">liaocyu</a>
 * @since 2024-01-03
 */
@Service
public class ContactDao extends ServiceImpl<ContactMapper, Contact> {

}
