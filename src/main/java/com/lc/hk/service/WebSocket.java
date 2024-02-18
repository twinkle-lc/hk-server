package com.lc.hk.service;

import com.lc.hk.common.VideoPreviewQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;

/**
 * @author lc
 **/

@Slf4j
@ServerEndpoint(value = "/hk/{lUserID}")
@Component
public class WebSocket {


    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(final Session session, @PathParam("lUserID") String lUserID) {
        log.info("有新连接加入sessionid：{}，摄像头登录用户的userId为：{}", session.getId(), lUserID);
        if (VideoPreviewQueue.bpMap.containsKey(lUserID)) {
            if (null == VideoPreviewQueue.findPlayIdByUserId(lUserID)) {
                System.out.println(String.format("警告:根据登录句柄%s,没有找到用户预览句柄", lUserID));
            }
            BlockingQueue<Object> blockingQueue = VideoPreviewQueue.bpMap.get(lUserID);
            while (session.isOpen() && null != blockingQueue) {
                try {
                    byte[] esBytes = (byte[]) blockingQueue.take();
                    if (esBytes.length < 1) {
                        System.out.println("取流失败，无内容");
                        continue;
                    }
                    ByteBuffer data = ByteBuffer.wrap(esBytes);
                    session.getBasicRemote().sendBinary(data);
                } catch (Exception e) {
                    log.error("socket 数据发失败，错误信息为：" + e.getMessage());
                    return;
                }
            }
        } else {
            log.info("当前没有找到用户:{},无法播放", lUserID);
            try {
                session.close();
            } catch (IOException e) {
                log.error("socket session关闭异常，错误信息为：" + e.getMessage());
            }
        }
    }
}
