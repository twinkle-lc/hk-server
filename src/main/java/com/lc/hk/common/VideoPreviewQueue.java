package com.lc.hk.common;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * @author lc
 *  海康威视 摄像头视频实时预览
 **/
public class VideoPreviewQueue {
    /**
     * socket session
     */
    public static Map<String, String> sessionToUserIdMap = new HashMap<>();
    /**
     * 预览用户
     */
    public static Map<String, String> playToUserIdMap = new HashMap<>();

    public static Map<String, BlockingQueue<Object>> bpMap = new HashMap<>();

    /**
     * 通过摄像头登录句柄进行数据清理
     *
     * @param userId
     */
    static public void clearByUserId(String userId) {
        //清理码流数据
        if (null == VideoPreviewQueue.bpMap) {
            VideoPreviewQueue.bpMap = new HashMap<>(8);
        }
        if (null == VideoPreviewQueue.playToUserIdMap) {
            VideoPreviewQueue.playToUserIdMap = new HashMap<>(8);
        }
        if (null == VideoPreviewQueue.sessionToUserIdMap) {
            VideoPreviewQueue.sessionToUserIdMap = new HashMap<>(8);
        }
        for (String key : VideoPreviewQueue.playToUserIdMap.keySet()) {
            String value = VideoPreviewQueue.playToUserIdMap.get(key);
            if (value.equals(userId)) {
                VideoPreviewQueue.playToUserIdMap.remove(key);
                break;
            }
        }
        for (String key : VideoPreviewQueue.sessionToUserIdMap.keySet()) {
            String value = VideoPreviewQueue.sessionToUserIdMap.get(key);
            if (value.equals(userId)) {
                VideoPreviewQueue.sessionToUserIdMap.remove(key);
                break;
            }
        }
    }

    /**
     * 通过摄像头登录句柄查询当前用户是否正在预览
     *
     * @param userId
     */
    static public String findPlayIdByUserId(String userId) {
        for (String key : VideoPreviewQueue.playToUserIdMap.keySet()) {
            String value = VideoPreviewQueue.playToUserIdMap.get(key);
            if (value.equals(userId)) {
                return key;
            }
        }
        return null;
    }
}
