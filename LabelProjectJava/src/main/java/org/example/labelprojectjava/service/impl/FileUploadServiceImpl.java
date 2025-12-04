package org.example.labelprojectjava.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.labelprojectjava.consumer.ManualDeadLetterConsumer;
import org.example.labelprojectjava.mapper.UploadFileMapper;
import org.example.labelprojectjava.po.UploadFilePo;
import org.example.labelprojectjava.redis.UploadFileRedis;
import org.example.labelprojectjava.redis.UserInformationRedis;
import org.example.labelprojectjava.service.FileUploadService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private UploadFileRedis uploadFileRedis;

    @Resource
    private ManualDeadLetterConsumer manualDeadLetterConsumer;

    @Resource
    private UploadFileMapper uploadFileMapper;

    private static final String SERVERIP = "http://26.46.22.92:8080/files/";


    private final String UPLOAD_DIR;

    {
        UPLOAD_DIR = "D:/private_project/file-to-label/";
    }

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();

        if (originalFilename != null) {

            String fixedId = calculateMD5(file);

            String[] split = originalFilename.split("\\.");

            String fileName = fixedId + "." + split[split.length - 1];

            String fileUrl = SERVERIP + fileName;

            if (!uploadFileRedis.getFileAttribute(fileUrl).isEmpty()) return "该文件已存在";

            uploadFileRedis.storeFileVectorAndFileName(fileUrl,"","", originalFilename);

            File folderPath = new File(UPLOAD_DIR);

            if (!folderPath.exists()) {
                boolean successCreatePath = folderPath.mkdirs();
            }

            String filePath = UPLOAD_DIR + fileName;
            File dest = new File(filePath);
            file.transferTo(dest);
            rabbitTemplate.convertAndSend("fanout.uploadFile.exchange", "", fileUrl);
            return "文件上传成功";
        }

        return "文件上传失败";
    }

    @Override
    public String getUrls(String userUUID) {
        log.info("current thread{}",Thread.currentThread());
        return uploadFileRedis.getUserToDoFiles(userUUID);
    }

    @Override
    public String storeVector(String userUUID, String fileUrl, String textVectorSequence, String offsetVectorSequence) throws InterruptedException {
        String s = uploadFileRedis.setUserDone(userUUID, fileUrl);

        if(s.equals("complete")){
            manualDeadLetterConsumer.manuallyConsumeDeadLetters();
            Map<Object, Object> fileVector = uploadFileRedis.getFileAttribute(fileUrl);

            for (Map.Entry<Object, Object> entry : fileVector.entrySet()) {
                String stringKey = entry.getKey().toString();
                String stringValue = entry.getValue().toString();

                log.info("{}:{}", stringKey, stringValue);

                if(stringKey.equals("\"textVectorSequence\"") && !stringValue.equals(textVectorSequence)){
                    rabbitTemplate.convertAndSend("fanout.uploadFile.exchange", "", fileUrl);
                    uploadFileRedis.storeFileVector(fileUrl, "", "");
                    return "回炉重造";
                }
                else if(stringKey.equals("\"offsetVectorSequence\"") && !stringValue.equals(offsetVectorSequence)){
                    rabbitTemplate.convertAndSend("fanout.uploadFile.exchange", "", fileUrl);
                    uploadFileRedis.storeFileVector(fileUrl, "", "");
                    return "回炉重造";
                }
            }
            rabbitTemplate.convertAndSend("direct.saveMySQL.exchange", "saveMySQL", fileUrl);
            return "消费成功";
        }
        else if(s.equals("incomplete")){
            uploadFileRedis.storeFileVector(fileUrl, textVectorSequence, offsetVectorSequence);
            return "消费成功";
        }
        return "消费失败";
    }

    @Override
    public List<UploadFilePo> getUploadFiles() {
        return uploadFileMapper.selectUploadFileList();
    }

    private static String calculateMD5(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            return DigestUtils.md5DigestAsHex(is);
        }
    }
}
