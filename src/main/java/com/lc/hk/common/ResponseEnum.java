package com.lc.hk.common;

public enum ResponseEnum {
    //公共
    SUCCESS(200, "成功"),
    FILED(500, "失败"),

    HK_LOGIN_ERROR(2001,"登录失败"),
    HK_USERID_ERROR(2100,"userId不能为空"),
    HK_CHANNELID_ERROR(2101,"channelId不能为空"),
    HK_VIDEO_PREVIEW_ERROR(2102,"实时预览失败"),
    HK_GET_TEMPERATURE_ERROR(2103,"获取温度失败"),

    REQUEST_NUM(9999, "请求太频繁，请稍后再试");
    private int code;
    private String message;

    ResponseEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    //public String getMessage(int key) {
    //    ResponseEnum[] values = ResponseEnum.values();
    //    for (ResponseEnum value : values) {
    //        if (value.getCode() == key) {
    //            System.out.println(value.getMessage());
    //            return value.getMessage();
    //        }
    //    }
    //
    //    return message;
    //}
    public void setMessage(String message) {
        this.message = message;
    }
}
