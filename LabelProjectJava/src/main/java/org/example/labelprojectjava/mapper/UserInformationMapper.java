package org.example.labelprojectjava.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.labelprojectjava.po.UserInformationPo;

import java.util.List;

@Mapper
public interface UserInformationMapper {
    List<String> getUserUUID();
    String getUserUUIDByUserNameAndUserPassword(String userName,String userPassword);
}
