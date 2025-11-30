package org.example.labelprojectjava.controller;

import jakarta.annotation.Resource;
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
public class FileUploadController {

    @Resource
    private FileUploadService fileUploadService;

    @PostMapping("/upload")
    public String uploadFile(@RequestParam MultipartFile file) throws NoSuchAlgorithmException, IOException {
        return fileUploadService.uploadFile(file);
    }

    @PostMapping("/urls")
    public NormalResult getUrls(@RequestParam String userUUID){
        Set<String> urls = fileUploadService.getUrls(userUUID);
        if(!urls.isEmpty()) return NormalResult.success(urls);
        return NormalResult.error("获取错误");
    }

}
