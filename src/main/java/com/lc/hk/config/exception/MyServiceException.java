package com.lc.hk.config.exception;

import com.lc.hk.common.ResponseEnum;

/**
 * @author lc
 **/

public class MyServiceException extends RuntimeException {
    private ResponseEnum responseEnum;

    public MyServiceException(ResponseEnum message) {
        this.responseEnum = message;
    }

    public ResponseEnum getResponseEnum() {
        return responseEnum;
    }

    public void setResponseEnum(ResponseEnum responseEnum) {
        this.responseEnum = responseEnum;
    }
}
