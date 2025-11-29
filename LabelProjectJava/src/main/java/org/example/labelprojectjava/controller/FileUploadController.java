package org.example.labelprojectjava.controller;

import jakarta.annotation.Resource;
import org.example.labelprojectjava.service.FileUploadService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

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


}
