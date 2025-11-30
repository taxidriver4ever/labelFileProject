package org.example.labelprojectjava.redis;

import java.util.Set;

public interface UploadFileRedis {
    void storeUploadFile(String uploadFileMD5);
    void storeUploadFileVector(String uploadFileMD5,String textVectorSequence,String offsetVectorSequence);
    boolean existsUploadFile(String uploadFileMD5);
    void storeToDoFile(String userUUID, String fileUrl);
    Set<String> getUserToDoFiles(String userUUID);
}
