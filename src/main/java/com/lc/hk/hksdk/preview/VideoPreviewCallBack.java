package com.lc.hk.hksdk.preview;

import com.lc.hk.common.RedisUtil;
import com.lc.hk.service.socket.WebSocketService;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;

/**
 * @author lc
 **/

@Component
@Slf4j
public class VideoPreviewCallBack implements HCNetSDK.FRealDataCallBack_V30 {
    @Autowired
    private WebSocketService webSocketService;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void invoke(int lRealHandle, int dwDataType, ByteByReference pBuffer, int dwBufSize, Pointer pUser) {
        switch (dwDataType) {
            case HCNetSDK.NET_DVR_SYSHEAD:
                //系统头
                break;
            case HCNetSDK.NET_DVR_STREAMDATA:
                //码流数据
                if ((dwBufSize > 0)) {
                    byte[] outputData = pBuffer.getPointer().getByteArray(0, dwBufSize);
                    try {
                        String userId = redisUtil.getValue("realHandle" + lRealHandle).toString();
                        ByteBuffer data = ByteBuffer.wrap(outputData);
                        webSocketService.sendBinary(userId, data);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
                break;
            default:
                break;
        }
    }
}
