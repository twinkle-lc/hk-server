package com.lc.hk.config.websocket;

import com.lc.hk.common.RedisUtil;
import com.lc.hk.hksdk.preview.VideoPreviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.net.URI;

/**
 * @author lc
 **/
@Component("webSocketHandler")
@Slf4j
public class WebSocketHandler extends AbstractWebSocketHandler {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private VideoPreviewService videoPreviewService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        //连接成功后触发
        log.info("建立websocket连接");
        URI uri = session.getUri();
        String userId = null;
        if (uri != null) {
            userId = uri.getQuery().split("=")[1];
        }
        log.info("sessionId:{}", userId);
        session.getAttributes().put("userId", userId);
        WebSocketSessionManager.add(userId, session);

        log.info("连接用户信息:{}", WebSocketSessionManager.SESSION_POLL);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        //客户端发送普通文件信息时触发
        log.info("接收文本消息");
        String payload = message.getPayload();
        log.info("服务的接收到的消息:{}", payload);
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        //客户端发送二进制信息时触发
        log.info("客户端发送二进制信息");
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        //异常时触发
        log.info("socket异常处理");
        Object userId = redisUtil.getValue("videoPreview" + session.getAttributes().get("userId"));
        videoPreviewService.videoPreviewStop(Integer.parseInt(userId.toString()));
        WebSocketSessionManager.remove(session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        //关闭连接时触发
        log.info("关闭socket连接");
        Object userId = redisUtil.getValue("videoPreview" + session.getAttributes().get("userId"));
        videoPreviewService.videoPreviewStop(Integer.parseInt(userId.toString()));
        log.info("关闭句柄为：{}的视频预览",userId);
        WebSocketSessionManager.removeAndClose(session.getId());
    }
}
