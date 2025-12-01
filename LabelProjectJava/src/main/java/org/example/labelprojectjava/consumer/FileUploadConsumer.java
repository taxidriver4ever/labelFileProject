package org.example.labelprojectjava.consumer;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.labelprojectjava.redis.UploadFileRedis;
import org.example.labelprojectjava.service.SendTaskService;
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

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class FileUploadConsumer {

    @Resource
    private SendTaskService sendTaskService;

    @RabbitListener(queues = "#{uploadFileQueue1.name}")
    public void receive(String fileUrl) {
        sendTaskService.sendTask(fileUrl);
    }

    @RabbitListener(queues = "#{uploadFileQueue2.name}")
    public void receive2(String fileUrl) {
        sendTaskService.sendTask(fileUrl);
    }

}
