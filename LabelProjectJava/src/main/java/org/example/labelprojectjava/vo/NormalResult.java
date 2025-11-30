package org.example.labelprojectjava.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class NormalResult implements Serializable {

    private Integer code;
    private String msg;
    private Object data;

    public static NormalResult success(Object data) {
        return new NormalResult(200, "success", data);
    }

    public static NormalResult success() {
        return new NormalResult(200, "success", null);
    }

    public static NormalResult error(String msg) {
        return new NormalResult(500, msg, null);
    }
}
