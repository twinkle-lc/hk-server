package com.lc.hk.config;

import com.lc.hk.common.CommonConstant;
import com.lc.hk.common.RedisUtil;
import com.lc.hk.hksdk.login.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author lc
 **/
@Slf4j
@Component
public class MyCleanupService implements DisposableBean {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private LoginService loginService;

    @Override
    public void destroy() throws Exception {
        log.info("springBoot销毁");
        Set<Object> hgetkey = redisUtil.hashGetKey(CommonConstant.CAMERA_ID);
        if (!hgetkey.isEmpty()) {
            log.info("摄像头执行退出操作");
            for (Object o : hgetkey) {
                loginService.logout(Integer.parseInt(o.toString()));
            }
            redisUtil.delHashKey(CommonConstant.CAMERA_ID);
        }
    }
}
