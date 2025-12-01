package org.example.labelprojectjava.redis.impl;

import jakarta.annotation.Resource;
import org.example.labelprojectjava.redis.UploadFileRedis;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Array;
import java.util.*;

@Repository
public class UploadFileRedisImpl implements UploadFileRedis {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    private final static Integer DONE = 1;

    private final static Integer UNDONE = 0;

    private final static Integer FREE = 0;

    @Override
    public void storeToDoFile(String userUUID,String fileUrl) {
        redisTemplate.opsForValue().set("user:toDo:" + userUUID,fileUrl);
    }

    @Override
    public String getUserToDoFiles(String userUUID) {
        return redisTemplate.opsForValue().get("user:toDo:" + userUUID);
    }

    @Override
    public String setUserDone(String userUUID, String fileUrl) {
        String luaScript =
                "local current = redis.call('HGET', KEYS[1], ARGV[1]) " +
                        "if current ~= false and current == ARGV[4] then " +
                        "    redis.call('DEL', KEYS[3]) " +
                        "    redis.call('HSET', KEYS[1], ARGV[1], ARGV[2]) " +
                        "    local fields = redis.call('HKEYS', KEYS[1]) " +
                        "    for i, field in ipairs(fields) do " +
                        "        local value = redis.call('HGET', KEYS[1], field) " +
                        "        if value ~= ARGV[2] then " +
                        "            return '\"incomplete\"' " +
                        "        end " +
                        "    end " +
                        "    for i, field in ipairs(fields) do " +
                        "        redis.call('ZADD', KEYS[2], ARGV[3], field) " +
                        "    end " +
                        "    redis.call('DEL', KEYS[1]) " +
                        "    return '\"complete\"' " +
                        "else " +
                        "    return '\"repeat\"' " +
                        "end";

        DefaultRedisScript<String> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(luaScript);
        redisScript.setResultType(String.class);
        List<String> keys = Arrays.asList("uploadFile:userDo:" + fileUrl, "user:status", "user:toDo:" + userUUID);
        return redisTemplate.execute(redisScript, keys, userUUID, DONE, FREE, UNDONE);
    }

    @Override
    public void storeFileVector(String fileUrl, String textVectorSequence, String offsetVectorSequence) {
        String luaScript =
                "redis.call('HSET', KEYS[1], ARGV[1], ARGV[2])" +
                        "redis.call('HSET', KEYS[1], ARGV[3], ARGV[4])";
        DefaultRedisScript<String> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(luaScript);
        redisScript.setResultType(String.class);
        List<String> keys = List.of("uploadFile:attribute:" + fileUrl);
        redisTemplate.execute(redisScript, keys, "textVectorSequence",textVectorSequence , "offsetVectorSequence",offsetVectorSequence);
    }

    @Override
    public Map<Object,Object> getFileVector(String fileUrl) {
        return redisTemplate.opsForHash().entries("uploadFile:attribute:" + fileUrl);
    }

}
