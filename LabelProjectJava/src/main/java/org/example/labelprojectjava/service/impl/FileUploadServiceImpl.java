package org.example.labelprojectjava.service.impl;

import jakarta.annotation.Resource;
import org.example.labelprojectjava.redis.UploadFileRedis;
import org.example.labelprojectjava.service.FileUploadService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private UploadFileRedis uploadFileRedis;

    private final String UPLOAD_DIR;

    {
        UPLOAD_DIR = "D:/private_project/file-to-label/";
    }

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();

        String fixedId = calculateMD5(file);

        if(uploadFileRedis.existsUploadFile(fixedId)) return "该文件已存在";

        uploadFileRedis.storeUploadFile(fixedId);

        File folderPath = new File(UPLOAD_DIR);

        if(!folderPath.exists()) {
            boolean successCreatePath = folderPath.mkdirs();
        }

        if (originalFilename != null) {
            String[] split = originalFilename.split("\\.");
            String fileName = fixedId + "." + split[split.length - 1];
            String filePath = UPLOAD_DIR + fileName;
            File dest = new File(filePath);
            file.transferTo(dest);
            rabbitTemplate.convertAndSend("fanout.uploadFile.exchange", "", fileName);
            return "文件上传成功";
        }

        return "文件上传失败";
    }

    @Override
    public Set<String> getUrls(String userUUID) {
        return uploadFileRedis.getUserToDoFiles(userUUID);
    }

    private static String calculateMD5(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            return DigestUtils.md5DigestAsHex(is);
        }
    }
}
