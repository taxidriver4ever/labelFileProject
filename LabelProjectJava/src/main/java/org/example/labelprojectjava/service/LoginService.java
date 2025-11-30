package org.example.labelprojectjava.service;

import org.example.labelprojectjava.vo.UserUUIDAndLoginUUID;

public interface LoginService {
    UserUUIDAndLoginUUID login(String username, String password);
}
