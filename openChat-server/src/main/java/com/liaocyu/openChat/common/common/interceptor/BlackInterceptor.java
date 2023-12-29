package com.liaocyu.openChat.common.common.interceptor;

import cn.hutool.core.collection.CollectionUtil;
import com.liaocyu.openChat.common.common.domain.dto.RequestInfo;
import com.liaocyu.openChat.common.common.exception.HttpErrorEnum;
import com.liaocyu.openChat.common.common.utils.RequestHolder;
import com.liaocyu.openChat.common.user.domain.enums.BlackTypeEnum;
import com.liaocyu.openChat.common.user.service.cache.UserCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/19 13:29
 * @description :
 * 用户黑名单拦截器
 */
@Component
public class BlackInterceptor implements HandlerInterceptor {
    private final UserCache userCache;

    @Autowired
    public BlackInterceptor(UserCache userCache) {
        this.userCache = userCache;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 获取用户拉黑列表

        Map<Integer, Set<String>> blackMap = userCache.getBlackMap();
        RequestInfo requestInfo = RequestHolder.get();
        // 判断当前用户是否在黑名单列表中
        if (inBlackList(requestInfo.getUid(), blackMap.get(BlackTypeEnum.UID.getType()))) {
            // 在黑名单列表中 返回401给前端
            HttpErrorEnum.ACCESS_DENIED.sendHttpError(response);
            return false;
        }

        if (inBlackList(requestInfo.getUid(), blackMap.get(BlackTypeEnum.IP.getType()))) {
            // 在黑名单列表中 返回401给前端
            HttpErrorEnum.ACCESS_DENIED.sendHttpError(response);
            return false;
        }
        return true;
    }

    private boolean inBlackList(Object target, Set<String> set) {
        if (Objects.isNull(target) || CollectionUtil.isEmpty(set)) {
            return false;
        }
        return set.contains(target.toString());
    }
}
