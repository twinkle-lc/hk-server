package com.lc.hk.hksdk.preview;

import com.lc.hk.common.ResponseEnum;
import com.lc.hk.config.exception.MyServiceException;
import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * @author lc
 **/

@Service
@Slf4j
public class VideoPreviewService {

    private HCNetSDK hcNetSdk;
    @Autowired
    private VideoPreviewCallBack videoPreviewCallBack;

    public void init() {
        if (hcNetSdk == null) {
            synchronized (HCNetSDK.class) {
                try {
                    String loadLibrary = System.getProperty("user.dir") + File.separator + "lib" + File.separator + "HCNetSDK.dll";
                    hcNetSdk = (HCNetSDK) Native.loadLibrary(loadLibrary, HCNetSDK.class);
                    log.info("=================预览 Load library  success ==================");
                } catch (Exception ex) {
                    log.error("[海康威视] Load library-error", ex);
                }
            }
        }
    }

    public int videoPreview(Integer userId) {
        log.info("[海康威视] 实时预览，userId：{}", userId);
        HCNetSDK.NET_DVR_PREVIEWINFO netDvrPreviewinfo = new HCNetSDK.NET_DVR_PREVIEWINFO();
        //通道号
        netDvrPreviewinfo.lChannel = 1;
        //码流类型
        netDvrPreviewinfo.dwStreamType = 0;
        //连接方式
        netDvrPreviewinfo.dwLinkMode = 0;
        //延迟预览模式：0- 正常预览，1- 延迟预览
        netDvrPreviewinfo.byPreviewMode = 0;
        netDvrPreviewinfo.byVideoCodingType = 0;
        int realHandle = hcNetSdk.NET_DVR_RealPlay_V40(userId, netDvrPreviewinfo, videoPreviewCallBack, null);
        if (realHandle < 0) {
            int errorCode = hcNetSdk.NET_DVR_GetLastError();
            String errorMsg = hcNetSdk.NET_DVR_GetErrorMsg(new IntByReference(errorCode));
            log.info("[海康威视] 实时预览失败：{}", errorMsg);
            throw new MyServiceException(ResponseEnum.HK_VIDEO_PREVIEW_ERROR);
        }
        log.info("[海康威视] 实时预览成功：{}", realHandle);
        return realHandle;
    }

    public void videoPreviewStop(Integer realHandle) {
        hcNetSdk.NET_DVR_StopRealPlay(realHandle);
    }
}
