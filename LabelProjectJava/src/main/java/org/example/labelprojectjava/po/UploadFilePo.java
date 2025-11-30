package org.example.labelprojectjava.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@ToString
public class UploadFilePo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String fileUrl;
    private String textVectorSequence;
    private String offsetVectorSequence;
}
