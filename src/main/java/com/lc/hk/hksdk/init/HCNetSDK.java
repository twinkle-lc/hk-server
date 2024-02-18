package com.lc.hk.hksdk.init;

import com.lc.hk.hksdk.Common;
import com.sun.jna.Library;
import com.sun.jna.Structure;

public interface HCNetSDK extends Library, Common {
    boolean NET_DVR_Init();
    int NET_DVR_GetSDKVersion();
    boolean NET_DVR_GetSDKState(NET_DVR_SDKSTATE pSDKState);

    //SDK状态信息(9000新增)
    public static class NET_DVR_SDKSTATE extends Structure {
        public int dwTotalLoginNum;        //当前login用户数
        public int dwTotalRealPlayNum;    //当前realplay路数
        public int dwTotalPlayBackNum;    //当前回放或下载路数
        public int dwTotalAlarmChanNum;    //当前建立报警通道路数
        public int dwTotalFormatNum;        //当前硬盘格式化路数
        public int dwTotalFileSearchNum;    //当前日志或文件搜索路数
        public int dwTotalLogSearchNum;    //当前日志或文件搜索路数
        public int dwTotalSerialNum;        //当前透明通道路数
        public int dwTotalUpgradeNum;    //当前升级路数
        public int dwTotalVoiceComNum;    //当前语音转发路数
        public int dwTotalBroadCastNum;    //当前语音广播路数
        public int[] dwRes = new int[10];
    }
}
