package org.example.labelprojectjava.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUUIDAndLoginUUID implements Serializable {
    private String userUUID;
    private String loginUUID;
    private String msg;
}
