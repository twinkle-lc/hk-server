package com.lc.hk.service.socket;

import com.lc.hk.config.websocket.WebSocketSessionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.nio.ByteBuffer;

/**
 * @author lc
 **/

@Service
@Slf4j
public class WebSocketService {

    /**
     * 发送文本消息
     *
     * @author lc
     */
    public void sendMsg(String sessionId, String text) {
        try {
            WebSocketSession session = WebSocketSessionManager.get(sessionId);
            session.sendMessage(new TextMessage(text));
        } catch (Exception e) {
            log.error("socket--Error sending message", e);
        }
    }
    /**
     * 发送字节消息
     *
     * @author lc
     */
    public void sendBinary(String sessionId, ByteBuffer buffer) {
        try {
            WebSocketSession session = WebSocketSessionManager.get(sessionId);
            session.sendMessage(new BinaryMessage(buffer));
        } catch (Exception e) {
            log.error("socket--Error sending message", e);
        }
    }

    /**
     * 广播消息
     *
     * @author lc
     */
    public void broadCastMsg(String text) {
        try {
            for (WebSocketSession session : WebSocketSessionManager.SESSION_POLL.values()) {
                session.sendMessage(new TextMessage(text));
            }
        } catch (Exception e) {
            log.error("socket--Error sending message", e);
        }
    }

}
