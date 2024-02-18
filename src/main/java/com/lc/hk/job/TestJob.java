package com.lc.hk.job;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.lc.hk.common.HkLoginVo;
import com.lc.hk.common.RedisUtil;
import com.lc.hk.common.UserVo;
import com.lc.hk.service.HkApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lc
 */
@Component
@Slf4j
public class TestJob {
    private static List<String> list = new ArrayList<>();
    @Autowired
    private HkApiService sdkService;
    @Autowired
    private RedisUtil redisUtil;

    private static Map<String,String> map =new HashMap<>();

    static {
        list.add("127.0.0.18");
    }

    @Scheduled(cron = "${jobCorm}")
    public void run() {
        Object testKey = redisUtil.getValue("testKey");
        if (testKey == null) {
            map=new HashMap<>(6);
            List<Integer> stringList = new ArrayList<>();
            for (String s : list) {
                HkLoginVo hkLoginVo = new HkLoginVo();
                hkLoginVo.setPort(8000);
                hkLoginVo.setIp(s);
                hkLoginVo.setUsername("admin1");
                hkLoginVo.setPassword("Aa12345-");
               UserVo userVo = sdkService.hkLogin(hkLoginVo);
                stringList.add(userVo.getId());
                map.put(userVo.getId()+"",s.replace(".",""));
            }
            redisUtil.setValue("testKey", JSONUtil.toJsonStr(stringList));
        }
        JSONArray objects = JSONUtil.parseArray(testKey);
        for (Object object : objects) {
            log.info("{}：开始抓图",object);
            //截图
            boolean b = sdkService.takePictures(Integer.parseInt(object.toString()), 1, map.get(object.toString()));
            log.info("{}：抓图结束",b );

        }


    }
}
