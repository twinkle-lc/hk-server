package com.lc.hk.hksdk.login;


import com.lc.hk.common.CommonConstant;
import com.lc.hk.common.RedisUtil;
import com.lc.hk.common.ResponseEnum;
import com.lc.hk.common.UserVo;
import com.lc.hk.config.exception.MyServiceException;
import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * @author lc
 **/

@Slf4j
@Service
public class LoginService {
    @Getter
    private HCNetSDK hcNetSdk;
    @Autowired
    private RedisUtil redisUtil;

    public void init() {
        if (hcNetSdk == null) {
            synchronized (HCNetSDK.class) {
                try {
                    String loadLibrary = System.getProperty("user.dir") + File.separator + "lib" + File.separator + "HCNetSDK.dll";
                    hcNetSdk = (HCNetSDK) Native.loadLibrary(loadLibrary, HCNetSDK.class);
                    log.info("=================登录 Load library  success ==================");
                } catch (Exception ex) {
                    log.error("InitSdk-error", ex);
                }
            }
        }
    }

    /**
     * 登录
     *
     * @return 用户ID号
     */

    public UserVo login(String ip, String username, String password, Integer port) {
        log.info("用户登录：ip:{}, username:{}, password:{}", ip, username, password);
        HCNetSDK.NET_DVR_USER_LOGIN_INFO loginInfo = new HCNetSDK.NET_DVR_USER_LOGIN_INFO();
        loginInfo.sDeviceAddress = new byte[HCNetSDK.NET_DVR_DEV_ADDRESS_MAX_LEN];
        System.arraycopy(ip.getBytes(), 0, loginInfo.sDeviceAddress, 0, ip.length());

        loginInfo.sUserName = new byte[HCNetSDK.NET_DVR_LOGIN_USERNAME_MAX_LEN];
        System.arraycopy(username.getBytes(), 0, loginInfo.sUserName, 0, username.length());

        loginInfo.sPassword = new byte[HCNetSDK.NET_DVR_LOGIN_PASSWD_MAX_LEN];
        System.arraycopy(password.getBytes(), 0, loginInfo.sPassword, 0, password.length());
        //设备端口号
        loginInfo.wPort = port.shortValue();
        //是否异步登录：0- 否，1- 是
        loginInfo.bUseAsynLogin = false;
        loginInfo.write();
        //设备信息
        HCNetSDK.NET_DVR_DEVICEINFO_V40 mStrDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V40();
        int userId = hcNetSdk.NET_DVR_Login_V40(loginInfo, mStrDeviceInfo);
        if (userId == -1) {
            int errorCode = hcNetSdk.NET_DVR_GetLastError();
            IntByReference errorInt = new IntByReference(errorCode);
            log.error("[海康威视] login fail errorCode:{}, errMsg:{}", errorCode, hcNetSdk.NET_DVR_GetErrorMsg(errorInt));
            ResponseEnum.HK_LOGIN_ERROR.setMessage(hcNetSdk.NET_DVR_GetErrorMsg(errorInt));
            throw new MyServiceException(ResponseEnum.HK_LOGIN_ERROR);
        } else {
            byte dvrType = mStrDeviceInfo.struDeviceV30.byDVRType;
            log.info("[海康威视] 设备类型：" + String.valueOf(dvrType));
            byte byStartChan = mStrDeviceInfo.struDeviceV30.byStartChan;
            log.info("[海康威视] 预览起始通道号：" + byStartChan);
            //相机一般只有一个通道号，热成像相机有2个通道号，通道号为1或1,2
            //byStartDChan为IP通道起始通道号, 预览回放NVR的IP通道时需要根据起始通道号进行取值
            if ((int) mStrDeviceInfo.struDeviceV30.byStartDChan == 1 || (int) mStrDeviceInfo.struDeviceV30.byStartDChan == 33) {
                //byStartDChan为IP通道起始通道号, 预览回放NVR的IP通道时需要根据起始通道号进行取值,NVR起始通道号一般是33或者1开始
                int ldChannel = mStrDeviceInfo.struDeviceV30.byStartDChan;
                log.info("[海康威视] 预览起始通道号：" + ldChannel);
            }
            loginInfo.read();
            UserVo userVo = new UserVo();
            userVo.setId(userId);
            userVo.setChannelId((int) byStartChan);
            redisUtil.hashSet(CommonConstant.CAMERA_ID, userId + "", userVo);
            log.info("[海康威视] login success: userId=>" + userId);
            return userVo;
        }
    }

    /**
     * 退出
     *
     * @return boolean
     */
    public Boolean logout(Integer userId) {
        if (hcNetSdk.NET_DVR_Logout(userId)) {
            return true;
        } else {
            int errorCode = hcNetSdk.NET_DVR_GetLastError();
            IntByReference errorInt = new IntByReference(errorCode);
            log.error("[海康威视] logout fail errorCode:{}, errMsg:{}", errorCode, hcNetSdk.NET_DVR_GetErrorMsg(errorInt));
        }
        return false;

    }


}
