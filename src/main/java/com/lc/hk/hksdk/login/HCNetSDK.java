package com.lc.hk.hksdk.login;

import com.lc.hk.hksdk.Common;
import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * @author lc
 */
public interface HCNetSDK extends Library, Common {
    public static final int NET_DVR_DEV_ADDRESS_MAX_LEN = 129;
    public static final int NET_DVR_LOGIN_USERNAME_MAX_LEN = 64;
    public static final int NET_DVR_LOGIN_PASSWD_MAX_LEN = 64;

    int NET_DVR_Login_V40(NET_DVR_USER_LOGIN_INFO pLoginInfo, NET_DVR_DEVICEINFO_V40 lpDeviceInfo);



    boolean NET_DVR_Logout(int lUserID);
    //sdk状态信息

    //sdk版本信息

    /**
     * NET_DVR_Login_V40()参数
     */
    public static class NET_DVR_USER_LOGIN_INFO extends Structure {
        public byte[] sDeviceAddress = new byte[NET_DVR_DEV_ADDRESS_MAX_LEN];
        public byte byUseTransport;
        public short wPort;
        public byte[] sUserName = new byte[NET_DVR_LOGIN_USERNAME_MAX_LEN];
        public byte[] sPassword = new byte[NET_DVR_LOGIN_PASSWD_MAX_LEN];
        public FLoginResultCallBack cbLoginResult;
        public Pointer pUser;
        public boolean bUseAsynLogin;
        //0:不使用代理，1：使用标准代理，2：使用EHome代理
        public byte byProxyType;
        //0-不进行转换，默认,1-接口上输入输出全部使用UTC时间,SDK完成UTC时间与设备时区的转换,2-接口上输入输出全部使用平台本地时间，SDK完成平台本地时间与设备时区的转换
        public byte byUseUTCTime;
        //0-Private 1-ISAPI 2-自适应
        public byte byLoginMode;
        //0-不适用tls，1-使用tls 2-自适应
        public byte byHttps;
        //代理服务器序号，添加代理服务器信息时，相对应的服务器数组下表值
        public int iProxyID;
        //认证方式，0-不认证，1-双向认证，2-单向认证；认证仅在使用TLS的时候生效;
        public byte byVerifyMode;
        public byte[] byRes2 = new byte[119];
    }


    public static class NET_DVR_DEVICEINFO_V40 extends Structure {
        public NET_DVR_DEVICEINFO_V30 struDeviceV30 = new NET_DVR_DEVICEINFO_V30();
        public byte bySupportLock;
        public byte byRetryLoginTime;
        public byte byPasswordLevel;
        public byte byRes1;
        public int dwSurplusLockTime;
        //字符编码类型：0- 无字符编码信息(老设备)，1- GB2312(简体中文)，2- GBK，3- BIG5(繁体中文)，4- Shift_JIS(日文)，5- EUC-KR(韩文)，6- UTF-8，7- ISO8859-1，8- ISO8859-2，9- ISO8859-3，…，依次类推，21- ISO8859-15(西欧)
        public byte byCharEncodeType;
        //支持v50版本的设备参数获取，设备名称和设备类型名称长度扩展为64字节
        public byte bySupportDev5;
        //能力集扩展，位与结果：0- 不支持，1- 支持
        public byte bySupport;
        //登录模式 0-Private登录 1-ISAPI登录
        public byte byLoginMode;
        public int dwOEMCode;
        //该用户密码剩余有效天数，单位：天，返回负值，表示密码已经超期使用，例如“-3表示密码已经超期使用3天”
        public int iResidualValidity;
        // iResidualValidity字段是否有效，0-无效，1-有效
        public byte byResidualValidity;
        //独立音轨接入的设备，起始接入通道号，0-为保留字节，无实际含义，音轨通道号不能从0开始
        public byte bySingleStartDTalkChan;
        //独立音轨接入的设备的通道总数，0-表示不支持
        public byte bySingleDTalkChanNums;
        //0-无效，1-管理员创建一个非管理员用户为其设置密码，该非管理员用户正确登录设备后要提示“请修改初始登录密码”，未修改的情况下，用户每次登入都会进行提醒；2-当非管理员用户的密码被管理员修改，该非管理员用户再次正确登录设备后，需要提示“请重新设置登录密码”，未修改的情况下，用户每次登入都会进行提醒。
        public byte byPassWordResetLevel;
        //能力集扩展，位与结果：0- 不支持，1- 支持 bySupportStreamEncrypt & 0x1:表示是否支持RTP/TLS取流 bySupportStreamEncrypt & 0x2:  表示是否支持SRTP/UDP取流 bySupportStreamEncrypt & 0x4:  表示是否支持SRTP/MULTICAST取流
        public byte bySupportStreamEncrypt;
        //0-无效（未知类型）,1-经销型，2-行业型
        public byte byMarketType;
        public byte[] byRes2 = new byte[238];
    }
    public static interface FLoginResultCallBack extends Callback {
        public int invoke(int lUserID, int dwResult, HCNetSDK.NET_DVR_DEVICEINFO_V30 lpDeviceinfo, Pointer pUser);
    }
    public static class NET_DVR_DEVICEINFO_V30 extends Structure {
        //序列号
        public byte[] sSerialNumber = new byte[SERIALNO_LEN];
        //报警输入个数
        public byte byAlarmInPortNum;
        //报警输出个数
        public byte byAlarmOutPortNum;
        //硬盘个数
        public byte byDiskNum;
        //设备类型, 1:DVR 2:ATM DVR 3:DVS ......
        public byte byDVRType;
        //模拟通道个数
        public byte byChanNum;
        //起始通道号,例如DVS-1,DVR - 1
        public byte byStartChan;
        //语音通道数
        public byte byAudioChanNum;
        //最大数字通道个数，低位
        public byte byIPChanNum;
        //零通道编码个数 //2010-01-16
        public byte byZeroChanNum;
        //主码流传输协议类型 0-private, 1-rtsp,2-同时支持private和rtsp
        public byte byMainProto;
        //子码流传输协议类型0-private, 1-rtsp,2-同时支持private和rtsp
        public byte bySubProto;
        //能力，位与结果为0表示不支持，1表示支持，
        public byte bySupport;
        // 能力集扩充，位与结果为0表示不支持，1表示支持
        public byte bySupport1;
        /*能力*/
        public byte bySupport2;
        //设备型号
        public short wDevType;
        //能力集扩展
        public byte bySupport3;
        //是否支持多码流,按位表示,0-不支持,1-支持,bit1-码流3,bit2-码流4,bit7-主码流，bit-8子码流
        public byte byMultiStreamProto;
        //起始数字通道号,0表示无效
        public byte byStartDChan;
        //起始数字对讲通道号，区别于模拟对讲通道号，0表示无效
        public byte byStartDTalkChan;
        //数字通道个数，高位
        public byte byHighDChanNum;
        //能力集扩展
        public byte bySupport4;
        // 支持语种能力,按位表示,每一位0-不支持,1-支持
        public byte byLanguageType;
        //  byLanguageType 等于0 表示 老设备
        //  byLanguageType & 0x1表示支持中文
        //  byLanguageType & 0x2表示支持英文
        //音频输入通道数
        public byte byVoiceInChanNum;
        //音频输入起始通道号 0表示无效
        public byte byStartVoiceInChanNo;
        public byte bySupport5;
        //能力
        public byte bySupport6;
        //镜像通道个数，<录播主机中用于表示导播通道>
        public byte byMirrorChanNum;
        //起始镜像通道号
        public short wStartMirrorChanNo;
        //能力
        public byte bySupport7;
        //保留
        public byte byRes2;
    }



}
