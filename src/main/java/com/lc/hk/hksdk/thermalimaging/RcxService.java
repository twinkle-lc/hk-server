package com.lc.hk.hksdk.thermalimaging;


import com.lc.hk.common.CommonConstant;
import com.lc.hk.common.RedisUtil;
import com.lc.hk.common.ResponseEnum;
import com.lc.hk.config.exception.MyServiceException;
import com.lc.hk.hksdk.Common;
import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author lc
 **/

@Slf4j
@Service
public class RcxService {

    private HCNetSDK hcNetSdk;

    @Autowired
    private RedisUtil redisUtil;
    private int lHandleId;
    @Autowired
    RcxServiceCallBack rcxServiceCallBack;
    private final ReentrantLock lock = new ReentrantLock();

    public void init() {
        if (hcNetSdk == null) {
            synchronized (HCNetSDK.class) {
                try {
                    String loadLibrary = System.getProperty("user.dir") + File.separator + "lib" + File.separator + "HCNetSDK.dll";
                    hcNetSdk = (HCNetSDK) Native.loadLibrary(loadLibrary, HCNetSDK.class);
                    log.info("=================热成像 Load library  success ==================");
                } catch (Exception ex) {
                    log.error("[海康威视] Load library-error", ex);
                }
            }
        }
    }


    /**
     * 获取温度
     */

    public String getTemperature(Integer userId) {
        lock.lock();
        try {
            if (null == userId) {
                return "userId不能为空";
            }
            //缓冲区大小
            HCNetSDK.NET_DVR_REALTIME_THERMOMETRY_COND config = new HCNetSDK.NET_DVR_REALTIME_THERMOMETRY_COND();
            config.read();
            config.dwSize = config.size();
            config.byRuleID = 0;
            config.dwChan = 1;
            config.write();
            //启动远程配置
            lHandleId = hcNetSdk.NET_DVR_StartRemoteConfig(userId, Common.NET_DVR_GET_REALTIME_THERMOMETRY, config.getPointer(), config.size(), rcxServiceCallBack, null);
            if (lHandleId < 0) {
                int errorCode = hcNetSdk.NET_DVR_GetLastError();
                String errorMsg = hcNetSdk.NET_DVR_GetErrorMsg(new IntByReference(errorCode));
                log.info("[海康威视] 获取温度失败：{}", errorMsg);
                ResponseEnum.HK_GET_TEMPERATURE_ERROR.setMessage(errorMsg);
                throw new MyServiceException(ResponseEnum.HK_GET_TEMPERATURE_ERROR);
            } else {
                log.info("[海康威视] 获取温度成功");
                redisUtil.setValue(CommonConstant.CLEAN_DATA, "1");
                boolean flag = false;
                while (!flag) {
                    flag = CommonConstant.STATE_ENABLE.equals(redisUtil.getValue(CommonConstant.CLEAN_DATA));
                    if (flag) {
                        stopGetTemperature();
                        //将回调函数中的list清除
                        RcxServiceCallBack.idList.clear();
                    }
                }
            }
            return null;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 停止获取温度
     */
    private void stopGetTemperature() {
        boolean b = hcNetSdk.NET_DVR_StopRemoteConfig(lHandleId);
        if (b) {
            log.info("[海康威视] 关闭长连接配置接口所创建的句柄成功");
        } else {
            int errorCode = hcNetSdk.NET_DVR_GetLastError();
            String errorMsg = hcNetSdk.NET_DVR_GetErrorMsg(new IntByReference(errorCode));
            log.info("[海康威视] 关闭长连接配置接口所创建的句柄成功失败：{}", errorMsg);
        }
    }

}
