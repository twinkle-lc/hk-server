package com.lc.hk.hksdk.init;

import cn.hutool.json.JSONUtil;
import com.sun.jna.Native;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * @author lc
 **/
@Service
@Slf4j
public class SdkInitService {
    private HCNetSDK hcNetSdk;

    public void init() {
        if (hcNetSdk == null) {
            synchronized (com.lc.hk.hksdk.login.HCNetSDK.class) {
                try {
                    String loadLibrary = System.getProperty("user.dir") + File.separator + "lib" + File.separator + "HCNetSDK.dll";
                    hcNetSdk = (HCNetSDK) Native.loadLibrary(loadLibrary, HCNetSDK.class);
                    hcNetSdk.NET_DVR_Init();
                    getInfo();
                    log.info("================= 初始化SDK  success ==================");
                } catch (Exception ex) {
                    log.error("[海康威视] InitSdk-error", ex);
                }
            }
        }
    }

    /**
     * 获取设备信息
     */

    private void getInfo() {
        HCNetSDK.NET_DVR_SDKSTATE a = new HCNetSDK.NET_DVR_SDKSTATE();
        hcNetSdk.NET_DVR_GetSDKState(a);
        String jsonStr = JSONUtil.toJsonStr(a);
        log.info("[海康威视] SDK版本信息：{}", Integer.toHexString(hcNetSdk.NET_DVR_GetSDKVersion()));
        log.info("[海康威视] SDK状态信息：{}", jsonStr);

    }

}
