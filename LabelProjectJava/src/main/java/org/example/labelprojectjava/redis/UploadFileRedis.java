package org.example.labelprojectjava.redis;

public interface UploadFileRedis {
    void storeUploadFile(String uploadFileMD5);
    void storeUploadFileVector(String uploadFileMD5,String textVectorSequence,String offsetVectorSequence);
    boolean existsUploadFile(String uploadFileMD5);
}
