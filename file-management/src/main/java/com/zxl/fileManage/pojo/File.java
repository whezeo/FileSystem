package com.zxl.fileManage.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class File {
    private Long id;
    private String name;
    private String path;
    private Long size;
    private String type;
    private Timestamp updateTime;
    private Long userId;
    private String link;
}
