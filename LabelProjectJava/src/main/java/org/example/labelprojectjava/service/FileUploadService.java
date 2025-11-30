package org.example.labelprojectjava.service;

import org.example.labelprojectjava.po.UploadFilePo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

public interface FileUploadService {
    String uploadFile(MultipartFile file) throws NoSuchAlgorithmException, IOException;
    String getUrls(String userUUID);
    String storeVector(String textVectorSequence, String userUUID, String offsetVectorSequence);
}
