package org.example.labelprojectjava.redis.impl;

import jakarta.annotation.Resource;
import org.example.labelprojectjava.redis.UploadFileRedis;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UploadFileRedisImpl implements UploadFileRedis {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void storeUploadFile(String uploadFileMD5) {
        redisTemplate.opsForHash().put("uploadFile:attribute:" + uploadFileMD5, "TextVectorSequence", "");
        redisTemplate.opsForHash().put("uploadFile:attribute:" + uploadFileMD5, "OffsetVectorSequence", "");
    }

    @Override
    public void storeUploadFileVector(String uploadFileMD5,String textVectorSequence,String offsetVectorSequence) {
        redisTemplate.opsForHash().put("uploadFile:attribute:" + uploadFileMD5, "TextVectorSequence", textVectorSequence);
        redisTemplate.opsForHash().put("uploadFile:attribute:" + uploadFileMD5, "OffsetVectorSequence", offsetVectorSequence);
    }

    @Override
    public boolean existsUploadFile(String uploadFileMD5) {
        Object o = redisTemplate.opsForHash().get("uploadFile:attribute:" + uploadFileMD5, "TextVectorSequence");
        return o != null;
    }


}
