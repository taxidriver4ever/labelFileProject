package org.example.labelprojectjava.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class FileUpload2Consumer {

    @RabbitListener(queues = "#{uploadFileQueue2.name}")
    public void receive(String filePath) {
        System.out.println(filePath);
    }
}
