package org.example.labelprojectjava.consumer;

import jakarta.annotation.Resource;
import org.example.labelprojectjava.mapper.UploadFileMapper;
import org.example.labelprojectjava.po.UploadFilePo;
import org.example.labelprojectjava.redis.UploadFileRedis;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SaveMySQLConsumer {

    public static final String DIRECT_SAVE_MYSQL_QUEUE_NAME = "direct.saveMySQL.queue";

    @Resource
    private UploadFileRedis uploadFileRedis;

    @Resource
    private UploadFileMapper uploadFileMapper;

    @Transactional(rollbackFor = Exception.class)
    @RabbitListener(queues = DIRECT_SAVE_MYSQL_QUEUE_NAME)
    public void receiveMySQLMessage(String fileUrl) {
        Map<Object, Object> fileAttributeMap = uploadFileRedis.getFileAttribute(fileUrl);
        String fileName = "";
        String textVectorSequence = "";
        String offsetVectorSequence = "";
        for (Map.Entry<Object, Object> entry : fileAttributeMap.entrySet()) {
            switch (entry.getKey().toString()) {
                case "\"fileName\"":
                    fileName = (String) entry.getValue();
                    break;
                case "\"textVectorSequence\"":
                    textVectorSequence = (String) entry.getValue();
                    break;
                case "\"offsetVectorSequence\"":
                    offsetVectorSequence = (String) entry.getValue();
                    break;
            }
        }
        UploadFilePo uploadFilePo = new UploadFilePo(fileUrl, textVectorSequence, offsetVectorSequence, fileName);
        uploadFileMapper.insertUploadFile(uploadFilePo);
    }
}
