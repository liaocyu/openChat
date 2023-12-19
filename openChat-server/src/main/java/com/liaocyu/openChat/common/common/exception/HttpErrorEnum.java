package com.liaocyu.openChat.common.common.exception;

import cn.hutool.http.ContentType;
import com.google.common.base.Charsets;
import com.liaocyu.openChat.common.common.domain.vo.resp.ApiResult;
import com.liaocyu.openChat.common.common.utils.JsonUtils;
import lombok.AllArgsConstructor;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/19 13:22
 * @description :
 * http 错误状态码对象
 */
@AllArgsConstructor
public enum HttpErrorEnum {
    ACCESS_DENIED(401, "登录失效请重新登录");

    private Integer httpCode;
    private String desc;

    public void sendHttpError(HttpServletResponse response) throws IOException {
        response.setStatus(httpCode);
        response.setContentType(ContentType.JSON.toString(Charsets.UTF_8));
        response.getWriter().write(JsonUtils.toStr(ApiResult.fail(httpCode , desc)));
    }


}
