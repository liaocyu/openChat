package com.liaocyu.openChat.common.user.service.impl;

import com.liaocyu.openChat.common.user.dao.UserBackpackDao;
import com.liaocyu.openChat.common.user.dao.UserDao;
import com.liaocyu.openChat.common.user.domain.entity.User;
import com.liaocyu.openChat.common.user.domain.enums.ItemEnum;
import com.liaocyu.openChat.common.user.domain.enums.ItemTypeEnum;
import com.liaocyu.openChat.common.user.domain.vo.resp.UserInfoResp;
import com.liaocyu.openChat.common.user.service.UserService;
import com.liaocyu.openChat.common.user.service.adapter.UserAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        return UserAdapter.buildUserInfo(user,modifyNameCount);
    }

    @Override
    public void modifyName(Long uid, String name) {

    }
}
