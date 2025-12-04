package org.example.labelprojectjava.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.labelprojectjava.po.UploadFilePo;

import java.util.List;

@Mapper
public interface UploadFileMapper {
    void insertUploadFile(UploadFilePo uploadFilePo);
    List<UploadFilePo> selectUploadFileList();
}
