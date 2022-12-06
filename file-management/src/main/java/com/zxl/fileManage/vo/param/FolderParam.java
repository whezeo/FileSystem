package com.zxl.fileManage.vo.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FolderParam {
    private String path;
    private String sortMethod;
}
