package org.example.labelprojectjava.po;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class UserInformationPo implements Serializable {
    private Long id;
    private String userName;
    private String userPassword;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String userUUID;
}
