package com.liaocyu.openChat.common.user.service.impl;

import com.liaocyu.openChat.common.common.utils.AssertUtil;
import com.liaocyu.openChat.common.user.domain.enums.OssSceneEnum;
import com.liaocyu.openChat.common.user.domain.vo.req.oss.UploadUrlReq;
import com.liaocyu.openChat.common.user.service.OssService;
import com.liaocyu.openchat.oss.MinIOTemplate;
import com.liaocyu.openchat.oss.domain.OssReq;
import com.liaocyu.openchat.oss.domain.OssResp;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/24 11:17
 * @description :
 */
@Service("ossService")
@RequiredArgsConstructor
public class OssServiceImpl implements OssService {

    private final MinIOTemplate minIOTemplate;
    @Override
    public OssResp getUploadUrl(Long uid, UploadUrlReq req) {
        OssSceneEnum sceneEnum = OssSceneEnum.of(req.getScene());
        AssertUtil.isNotEmpty(sceneEnum , "上传的场景有误");
        OssReq ossReq = OssReq.builder()
                .fileName(req.getFileName())
                .filePath(sceneEnum.getPath())
                .uid(uid)
                .build();
        return minIOTemplate.getPreSignedObjectUrl(ossReq);
    }
}
