package org.example.labelprojectjava.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

public interface FileUploadService {
    String uploadFile(MultipartFile file) throws NoSuchAlgorithmException, IOException;
    Set<String> getUrls(String userUUID);
}
