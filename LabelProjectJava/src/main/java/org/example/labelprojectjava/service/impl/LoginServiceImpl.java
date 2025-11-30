package org.example.labelprojectjava.service.impl;

import jakarta.annotation.Resource;
import org.example.labelprojectjava.mapper.UserInformationMapper;
import org.example.labelprojectjava.redis.UserInformationRedis;
import org.example.labelprojectjava.service.LoginService;
import org.example.labelprojectjava.vo.UserUUIDAndLoginUUID;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LoginServiceImpl implements LoginService {

    @Resource
    private UserInformationMapper userInformationMapper;

    @Resource
    private UserInformationRedis userInformationRedis;

    @Override
    public UserUUIDAndLoginUUID login(String userName, String userPassword) {
        UserUUIDAndLoginUUID userUUIDAndLoginUUID = new UserUUIDAndLoginUUID();
        if(userName != null && userPassword != null) {
            String userUUID = userInformationMapper.getUserUUIDByUserNameAndUserPassword(userName, userPassword);
            if (userUUID != null) {
                userUUIDAndLoginUUID.setUserUUID(userUUID);
                userUUIDAndLoginUUID.setMsg("登录成功");

                String loginUUID = UUID.randomUUID().toString();
                userUUIDAndLoginUUID.setLoginUUID(loginUUID);

                userInformationRedis.storeUserUUID(userUUID, loginUUID);
                return userUUIDAndLoginUUID;
            }
        }
        userUUIDAndLoginUUID.setMsg("登录失败");
        return userUUIDAndLoginUUID;
    }
}
