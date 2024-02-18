package com.lc.hk.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.lc.hk.common.*;
import com.lc.hk.hksdk.capture.CaptureService;
import com.lc.hk.hksdk.login.LoginService;
import com.lc.hk.hksdk.preview.VideoPreviewService;
import com.lc.hk.hksdk.thermalimaging.RcxService;
import com.lc.hk.hksdk.thermalimaging.RcxServiceCallBack;
import com.lc.hk.service.HkApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author lc
 **/

@Service
@Slf4j
public class HkApiServiceImpl implements HkApiService {
    @Autowired
    private LoginService loginService;
    @Autowired
    private CaptureService captureService;
    @Autowired
    private RcxService rcxService;
    @Autowired
    private VideoPreviewService videoPreviewService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private MinioUtil minioUtil;
    @Value("${imgPath}")
    private String path;

    @Override
    public UserVo hkLogin(HkLoginVo hkLoginVo) {
        return loginService.login(hkLoginVo.getIp(), hkLoginVo.getUsername(), hkLoginVo.getPassword(), hkLoginVo.getPort());
    }

    /**
     * 抓拍图片（通用）
     */
    @Override
    public boolean takePictures(Integer userId, Integer channelId,String prefix) {
        try {
            File directory = new File(path+prefix);
            // 判断文件夹是否存在
            if (!directory.exists()) {
                boolean mkdir = directory.mkdirs();
                log.info("创建文件夹：{}",mkdir);
            }
            String filePath =path+prefix+ "\\"+DateUtil.format(new Date(), DatePattern.PURE_DATETIME_FORMAT) + ".png";
            InputStream inputStream = captureService.captureImgNew(userId, channelId);
            if (inputStream != null) {
                BufferedImage image = ImageIO.read(inputStream);
                ImageIO.write(image, "png", new File(filePath));
                log.info("抓拍图片成功,图片保存在:{}", filePath);
                return true;
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public String takePicturesToInputStream(Integer userId, Integer channelId) {
        InputStream inputStream = captureService.captureImgNew(userId, channelId);
        String name = IdUtil.simpleUUID() + ".png";
        String timePrefix = DateUtil.format(DateUtil.date(), "yyyyMMdd");
        String imgPath = timePrefix + "/" + name;
        minioUtil.uploadInputStream("image", imgPath, inputStream, "image/png");
        loginService.logout(userId);
        return imgPath;
    }

    /**
     * 获取热成像温度
     *
     * @param userId 设备登录返回的id
     */
    @Override
    public List<RcxVo> getRcxList(Integer userId) {
        List<RcxVo> list = new ArrayList<>();
        String temperature = rcxService.getTemperature(userId);
        if (temperature != null) {
            return list;
        }
        Map<String, Object> temperatureMap = RcxServiceCallBack.temperatureMap;
        if (temperatureMap != null) {
            for (String s : temperatureMap.keySet()) {
                RcxVo rcxVo = new RcxVo();
                rcxVo.setId(s);
                rcxVo.setValue(temperatureMap.get(s).toString());
                list.add(rcxVo);
            }
        }
        loginService.logout(userId);
        return list;
    }

    /**
     * 实时预览
     *
     * @param userId id
     */
    @Override
    public int videoPreview(Integer userId) {
        int videoId = videoPreviewService.videoPreview(userId);
        log.info("预览句柄：{}", videoId);
        redisUtil.setValue("videoPreview" + userId, videoId);
        redisUtil.setValue("realHandle" + videoId, userId);
        return videoId;
    }

    @Override
    public boolean hkLogout(HkLogoutVo hkLogoutVo) {
        return  loginService.logout(Integer.parseInt(hkLogoutVo.getId()));
    }
}
