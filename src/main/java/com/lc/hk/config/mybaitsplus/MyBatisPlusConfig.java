package com.lc.hk.config.mybaitsplus;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.lc.hk.common.CommonConstant;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author lc
 **/
@Component
public class MyBatisPlusConfig implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", LocalDateTime::now, LocalDateTime.class);
        //this.strictInsertFill(metaObject, "createdBy", UserUtil::getUserId, String.class);
        this.strictInsertFill(metaObject, "delFlag", () -> CommonConstant.STATE_ENABLE, String.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class);
        //this.strictUpdateFill(metaObject, "updatedBy", UserUtil::getUserId, String.class);
    }
}
