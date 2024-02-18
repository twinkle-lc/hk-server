package com.lc.hk.hksdk.capture;

import com.lc.hk.hksdk.Common;
import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;

import java.nio.ByteBuffer;

public interface HCNetSDK extends Library, Common {



    /**
     * 设置抓图模式
     */
    boolean NET_DVR_SetCapturePictureMode(int dwCaptureMode);

    /**
     * 单帧数据捕获并保存成JPEG图
     */
    boolean NET_DVR_CaptureJPEGPicture(int lUserID, int lChannel, NET_DVR_JPEGPARA lpJpegPara, byte[] sPicFileName);

    /**
     * JPEG抓图到内存
     */
    boolean NET_DVR_CaptureJPEGPicture_NEW(int lUserID, int lChannel, NET_DVR_JPEGPARA lpJpegPara, ByteBuffer sJpegPicBuffer, int dwPicSize, IntByReference lpSizeReturned);


    boolean NET_DVR_CapturePicture_V50(int lUserID, int lChannel, LPNET_DVR_PICPARAM_V50 lpPicParam, Pointer sPicBuffer, int dwPicSize, IntByReference lpSizeReturned);

    /**
     * 图片质量
     */
    public static class NET_DVR_JPEGPARA extends Structure {
        /*注意：当图像压缩分辨率为VGA时，支持0=CIF, 1=QCIF, 2=D1抓图，
	当分辨率为3=UXGA(1600x1200), 4=SVGA(800x600), 5=HD720p(1280x720),6=VGA,7=XVGA, 8=HD900p
	仅支持当前分辨率的抓图*/
        public short wPicSize;                /* 0=CIF, 1=QCIF, 2=D1 3=UXGA(1600x1200), 4=SVGA(800x600), 5=HD720p(1280x720),6=VGA*/
        public short wPicQuality;            /* 图片质量系数 0-最好 1-较好 2-一般 */
    }

    public static class LPNET_DVR_PICPARAM_V50 extends Structure {

        public NET_DVR_JPEGPARA struParam;
        // 设备抓图图片格式 0-Jpeg
        public byte byPicFormat;
        /**
         * 设备抓图类型:
         * 0-设备通用抓图，1-鹰眼标定图片（FPJA重启刷新获取图片 最长超时3min）,2-从Flash中获取已存在鹰眼标定图片,
         * 3-获取鱼眼相机圆形图片,4-专用高清抓图,5-获取鱼眼相机壁装全景展开图（矩形图）
         */
        public byte byCapturePicType;
        //0-不支持，1-场景1，2-场景2 ……
        public byte bySceneID;
        public byte[] byRes = new byte[253];
    }
}
