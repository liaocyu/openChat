package com.liaocyu.openChat.common.user.service.cache;

import com.liaocyu.openChat.common.user.dao.ItemConfigDao;
import com.liaocyu.openChat.common.user.domain.entity.ItemConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.cache.annotation.CachePut;
import java.util.List;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/22 16:20
 * @description :
 */
@Component
public class ItemCache {

    @Autowired
    private ItemConfigDao itemConfigDao;

     // 获取缓存，如果没有 执行下面的方法
    @Cacheable(cacheNames = "item" , key = "'itemsByType:' + #itemType")
    public List<ItemConfig> getByType(Integer itemType) {
        return itemConfigDao.getValidByType(itemType);
    }
     // @CachePut // 刷新缓存

    @CacheEvict(cacheNames = "item" , key ="'itemsByType:' + #itemType") // 删除缓存
    public void evictByType(Integer itemType) {

    }

    @Cacheable(cacheNames = "item", key = "'item:'+#itemId")
    public ItemConfig getById(Long itemId) {
        return itemConfigDao.getById(itemId);
    }
}
