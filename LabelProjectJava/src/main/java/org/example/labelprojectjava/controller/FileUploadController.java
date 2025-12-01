package org.example.labelprojectjava.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.labelprojectjava.po.UploadFilePo;
import org.example.labelprojectjava.service.FileUploadService;
import org.example.labelprojectjava.vo.NormalResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("/file")
@Slf4j
public class FileUploadController {

    @Resource
    private FileUploadService fileUploadService;

    @PostMapping("/upload")
    public String uploadFile(@RequestParam MultipartFile file) throws NoSuchAlgorithmException, IOException {
        return fileUploadService.uploadFile(file);
    }

    @PostMapping("/url")
    public NormalResult getUrls(@RequestParam String userUUID){
        String fileUrl = fileUploadService.getUrls(userUUID);
        if(fileUrl != null) return NormalResult.success(fileUrl);
        return NormalResult.error("获取错误");
    }

    @PostMapping("/vector")
    public NormalResult storeVector(@RequestParam String userUUID,
                                    @RequestParam String fileUrl,
                                    @RequestParam String textVectorSequence,
                                    @RequestParam String offsetVectorSequence) throws InterruptedException {
        String s = fileUploadService.storeVector(userUUID, fileUrl, textVectorSequence, offsetVectorSequence);
        log.info(s);
        return NormalResult.success(s);
    }

    @GetMapping("/nothing")
    public NormalResult getNothing(){
        return NormalResult.success();
    }
}
