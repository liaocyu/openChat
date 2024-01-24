package com.liaocyu.openChat.common.user.dao;

import com.liaocyu.openChat.common.user.domain.entity.ItemConfig;
import com.liaocyu.openChat.common.user.mapper.ItemConfigMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 功能物品配置表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/liaocyu">liaocyu</a>
 * @since 2023-12-18
 */
@Service
public class ItemConfigDao extends ServiceImpl<ItemConfigMapper, ItemConfig> {


    public List<ItemConfig> getValidByType(Integer itemType) {

        // 应该从 caffeine 去获取徽章
        return lambdaQuery()
                .eq(ItemConfig::getType, itemType)
                .list();
    }
}
