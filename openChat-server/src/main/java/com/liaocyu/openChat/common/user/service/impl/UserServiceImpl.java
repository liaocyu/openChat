package com.liaocyu.openChat.common.user.service.impl;

import com.liaocyu.openChat.common.common.exception.BusinessException;
import com.liaocyu.openChat.common.common.utils.AssertUtil;
import com.liaocyu.openChat.common.user.dao.UserBackpackDao;
import com.liaocyu.openChat.common.user.dao.UserDao;
import com.liaocyu.openChat.common.user.domain.entity.User;
import com.liaocyu.openChat.common.user.domain.entity.UserBackpack;
import com.liaocyu.openChat.common.user.domain.enums.ItemEnum;
import com.liaocyu.openChat.common.user.domain.enums.ItemTypeEnum;
import com.liaocyu.openChat.common.user.domain.vo.resp.UserInfoResp;
import com.liaocyu.openChat.common.user.service.UserService;
import com.liaocyu.openChat.common.user.service.adapter.UserAdapter;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/12 13:34
 * @description :
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;
    @Autowired
    UserBackpackDao userBackpackDao;

    @Override
    @Transactional
    public Long register(User insert) {
        userDao.save(insert);
        // TODO 用户注册的事件
        return insert.getId();
    }

    @Override
    public UserInfoResp getUserInfo(Long uid) {
        User user = userDao.getById(uid);
        Integer modifyNameCount = userBackpackDao.getCountByValidItemId(uid, ItemEnum.MODIFY_NAME_CARD.getId());

        return UserAdapter.buildUserInfo(user, modifyNameCount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyName(Long uid, String name) {
        User oldUser = userDao.getByName(name);
        AssertUtil.isEmpty(oldUser , "名字已经被占用了，请重新换个名字");
        UserBackpack userBackpack = userBackpackDao.getFirstValidItem(uid, ItemEnum.MODIFY_NAME_CARD.getId());
        AssertUtil.isNotEmpty(userBackpack , "你已经改过名字啦~");
        // 使用改名卡
        boolean success = userBackpackDao.useItem(userBackpack);
        if (success) {
            // 改名
            userDao.modifyName(uid, name);
        }
    }
}
