package com.liaocyu.openChat.common.common.interceptor;

import cn.hutool.http.ContentType;
import com.google.common.base.Charsets;
import com.liaocyu.openChat.common.common.exception.HttpErrorEnum;
import com.liaocyu.openChat.common.user.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.Optional;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/18 17:46
 * @description :
 * token拦截器
 */
@Component
public class TokenInterceptor implements HandlerInterceptor {

    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String AUTHORIZATION_SCHEMA = "Bearer ";
    public static final String UID = "uid";

    @Autowired
    LoginService loginService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 解析token
        String token = getToken(request);
        Long validUid = loginService.getValidUid(token);
        if (Objects.nonNull(validUid)) {
            // 用户有登录态
            request.setAttribute(UID, validUid);
        } else {
            // 用户未登录
            boolean isPublicURI = isPublicURI(request);
            if(!isPublicURI) {
                // 返回未授权 ，返回码 401
                HttpErrorEnum.ACCESS_DENIED.sendHttpError(response);
                return false;
            }
        }
        return true;
    }

    private static boolean isPublicURI(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String[] split = requestURI.split("/");
        boolean isPublicURI = split.length>3&&"public".equals(split[3]);
        return isPublicURI;
    }

    /**
     * 解析token
     * @param request 请求对象
     * @return 返回token
     */
    private String getToken(HttpServletRequest request) {
        String header = request.getHeader(HEADER_AUTHORIZATION);
        return Optional.ofNullable(header)
                .filter(h -> h.startsWith(AUTHORIZATION_SCHEMA))
                .map(h -> h.replaceFirst(AUTHORIZATION_SCHEMA, ""))
                .orElse(null);
    }
}
