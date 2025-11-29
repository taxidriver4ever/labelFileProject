package org.example.labelprojectjava.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class FileUpload1Consumer {

    @RabbitListener(queues = "#{uploadFileQueue1.name}")
    public void receive(String filePath) {
        System.out.println(filePath);
    }
}
