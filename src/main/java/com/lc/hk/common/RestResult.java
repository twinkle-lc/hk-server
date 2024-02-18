package com.lc.hk.common;

import lombok.Data;

/**
 * @author lc
 **/
@Data
public class RestResult {

    private int code;

    private String msg;

    private Object data;

    public RestResult(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static RestResult ok() {
        return new RestResult(200, "成功", null);
    }
    public static RestResult ok(int code,String msg) {
        return new RestResult(code, msg, null);
    }
    public static RestResult ok(Object data) {
        return new RestResult(200, "成功", data);
    }

    public static RestResult ok(String msg) {
        return new RestResult(200, msg, null);
    }

    public static RestResult ok(int code, String msg, Object data) {
        return new RestResult(code, msg, data);
    }

    public static RestResult fail(String msg) {
        return new RestResult(500, msg, null);
    }
}
