package org.example.labelprojectjava.redis.impl;

import jakarta.annotation.Resource;
import org.example.labelprojectjava.redis.UserInformationRedis;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
public class UserInformationRedisImpl implements UserInformationRedis {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    private final static Integer free = 0;

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

    @Override
    public boolean setUserFreeWithLuaScript(String userUUID) {
        String luaScript =
                "local removed = redis.call('ZREM', KEYS[1], ARGV[2]) " +
                        "if removed == 1 then " +
                        "    redis.call('ZADD', KEYS[1], ARGV[1], ARGV[2]) " +
                        "    return 1 " +
                        "else " +
                        "    return 0 " +
                        "end";

        DefaultRedisScript<Boolean> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(luaScript);
        redisScript.setResultType(Boolean.class);

        List<String> keys = List.of("user:status");
        return redisTemplate.execute(redisScript, keys, free, userUUID);
    }

}
