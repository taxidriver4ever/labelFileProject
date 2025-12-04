package org.example.labelprojectjava.consumer;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.labelprojectjava.service.SendTaskService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

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
