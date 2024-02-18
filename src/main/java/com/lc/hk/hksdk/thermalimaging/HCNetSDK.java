package com.lc.hk.hksdk.thermalimaging;

import com.lc.hk.hksdk.Common;
import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * @author lc
 */
public interface HCNetSDK extends Library, Common {

    /**
     *实时测温
     */
    int NET_DVR_StartRemoteConfig(int lUserID, int dwCommand, Pointer lpInBuffer, int dwInBufferLen, FRemoteConfigCallBack cbStateCallBack, Pointer pUserData);

    /**
     *停止实时测温
     */
    boolean NET_DVR_StopRemoteConfig(int lHandle);
    /**
     * 回调方法
     */
    public static interface FRemoteConfigCallBack extends Callback {
        public void invoke(int dwType, Pointer lpBuffer, int dwBufLen, Pointer pUserData);
    }
    /**
     * 状态
     */
    public static class REMOTECONFIGSTATUS extends Structure {
        public byte[] byStatus = new byte[4];
        public byte[] byErrorCode = new byte[4];
    }
    /**
     * 实时温度检测条件参数
     */
    public static class NET_DVR_REALTIME_THERMOMETRY_COND extends Structure {
        public int dwSize; /*结构体大小*/
        public int dwChan; /*通道号，从1开始，0xffffffff代表获取全部通道*/
        public byte byRuleID;/*规则ID，0代表获取全部规则，具体规则ID从1开始*/
        public byte byMode;  //长连接模式：0- 保留（兼容不支持该功能的老设备），1- 定时模式，2- 温差模式
        public short wInterval; //上传间隔（仅温差模式支持），取值范围：1-3600 秒，填0则默认3600S上传一次
        public byte[] byRes2 = new byte[60];
    }

    //点测温实时信息
    public static class NET_DVR_POINT_THERM_CFG extends Structure {
        public float fTemperature;
        public NET_VCA_POINT struPoint;
        public byte[] byRes = new byte[120];
    }

    //框/线测温实时信息
    public static class NET_DVR_LINEPOLYGON_THERM_CFG extends Structure {
        public float fMaxTemperature;
        public float fMinTemperature;
        public float fAverageTemperature;
        public float fTemperatureDiff;
        public NET_VCA_POLYGON struRegion;
        public byte[] byRes = new byte[32];
    }
    //点坐标参数
    public static class NET_VCA_POINT extends Structure {
        public float fX;
        public float fY;


    }
    //多边形结构体
    public static class NET_VCA_POLYGON extends Structure {
        public int dwPointNum;
        public NET_VCA_POINT[] struPos = new NET_VCA_POINT[VCA_MAX_POLYGON_POINT_NUM];
    }
    //实时温度信息
    public static class NET_DVR_THERMOMETRY_UPLOAD extends Structure {
        public int dwSize; /* 结构体大小 */
        public int dwRelativeTime;
        public int dwAbsTime;
        public byte[] szRuleName = new byte[NAME_LEN];
        public byte byRuleID;/* 规则ID，0代表获取全部规则，具体规则ID从1开始 */
        public byte byRuleCalibType;
        public short wPresetNo;
        public NET_DVR_POINT_THERM_CFG struPointThermCfg;
        public NET_DVR_LINEPOLYGON_THERM_CFG struLinePolygonThermCfg;
        public byte byThermometryUnit;
        public byte byDataType;
        public byte byRes1;
        public byte bySpecialPointThermType;
        public float fCenterPointTemperature;
        public float fHighestPointTemperature;
        public float fLowestPointTemperature;
        public NET_VCA_POINT struHighestPoint;
        public NET_VCA_POINT struLowestPoint;
        public byte byIsFreezedata;
        public byte[] byRes = new byte[95];
    }

}
