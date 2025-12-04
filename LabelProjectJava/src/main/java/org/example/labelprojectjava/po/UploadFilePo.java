package org.example.labelprojectjava.po;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@ToString
public class UploadFilePo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @ExcelProperty("文件ID")
    private Long id;

    @ExcelProperty("文件链接")
    private String fileUrl;

    @ExcelProperty("文档向量")
    private String textVectorSequence;

    @ExcelProperty("文档向量偏移量")
    private String offsetVectorSequence;

    @ExcelProperty("文件名称")
    private String fileName;

    @ExcelProperty("文件审批成功到数据库的时间")
    private String createdTime;

    public UploadFilePo(String fileUrl, String textVectorSequence, String offsetVectorSequence, String fileName) {
        this.fileUrl = fileUrl;
        this.textVectorSequence = textVectorSequence;
        this.offsetVectorSequence = offsetVectorSequence;
        this.fileName = fileName;
    }
}
