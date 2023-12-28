package com.liaocyu.openChat.common.user.domain.entity;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/26 15:58
 * @description :
 */
@Data
public class IpInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    // 注册时的ip
    private String createIp;
    // 注册时的ip详情
    private IpDetail createIpDetail;
    // 最新登录的ip
    private String updateIp;
    // 最新登录的ip详情
    private IpDetail updateIpDetail;


    public void refreshIp(String ip) {
        if (StringUtils.isBlank(ip)) {
            return;
        }
        if (StringUtils.isBlank(createIp)) {
            createIp = ip;
        }
        updateIp = ip;
    }

    public String needRefreshIp() {
        boolean notNeedRefresh = Optional.ofNullable(updateIpDetail)
                .map(IpDetail::getIp)
                .filter(ip -> Objects.equals(ip, updateIp))
                .isPresent();
        return notNeedRefresh ? null : updateIp;
    }

    public void refreshIpDetail(IpDetail ipDetail) {
        if (Objects.equals(createIp, ipDetail.getIp())) {
            createIpDetail = ipDetail;
        }
        if (Objects.equals(updateIp, ipDetail.getIp())) {
            updateIpDetail = ipDetail;
        }
    }
}
