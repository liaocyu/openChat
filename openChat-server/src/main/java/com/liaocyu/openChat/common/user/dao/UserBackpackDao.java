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

    /**
     * 判断用户改名卡是否可用
     * @param uid
     * @param itemId
     */
    public UserBackpack getFirstValidItem(Long uid, Long itemId) {
        return lambdaQuery()
                .eq(UserBackpack::getUid, uid)
                .eq(UserBackpack::getItemId, itemId)
                .eq(UserBackpack::getStatus, YesOrNoEnum.NO.getStatus())
                .orderByAsc(UserBackpack::getId)
                .last("limit 1")
                .one();
    }

    /**
     * 使用改名卡
     * @param userBackpack
     */
    public boolean useItem(UserBackpack userBackpack) {
        return lambdaUpdate()
                .eq(UserBackpack::getId, userBackpack.getId())
                .eq(UserBackpack::getStatus, YesOrNoEnum.NO.getStatus())
                .set(UserBackpack::getStatus, YesOrNoEnum.YES.getStatus())
                .update();
    }
}
