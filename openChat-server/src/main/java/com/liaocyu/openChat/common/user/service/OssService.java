package com.liaocyu.openChat.common.user.service;

import com.liaocyu.openChat.common.user.domain.vo.req.oss.UploadUrlReq;
import com.liaocyu.openchat.oss.domain.OssResp;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/24 11:16
 * @description :
 */
public interface OssService {
    /**
     * 获取临时的上传链接
     */
    OssResp getUploadUrl(Long uid, UploadUrlReq req);
}
