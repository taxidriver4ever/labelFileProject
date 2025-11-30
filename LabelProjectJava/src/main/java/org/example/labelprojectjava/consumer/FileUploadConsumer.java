package org.example.labelprojectjava.consumer;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.labelprojectjava.redis.UploadFileRedis;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import java.util.List;

@Slf4j
@Component
public class FileUploadConsumer {

    @Resource
    private UploadFileRedis uploadFileRedis;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private SimpMessagingTemplate simpMessagingTemplate;

    @Resource
    private RabbitTemplate rabbitTemplate;

    private static final String ossIP = "http://127.0.0.1:8080/files/";

    private static final Integer free = 0;

    private static final Integer busy = 1;

    private static final String DLX_EXCHANGE_NAME = "dlx.uploadFile.exchange";

    private static final String DLX_ROUTING_KEY = "upload.file.dlx";

    @RabbitListener(queues = "#{uploadFileQueue1.name}")
    public void receive(String fileName) {
        sendTask(fileName);
    }

    @RabbitListener(queues = "#{uploadFileQueue2.name}")
    public void receive2(String fileName) {
        sendTask(fileName);
    }

    private void sendTask(String fileName) {
        getInfo(Thread.currentThread().getName());

        String fileUrl = ossIP + fileName;
        getInfo("Processing file: " + fileUrl);

        String userUUID = getUserWithLuaScript();

        if (userUUID != null && !userUUID.isEmpty()) {
            // 成功找到空闲用户，正常处理
            uploadFileRedis.storeToDoFile(userUUID, fileUrl);
            simpMessagingTemplate.convertAndSendToUser(userUUID, "/queue/upload.private", fileUrl);
            log.info("File {} assigned to user {}", fileUrl, userUUID);
        } else {
            // 没有空闲用户，将消息发送到死信队列永久保存
            sendToDlxQueue(fileName);
            log.warn("No available users found for file {}, sent to DLX queue", fileUrl);
        }
    }

    /**
     * 将消息发送到死信队列永久保存
     */
    private void sendToDlxQueue(String fileName) {
        try {
            // 创建消息，设置持久化
            Message message = MessageBuilder
                    .withBody(fileName.getBytes())
                    .setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN)
                    .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                    .build();

            // 发送到死信交换机
            rabbitTemplate.send(DLX_EXCHANGE_NAME, DLX_ROUTING_KEY, message);

            log.info("Message sent to DLX queue: {}", fileName);
        } catch (Exception e) {
            log.error("Failed to send message to DLX queue: {}", fileName, e);
            // 这里可以添加重试逻辑或者其他的错误处理策略
        }
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
                        "    return '' " +
                        "end";

        DefaultRedisScript<String> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(luaScript);
        redisScript.setResultType(String.class);

        List<String> keys = List.of("user:status");
        return redisTemplate.execute(redisScript, keys, free, busy);
    }

    private static void getInfo(String message) {
        log.info(message);
    }

}
