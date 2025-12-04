package org.example.labelprojectjava.redis;

import java.util.Map;

public interface UploadFileRedis {
    void storeToDoFile(String userUUID, String fileUrl);
    String getUserToDoFiles(String userUUID);
    String setUserDone(String userUUID, String fileUrl);
    void storeFileVectorAndFileName(String fileUrl, String textVectorSequence, String offsetVectorSequence, String fileName);
    void storeFileVector(String fileUrl, String textVectorSequence, String offsetVectorSequence);
    Map<Object, Object> getFileAttribute(String fileUrl);
}
