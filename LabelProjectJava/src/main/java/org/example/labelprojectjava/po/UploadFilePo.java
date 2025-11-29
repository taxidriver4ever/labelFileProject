package org.example.labelprojectjava.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@AllArgsConstructor
@ToString
public class UploadFilePo implements Serializable {
    private String MD5Code;
    private String textVectorSequence;
    private String offsetVectorSequence;
}
