package org.example.labelprojectjava.consumer;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.labelprojectjava.redis.UploadFileRedis;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class ManualDeadLetterConsumer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private UploadFileRedis uploadFileRedis;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private SimpMessagingTemplate simpMessagingTemplate;

    private static final String ossIP = "http://127.0.0.1:8080/files/";

    private static final Integer free = 0;

    private static final Integer busy = 1;

    private static final Integer undone = 1;

    private static final String DLX_EXCHANGE_NAME = "dlx.uploadFile.exchange";

    private static final String DLX_ROUTING_KEY = "upload.file.dlx";

    /**
     * 手动从死信队列获取消息
     */
    public void manuallyConsumeDeadLetters() {
        int batchSize = 2; // 每次处理的消息数量
        int processedCount = 0;

        for (int i = 0; i < batchSize; i++) {
            Message message = rabbitTemplate.receive("dead.letter.queue");
            if (message == null) {
                break; // 队列为空
            }

            try {
                String messageBody = new String(message.getBody());
                log.info("手动消费死信消息: {}", messageBody);
                // 处理消息业务逻辑
                processMessage(messageBody);

                processedCount++;

            } catch (Exception e) {
                log.error("处理消息失败: {}", e.getMessage());
            }
        }

        log.info("本次处理了 {} 条死信消息", processedCount);
    }

    private void processMessage(String messageBody) {
        try {
            log.info("处理消息: {}", messageBody);
            sendTask(messageBody);
        } catch (Exception e) {
            throw new RuntimeException("消息处理失败", e);
        }
    }

    private void sendTask(String fileName) {
        getInfo(Thread.currentThread().getName());

        String fileUrl = ossIP + fileName;
        getInfo("Processing file: " + fileUrl);

        String userUUID = getUserWithLuaScript(fileName);

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

    private String getUserWithLuaScript(String fileName) {
        String luaScript =
                "local users = redis.call('ZRANGEBYSCORE', KEYS[1], ARGV[1], ARGV[1], 'LIMIT', 0, 1) " +
                        "if #users == 0 then " +
                        "    return '' " +
                        "end " +
                        "local userUUID = users[1] " +
                        "local removed = redis.call('ZREM', KEYS[1], userUUID) " +
                        "if removed == 1 then " +
                        "    redis.call('ZADD', KEYS[1], ARGV[2], userUUID) " +
                        "    redis.call('HSET', KEYS[2], userUUID, ARGV[3]) " +
                        "    return userUUID " +
                        "else " +
                        "    return '' " +
                        "end";

        DefaultRedisScript<String> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(luaScript);
        redisScript.setResultType(String.class);

        List<String> keys = Arrays.asList("user:status", "uploadFile:userDo:" + fileName);
        return redisTemplate.execute(redisScript, keys, free, busy, undone);
    }

    private static void getInfo(String message) {
        log.info(message);
    }
}
