package org.example.labelprojectjava.service.pre;

import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class QueueDestroyService {

    @Resource
    private AmqpAdmin amqpAdmin;

    @Resource
    private Queue uploadFileQueue1;

    @Resource
    private Queue uploadFileQueue2;

    /**
     * 手动销毁配置中的匿名队列
     */
    @PreDestroy
    public void destroyConfiguredQueues() {

        getInfo("开始销毁配置中的匿名队列...");

        try {
            // 销毁第一个队列
            String queue1Name = uploadFileQueue1.getName();
            amqpAdmin.deleteQueue(queue1Name);
            getInfo("队列销毁成功: " + queue1Name);
        } catch (Exception e) {
            getInfo("队列1销毁失败: " + e.getMessage());
        }

        try {
            // 销毁第二个队列
            String queue2Name = uploadFileQueue2.getName();
            amqpAdmin.deleteQueue(queue2Name);
            getInfo("队列销毁成功: " + queue2Name);
        } catch (Exception e) {
            getInfo("队列2销毁失败: " + e.getMessage());
        }

        getInfo("队列销毁完成");
    }

    private static void getInfo(String queue1Name) {
        log.info(queue1Name);
    }
}