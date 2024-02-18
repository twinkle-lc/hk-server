package com.lc.hk.config.exception;

import com.lc.hk.common.RestResult;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;


/**
 * @author lc
 **/
@RestControllerAdvice
public class RestExceptionAdvice {

    @ExceptionHandler(value = {Exception.class})
    public RestResult exception(Exception e) {
        return RestResult.ok(500, e.getMessage());
    }

    @ExceptionHandler(value = {MyServiceException.class})
    public RestResult restException(MyServiceException e) {
        return RestResult.ok(e.getResponseEnum().getCode(), e.getResponseEnum().getMessage());
    }

    @ExceptionHandler(value = {BindException.class})
    public RestResult validationException(BindException e) {
        return RestResult.ok(400, e.getMessage());
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public RestResult validationException(MethodArgumentNotValidException e) {
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        if (!allErrors.isEmpty()) {
            return RestResult.ok(400, allErrors.get(0).getDefaultMessage());
        }
        return RestResult.ok();
    }

}
