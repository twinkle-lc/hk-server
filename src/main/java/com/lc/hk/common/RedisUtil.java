package com.lc.hk.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * @author lc
 **/
@Component
public class RedisUtil {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void setValue(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void setValue(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public Object getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hashSet(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Object hashGet(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    public Set<Object> hashGetKey(String key) {
        return redisTemplate.opsForHash().keys(key);
    }

    public void delHashKey(String key) {
        Set<Object> keys = redisTemplate.opsForHash().keys(key);
        redisTemplate.opsForHash().delete(key, (Object) keys.toArray(new Object[0]));
    }

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().getOperations().hasKey(key));
    }

    public void deleteKey(String key) {
        redisTemplate.delete(key);

    }

    public void deleteKeysByPrefix(String prefix) {
        Set<String> keys = redisTemplate.keys(prefix + "*");
        if (!CollectionUtils.isEmpty(keys)) {
            redisTemplate.delete(keys);
        }
    }

}
