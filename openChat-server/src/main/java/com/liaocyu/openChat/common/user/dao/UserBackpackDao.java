package com.liaocyu.openChat.common.user.dao;

import com.liaocyu.openChat.common.common.domain.enums.YesOrNoEnum;
import com.liaocyu.openChat.common.user.domain.entity.UserBackpack;
import com.liaocyu.openChat.common.user.domain.enums.ItemTypeEnum;
import com.liaocyu.openChat.common.user.mapper.UserBackpackMapper;
import com.liaocyu.openChat.common.user.service.IUserBackpackService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户背包表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/liaocyu">liaocyu</a>
 * @since 2023-12-18
 */
@Service
public class UserBackpackDao extends ServiceImpl<UserBackpackMapper, UserBackpack> {

    /**
     * 查询指定用户背包物品数量
     * @param uid
     * @param itemId
     * @return
     */
    public Integer getCountByValidItemId(Long uid, Long itemId) {
        return lambdaQuery()
                .eq(UserBackpack::getUid, uid)
                .eq(UserBackpack::getItemId, itemId)
                .eq(UserBackpack::getStatus, YesOrNoEnum.NO.getStatus())
                .count();
    }
}
