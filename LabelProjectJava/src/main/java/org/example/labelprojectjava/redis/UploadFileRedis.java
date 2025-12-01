package org.example.labelprojectjava.redis;

import java.util.Map;
import java.util.Set;

public interface UploadFileRedis {
    void storeToDoFile(String userUUID, String fileUrl);
    String getUserToDoFiles(String userUUID);
    String setUserDone(String userUUID, String fileUrl);
    void storeFileVector(String fileUrl, String textVectorSequence, String offsetVectorSequence);
    Map<Object,Object> getFileVector(String fileUrl);
}
