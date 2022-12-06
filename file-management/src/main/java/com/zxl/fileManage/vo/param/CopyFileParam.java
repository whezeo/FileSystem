package com.zxl.fileManage.vo.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 复制文件pojo类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CopyFileParam {
    private String originPath;
    private String targetPath;
    private boolean overwrite;

}
