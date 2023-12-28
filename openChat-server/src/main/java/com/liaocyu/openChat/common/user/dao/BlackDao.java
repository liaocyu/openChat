package com.liaocyu.openChat.common.user.dao;

import com.liaocyu.openChat.common.user.domain.entity.Black;
import com.liaocyu.openChat.common.user.mapper.BlackMapper;
import com.liaocyu.openChat.common.user.service.IBlackService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 黑名单 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/liaocyu">liaocyu</a>
 * @since 2023-12-28
 */
@Service
public class BlackDao extends ServiceImpl<BlackMapper, Black> {

}
