package com.lc.hk.config.hksdk;

import com.lc.hk.hksdk.capture.CaptureService;
import com.lc.hk.hksdk.init.SdkInitService;
import com.lc.hk.hksdk.login.LoginService;
import com.lc.hk.hksdk.preview.VideoPreviewService;
import com.lc.hk.hksdk.thermalimaging.RcxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author lc
 **/
@Component
@Slf4j
public class InitSdk implements ApplicationRunner {
    @Autowired
    private LoginService loginService;
    @Autowired
    private RcxService rcxService;
    @Autowired
    private CaptureService captureService;
    @Autowired
    private VideoPreviewService videoPreviewService;
    @Autowired
    private SdkInitService sdkInitService;

    @Override
    public void run(ApplicationArguments args) {
        //登录
        loginService.init();
        //热成像
        rcxService.init();
        //抓图
        captureService.init();
        //实时预览
        videoPreviewService.init();
        //初始化SKD
        sdkInitService.init();
    }

}
