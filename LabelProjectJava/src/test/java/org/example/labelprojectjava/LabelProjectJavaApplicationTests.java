package org.example.labelprojectjava;

import jakarta.annotation.Resource;
import org.example.labelprojectjava.mapper.UserInformationMapper;
import org.example.labelprojectjava.redis.UserInformationRedis;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

@SpringBootTest
class LabelProjectJavaApplicationTests {

    @Resource
    private UserInformationRedis userInformationRedis;

    @Resource
    private UserInformationMapper userInformationMapper;

    @Test
    void contextLoads() {
        userInformationRedis.storeUserStatus("3b61bcb2-0cf8-449e-8517-b17c4d1d693a",0);
        userInformationRedis.storeUserStatus("70b063f7-e25a-4e30-bb8b-2edeba5b455f",0);
        userInformationRedis.storeUserStatus("e57171a6-ad2b-4399-9816-5e949bc5c5e8",0);
        userInformationRedis.storeUserStatus("f61c2dea-a955-47e1-ac37-771cd17641b9",0);
        userInformationRedis.storeUserStatus("dd0f376f-d764-4ef2-a534-2e4d7086fa11",0);
        userInformationRedis.storeUserStatus("6c706509-70f9-4a6c-aa89-e05303183ed2",0);
        userInformationRedis.storeUserStatus("c002eb77-b1a2-4900-903d-a9ae5fa1ad37",0);
        userInformationRedis.storeUserStatus("aefb3110-1c78-4719-8691-479872ef106f",0);
        userInformationRedis.storeUserStatus("cf8ad81d-1271-48b8-99a2-def8d93a9e71",0);
        userInformationRedis.storeUserStatus("5ea798ac-36dc-4b54-bf47-0b498bb61477",0);
    }

}
