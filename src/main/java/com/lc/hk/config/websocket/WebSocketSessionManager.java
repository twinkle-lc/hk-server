package com.lc.hk.config.websocket;

import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lc
 **/
public class WebSocketSessionManager {
    /**
     * 保存session
     */
    public static ConcurrentHashMap<String, WebSocketSession> SESSION_POLL = new ConcurrentHashMap<>();

    /**
     * 添加session
     */
    public static void add(String sessionId, WebSocketSession session) {
        SESSION_POLL.put(sessionId, session);
    }

    /**
     * 移除session
     */
    public static WebSocketSession remove(String sessionId) {
        return SESSION_POLL.remove(sessionId);
    }

    /**
     * 移除session,并关闭连接
     */
    public static void removeAndClose(String sessionId) {
        WebSocketSession session = SESSION_POLL.remove(sessionId);

        if (session != null) {
            try {
                session.close();
            } catch (Exception e) {
                e.getStackTrace();
            }
        }
    }

    /**
     * 获取session
     */

    public static WebSocketSession get(String sessionId) {
        return SESSION_POLL.get(sessionId);
    }
}
