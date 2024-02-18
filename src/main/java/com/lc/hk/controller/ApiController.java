package com.lc.hk.controller;

import com.lc.hk.common.*;
import com.lc.hk.service.HkApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author lc
 **/
@RestController
@RequestMapping("/service")
public class ApiController {
    @Autowired
    private HkApiService sdkService;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 设备登录
     */

    @PostMapping(value = "/login")
    private RestResult login(@RequestBody HkLoginVo hkLoginVo) {
        return RestResult.ok(sdkService.hkLogin(hkLoginVo));
    }

    /**
     * 设备退出
     */

    @PostMapping(value = "/logout")
    private RestResult logout(@RequestBody HkLogoutVo hkLogoutVo) {
        return RestResult.ok(sdkService.hkLogout(hkLogoutVo));
    }

    /**
     * 实时预览
     */
    @GetMapping(value = "/videoPreview/{id}")
    public RestResult videoPreview(@PathVariable("id") Integer id) {
        int videoed = sdkService.videoPreview(id);
        return RestResult.ok(videoed);

    }

    /**
     * 获取温度
     */
    @GetMapping(value = "/getInfo/{id}")
    public RestResult test(@PathVariable("id") Integer id) {
        return RestResult.ok(sdkService.getRcxList(id));
    }

    /**
     * 抓图
     */
    @GetMapping(value = "/takePictures/{id}")
    public RestResult takePictures(@PathVariable("id") Integer id) {
        UserVo value = (UserVo) redisUtil.hashGet(CommonConstant.CAMERA_ID, id + "");
        return RestResult.ok(sdkService.takePicturesToInputStream(id, value.getChannelId()));

    }


}
