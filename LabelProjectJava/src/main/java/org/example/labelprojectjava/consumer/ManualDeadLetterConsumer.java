package org.example.labelprojectjava.consumer;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.labelprojectjava.service.SendTaskService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
public class ManualDeadLetterConsumer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private SendTaskService sendTaskService;

    private static final String DLX_QUEUE_NAME = "dlx.uploadFile.queue";
    // 使用 ReentrantLock 替代 synchronized 方法，更灵活
    private final Lock lock = new ReentrantLock();

    /**
     * 手动从死信队列获取消息
     */
    public void manuallyConsumeDeadLetters() {
        // 尝试获取锁，避免长时间阻塞
        if (!lock.tryLock()) {
            log.info("已有其他线程正在处理死信消息");
            return;
        }

        try {
            processDeadLettersInBatch(); // 每次处理2条
        } finally {
            lock.unlock();
        }
    }

    private void processDeadLettersInBatch() {
        int availableMessages = getQueueMessageCount();
        log.info("死信队列中的消息数量: {}", availableMessages);

        if (availableMessages < 2) {
            log.info("消息数量不足 {} 条，当前只有 {} 条", 2, availableMessages);
            return;
        }

        int processedCount = 0;
        for (int i = 0; i < 2; i++) {
            Message message = rabbitTemplate.receive(DLX_QUEUE_NAME);
            if (message == null) {
                break;
            }

            try {
                processSingleMessage(message);
                processedCount++;
            } catch (Exception e) {
                log.error("处理消息失败，消息将重新入队: {}", e.getMessage());
                // 失败的消息重新放回队列
                requeueMessage(message);
            }
        }

        log.info("本次成功处理了 {} 条死信消息", processedCount);
    }

    private void processSingleMessage(Message message) throws Exception {
        String messageBody = new String(message.getBody());
        log.info("处理死信消息: {}", messageBody);

        // 处理业务逻辑
        sendTaskService.sendTask(messageBody);

        // 确认消息已处理
        rabbitTemplate.execute(channel -> {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            return null;
        });
    }

    private void requeueMessage(Message message) {
        try {
            // 重新发送到死信队列
            rabbitTemplate.send(DLX_QUEUE_NAME, message);
            log.info("失败消息已重新入队");
        } catch (Exception e) {
            log.error("重新入队失败: {}", e.getMessage());
        }
    }

    private int getQueueMessageCount() {
        try {
            return rabbitTemplate.execute(channel ->
                    channel.queueDeclarePassive(DLX_QUEUE_NAME).getMessageCount()
            );
        } catch (Exception e) {
            log.error("获取队列消息数失败", e);
            return 0;
        }
    }
}
