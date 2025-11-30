package org.example.labelprojectjava.redis.impl;

import jakarta.annotation.Resource;
import org.example.labelprojectjava.redis.UserInformationRedis;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
public class UserInformationRedisImpl implements UserInformationRedis {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void storeUserUUID(String userUUID, String LoginUUID) {
        redisTemplate.opsForValue().set("user:uuid:" + userUUID, LoginUUID, 5, TimeUnit.DAYS);
    }

    @Override
    public String getUserUUID(String userUUID) {
        return redisTemplate.opsForValue().get("user:uuid:" + userUUID);
    }

    @Override
    public void storeUserStatus(String userUUID, Integer status) {
        redisTemplate.opsForZSet().add("user:status", userUUID, status);
    }


}
