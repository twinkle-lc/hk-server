package com.lc.hk.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lc
 **/
@Data
public class UserVo implements Serializable {
    private Integer id;
    private Integer channelId;
}
