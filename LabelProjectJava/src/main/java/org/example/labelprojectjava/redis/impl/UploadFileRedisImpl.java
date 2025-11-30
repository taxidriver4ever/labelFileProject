package org.example.labelprojectjava.redis.impl;

import jakarta.annotation.Resource;
import org.example.labelprojectjava.redis.UploadFileRedis;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public class UploadFileRedisImpl implements UploadFileRedis {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void storeFileWithLuaScript(String fileMD5) {
        String luaScript =
                "redis.call('HSET', KEYS[1], 'textVectorSequence', '', 'offsetVectorSequence', '', 'textVectorSequence2', '', 'offsetVectorSequence2', '') " +
                        "return true";

        DefaultRedisScript<Boolean> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(luaScript);
        redisScript.setResultType(Boolean.class);

        List<String> keys = List.of("uploadFile:attribute:" + fileMD5);
        redisTemplate.execute(redisScript, keys);
    }

    @Override
    public void storeUploadFileVector(String uploadFileMD5,String textVectorSequence,String offsetVectorSequence) {
        redisTemplate.opsForHash().put("uploadFile:attribute:" + uploadFileMD5, "textVectorSequence", textVectorSequence);
        redisTemplate.opsForHash().put("uploadFile:attribute:" + uploadFileMD5, "offsetVectorSequence", offsetVectorSequence);
    }

    @Override
    public boolean existsUploadFile(String uploadFileMD5) {
        return redisTemplate.opsForHash().get("uploadFile:attribute:" + uploadFileMD5, "textVectorSequence") != null;
    }

    @Override
    public void storeToDoFile(String userUUID,String fileUrl) {
        redisTemplate.opsForValue().set("user:toDo:" + userUUID,fileUrl);
    }

    @Override
    public String getUserToDoFiles(String userUUID) {
        return redisTemplate.opsForValue().get("user:toDo:" + userUUID);
    }

}
