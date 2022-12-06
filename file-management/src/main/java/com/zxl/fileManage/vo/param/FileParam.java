package com.zxl.fileManage.vo.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * 上传文件pojo类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileParam {
    private String fullPath;
    private MultipartFile file;
    private String overwrite;
}
