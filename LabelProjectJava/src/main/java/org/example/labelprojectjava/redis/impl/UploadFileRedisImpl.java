package org.example.labelprojectjava.redis.impl;

import jakarta.annotation.Resource;
import org.example.labelprojectjava.redis.UploadFileRedis;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public class UploadFileRedisImpl implements UploadFileRedis {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void storeUploadFile(String uploadFileMD5) {
        redisTemplate.opsForHash().put("uploadFile:attribute:" + uploadFileMD5, "textVectorSequence", "");
        redisTemplate.opsForHash().put("uploadFile:attribute:" + uploadFileMD5, "offsetVectorSequence", "");
    }

    @Override
    public void storeUploadFileVector(String uploadFileMD5,String textVectorSequence,String offsetVectorSequence) {
        redisTemplate.opsForHash().put("uploadFile:attribute:" + uploadFileMD5, "textVectorSequence", textVectorSequence);
        redisTemplate.opsForHash().put("uploadFile:attribute:" + uploadFileMD5, "offsetVectorSequence", offsetVectorSequence);
    }

    @Override
    public boolean existsUploadFile(String uploadFileMD5) {
        Object o = redisTemplate.opsForHash().get("uploadFile:attribute:" + uploadFileMD5, "textVectorSequence");
        return o != null;
    }

    @Override
    public void storeToDoFile(String userUUID,String fileUrl) {
        redisTemplate.opsForZSet().add("user:toDoList:" + userUUID,fileUrl,System.currentTimeMillis());
    }

    @Override
    public Set<String> getUserToDoFiles(String userUUID) {
        return redisTemplate.opsForZSet().range("user:toDoList:" + userUUID, 0, -1);
    }

}
