package org.example.labelprojectjava.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.labelprojectjava.po.UploadFilePo;
import org.example.labelprojectjava.service.FileUploadService;
import org.example.labelprojectjava.vo.NormalResult;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.List;
import java.util.Set;

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("/file")
@Slf4j
public class FileUploadController {

    @Resource
    private FileUploadService fileUploadService;

    @PostMapping("/upload")
    public String uploadFile(@RequestParam MultipartFile file) throws NoSuchAlgorithmException, IOException {
        return fileUploadService.uploadFile(file);
    }

    @PostMapping("/url")
    public NormalResult getUrls(@RequestParam String userUUID){
        log.info("current thread{}",Thread.currentThread());
        String fileUrl = fileUploadService.getUrls(userUUID);
        if(fileUrl != null) return NormalResult.success(fileUrl);
        return NormalResult.error("获取错误");
    }

    @PostMapping("/vector")
    public NormalResult storeVector(@RequestParam String userUUID,
                                    @RequestParam String fileUrl,
                                    @RequestParam String textVectorSequence,
                                    @RequestParam String offsetVectorSequence) throws InterruptedException {
        String s = fileUploadService.storeVector(userUUID, fileUrl, textVectorSequence, offsetVectorSequence);
        log.info(s);
        return NormalResult.success(s);
    }

    @GetMapping("/excel")
    public void getExcel(HttpServletResponse response) throws IOException {
        List<UploadFilePo> uploadFiles = fileUploadService.getUploadFiles();
        log.info("Excel导出文件uploadFiles:{}", uploadFiles);
        // 2. 设置响应头
        String fileName = URLEncoder.encode("用户数据", StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        // 3. 写入Excel并输出到响应流
        EasyExcel.write(response.getOutputStream(), UploadFilePo.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()) // 自动列宽
                .sheet("文件识别结果数据")
                .doWrite(uploadFiles);
    }

    @GetMapping("/nothing")
    public NormalResult getNothing(){
        return NormalResult.success();
    }
}
