package com.liaocyu.openChat.common.user.service.cache;

import com.liaocyu.openChat.common.common.domain.dto.SummeryInfoDTO;
import com.liaocyu.openChat.common.common.service.cache.AbstractRedisStringCache;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/9 16:03
 * @description :
 */
@Component
public class UserSummaryCache extends AbstractRedisStringCache<Long , SummeryInfoDTO> {


    @Override
    protected String getKey(Long req) {
        return null;
    }

    @Override
    protected Long getExpireSeconds() {
        return null;
    }

    @Override
    protected Map<Long, SummeryInfoDTO> load(List<Long> req) {
        return null;
    }
}
