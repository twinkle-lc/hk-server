package com.lc.hk.hksdk.thermalimaging;

import com.lc.hk.common.CommonConstant;
import com.lc.hk.common.RedisUtil;
import com.sun.jna.Pointer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lc
 **/

@Slf4j
@Component
public class RcxServiceCallBack implements HCNetSDK.FRemoteConfigCallBack {

    @Autowired
    private RedisUtil redisUtil;
    public static Map<String, Object> temperatureMap = new HashMap<>();
    public static List<Integer> idList = new ArrayList<>();

    @Override
    public void invoke(int dwType, Pointer lpBuffer, int dwBufLen, Pointer pUserData) {
        {
            log.info("[海康威视] 获取实时温度回调接口 dwType:{}, dwBufLen:{}", dwType, dwBufLen);
            if (dwType == CommonConstant.DW_TYPE) {
                HCNetSDK.NET_DVR_THERMOMETRY_UPLOAD temperatureUpload = new HCNetSDK.NET_DVR_THERMOMETRY_UPLOAD();
                temperatureUpload.write();
                Pointer pInfoV30 = temperatureUpload.getPointer();
                pInfoV30.write(0, lpBuffer.getByteArray(0, temperatureUpload.size()), 0, temperatureUpload.size());
                temperatureUpload.read();
                if (temperatureUpload.byRuleCalibType == CommonConstant.DOT) {
                    log.info("[海康威视] 点测温信息:" + temperatureUpload.struPointThermCfg.fTemperature);
                }
                if ((int) temperatureUpload.byRuleCalibType == CommonConstant.BOX_SELECTION
                        || (int) temperatureUpload.byRuleCalibType == CommonConstant.LINE) {
                    if (!idList.contains((int) temperatureUpload.byRuleID)) {
                        log.info("规则id：{}", temperatureUpload.byRuleID);
                        log.info("[海康威视] 最大温度" + temperatureUpload.struLinePolygonThermCfg.fMaxTemperature);
                        log.info("[海康威视] 最小温度" + temperatureUpload.struLinePolygonThermCfg.fMinTemperature);
                        log.info("[海康威视] 平均温度" + temperatureUpload.struLinePolygonThermCfg.fAverageTemperature);
                        log.info("[海康威视] 温度差值" + temperatureUpload.struLinePolygonThermCfg.fTemperatureDiff);
                        log.info("[海康威视] 中心温度" + temperatureUpload.fCenterPointTemperature);
                        BigDecimal bigDecimal = new BigDecimal(temperatureUpload.struLinePolygonThermCfg.fMaxTemperature).setScale(2, RoundingMode.HALF_UP);
                        idList.add((int) temperatureUpload.byRuleID);
                        temperatureMap.put(temperatureUpload.byRuleID + "", bigDecimal.toString());
                    } else {
                        redisUtil.setValue(CommonConstant.CLEAN_DATA, "0");
                    }


                }
            }
        }
    }
}
