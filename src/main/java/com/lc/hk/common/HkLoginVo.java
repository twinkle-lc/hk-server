package com.lc.hk.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lc
 **/
@Data
public class HkLoginVo implements Serializable {
    private String ip;
    private String username;
    private String password;
    private Integer port;
}
