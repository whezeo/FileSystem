package com.zxl.fileManage.vo.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class ResponseFile implements Serializable {
    private String name;
    private String link;
    private Long size;
    private String type;
    private Timestamp updateTime;
}
