package com.lc.hk.hksdk.capture;

import cn.hutool.core.util.StrUtil;
import com.lc.hk.common.*;
import com.lc.hk.config.exception.MyServiceException;
import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * @author lc
 **/
@Service
@Slf4j
public class CaptureService {
    private HCNetSDK hcNetSdk;

    @Autowired
    private RedisUtil redisUtil;

    public void init() {
        if (hcNetSdk == null) {
            synchronized (com.lc.hk.hksdk.thermalimaging.HCNetSDK.class) {
                try {
                    String loadLibrary = System.getProperty("user.dir") + File.separator + "lib" + File.separator + "HCNetSDK.dll";
                    hcNetSdk = (HCNetSDK) Native.loadLibrary(loadLibrary, HCNetSDK.class);
                    log.info("=================设备抓图 Load library  success ==================");
                } catch (Exception ex) {
                    log.error("[海康威视] Load library-error", ex);
                }
            }
        }
    }

    /**
     * 抓取图片
     * 不一定每次都能成功
     */
    public String captureImg(Integer userId,String filePath) {
        if (StrUtil.isBlank(filePath)) {
            return "[海康威视] 保存路径不能为空";
        }

        UserVo userVo = (UserVo)redisUtil.getValue(CommonConstant.CAMERA_ID + userId);
        //图片质量设置
        HCNetSDK.NET_DVR_JPEGPARA config = new HCNetSDK.NET_DVR_JPEGPARA();
        config.wPicSize = (short) 5;
        config.wPicQuality = 0;

        filePath += System.currentTimeMillis() + ".png";
        log.info("[海康威视] 保存路径：{}", filePath);
        boolean b = hcNetSdk.NET_DVR_CaptureJPEGPicture(userId, userVo.getChannelId(), config, filePath.getBytes());
        if (b) {
            log.info("[海康威视] 抓图成功：保存路径：{}", filePath);
            return filePath;
        } else {
            int errorCode = hcNetSdk.NET_DVR_GetLastError();
            String errorMsg = hcNetSdk.NET_DVR_GetErrorMsg(new IntByReference(errorCode));
            log.info("[海康威视] 抓图异常：{}", errorMsg);
        }
        return null;
    }

    /**
     * 基于缓冲区抓图
     * 建议使用这个
     */
    public InputStream captureImgNew(Integer userId, Integer channelId) {
        if (null == userId) {
            throw new MyServiceException(ResponseEnum.HK_USERID_ERROR);
        }
        if (null == channelId) {
            throw new MyServiceException(ResponseEnum.HK_CHANNELID_ERROR);
        }
        //图片质量设置
        HCNetSDK.NET_DVR_JPEGPARA config = new HCNetSDK.NET_DVR_JPEGPARA();
        config.wPicSize = (short) 5;
        config.wPicQuality = 0;
        int nBufferSize = 1024 * 1024 * 5;
        ByteBuffer jpegBuffer = ByteBuffer.allocate(nBufferSize);
        IntByReference imgSize = new IntByReference();
        boolean b = hcNetSdk.NET_DVR_CaptureJPEGPicture_NEW(userId, channelId, config, jpegBuffer, nBufferSize, imgSize);
        if (b) {
            return new ByteArrayInputStream(jpegBuffer.array(), 0, imgSize.getValue());
        } else {
            int errorCode = hcNetSdk.NET_DVR_GetLastError();
            String errorMsg = hcNetSdk.NET_DVR_GetErrorMsg(new IntByReference(errorCode));
            log.info("[海康威视] 抓图异常：{}", errorMsg);
        }
        return null;
    }
}
