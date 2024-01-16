package com.liaocyu.openChat.common.user.service.impl;

import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.http.HttpUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.liaocyu.openChat.common.common.domain.vo.resp.ApiResult;
import com.liaocyu.openChat.common.user.dao.UserDao;
import com.liaocyu.openChat.common.user.domain.entity.IpDetail;
import com.liaocyu.openChat.common.user.domain.entity.IpInfo;
import com.liaocyu.openChat.common.user.domain.entity.User;
import com.liaocyu.openChat.common.user.service.IpService;
import com.liaocyu.openchat.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/26 16:28
 * @description :
 */
@Service
@Slf4j
public class IpServiceImpl implements IpService, DisposableBean {

    private final UserDao userDao;

    @Autowired
    public IpServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    private static ExecutorService executor = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(500), new NamedThreadFactory("refresh-ipDetail", false));

    @Override
    public void refreshDetailAsync(Long uid) {
        executor.execute(() -> {
            User user = userDao.getById(uid);
            IpInfo ipInfo = user.getIpInfo();
            if (Objects.isNull(ipInfo)) {
                return;
            }
            String ip = ipInfo.needRefreshIp();
            if (StringUtils.isBlank(ip)) {
                return;
            }
            IpDetail ipDetail = tryGetIpDetailOrNullTreeTimes(ip);
            if (Objects.nonNull(ipDetail)) {
                // 刷新注册的和更新的ip
                ipInfo.refreshIpDetail(ipDetail);
                User update = new User();
                update.setId(uid);
                update.setIpInfo(ipInfo);
                userDao.updateById(update);
            }
        });
    }

    private static IpDetail tryGetIpDetailOrNullTreeTimes(String ip) {
        for (int i = 0; i < 3; i++) {
            IpDetail ipDetail = getIpDetailOrNull(ip);
            if (Objects.nonNull(ipDetail)) {
                return ipDetail;
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                log.error("tryGetIpDetailOrNullTreeTimesTreeTimes InterruptedException", e);
            }
        }
        return null;
    }

    private static IpDetail getIpDetailOrNull(String ip) {
        try {
            String url = "https://ip.taobao.com/outGetIpInfo?ip=%s&accessKey=alibaba-inc";
            String ipUrl = String.format(url, ip);
            String data = HttpUtil.get(ipUrl);
            ApiResult<IpDetail> result = JsonUtils.toObj(data, new TypeReference<ApiResult<IpDetail>>() {
            });
            IpDetail detail = result.getData();
            return detail;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 优雅停机
     *
     * @throws Exception
     */
    @Override
    public void destroy() throws Exception {
        // 线程池不会再接收新的任务了 ，等待任务执行完成之后再进行优雅停机
        executor.shutdown();
        // 最多等待30s 处理剩余任务，如果30s没有处理完成，那么就直接丢掉该任务
        if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
            if (log.isErrorEnabled()) {
                log.error("Timed out while waiting for executor [{}] to terminate", executor);
            }
        }
    }

    public static void main(String[] args) {
        Date begin = new Date(); // 记录一下当前时间
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            executor.execute(() -> {
                IpDetail ipDetail = tryGetIpDetailOrNullTreeTimes("112.96.226.245");
                if (Objects.nonNull(ipDetail)) {
                    Date date = new Date();
                    System.out.println(String.format("第%d次成功，目前耗时%dms", finalI, (date.getTime() - begin.getTime())));
                }
            });
        }
    }


}
