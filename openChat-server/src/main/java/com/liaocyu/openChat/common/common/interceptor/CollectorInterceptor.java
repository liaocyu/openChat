package com.liaocyu.openChat.common.common.interceptor;

import cn.hutool.extra.servlet.ServletUtil;
import com.liaocyu.openChat.common.common.domain.dto.RequestInfo;
import com.liaocyu.openChat.common.common.utils.RequestHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/19 14:09
 * @description :
 */
@Component
public class CollectorInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Long uid = Optional.ofNullable(request.getAttribute(TokenInterceptor.UID)).map(Object::toString).map(Long::parseLong).orElse(null);
        // 利用hutool 获取当前ip
        String clientIP = ServletUtil.getClientIP(request);
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.setIp(ServletUtil.getClientIP(request));
        requestInfo.setUid(uid);
        RequestHolder.set(requestInfo);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        RequestHolder.remove();
    }
}
