package org.example.labelprojectjava.consumer;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.labelprojectjava.redis.UploadFileRedis;
import org.example.labelprojectjava.redis.UserInformationRedis;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class FileUploadConsumer {

    @Resource
    private UploadFileRedis uploadFileRedis;

    @Resource
    private UserInformationRedis userInformationRedis;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private SimpMessagingTemplate simpMessagingTemplate;

    private static final String ossIP = "http://127.0.0.1:8080/files/";

    private static final Integer free = 0;

    private static final Integer busy = 1;

    @RabbitListener(queues = "#{uploadFileQueue1.name}")
    public void receive(String fileName) {
        sendTask(fileName);
    }

    @RabbitListener(queues = "#{uploadFileQueue2.name}")
    public void receive2(String fileName) {
        sendTask(fileName);
    }

    private void sendTask(String fileName) {
        String fileUrl = ossIP + fileName;
        getInfo(fileUrl);

        String userUUID = getUserWithLuaScript();

        if (!userUUID.isEmpty()) {
            uploadFileRedis.storeToDoFile(userUUID, fileUrl);
            simpMessagingTemplate.convertAndSendToUser(userUUID, "/queue/upload.private", fileUrl);
        }
        getInfo(Thread.currentThread().getName());
    }


    private String getUserWithLuaScript() {
        String luaScript =
                "local users = redis.call('ZRANGEBYSCORE', KEYS[1], ARGV[1], ARGV[1], 'LIMIT', 0, 1) " +
                        "if #users == 0 then " +
                        "    return '' " +
                        "end " +
                        "local userUUID = users[1] " +
                        "local removed = redis.call('ZREM', KEYS[1], userUUID) " +
                        "if removed == 1 then " +
                        "    redis.call('ZADD', KEYS[1], ARGV[2], userUUID) " +
                        "    return userUUID " +
                        "else " +
                        "    return '' " +  // 如果同时被其他进程移除
                        "end";
        
        DefaultRedisScript<String> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(luaScript);
        redisScript.setResultType(String.class);

        List<String> keys = List.of("user:status");
        return redisTemplate.execute(redisScript, keys, free, busy);  // ARGV[1]=0, ARGV[2]=1
    }

    private static void getInfo(String fileUrl) {
        log.info(fileUrl);
    }

}
