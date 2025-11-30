package org.example.labelprojectjava.redis;

import org.springframework.data.redis.core.ZSetOperations;

import java.util.Set;

public interface UserInformationRedis {
    void storeUserUUID(String userUUID,String loginUUID);
    String getUserUUID(String userUUID);
    void storeUserStatus(String userUUID,Integer status);
}
