package com.lc.hk.hksdk.preview;

import com.lc.hk.hksdk.Common;
import com.sun.jna.*;
import com.sun.jna.ptr.ByteByReference;

public interface HCNetSDK extends Library, Common {
    /**
     * 实时预览
     */

    int NET_DVR_RealPlay_V40(int lUserID, NET_DVR_PREVIEWINFO lpPreviewInfo, FRealDataCallBack_V30 fRealDataCallBack_V30, Pointer pUser);

    /**
     * 停止预览
     */
    boolean NET_DVR_StopRealPlay(int lRealHandle);

    public static class NET_DVR_PACKET_INFO_EX extends Union {
        public short wWidth;         //width
        public short wHeight;        //height
        public int dwTimeStamp;    //lower time stamp
        public int dwTimeStampHigh;//higher time stamp
        public int dwYear;            //year
        public int dwMonth;         //month
        public int dwDay;           //day
        public int dwHour;          //hour
        public int dwMinute;        //minute
        public int dwSecond;        //second
        public int dwMillisecond;   //millisecond
        public int dwFrameNum;     //frame num
        public int dwFrameRate;    //frame rate,当帧率小于0时，0x80000002:表示1/2帧率，同理可推0x80000010为1/16帧率
        public int dwFlag;         //flag E帧标记
        public int dwFilePos;      //file pos
        public int dwPacketType;    //Packet type:0 -file head，1 -video I frame，2- video B frame， 3- video P frame， 10- audio packet， 11- private packet
        public int dwPacketSize;   //packet size
        public Pointer pPacketBuffer;  //packet buffer
        public byte[] byRes1 = new byte[4];
        public int dwPacketMode;   //打包方式:0-保留，1-FU_A打包方式
        public byte[] byRes2 = new byte[16];
        public int[] dwReserved = new int[6];    //reserved[0] 表示私有数据类型
    }
    public static interface FRealDataCallBack_V30 extends Callback {
        public void invoke(int lRealHandle, int dwDataType,
                           ByteByReference pBuffer, int dwBufSize, Pointer pUser);
    }

    //预览V40接口
    public static class NET_DVR_PREVIEWINFO extends Structure {
        public int lChannel;//通道号
        public int dwStreamType;    // 码流类型，0-主码流，1-子码流，2-码流3，3-码流4, 4-码流5,5-码流6,7-码流7,8-码流8,9-码流9,10-码流10
        public int dwLinkMode;// 0：TCP方式,1：UDP方式,2：多播方式,3 - RTP方式，4-RTP/RTSP,5-RSTP/HTTP ,6- HRUDP（可靠传输） ,7-RTSP/HTTPS
        public int bBlocked;  //0-非阻塞取流, 1-阻塞取流, 如果阻塞SDK内部connect失败将会有5s的超时才能够返回,不适合于轮询取流操作.
        public int bPassbackRecord; //0-不启用录像回传,1启用录像回传
        public byte byPreviewMode;//预览模式，0-正常预览，1-延迟预览
        public byte[] byStreamID = new byte[32];//流ID，lChannel为0xffffffff时启用此参数
        public byte byProtoType; //应用层取流协议，0-私有协议，1-RTSP协议
        public byte byRes1;
        public byte byVideoCodingType; //码流数据编解码类型 0-通用编码数据 1-热成像探测器产生的原始数据（温度数据的加密信息，通过去加密运算，将原始数据算出真实的温度值）
        public int dwDisplayBufNum; //播放库播放缓冲区最大缓冲帧数，范围1-50，置0时默认为1
        public byte byNPQMode;    //NPQ是直连模式，还是过流媒体 0-直连 1-过流媒体
        public byte[] byRes = new byte[215];
    }
}
