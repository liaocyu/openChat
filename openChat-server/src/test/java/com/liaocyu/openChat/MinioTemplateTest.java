package com.liaocyu.openChat;

import com.liaocyu.openchat.oss.MinIOTemplate;
import com.liaocyu.openchat.oss.domain.OssReq;
import com.liaocyu.openchat.oss.domain.OssResp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/24 15:43
 * @description :
 */
@SpringBootTest
public class MinioTemplateTest {

    @Autowired
    private MinIOTemplate minIOTemplate;

    @Test
    public void getUploadUrl() {
        OssReq ossReq = OssReq.builder()
                .fileName("test.jpeg")
                .filePath("/test")
                .autoPath(false)
                .build();
        OssResp preSignedObjectUrl = minIOTemplate.getPreSignedObjectUrl(ossReq);
        System.out.println(preSignedObjectUrl);
    }
}
