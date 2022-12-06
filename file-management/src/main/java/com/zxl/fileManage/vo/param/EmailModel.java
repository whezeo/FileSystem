package com.zxl.fileManage.vo.param;

import lombok.Data;

/*邮箱实体类*/
@Data
public class EmailModel {
    private String to;
    private String subject;
    private String content;
}