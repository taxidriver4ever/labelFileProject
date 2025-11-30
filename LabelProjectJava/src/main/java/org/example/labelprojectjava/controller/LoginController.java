package org.example.labelprojectjava.controller;

import jakarta.annotation.Resource;
import org.example.labelprojectjava.po.UserInformationPo;
import org.example.labelprojectjava.service.LoginService;
import org.example.labelprojectjava.vo.NormalResult;
import org.example.labelprojectjava.vo.UserUUIDAndLoginUUID;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("/login")
public class LoginController {

    @Resource
    private LoginService loginService;

    @PostMapping("/name-password")
    public NormalResult namePassword(@RequestBody UserInformationPo userInformationPo) {
        UserUUIDAndLoginUUID login = loginService.login(userInformationPo.getUserName(), userInformationPo.getUserPassword());
        String msg = login.getMsg();
        if (msg.equals("登录成功")) return NormalResult.success(login);
        return NormalResult.error(msg);
    }

}
