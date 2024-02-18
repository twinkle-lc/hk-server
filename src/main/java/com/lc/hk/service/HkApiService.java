package com.lc.hk.service;

import com.lc.hk.common.HkLoginVo;
import com.lc.hk.common.HkLogoutVo;
import com.lc.hk.common.RcxVo;
import com.lc.hk.common.UserVo;

import java.util.List;

/**
 * @author lc
 */
public interface HkApiService {
    /**
     * 登录
     * @param hkLoginVo 参数
     * @return 结果
     */
    UserVo hkLogin(HkLoginVo hkLoginVo);

    /**
     * 拍摄图片
     * @param userId 用户id
     * @param channelId 通道id
     * @return 结果
     */
    boolean takePictures(Integer userId, Integer channelId,String prefix);

    /**
     *拍摄图片
     * @param userId 用户id
     * @param channelId 通道id
     * @return 结果
     */
    String takePicturesToInputStream(Integer userId, Integer channelId);

    /**
     * 获取热成像温度
     * @param userId 用户id
     * @return 结果
     */
    List<RcxVo> getRcxList(Integer userId);

    /**
     * 视频预览
     * @param userId 用户id
     * @return 结果
     */
    int videoPreview(Integer userId);

    /**
     * 退出
     * @param hkLogoutVo
     * @return
     */

    boolean hkLogout(HkLogoutVo hkLogoutVo);
}
